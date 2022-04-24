package se.mau.aj9191.assignment_1;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewableHolder extends RecyclerView.ViewHolder implements View.OnClickListener
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

        group.viewable = !group.viewable;
        cbViewable.setChecked(group.viewable);

        viewModel.updateViewable(group);
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
