package se.mau.aj9191.assignment_1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.security.Permission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapsFragment extends Fragment implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener
{
    private static int UPDATE_INTERVAL = 30000;
    private static int UPDATE_DISTANCE = 0;

    private MainActivity mainActivity;

    private LocationManager locationManager;
    private MainViewModel viewModel;

    private MapView mapView;
    private GoogleMap map;
    private Button btnGroups;
    private Button btnLanguage;
    private RecyclerView rvViewable;
    private ViewableAdapter viewableAdapter;

    private ActivityResultLauncher<String[]> locationPermission;

    private final HashMap<String, ArrayList<Marker>> mapMarkers = new HashMap<>(); // group, markers
    private HashMap<String, ArrayList<MarkerOptions>> mapMarkerOptions;

    private boolean languageSet = false;

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        if (savedInstanceState != null)
            mapMarkerOptions = (HashMap<String, ArrayList<MarkerOptions>>)savedInstanceState.getSerializable("MarkerOptions");
        else
            mapMarkerOptions = new HashMap<>();

        locationPermission = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), results ->
        {
            boolean fine = results.get(Manifest.permission.ACCESS_FINE_LOCATION);
            boolean coarse = results.get(Manifest.permission.ACCESS_COARSE_LOCATION);

            if (fine || coarse)
                map.setMyLocationEnabled(true);

            if (fine)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_INTERVAL, UPDATE_DISTANCE, this);
            else if (coarse)
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, UPDATE_INTERVAL, UPDATE_DISTANCE, this);
        });

        String currentLanguage = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString(LocaleHelper.SELECTED_LANGUAGE, "en");
        languageSet = !currentLanguage.equals("en");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putSerializable("MarkerOptions", mapMarkerOptions);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mainActivity = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        initializeComponents(view, savedInstanceState);
        registerListeners();

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap)
    {
        map = googleMap;

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            map.setMyLocationEnabled(true);
        }

        requestPermissions();

        for (String groupName : mapMarkerOptions.keySet())
        {
            ArrayList<MarkerOptions> markerOptions = mapMarkerOptions.get(groupName);
            mapMarkers.put(groupName, new ArrayList<>(markerOptions.size()));

            for (MarkerOptions mo : markerOptions)
            {
                Marker marker = map.addMarker(mo);
                mapMarkers.get(groupName).add(marker);
            }
        }

        addObservers();
    }

    private void initializeComponents(View view, Bundle savedInstanceState)
    {
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        btnGroups = view.findViewById(R.id.btnGroups);
        btnLanguage = view.findViewById(R.id.btnLanguage);
        rvViewable = view.findViewById(R.id.rvViewableGroups);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        rvViewable.setAdapter(viewableAdapter = new ViewableAdapter(viewModel));
        rvViewable.setLayoutManager(linearLayoutManager);
        rvViewable.addItemDecoration(new DividerItemDecoration(rvViewable.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void registerListeners()
    {
        btnGroups.setOnClickListener(view ->
        {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fcvMain, new GroupsFragment())
                    .addToBackStack(null).commit();
        });
        btnLanguage.setOnClickListener(view ->
        {
            if (languageSet = !languageSet)
                LocaleHelper.setLocale(requireContext(), "sv");
            else
                LocaleHelper.setLocale(requireContext(), "en");

            btnGroups.setText(getResources().getString(R.string.btn_groups));
            btnLanguage.setText(getResources().getString(R.string.btn_language));
        });
    }

    private void addObservers()
    {
        viewModel.getLocationsLiveData().observe(getViewLifecycleOwner(), groupLocations ->
        {
            String groupName = groupLocations.first;
            Group group = viewModel.getGroup(groupName);

            if (group == null)
            {
                clearMarkers(groupName);
                return;
            }

            showUsers(group, groupLocations.second);
        });
        viewModel.getViewableLiveData().observe(getViewLifecycleOwner(), group ->
        {
            String groupName = group.getName();

            if (mapMarkers.containsKey(groupName))
            {
                for (Marker marker : mapMarkers.get(groupName))
                    marker.setVisible(group.viewable);
            }
        });
    }

    private void showUsers(Group group, Location[] locations)
    {
        String groupName = group.getName();

        if (!mapMarkers.containsKey(groupName))
        {
            mapMarkers.put(groupName, new ArrayList<>(locations.length));
            mapMarkerOptions.put(groupName, new ArrayList<>(locations.length));
        }

        clearMarkers(groupName);
        addImageMarkers();

        for (Location loc : locations)
        {
            MarkerOptions markerOptions = new MarkerOptions();

            double longitude = loc.getLongitude();
            double latitude = loc.getLatitude();

            if (Double.isNaN(longitude) || Double.isNaN(latitude))
                continue;

            markerOptions.position(new LatLng(latitude, longitude));
            markerOptions.title(loc.getMember());
            markerOptions.snippet(loc.getMember() + " last recorded location");

            Marker marker = map.addMarker(markerOptions);
            marker.setVisible(group.viewable);

            mapMarkers.get(groupName).add(marker);
            mapMarkerOptions.get(groupName).add(markerOptions);
        }
    }

    @Override
    public void onLocationChanged(@NonNull android.location.Location location)
    {
        if (mainActivity == null)
            return;

        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        for (int i = 0; i < viewModel.getGroupsSize(); ++i)
        {
            Group group = viewModel.getGroup(i);
            mainActivity.getController()
                    .sendMessage(JsonHelper.sendLocation(group.getId(), longitude, latitude));
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker)
    {
        return false;
    }

    private void requestPermissions()
    {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_INTERVAL, UPDATE_DISTANCE, this);
        else if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, UPDATE_INTERVAL, UPDATE_DISTANCE, this);
        else
            locationPermission.launch(new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION });
    }

    private void clearMarkers(String groupName)
    {
        if (!mapMarkers.containsKey(groupName))
            return;

        for (Marker marker : mapMarkers.get(groupName))
            marker.remove();

        mapMarkers.get(groupName).clear();
        mapMarkerOptions.get(groupName).clear();
    }

    private void addImageMarkers()
    {
        executorService.execute(() ->
        {
            for (int i = 0; i < viewModel.getGroupsSize(); ++i)
            {
                Group group = viewModel.getGroup(i);
                for (TextMessage message : group.getMessages())
                {
                    if (message.getType() == TextMessage.TEXT_TYPE)
                        continue;

                    ImageMessage imageMessage = (ImageMessage)message;
                    Bitmap bitmap = imageMessage.bitmap;

                    if (bitmap == null)
                        continue;

                    Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 64, 64);

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(imageMessage.latitude, imageMessage.longitude));
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(thumbnail));
                    markerOptions.anchor(0.5f, 1);

                    mainActivity.runOnUiThread(() ->
                    {
                        Marker marker = map.addMarker(markerOptions);

                        if (!mapMarkers.containsKey(message.groupName))
                        {
                            mapMarkers.put(message.groupName, new ArrayList<>());
                            mapMarkerOptions.put(message.groupName, new ArrayList<>());
                        }

                        mapMarkers.get(message.groupName).add(marker);
                        mapMarkerOptions.get(message.groupName).add(markerOptions);
                    });
                }
            }
        });
    }

    @Override
    public void onProviderEnabled(@NonNull String provider)
    {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider)
    {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (mapView != null)
            mapView.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if (mapView != null)
            mapView.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (mapView != null)
            mapView.onDestroy();
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();

        if (mapView != null)
            mapView.onLowMemory();
    }

}
