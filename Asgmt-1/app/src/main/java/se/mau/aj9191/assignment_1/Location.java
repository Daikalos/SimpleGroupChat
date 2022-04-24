package se.mau.aj9191.assignment_1;

public class Location
{
    private final String member;
    private final Coordinates coordinates;

    public Location(String member, Coordinates coordinates)
    {
        this.member = member;
        this.coordinates = coordinates;
    }

    public String getMember()
    {
        return member;
    }
    public Coordinates getCoordinates()
    {
        return coordinates;
    }
}
