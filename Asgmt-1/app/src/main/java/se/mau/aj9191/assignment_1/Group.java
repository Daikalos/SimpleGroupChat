package se.mau.aj9191.assignment_1;

import android.os.Message;

import java.io.Serializable;
import java.util.ArrayList;

public class Group implements Serializable
{
    public String id, name;
    public boolean viewable = true;

    private ArrayList<TextMessage> messages = new ArrayList<>();
    private ArrayList<String> members = new ArrayList<>();

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

    public ArrayList<String> getMembers()
    {
        return members;
    }
    public void addMember(String member)
    {
        members.add(member);
    }
    public void setMembers(ArrayList<String> members)
    {
        members.clear();
        members.addAll(members);
    }
    public int getMembersSize()
    {
        return members.size();
    }
}
