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
import android.os.Parcel;
import android.os.Parcelable;
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
    private static final int UPDATE_INTERVAL = 30000;
    private static final int UPDATE_DISTANCE = 0;

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

    private HashMap<String, ArrayList<Marker>> mapMarkers = new HashMap<>(); // group, markers
    private HashMap<String, ArrayList<MarkerContents>> mapMarkerContents;

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
            mapMarkerContents = (HashMap<String, ArrayList<MarkerContents>>)savedInstanceState.getSerializable("MarkerContents");
        else
            mapMarkerContents = new HashMap<>();

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
        savedInstanceState.putSerializable("MarkerContents", mapMarkerContents);
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
        addObservers();

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap)
    {
        map = googleMap;

        requestPermissions();

        for (String groupName : mapMarkerContents.keySet()) // add back all markers
        {
            ArrayList<MarkerContents> markerOptions = mapMarkerContents.get(groupName);
            mapMarkers.put(groupName, new ArrayList<>(markerOptions.size()));

            for (MarkerContents mo : markerOptions)
            {
                Marker marker = map.addMarker(mo.getMarkerOptions());
                marker.setTag(mo.imageMessage);

                mapMarkers.get(groupName).add(marker);
            }
        }
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
            mapMarkerContents.put(groupName, new ArrayList<>(locations.length));
        }

        clearMarkers(groupName);
        addImageMarkers();

        for (Location loc : locations)
        {
            double longitude = loc.getLongitude();
            double latitude = loc.getLatitude();

            if (Double.isNaN(longitude) || Double.isNaN(latitude))
                continue;

            MarkerContents markerContents = new MarkerContents();
            markerContents.latitude = latitude;
            markerContents.longitude = longitude;
            markerContents.title = loc.getMember();
            markerContents.snippet = loc.getMember() + " last recorded location";
            markerContents.visible = group.viewable;

            Marker marker = map.addMarker(markerContents.getMarkerOptions());

            mapMarkers.get(groupName).add(marker);
            mapMarkerContents.get(groupName).add(markerContents);
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
        ImageMessage imageMessage = (ImageMessage)marker.getTag();

        if (imageMessage == null)
            return false;



        return true;
    }

    private void requestPermissions()
    {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            map.setMyLocationEnabled(true);
        }

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
        mapMarkerContents.get(groupName).clear();
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

                    MarkerContents markerContents = new MarkerContents();
                    markerContents.latitude = imageMessage.latitude;
                    markerContents.longitude = imageMessage.longitude;
                    markerContents.icon = thumbnail;
                    markerContents.anchorX = 0.5f;
                    markerContents.anchorY = 1.0f;
                    markerContents.imageMessage = imageMessage;

                    mainActivity.runOnUiThread(() ->
                    {
                        Marker marker = map.addMarker(markerContents.getMarkerOptions());
                        marker.setTag(markerContents.imageMessage);

                        if (!mapMarkers.containsKey(message.groupName))
                        {
                            mapMarkers.put(message.groupName, new ArrayList<>());
                            mapMarkerContents.put(message.groupName, new ArrayList<>());
                        }

                        mapMarkers.get(message.groupName).add(marker);
                        mapMarkerContents.get(message.groupName).add(markerContents);
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

    private static class MarkerContents implements Parcelable
    {
        public double latitude = Double.NaN;
        public double longitude = Double.NaN;
        public String title = null;
        public String snippet = null;
        public float anchorX = Float.NaN;
        public float anchorY = Float.NaN;
        public boolean visible = true;
        public Bitmap icon = null;

        public ImageMessage imageMessage = null;

        public MarkerContents()
        {

        }

        protected MarkerContents(Parcel in)
        {
            latitude = in.readDouble();
            longitude = in.readDouble();
            title = in.readString();
            snippet = in.readString();
            anchorX = in.readFloat();
            anchorY = in.readFloat();
            visible = in.readInt() != 0;
            icon = in.readParcelable(Bitmap.class.getClassLoader());
            imageMessage = in.readParcelable(ImageMessage.class.getClassLoader());
        }

        public MarkerOptions getMarkerOptions()
        {
            MarkerOptions markerOptions = new MarkerOptions();

            if (!Double.isNaN(latitude) && !Double.isNaN(longitude))
                markerOptions.position(new LatLng(latitude, longitude));
            if (title != null)
                markerOptions.title(title);
            if (snippet != null)
                markerOptions.snippet(snippet);
            if (!Float.isNaN(anchorX) && !Float.isNaN(anchorY))
                markerOptions.anchor(anchorX, anchorY);
            if (icon != null)
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));

            markerOptions.visible(visible);

            return markerOptions;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i)
        {
            parcel.writeDouble(latitude);
            parcel.writeDouble(longitude);
            parcel.writeString(title);
            parcel.writeString(snippet);
            parcel.writeFloat(anchorX);
            parcel.writeFloat(anchorY);
            parcel.writeInt(visible ? 1 : 0);
            parcel.writeParcelable(icon, i);
            parcel.writeParcelable(imageMessage, i);
        }

        @Override
        public int describeContents()
        {
            return 0;
        }

        public static final Creator<MarkerContents> CREATOR = new Creator<MarkerContents>()
        {
            @Override
            public MarkerContents createFromParcel(Parcel in)
            {
                return new MarkerContents(in);
            }

            @Override
            public MarkerContents[] newArray(int size)
            {
                return new MarkerContents[size];
            }
        };
    }
}
