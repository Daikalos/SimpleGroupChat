package se.mau.aj9191.assignment_1;

import android.os.Message;

import java.io.Serializable;
import java.util.ArrayList;

public class Group implements Serializable
{
    private final String id, name;
    public boolean viewable = true;

    private ArrayList<TextMessage> messages = new ArrayList<>();

    public Group(String id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public String getId()
    {
        return id;
    }
    public String getName()
    {
        return name;
    }

    public ArrayList<TextMessage> getMessages()
    {
        return messages;
    }
    public void addMessage(TextMessage message)
    {
        messages.add(message);
    }
    public TextMessage getMessage(int pos)
    {
        return messages.get(pos);
    }
    public int getMessagesSize()
    {
        return messages.size();
    }
}
