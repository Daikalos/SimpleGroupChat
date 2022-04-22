package se.mau.aj9191.assignment_1;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class ChatFragment extends Fragment
{
    private EditText messageField;
    private Button btnUpload;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
    {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        initializeComponents(view);
        registerListeners();

        return view;
    }

    private void initializeComponents(View view)
    {
        messageField = view.findViewById(R.id.etMessage);
        btnUpload = view.findViewById(R.id.btnUpload);
    }

    private void registerListeners()
    {
        messageField.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent)
            {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_SEND)
                {
                    sendMessage(messageField.getText().toString());
                    messageField.setText("");
                    handled = true;
                }
                return handled;
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });
    }

    private void sendMessage(String message)
    {

    }
}
