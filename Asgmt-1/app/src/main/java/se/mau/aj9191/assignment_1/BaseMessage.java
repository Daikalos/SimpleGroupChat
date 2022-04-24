package se.mau.aj9191.assignment_1;

enum MessageType
{
    Text,
    Image
}

class Message
{
    public String username;
    public String message;
}

class ImageMessage extends Message
{
    public Byte[] image;
    public Coordinates coordinates;
}
