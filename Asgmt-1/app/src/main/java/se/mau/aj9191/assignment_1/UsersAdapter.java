package se.mau.aj9191.assignment_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

public class UsersAdapter extends RecyclerView.Adapter<UsersHolder>
{
    private ArrayList<String> users = new ArrayList<>();

    public UsersAdapter(String groupName, MainViewModel viewModel, LifecycleOwner lifecycleOwner)
    {
        viewModel.getMembersLiveData().observe(lifecycleOwner, members ->
        {
            if (!members.first.equals(groupName))
                return;

            users = new ArrayList<>(Arrays.asList(members.second));
            notifyDataSetChanged();

            viewModel.getMembersLiveData().removeObservers(lifecycleOwner);
        });
    }

    @Override
    public UsersHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_row, viewGroup, false);
        return new UsersHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersHolder holder, int position)
    {
        holder.getUsernameView().setText(users.get(position));
    }

    @Override
    public int getItemCount()
    {
        return users.size();
    }
}
