package se.mau.aj9191.assignment_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsHolder>
{
    private ArrayList<String> groups = new ArrayList<>();

    public GroupsAdapter(MainViewModel viewModel, LifecycleOwner lifecycleOwner)
    {
        viewModel.getGroupsLiveData().observe(lifecycleOwner, groups ->
        {
            this.groups = new ArrayList<>(Arrays.asList(groups));
            notifyDataSetChanged();

            viewModel.getGroupsLiveData().removeObservers(lifecycleOwner);
        });

        viewModel.getRegisterLiveData().observe(lifecycleOwner, name ->
        {
            for (String groupName : groups)
            {
                if (name.equals(groupName))
                    return;
            }

            groups.add(name);
            notifyDataSetChanged();
        });
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
        groupHolder.getGroupNameView().setText(groups.get(position));
    }

    @Override
    public int getItemCount()
    {
        return groups.size();
    }
}
