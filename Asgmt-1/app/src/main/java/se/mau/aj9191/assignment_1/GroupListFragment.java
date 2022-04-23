package se.mau.aj9191.assignment_1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GroupListFragment extends Fragment
{
    private ImageButton btnBack;
    private Button btnNew;

    private RecyclerView rvGroups;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
    {
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);

        initializeComponents(view);
        registerListeners(view.getContext());

        return view;
    }

    private void initializeComponents(View view)
    {
        btnBack = view.findViewById(R.id.btnBack);
        btnNew = view.findViewById(R.id.btnNew);

        final MainViewModel viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        final RecyclerView rvGroups = view.findViewById(R.id.rvGroups);
        rvGroups.setLayoutManager(new LinearLayoutManager(getContext()));
        rvGroups.setAdapter(new GroupAdapter(getContext(), viewModel));
    }

    private void registerListeners(Context context)
    {
        btnBack.setOnClickListener(view ->
        {
            getParentFragmentManager().popBackStack();

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fcvMain, new MapsFragment());
            transaction.commit();
        });

        btnNew.setOnClickListener(view ->
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

            builder.setPositiveButton("OK", (dialogInterface, i) ->
            {

            });
            builder.setNegativeButton("Cancel", null);

            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(32, 32, 32, 32);

            EditText edGroup = new EditText(getContext());
            edGroup.setHint("enter group name");

            EditText edUsername = new EditText(getContext());
            edUsername.setHint("enter user name");

            layout.addView(edGroup);
            layout.addView(edUsername);

            AlertDialog dialog = builder.create();
            dialog.setTitle("Create group");
            dialog.setView(layout);

            dialog.show();
        });
    }
}
