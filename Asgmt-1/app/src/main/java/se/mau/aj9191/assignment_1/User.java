package se.mau.aj9191.assignment_1;

public class User
{
    private final String name, groupId;

    public User(String name, String groupId)
    {
        this.name = name;
        this.groupId = groupId;
    }

    public String getName()
    {
        return name;
    }
    public String getGroupId()
    {
        return groupId;
    }
}
