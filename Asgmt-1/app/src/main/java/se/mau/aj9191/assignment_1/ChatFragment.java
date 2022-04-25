package se.mau.aj9191.assignment_1;

import android.Manifest;
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
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatFragment extends Fragment
{
    private LocationManager locationManager;
    private MainViewModel viewModel;

    private TextView tvGroupName;
    private EditText etMessage;
    private ImageButton btnUpload;
    private ImageButton btnBack;
    private RecyclerView rvChat;

    private ChatAdapter chatAdapter;

    private Group group;

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
            group = (Group)savedInstanceState.getSerializable("ChatGroup");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putSerializable("ChatGroup", group);
        super.onSaveInstanceState(savedInstanceState);
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
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        rvChat.setLayoutManager(linearLayoutManager);
        rvChat.setAdapter(chatAdapter = new ChatAdapter(group.getMessages()));
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
                sendMessage(etMessage.getText().toString());
                etMessage.setText("");
                return true;
            }
            return false;
        });

        btnUpload.setOnClickListener(view ->
        {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            {
                Controller.sendMessage(JsonHelper.sendEnterImage(group.getId(), "", viewModel.getLocation().longitude, viewModel.getLocation().longitude));
            }
            else
            {
                ActivityResultLauncher<String> photoPermissionResult = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result ->
                {
                    if (result)
                        Controller.sendMessage(JsonHelper.sendEnterImage(group.getId(), "", viewModel.getLocation().longitude, viewModel.getLocation().longitude));
                });

                photoPermissionResult.launch(Manifest.permission.CAMERA);
            }
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
            takePicture(sentImage.imageid, sentImage.port);
        });
    }

    private void takePicture(String imageid, String port)
    {
        String time = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        String filename = "JPEG_" + time;
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), filename);
        Uri uri = FileProvider.getUriForFile(getActivity().getApplicationContext(), getActivity().getPackageName() + ".camera", file);

        ActivityResultLauncher<Uri> photoResult = registerForActivityResult(new ActivityResultContracts.TakePicture(), result ->
        {
            if (result)
            {
                Executors.newSingleThreadExecutor().execute(() ->
                {
                    try
                    {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), uri);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 64, baos);

                        byte[] uploadArray = baos.toByteArray();
                        Socket socket = new Socket(NetworkService.IP, Integer.parseInt(port));
                        ObjectOutputStream output= new ObjectOutputStream(socket.getOutputStream());
                        output.flush();
                        output.writeUTF(imageid);
                        output.flush();
                        output.writeObject(uploadArray);
                        output.flush();

                        socket.close();
                    }
                    catch (IOException exception)
                    {
                        exception.printStackTrace();
                    }
                });
            }
        });

        photoResult.launch(uri);
    }

    private void sendMessage(String message)
    {
        if (message.isEmpty())
            return;

        Controller.sendMessage(JsonHelper.sendEnterText(group.getId(), message));
    }
}
