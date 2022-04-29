package se.mau.aj9191.assignment_1;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatFragment extends Fragment
{
    private MainActivity mainActivity;
    private MainViewModel viewModel;

    private TextView tvGroupName;
    private EditText etMessage;
    private ImageButton btnUpload;
    private ImageButton btnBack;
    private RecyclerView rvChat;
    private ChatAdapter chatAdapter;

    private Uri uri;

    private Group group;
    private boolean accessCamera = false;

    ActivityResultLauncher<String[]> cameraPermissions;
    ActivityResultLauncher<Uri> takePicture;

    public ChatFragment() { }
    public ChatFragment(Group group)
    {
        this.group = group;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        if (savedInstanceState != null)
            group = savedInstanceState.getParcelable("ChatGroup");

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            accessCamera = true;
        }

        cameraPermissions = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), results ->
        {
            boolean camera = results.get(Manifest.permission.CAMERA);
            boolean write = results.get(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            boolean read = results.get(Manifest.permission.READ_EXTERNAL_STORAGE);

            if (camera && write && read)
                accessCamera = true;
            else
                Toast.makeText(getContext(), R.string.error_permissions, Toast.LENGTH_SHORT).show();
        });

        takePicture = registerForActivityResult(new ActivityResultContracts.TakePicture(), result ->
        {
            if (result)
                enterImageDescription();
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putParcelable("ChatGroup", group);
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
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        initializeComponents(view);
        registerListeners();
        addObservers();

        return view;
    }

    private void initializeComponents(View view)
    {
        tvGroupName = view.findViewById(R.id.tvGroupName);
        etMessage = view.findViewById(R.id.etMessage);
        btnUpload = view.findViewById(R.id.btnUpload);
        btnBack = view.findViewById(R.id.btnBack);
        rvChat = view.findViewById(R.id.rvChat);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(true);

        rvChat.setLayoutManager(linearLayoutManager);
        rvChat.setAdapter(chatAdapter = new ChatAdapter(getActivity(), group.getMessages()));
        rvChat.addItemDecoration(new DividerItemDecoration(rvChat.getContext(), DividerItemDecoration.VERTICAL));

        tvGroupName.setText(group.getName());
    }

    private void registerListeners()
    {
        btnBack.setOnClickListener(view ->
        {
            getParentFragmentManager().popBackStack();
        });

        etMessage.setOnEditorActionListener((textView, i, keyEvent) ->
        {
            if (i == EditorInfo.IME_ACTION_SEND)
            {
                sendTextMessage(etMessage.getText().toString());
                etMessage.setText("");
                return true;
            }
            return false;
        });

        btnUpload.setOnClickListener(view ->
        {
            takePicture();
        });
    }

    private void addObservers()
    {
        viewModel.getTextMessageLiveData().observe(getViewLifecycleOwner(), textMessage ->
        {
            if (!group.getName().equals(textMessage.groupName))
                return;

            chatAdapter.notifyItemChanged(group.getMessagesSize() - 1);
        });
        viewModel.getImageMessageLiveData().observe(getViewLifecycleOwner(), imageMessage ->
        {
            if (!group.getName().equals(imageMessage.groupName))
                return;

            chatAdapter.notifyItemChanged(group.getMessagesSize() - 1);
        });

        viewModel.getSentImageLiveData().observe(getViewLifecycleOwner(), sentImage ->
        {
            upload(sentImage.imageid, sentImage.port);
        });
    }

    private void sendTextMessage(String message)
    {
        if (message.isEmpty() || mainActivity == null)
            return;

        mainActivity.getController()
                .sendMessage(JsonHelper.sendEnterText(group.getId(), message));
    }

    private void takePicture()
    {
        if (accessCamera)
        {
            String time = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
            String filename = "JPEG_" + time;
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), filename + ".jpg");
            Uri uri = FileProvider.getUriForFile(mainActivity.getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file);

            this.uri = uri;

            takePicture.launch(uri);
        }
        else
        {
            cameraPermissions.launch(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE});
        }
    }

    private void upload(String imageid, String port)
    {
        Executors.newSingleThreadExecutor().execute(() ->
        {
            try
            {
                Bitmap bitmap = getScaledBitmap(MediaStore.Images.Media.getBitmap(mainActivity.getContentResolver(), uri), 64 * 1024);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                InetSocketAddress socketAddress = new InetSocketAddress(NetworkService.IP, Integer.parseInt(port));

                Socket socket = new Socket();
                socket.connect(socketAddress, 7500);

                byte[] uploadArray = baos.toByteArray();
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                output.flush();
                output.writeUTF(imageid);
                output.flush();
                output.writeObject(uploadArray);
                output.flush();

                socket.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                getActivity().runOnUiThread(() ->
                {
                    Toast.makeText(getContext(), R.string.error_connect_socket, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void enterImageDescription()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        EditText edMessage = new EditText(getContext());
        edMessage.setHint(R.string.hint_enter_description);

        builder.setPositiveButton("OK", (dialogInterface, i) ->
        {
            String message = edMessage.getText().toString();

            if (message.isEmpty())
            {
                Toast.makeText(requireContext(), R.string.error_empty, Toast.LENGTH_SHORT).show();
                return;
            }

            if (mainActivity != null)
            {
                LatLng location = viewModel.getLocation();
                if (!Double.isNaN(location.latitude) && !Double.isNaN(location.longitude))
                {
                    mainActivity.getController()
                            .sendMessage(JsonHelper.sendEnterImage(group.getId(), message, location.longitude, location.latitude));
                }
                else
                    Toast.makeText(getContext(), R.string.error_location, Toast.LENGTH_SHORT).show();
            }

        });
        builder.setNegativeButton(R.string.btn_cancel, null);

        layout.addView(edMessage);

        AlertDialog dialog = builder.create();
        dialog.setTitle(R.string.title_image_description);
        dialog.setView(layout);

        dialog.show();
    }

    private Bitmap getScaledBitmap(Bitmap bitmap, int maxBytes)
    {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int pixels = width * height;

        int maxPixels = maxBytes / 4;

        if (pixels <= maxPixels)
            return bitmap;

        double scaleFactor = Math.sqrt(maxPixels / (double)pixels);

        int newWidth = (int)Math.floor(width * scaleFactor);
        int newHeight = (int)Math.floor(height * scaleFactor);

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }
}
