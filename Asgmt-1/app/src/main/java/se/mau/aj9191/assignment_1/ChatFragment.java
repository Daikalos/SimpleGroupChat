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
import androidx.recyclerview.widget.RecyclerView;

public class ChatFragment extends Fragment
{
    private EditText etMessage;
    private ImageButton btnUpload;
    private ImageButton btnBack;
    private RecyclerView rvChat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        initializeComponents(view);
        registerListeners();

        return view;
    }

    private void initializeComponents(View view)
    {
        etMessage = view.findViewById(R.id.etMessage);
        btnUpload = view.findViewById(R.id.btnUpload);
        btnBack = view.findViewById(R.id.btnBack);
        rvChat = view.findViewById(R.id.rvChat);
    }

    private void registerListeners()
    {
        etMessage.setOnEditorActionListener((textView, i, keyEvent) ->
        {
            boolean handled = false;
            if (i == EditorInfo.IME_ACTION_SEND)
            {
                sendMessage(etMessage.getText().toString());
                etMessage.setText("");
                handled = true;
            }
            return handled;
        });

        btnUpload.setOnClickListener(view ->
        {

        });

        btnBack.setOnClickListener(view ->
        {
            getParentFragmentManager().popBackStack();
        });
    }

    private void sendMessage(String message)
    {

    }
}
