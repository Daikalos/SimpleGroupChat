package se.mau.aj9191.assignment_1;

import android.os.Bundle;
import android.os.Message;
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

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatFragment extends Fragment
{
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
    }

    private void sendMessage(String message)
    {
        if (message.isEmpty())
            return;

        Controller.sendMessage(JsonHelper.sendEnterText(group.getId(), message));
    }
}
