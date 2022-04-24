package se.mau.aj9191.assignment_1;

import java.util.ArrayList;

public class Group
{
    private final String id, name;
    public boolean viewable = true;

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
}
