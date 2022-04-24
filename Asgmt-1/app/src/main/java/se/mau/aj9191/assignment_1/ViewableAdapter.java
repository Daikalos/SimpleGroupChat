package se.mau.aj9191.assignment_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewableAdapter extends RecyclerView.Adapter<ViewableHolder>
{
    private final MainViewModel viewModel;

    public ViewableAdapter(MainViewModel viewModel)
    {
        this.viewModel = viewModel;
    }

    @Override
    public ViewableHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.viewable_row, viewGroup, false);
        return new ViewableHolder(view, viewModel);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewableHolder holder, int position)
    {
        Group group = viewModel.getGroup(position);

        holder.getGroupNameView().setText(group.getName());
        holder.getViewableView().setChecked(group.viewable);
    }

    @Override
    public int getItemCount()
    {
        return viewModel.getGroupsSize();
    }
}
