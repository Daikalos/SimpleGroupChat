package se.mau.aj9191.assignment_1;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

public class GroupsHolder extends RecyclerView.ViewHolder implements View.OnClickListener
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
        FragmentTransaction transaction = ((AppCompatActivity)view.getContext()).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fcvMain, new UsersFragment(tvGroupName.getText().toString()));
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public TextView getGroupNameView()
    {
        return tvGroupName;
    }
}
