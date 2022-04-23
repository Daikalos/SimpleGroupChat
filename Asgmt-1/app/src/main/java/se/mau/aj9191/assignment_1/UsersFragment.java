package se.mau.aj9191.assignment_1;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UsersFragment extends Fragment
{
    private MainViewModel viewModel;

    private ImageButton btnBack;
    private Button btnChat;
    private Button btnAction;
    private RecyclerView rvUsers;

    private final String groupName;

    public UsersFragment(String groupName)
    {
        this.groupName = groupName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
    {
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        initializeComponents(view);
        registerListeners();

        Controller.sendMessage(JsonHelper.sendGetMembers(groupName));

        return view;
    }

    private void initializeComponents(View view)
    {
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        btnBack = view.findViewById(R.id.btnBack);
        btnChat = view.findViewById(R.id.btnChat);
        btnAction = view.findViewById(R.id.btnAction);
        rvUsers = view.findViewById(R.id.rvUsers);

        if (viewModel.enteredGroup(groupName))
            btnAction.setText(R.string.btn_register);
        else
        {
            btnChat.setEnabled(false);
            btnAction.setText(R.string.btn_deregister);
        }

        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUsers.setAdapter(new UsersAdapter(new ViewModelProvider(requireActivity()).get(MainViewModel.class), this));
    }

    private void registerListeners()
    {
        btnBack.setOnClickListener(view ->
        {
            getParentFragmentManager().popBackStack();

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fcvMain, new GroupsFragment());
            transaction.commit();
        });

        btnChat.setOnClickListener(view ->
        {
            getParentFragmentManager().popBackStack();

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fcvMain, new ChatFragment());
            transaction.commit();
        });

        btnAction.setOnClickListener(view ->
        {
            if (viewModel.enteredGroup(groupName))
            {
                enterGroup(view);

                btnChat.setEnabled(true);
                btnAction.setText(R.string.btn_deregister);
            }
            else
            {
                leaveGroup(view);

                btnChat.setEnabled(false);
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

            FragmentTransaction transaction = ((AppCompatActivity)view.getContext()).getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fcvMain, new UsersFragment(groupName));
            transaction.addToBackStack(null);
            transaction.commit();
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

    }
}
