package se.mau.aj9191.assignment_1;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UsersFragment extends Fragment
{
    private MainViewModel viewModel;

    private TextView tvGroupName;
    private ImageButton btnBack;
    private Button btnChat;
    private Button btnAction;
    private RecyclerView rvUsers;

    private String groupName;

    public UsersFragment() { }
    public UsersFragment(String groupName)
    {
        this.groupName = groupName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        if (savedInstanceState != null)
            groupName = savedInstanceState.getString("GroupName");

        initializeComponents(view);
        registerListeners();

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Controller.sendMessage(JsonHelper.sendGetMembers(groupName));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("GroupName", groupName);
    }

    private void initializeComponents(View view)
    {
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        tvGroupName = view.findViewById(R.id.tvGroupName);
        btnBack = view.findViewById(R.id.btnBack);
        btnChat = view.findViewById(R.id.btnChat);
        btnAction = view.findViewById(R.id.btnAction);
        rvUsers = view.findViewById(R.id.rvUsers);

        tvGroupName.setText(groupName);

        if (viewModel.joinedGroup(groupName) != null)
            btnAction.setText(R.string.btn_deregister);
        else
        {
            btnChat.setEnabled(false);
            btnChat.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey));

            btnAction.setText(R.string.btn_register);
        }

        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUsers.setAdapter(new UsersAdapter(groupName, viewModel, getViewLifecycleOwner()));
    }

    private void registerListeners()
    {
        btnBack.setOnClickListener(view ->
        {
            getParentFragmentManager().popBackStack();
        });

        btnChat.setOnClickListener(view ->
        {
            getParentFragmentManager().beginTransaction()
                .replace(R.id.fcvMain, new ChatFragment())
                .addToBackStack(null).commit();
        });

        btnAction.setOnClickListener(view ->
        {
            if (viewModel.joinedGroup(groupName) != null)
            {
                leaveGroup(view);

                btnChat.setEnabled(true);
                btnChat.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));

                btnAction.setText(R.string.btn_deregister);
            }
            else
            {
                enterGroup(view);

                btnChat.setEnabled(false);
                btnChat.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey));

                btnAction.setText(R.string.btn_register);
            }
        });
    }

    private void enterGroup(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        EditText edUsername = new EditText(getContext());
        edUsername.setHint("enter user name");

        builder.setPositiveButton("OK", (dialogInterface, i) ->
        {
            String username = edUsername.getText().toString();

            if (username.isEmpty())
            {
                Toast.makeText(requireContext(), "empty string", Toast.LENGTH_SHORT).show();
                return;
            }

            Controller.sendMessage(JsonHelper.sendRegister(groupName, username));

            ((AppCompatActivity)view.getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.fcvMain, new UsersFragment(groupName))
                .addToBackStack(null).commit();
        });
        builder.setNegativeButton("Cancel", null);

        layout.addView(edUsername);

        AlertDialog dialog = builder.create();
        dialog.setTitle("Create group");
        dialog.setView(layout);

        dialog.show();
    }
    private void leaveGroup(View view)
    {
        Controller.sendMessage(JsonHelper.sendUnregister(viewModel.joinedGroup(groupName).getId()));
    }
}
