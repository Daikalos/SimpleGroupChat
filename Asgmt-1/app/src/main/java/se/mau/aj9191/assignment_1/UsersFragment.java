package se.mau.aj9191.assignment_1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

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

        });
    }
}
