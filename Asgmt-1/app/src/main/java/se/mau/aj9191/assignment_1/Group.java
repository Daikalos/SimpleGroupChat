package se.mau.aj9191.assignment_1;

import java.util.ArrayList;

public class Group
{
    private final String id, name;
    private final ArrayList<User> members;

    public Group(String id, String name, ArrayList<User> members)
    {
        this.id = id;
        this.name = name;
        this.members = members;
    }

    public String getId()
    {
        return id;
    }
    public String getName()
    {
        return name;
    }
    public ArrayList<User> getMembers()
    {
        return members;
    }
}
