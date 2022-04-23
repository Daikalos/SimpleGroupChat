package se.mau.aj9191.assignment_1;

public class User
{
    private final String name;
    public Coordinate coordinate;

    public User(String name, Coordinate coordinate)
    {
        this.name = name;
        this.coordinate = coordinate;
    }

    public String getId()
    {
        return name;
    }
}
