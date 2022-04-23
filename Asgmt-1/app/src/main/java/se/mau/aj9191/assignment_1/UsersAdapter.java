package se.mau.aj9191.assignment_1;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

public class UsersAdapter extends RecyclerView.Adapter<UsersHolder>
{
    private MainViewModel viewModel;

    public UsersAdapter(MainViewModel viewModel, LifecycleOwner lifecycleOwner)
    {
        this.viewModel = viewModel;
    }

    @Override
    public UsersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull UsersHolder holder, int position)
    {

    }

    @Override
    public int getItemCount()
    {
        return 0;
    }
}
