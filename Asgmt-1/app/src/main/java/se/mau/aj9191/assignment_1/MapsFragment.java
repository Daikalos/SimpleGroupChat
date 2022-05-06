package se.mau.aj9191.assignment_1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    private boolean languageSet = false;

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        locationPermission = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), results ->
        {
            boolean fine = results.get(Manifest.permission.ACCESS_FINE_LOCATION);
            boolean coarse = results.get(Manifest.permission.ACCESS_COARSE_LOCATION);

            if (fine || coarse)
                map.setMyLocationEnabled(true);

            if (fine)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_INTERVAL, UPDATE_DISTANCE, this);
            if (coarse)
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, UPDATE_INTERVAL, UPDATE_DISTANCE, this);
        });

        String currentLanguage = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString(LocaleHelper.SELECTED_LANGUAGE, "en");
        languageSet = !currentLanguage.equals("en");
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
        map.setOnMarkerClickListener(this);

        requestPermissions();

        for (int i = viewModel.getGroupsSize() - 1; i >= 0; --i)
            loadMarkers(viewModel.getGroup(i));
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
        viewModel.getLocationsLiveData().observe(getViewLifecycleOwner(), groupName ->
        {
            if (map == null)
                return;

            clearMarkers(groupName);
            loadMarkers(viewModel.getGroup(groupName));
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

    private void loadMarkers(Group group)
    {
        if (group == null)
            return;

        ArrayList<NormalMarker> markers = group.getMarkers();
        mapMarkers.put(group.getName(), new ArrayList<>(markers.size()));

        for (NormalMarker normalMarker : markers)
        {
            double longitude = normalMarker.longitude;
            double latitude = normalMarker.latitude;

            if (Double.isNaN(longitude) || Double.isNaN(latitude))
                continue;

            Marker marker = map.addMarker(normalMarker.getMarkerOptions());

            if (normalMarker.getType() == NormalMarker.IMAGE_MARKER)
                marker.setTag(((ImageMarker)normalMarker).imageMessage);

            mapMarkers.get(group.getName()).add(marker);
        }
    }

    private void clearMarkers(String groupName)
    {
        if (!mapMarkers.containsKey(groupName))
            return;

        for (Marker marker : mapMarkers.get(groupName))
            marker.remove();

        mapMarkers.get(groupName).clear();
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker)
    {
        ImageMessage imageMessage = (ImageMessage)marker.getTag();

        if (imageMessage == null)
            return false;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(8, 8, 8, 8);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        ImageView ivPicture = new ImageView(getContext());
        TextView tvDesc = new TextView(getContext());

        int color = ContextCompat.getColor(getContext(), R.color.black);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(256, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        ivPicture.setLayoutParams(params);

        ivPicture.setAdjustViewBounds(true);
        ivPicture.setPadding(0, 0, 0, 8);

        tvDesc.setTextColor(color);
        tvDesc.setTextSize(24);
        tvDesc.setGravity(Gravity.CENTER);

        ivPicture.setImageBitmap(imageMessage.bitmap);
        tvDesc.setText(imageMessage.message);

        builder.setPositiveButton("OK", null);

        layout.addView(ivPicture);
        layout.addView(tvDesc);

        AlertDialog dialog = builder.create();
        dialog.setView(layout);

        dialog.show();

        return true;
    }

    private void requestPermissions()
    {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            map.setMyLocationEnabled(true);
        }
        else
            locationPermission.launch(new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION });

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_INTERVAL, UPDATE_DISTANCE, this);
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, UPDATE_INTERVAL, UPDATE_DISTANCE, this);
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
