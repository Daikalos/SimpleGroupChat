package se.mau.aj9191.assignment_1;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

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

class TextMessage implements Parcelable
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

    public TextMessage(Parcel in)
    {
        groupName = in.readString();
        username = in.readString();
        message = in.readString();
        type = in.readInt();
    }
    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(groupName);
        parcel.writeString(username);
        parcel.writeString(message);
        parcel.writeInt(type);
    }
    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<TextMessage> CREATOR = new Creator<TextMessage>()
    {
        @Override
        public TextMessage createFromParcel(Parcel in)
        {
            return new TextMessage(in);
        }

        @Override
        public TextMessage[] newArray(int size)
        {
            return new TextMessage[size];
        }
    };
}

class ImageMessage extends TextMessage implements Parcelable
{
    public double longitude;
    public double latitude;
    public String imageid;
    public String port;

    public Bitmap bitmap;

    public ImageMessage(String groupName, String username, String message, double longitude, double latitude, String imageid, String port)
    {
        super(groupName, username, message);

        this.longitude = longitude;
        this.latitude = latitude;
        this.imageid = imageid;
        this.port = port;

        type = IMAGE_TYPE;
    }

    public ImageMessage(Parcel in)
    {
        super(in);

        longitude = in.readDouble();
        latitude = in.readDouble();
        imageid = in.readString();
        port = in.readString();
        bitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeString(imageid);
        dest.writeString(port);
        dest.writeParcelable(bitmap, flags);
    }
    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<ImageMessage> CREATOR = new Creator<ImageMessage>()
    {
        @Override
        public ImageMessage createFromParcel(Parcel in)
        {
            return new ImageMessage(in);
        }

        @Override
        public ImageMessage[] newArray(int size)
        {
            return new ImageMessage[size];
        }
    };
}
