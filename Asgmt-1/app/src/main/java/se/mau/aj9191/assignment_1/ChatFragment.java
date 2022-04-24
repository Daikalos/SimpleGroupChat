package se.mau.aj9191.assignment_1;

import android.os.Bundle;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatFragment extends Fragment
{
    private MainViewModel viewModel;

    private TextView tvGroupName;
    private EditText etMessage;
    private ImageButton btnUpload;
    private ImageButton btnBack;
    private RecyclerView rvChat;

    private Group group;

    public ChatFragment() { }
    public ChatFragment(Group group)
    {
        this.group = group;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        if (savedInstanceState != null)
            group = (Group)savedInstanceState.getSerializable("Group");

        initializeComponents(view);
        registerListeners();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("Group", group);
    }

    private void initializeComponents(View view)
    {
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        tvGroupName = view.findViewById(R.id.tvGroupName);
        etMessage = view.findViewById(R.id.etMessage);
        btnUpload = view.findViewById(R.id.btnUpload);
        btnBack = view.findViewById(R.id.btnBack);
        rvChat = view.findViewById(R.id.rvChat);

        rvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChat.setAdapter(new ChatAdapter(viewModel));
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

    private void sendMessage(String message)
    {

    }
}
