package se.mau.aj9191.assignment_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter
{
    private ArrayList<TextMessage> messages;

    public ChatAdapter(ArrayList<TextMessage> messages)
    {
        this.messages = messages;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType)
    {
        if (viewType == TextMessage.TEXT_TYPE)
        {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.textchat_row, viewGroup, false);
            return new TextChatHolder(view);
        }
        else if (viewType == TextMessage.IMAGE_TYPE)
        {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.imagechat_row, viewGroup, false);
            return new ImageChatHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        TextMessage message = messages.get(position);

        if (holder.getItemViewType() == TextMessage.TEXT_TYPE)
            ((TextChatHolder)holder).bind(message);
        else if (holder.getItemViewType() == TextMessage.IMAGE_TYPE)
            ((ImageChatHolder)holder).bind((ImageMessage)message);
    }

    @Override
    public int getItemViewType(int position)
    {
        return messages.get(position).getType();
    }

    @Override
    public int getItemCount()
    {
        return messages.size();
    }

    public static class TextChatHolder extends RecyclerView.ViewHolder
    {
        private final TextView tvTextChat;

        public TextChatHolder(@NonNull View itemView)
        {
            super(itemView);

            tvTextChat = itemView.findViewById(R.id.tvTextChat);
        }

        public void bind(TextMessage textMessage)
        {
            tvTextChat.setText(String.format("%s: %s", textMessage.username, textMessage.message));
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

        public void bind(ImageMessage imageMessage)
        {

        }
    }
}
