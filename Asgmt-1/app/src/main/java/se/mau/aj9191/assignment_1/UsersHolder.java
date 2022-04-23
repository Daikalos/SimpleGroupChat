package se.mau.aj9191.assignment_1;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UsersHolder extends RecyclerView.ViewHolder
{
    private final TextView tvUsername;

    public UsersHolder(@NonNull View itemView)
    {
        super(itemView);
        tvUsername = itemView.findViewById(R.id.tvUsername);
    }

    public TextView getUsernameView()
    {
        return tvUsername;
    }
}
