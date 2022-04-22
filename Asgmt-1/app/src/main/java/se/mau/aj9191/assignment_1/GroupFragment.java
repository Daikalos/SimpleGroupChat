package se.mau.aj9191.assignment_1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

public class GroupFragment extends Fragment
{
    private Button btnBack;
    private Button btnChat;
    private RecyclerView rvUsers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
    {
        View view = inflater.inflate(R.layout.fragment_group, container, false);

        initializeComponents(view);
        registerListeners();

        return view;
    }

    private void initializeComponents(View view)
    {
        btnBack = view.findViewById(R.id.btnBack);
        btnChat = view.findViewById(R.id.btnChat);
        rvUsers = view.findViewById(R.id.rvUsers);
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
                transaction.replace(R.id.fcvMain, new GroupListFragment());
                transaction.commit();
            }
        });
    }
}
