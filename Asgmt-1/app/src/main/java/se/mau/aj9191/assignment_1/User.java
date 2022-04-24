package se.mau.aj9191.assignment_1;

public class User
{
    private final String name;
    public Coordinates coordinate;

    public User(String name, Coordinates coordinate)
    {
        this.name = name;
        this.coordinate = coordinate;
    }

    public String getId()
    {
        return name;
    }
}
