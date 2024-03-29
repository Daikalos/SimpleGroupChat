package se.mau.aj9191.assignment_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersHolder>
{
    private final ArrayList<String> users;

    public UsersAdapter(ArrayList<String> users)
    {
        this.users = users;
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
        if (position >= 0 && position < users.size())
            holder.getUsernameView().setText(users.get(position));
    }

    @Override
    public int getItemCount()
    {
        return users.size();
    }


    public static class UsersHolder extends RecyclerView.ViewHolder
    {
        private final TextView tvUsername;

        public UsersHolder(@NonNull View itemView)
        {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvTextChat);
        }

        public TextView getUsernameView()
        {
            return tvUsername;
        }
    }
}
