package se.mau.aj9191.assignment_1;

import android.os.Build;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.util.ArrayList;

public class Group implements Parcelable
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

    public Group(Parcel in)
    {
        id = in.readString();
        name = in.readString();
        viewable = in.readInt() != 0;
        messages = in.readArrayList(null);
        members = in.readArrayList(null);
    }
    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeInt(viewable ? 1 : 0);
        parcel.writeList(messages);
        parcel.writeList(members);
    }
    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<Group> CREATOR = new Creator<Group>()
    {
        @Override
        public Group createFromParcel(Parcel in)
        {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size)
        {
            return new Group[size];
        }
    };
}
