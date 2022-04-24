package se.mau.aj9191.assignment_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class ChatAdapter extends RecyclerView.Adapter
{
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.group_row, viewGroup, false);
        return new TextChatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {

    }

    @Override
    public int getItemCount()
    {
        return 0;
    }

    public static class TextChatHolder extends RecyclerView.ViewHolder
    {
        private final TextView tvTextChat;

        public TextChatHolder(@NonNull View itemView)
        {
            super(itemView);

            tvTextChat = itemView.findViewById(R.id.tvTextChat);
        }

        public TextView getTextChatView()
        {
            return tvTextChat;
        }
    }
    public static class ImageChatHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private final TextView tvGroupName;

        public ImageChatHolder(@NonNull View itemView)
        {
            super(itemView);

            tvGroupName = itemView.findViewById(R.id.tvGroupName);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {

        }

        public TextView getGroupNameView()
        {
            return tvGroupName;
        }
    }
}
