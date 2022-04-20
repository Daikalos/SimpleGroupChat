package se.mau.aj9191.assignment_1;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

public class GroupHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    private final Context context;

    private final TextView tvGroupName;

    public GroupHolder(@NonNull View itemView, Context context)
    {
        super(itemView);

        this.context = context;
        tvGroupName = itemView.findViewById(R.id.tvGroupName);
    }

    @Override
    public void onClick(View view)
    {
        FragmentTransaction transaction = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fcvMain, new GroupFragment());
        transaction.commit();
    }

    public TextView getGroupName()
    {
        return tvGroupName;
    }
}
