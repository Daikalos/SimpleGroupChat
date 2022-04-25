package se.mau.aj9191.assignment_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupsHolder>
{
    private final ArrayList<Group> groups;

    public GroupsAdapter(ArrayList<Group> groups)
    {
        this.groups = groups;
    }

    @NonNull
    @Override
    public GroupsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.group_row, viewGroup, false);
        return new GroupsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsHolder groupHolder, int position)
    {
        groupHolder.getGroupNameView().setText(groups.get(position).getName());
    }

    @Override
    public int getItemCount()
    {
        return groups.size();
    }

    public static class GroupsHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private final TextView tvGroupName;

        public GroupsHolder(@NonNull View itemView)
        {
            super(itemView);

            tvGroupName = itemView.findViewById(R.id.tvGroupName);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            ((AppCompatActivity)view.getContext()).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fcvMain, new UsersFragment(tvGroupName.getText().toString()))
                    .addToBackStack(null).commit();
        }

        public TextView getGroupNameView()
        {
            return tvGroupName;
        }
    }
}
