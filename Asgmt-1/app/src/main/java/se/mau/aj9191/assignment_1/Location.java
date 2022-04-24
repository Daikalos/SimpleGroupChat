package se.mau.aj9191.assignment_1;

public class Location
{
    private final String member;
    private final double longitude, latitude;

    public Location(String member, double longitude, double latitude)
    {
        this.member = member;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getMember()
    {
        return member;
    }
    public double getLongitude()
    {
        return longitude;
    }
    public double getLatitude() { return latitude; }
}
