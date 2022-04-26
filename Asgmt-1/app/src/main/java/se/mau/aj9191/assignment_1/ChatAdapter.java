package se.mau.aj9191.assignment_1;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatAdapter extends RecyclerView.Adapter
{
    private ArrayList<TextMessage> messages;
    private Context context;

    public ChatAdapter(Context contxt, ArrayList<TextMessage> messages)
    {
        this.context = contxt;
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
            return new ImageChatHolder(view, context);
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
        private final TextView tvUsername;
        private ImageView ivImageChat;

        private Context context;

        private ImageMessage imageMessage;
        private boolean imageLoaded = false;

        public ImageChatHolder(@NonNull View itemView, Context context)
        {
            super(itemView);
            this.context = context;

            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivImageChat = itemView.findViewById(R.id.ivImageChat);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            if (!imageLoaded)
                showDescription();
            else
                loadImage();
        }

        public void bind(ImageMessage imageMessage)
        {
            this.imageMessage = imageMessage;
            tvUsername.setText(imageMessage.username + ":");
        }

        private void showDescription()
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(32, 32, 32, 32);

            TextView tvMessage = new TextView(context);
            TextView tvLongitude = new TextView(context);
            TextView tvLatitude = new TextView(context);

            tvMessage.setGravity(Gravity.CENTER);
            tvLongitude.setGravity(Gravity.CENTER);
            tvLatitude.setGravity(Gravity.CENTER);

            tvMessage.setPadding(0, 0, 0, 16);

            tvMessage.setText(imageMessage.message);
            tvLongitude.setText("Lng - " + imageMessage.longitude);
            tvLatitude.setText("Lat - " + imageMessage.latitude);

            builder.setPositiveButton("OK", null);

            layout.addView(tvMessage);
            layout.addView(tvLongitude);
            layout.addView(tvLatitude);

            AlertDialog dialog = builder.create();
            dialog.setView(layout);

            dialog.show();
        }
        private void loadImage()
        {
            Executors.newSingleThreadExecutor().execute(() ->
            {
                try
                {
                    Socket socket = new Socket(InetAddress.getByName(NetworkService.IP), Integer.parseInt(imageMessage.port));
                    ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                    output.flush();
                    output.writeUTF(imageMessage.imageid);
                    output.flush();
                    byte[] downloadArray = (byte[])input.readObject();
                    socket.close();

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inMutable = true;

                    Bitmap bitmap = BitmapFactory.decodeByteArray(downloadArray, 0, downloadArray.length, options);

                    ((MainActivity)context).runOnUiThread(() ->
                    {
                        ivImageChat.setImageBitmap(bitmap);
                    });

                    imageLoaded = true;
                }
                catch (IOException | ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
            });
        }
    }
}
