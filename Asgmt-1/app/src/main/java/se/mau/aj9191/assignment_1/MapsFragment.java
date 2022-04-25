package se.mau.aj9191.assignment_1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.security.Permission;
import java.util.ArrayList;
import java.util.HashMap;

public class MapsFragment extends Fragment implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {
    private static int UPDATE_INTERVAL = 30000;
    private static int UPDATE_DISTANCE = 0;

    private LocationManager locationManager;
    private MainViewModel viewModel;

    private MapView mapView;
    private GoogleMap map;
    private Button btnGroups;
    private Button btnLanguage;
    private RecyclerView rvViewable;
    private ViewableAdapter viewableAdapter;

    private HashMap<String, ArrayList<Marker>> mapMarkers; // group, markers
    private boolean languageSet = false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        if (savedInstanceState != null)
            mapMarkers = (HashMap<String, ArrayList<Marker>>)savedInstanceState.getSerializable("Markers");
        else
            mapMarkers = new HashMap<>();

        String currentLanguage = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString(LocaleHelper.SELECTED_LANGUAGE, "en");
        languageSet = !currentLanguage.equals("en");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putSerializable("Markers", mapMarkers);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        initializeComponents(view, savedInstanceState);
        registerListeners();

        requestPermissions();

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap)
    {
        map = googleMap;
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
            Group group = viewModel.joinedGroup(groupName);

            if (group == null || !group.viewable)
            {
                if (group == null && mapMarkers.containsKey(groupName))
                    clearMarkers(groupName);

                return;
            }

            showUsers(groupName, groupLocations.second);
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

    private void showUsers(String groupName, Location[] locations)
    {
        if (!mapMarkers.containsKey(groupName))
        {
            ArrayList<Marker> markers = new ArrayList<>(locations.length);
            mapMarkers.put(groupName, markers);
        }
        else
            clearMarkers(groupName);

        for (Location loc : locations)
        {
            MarkerOptions markerOptions = new MarkerOptions();

            double longitude = loc.getLongitude();
            double latitude = loc.getLatitude();

            if (Double.isNaN(longitude) || Double.isNaN(latitude))
                continue;

            markerOptions.position(new LatLng(longitude, latitude));
            markerOptions.title(loc.getMember());
            markerOptions.snippet(loc.getMember() + " last recorded location");

            Marker marker = map.addMarker(markerOptions);
            mapMarkers.get(groupName).add(marker);
        }
    }

    @Override
    public void onLocationChanged(@NonNull android.location.Location location)
    {
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        for (int i = 0; i < viewModel.getGroupsSize(); ++i)
        {
            Group group = viewModel.getGroup(i);
            Controller.sendMessage(JsonHelper.sendLocation(group.getId(), longitude, latitude));
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
        {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, UPDATE_INTERVAL, UPDATE_DISTANCE, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_INTERVAL, UPDATE_DISTANCE, this);
        }
        else
        {
            @SuppressLint("MissingPermission") ActivityResultLauncher<String> permissionResult = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result ->
            {
                if (result)
                {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, UPDATE_INTERVAL, UPDATE_DISTANCE, this);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_INTERVAL, UPDATE_DISTANCE, this);
                }
            });

            permissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void clearMarkers(String groupName)
    {
        ArrayList<Marker> markers = mapMarkers.get(groupName);

        for (Marker marker : markers)
            marker.remove();

        markers.clear();
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
