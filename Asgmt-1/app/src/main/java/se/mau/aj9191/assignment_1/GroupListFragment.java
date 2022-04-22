package se.mau.aj9191.assignment_1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GroupListFragment extends Fragment
{
    private Button btnBack;
    private Button btnNew;
    private RecyclerView rvGroups;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
    {
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);

        initializeComponents(view);
        registerListeners();

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

    private void registerListeners()
    {
        btnBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getParentFragmentManager().popBackStack();

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fcvMain, new MapsFragment());
                transaction.commit();
            }
        });

        btnNew.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.cancel();
                    }
                });

                EditText editText = new EditText(requireContext());

                AlertDialog dialog = builder.create();
                dialog.setTitle("Enter group name");
                dialog.setView(editText);

                dialog.show();
            }
        });
    }
}
