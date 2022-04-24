package se.mau.aj9191.assignment_1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MapsFragment extends Fragment implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {
    private static int UPDATE_INTERVAL = 30000;
    private static int UPDATE_DISTANCE = 0;

    private LocationManager locationManager;
    private MainViewModel viewModel;

    private MapView mapView;
    private GoogleMap map;

    private Button btnGroups;
    private Button btnLanguage;
    private RecyclerView rvGroupsViewable;

    private boolean languageSet = false;

    private HashMap<String, ArrayList<Marker>> mapMarkers = new HashMap<>(); // group, markers

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        if (savedInstanceState != null)
            mapMarkers = (HashMap<String, ArrayList<Marker>>)savedInstanceState.getSerializable("Markers");

        initializeComponents(view, savedInstanceState);
        registerListeners();

        requestPermissions();

        String currentLanguage = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString(LocaleHelper.SELECTED_LANGUAGE, "en");
        languageSet = !currentLanguage.equals("en");

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("Markers", mapMarkers);
    }

    private void initializeComponents(View view, Bundle savedInstanceState)
    {
        locationManager = (LocationManager) view.getContext().getSystemService(Context.LOCATION_SERVICE);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        btnGroups = view.findViewById(R.id.btnGroups);
        btnLanguage = view.findViewById(R.id.btnLanguage);
        rvGroupsViewable = view.findViewById(R.id.rvViewableGroups);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        rvGroupsViewable.setAdapter(new ViewableAdapter(viewModel));
        rvGroupsViewable.setLayoutManager(linearLayoutManager);
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

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap)
    {
        map = googleMap;

        viewModel.getLocationsLiveData().observe(getViewLifecycleOwner(), groupLocations ->
        {
            String groupName = groupLocations.first;
            Group group = viewModel.joinedGroup(groupName);

            if (group == null || !group.viewable)
                return;

            showUsers(groupName, groupLocations.second);
        });
        viewModel.getViewableLiveData().observe(getViewLifecycleOwner(), group ->
        {
            String groupName = group.getName();

            if (mapMarkers.containsKey(groupName))
            {
                if (!group.viewable)
                {
                    for (Marker marker : mapMarkers.get(groupName))
                        marker.remove();

                    mapMarkers.get(groupName).clear();
                }
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
        {
            for (Marker marker : mapMarkers.get(groupName))
                marker.remove();

            mapMarkers.get(groupName).clear();
        }

        for (Location loc : locations)
        {
            MarkerOptions markerOptions = new MarkerOptions();
            Coordinate coordinates = loc.getCoordinates();

            if (Double.isNaN(coordinates.longitude) || Double.isNaN(coordinates.latitude))
                continue;

            markerOptions.position(new LatLng(coordinates.longitude, coordinates.latitude));
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

    private void requestPermissions()
    {
        @SuppressLint("MissingPermission") ActivityResultLauncher<String> permissionResult = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result ->
        {
            if (result)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_INTERVAL, UPDATE_DISTANCE, this);
                map.setMyLocationEnabled(true);
            }
        });

        permissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker)
    {
        return false;
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
