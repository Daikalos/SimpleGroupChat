package se.mau.aj9191.assignment_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewableAdapter extends RecyclerView.Adapter<ViewableAdapter.ViewableHolder>
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

    public static class ViewableHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private final TextView tvGroupName;
        private final CheckBox cbViewable;

        private final MainViewModel viewModel;

        public ViewableHolder(@NonNull View itemView, MainViewModel viewModel)
        {
            super(itemView);

            this.viewModel = viewModel;

            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            cbViewable = itemView.findViewById(R.id.cbViewable);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view)
        {
            Group group = viewModel.getGroup(getAdapterPosition());
            if (group != null)
            {
                group.viewable = !group.viewable;
                cbViewable.setChecked(group.viewable);

                viewModel.postViewable(group);
            }
        }

        public TextView getGroupNameView()
        {
            return tvGroupName;
        }
        public CheckBox getViewableView()
        {
            return cbViewable;
        }
    }
}
