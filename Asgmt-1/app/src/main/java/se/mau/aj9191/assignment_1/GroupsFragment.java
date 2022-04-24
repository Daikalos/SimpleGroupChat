package se.mau.aj9191.assignment_1;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GroupsFragment extends Fragment
{
    private MainViewModel viewModel;

    private ImageButton btnBack;
    private Button btnNew;
    private RecyclerView rvGroups;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_groups, container, false);

        initializeComponents(view);
        registerListeners();

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();;
        Controller.sendMessage(JsonHelper.sendGetGroups());
    }

    private void initializeComponents(View view)
    {
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        btnBack = view.findViewById(R.id.btnBack);
        btnNew = view.findViewById(R.id.btnNew);
        rvGroups = view.findViewById(R.id.rvGroups);

        rvGroups.setLayoutManager(new LinearLayoutManager(getContext()));
        rvGroups.setAdapter(new GroupsAdapter(viewModel, getViewLifecycleOwner()));
        rvGroups.addItemDecoration(new DividerItemDecoration(rvGroups.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void registerListeners()
    {
        btnBack.setOnClickListener(view ->
        {
            getParentFragmentManager().popBackStack();
        });

        btnNew.setOnClickListener(view ->
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

            LinearLayout layout = new LinearLayout(requireContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(32, 32, 32, 32);

            EditText edGroup = new EditText(getContext());
            edGroup.setHint("enter group name");

            EditText edUsername = new EditText(getContext());
            edUsername.setHint("enter user name");

            builder.setPositiveButton("OK", (dialogInterface, i) ->
            {
                String groupName = edGroup.getText().toString();
                String username = edUsername.getText().toString();

                if (groupName.isEmpty() || username.isEmpty())
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

            layout.addView(edGroup);
            layout.addView(edUsername);

            AlertDialog dialog = builder.create();
            dialog.setTitle("Create group");
            dialog.setView(layout);

            dialog.show();
        });
    }
}
