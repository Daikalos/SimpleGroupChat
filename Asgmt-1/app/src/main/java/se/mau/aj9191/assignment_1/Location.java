package se.mau.aj9191.assignment_1;

public class Location
{
    private final String member;
    private final Coordinate coordinates;

    public Location(String member, Coordinate coordinates)
    {
        this.member = member;
        this.coordinates = coordinates;
    }

    public String getMember()
    {
        return member;
    }
    public Coordinate getCoordinates()
    {
        return coordinates;
    }
}
