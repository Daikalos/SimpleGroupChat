package se.mau.aj9191.assignment_1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GroupAdapter extends RecyclerView.Adapter<GroupHolder>
{
    private final Context context;
    private final MainViewModel viewModel;

    public GroupAdapter(Context context, MainViewModel viewModel)
    {
        this.context = context;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public GroupHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.group_row, viewGroup, false);
        return new GroupHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupHolder groupHolder, int position)
    {

    }

    @Override
    public int getItemCount()
    {
        return 0;
    }
}
