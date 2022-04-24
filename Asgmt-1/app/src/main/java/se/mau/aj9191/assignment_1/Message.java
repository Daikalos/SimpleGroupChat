package se.mau.aj9191.assignment_1;

class SendText
{
    public String id;
    public String message;

    public SendText(String id, String message)
    {
        this.id = id;
        this.message = message;
    }
}

class SendImage
{
    public String imageid;
    public String port;

    public SendImage(String imageid, String port)
    {
        this.imageid = imageid;
        this.port = port;
    }
}

class TextMessage
{
    public static final int TEXT_TYPE = 1;
    public static final int IMAGE_TYPE = 2;

    public String groupName;
    public String username;
    public String message;

    protected int type;

    public TextMessage(String groupName, String username, String message)
    {
        this.groupName = groupName;
        this.username = username;
        this.message = message;

        type = TEXT_TYPE;
    }

    public int getType()
    {
        return type;
    }
}

class ImageMessage extends TextMessage
{
    public double longitude;
    public double latitude;
    public String imageid;
    public String port;

    public ImageMessage(String groupName, String username, String message, double longitude, double latitude, String imageid, String port)
    {
        super(groupName, username, message);

        this.longitude = longitude;
        this.latitude = latitude;
        this.imageid = imageid;
        this.port = port;

        type = IMAGE_TYPE;
    }
}
