package se.mau.aj9191.assignment_1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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

public class MapsFragment extends Fragment implements OnMapReadyCallback, LocationListener {
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

    private final HashMap<String, ArrayList<Marker>> mapMarkers = new HashMap<>(); // group, markers

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        initializeComponents(view, savedInstance);
        registerListeners();

        String currentLanguage = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString(LocaleHelper.SELECTED_LANGUAGE, "en");
        languageSet = !currentLanguage.equals("en");

        return view;
    }

    @SuppressLint("MissingPermission")
    private void initializeComponents(View view, Bundle savedInstance)
    {
        locationManager = view.getContext().getSystemService(LocationManager.class);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_INTERVAL, UPDATE_DISTANCE, this);

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstance);
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
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fcvMain, new GroupsFragment());
            transaction.addToBackStack(null);
            transaction.commit();
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
        map.setMyLocationEnabled(true);

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
