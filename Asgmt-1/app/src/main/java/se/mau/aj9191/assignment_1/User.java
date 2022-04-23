package se.mau.aj9191.assignment_1;

public class User
{
    private final String id;
    public double longitude, latitude;

    public User(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }
}
