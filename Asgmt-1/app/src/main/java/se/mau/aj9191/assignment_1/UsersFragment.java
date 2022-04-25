package se.mau.aj9191.assignment_1;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

public class UsersFragment extends Fragment
{
    private MainViewModel viewModel;

    private TextView tvGroupName;
    private ImageButton btnBack;
    private Button btnChat;
    private Button btnAction;
    private RecyclerView rvUsers;

    private String groupName;
    private Group group = null;

    private ArrayList<String> data;
    private UsersAdapter usersAdapter;

    public UsersFragment() { }
    public UsersFragment(String groupName)
    {
        this.groupName = groupName;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        if (savedInstanceState != null)
        {
            groupName = savedInstanceState.getString("GroupName");
            data = savedInstanceState.getStringArrayList("UsersList");
        }
        else
            data = new ArrayList<>();

        group = viewModel.joinedGroup(groupName);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        group = viewModel.joinedGroup(groupName);
        Controller.sendMessage(JsonHelper.sendGetMembers(groupName));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putString("GroupName", groupName);
        savedInstanceState.putStringArrayList("UsersList", data);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        initializeComponents(view);
        registerListeners();
        addObservers();

        return view;
    }

    private void initializeComponents(View view)
    {
        tvGroupName = view.findViewById(R.id.tvGroupName);
        btnBack = view.findViewById(R.id.btnBack);
        btnChat = view.findViewById(R.id.btnChat);
        btnAction = view.findViewById(R.id.btnAction);
        rvUsers = view.findViewById(R.id.rvUsers);

        rvUsers.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvUsers.setAdapter(usersAdapter = new UsersAdapter(data));
        rvUsers.addItemDecoration(new DividerItemDecoration(rvUsers.getContext(), DividerItemDecoration.VERTICAL));

        tvGroupName.setText(groupName);

        if (group != null)
            enableControls();
        else
            disableControls();
    }

    private void registerListeners()
    {
        btnBack.setOnClickListener(view ->
        {
            getParentFragmentManager().popBackStack();
        });

        btnChat.setOnClickListener(view ->
        {
            if (group != null)
            {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fcvMain, new ChatFragment(group))
                        .addToBackStack(null).commit();
            }
        });

        btnAction.setOnClickListener(view ->
        {
            if (group != null)
                leaveGroup();
            else
                enterGroup();
        });
    }

    private void addObservers()
    {
        viewModel.getRegisterLiveData().observe(getViewLifecycleOwner(), group ->
        {
            if (groupName.equals(group.getName()))
            {
                this.group = group;

                enableControls();
                Controller.sendMessage(JsonHelper.sendGetMembers(groupName));
            }
        });

        viewModel.getUnregisterLiveData().observe(getViewLifecycleOwner(), id ->
        {
            if (group != null && id.equals(group.getId()))
            {
                disableControls();
                Controller.sendMessage(JsonHelper.sendGetMembers(groupName));
            }
        });

        viewModel.getMembersLiveData().observe(getViewLifecycleOwner(), members ->
        {
            if (!members.first.equals(groupName))
                return;

            data.clear();
            data.addAll(Arrays.asList(members.second));

            usersAdapter.notifyDataSetChanged();
        });
    }

    private void enterGroup()
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
        });
        builder.setNegativeButton("Cancel", null);

        layout.addView(edUsername);

        AlertDialog dialog = builder.create();
        dialog.setTitle("Enter group");
        dialog.setView(layout);

        dialog.show();
    }
    private void leaveGroup()
    {
        Controller.sendMessage(JsonHelper.sendUnregister(group.getId()));
    }

    private void disableControls()
    {
        btnChat.setEnabled(false);
        btnChat.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey));

        btnAction.setText(R.string.btn_register);
    }
    private void enableControls()
    {
        btnChat.setEnabled(true);
        btnChat.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));

        btnAction.setText(R.string.btn_deregister);
    }
}
