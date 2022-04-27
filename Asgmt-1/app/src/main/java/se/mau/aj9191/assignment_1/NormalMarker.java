package se.mau.aj9191.assignment_1;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

class NormalMarker implements Parcelable
{
    public static final int NORMAL_MARKER = 1;
    public static final int IMAGE_MARKER = 2;

    public double latitude = Double.NaN;
    public double longitude = Double.NaN;
    public String title = null;
    public String snippet = null;
    public float anchorX = Float.NaN;
    public float anchorY = Float.NaN;
    public boolean visible = true;

    protected int type = NORMAL_MARKER;

    public NormalMarker()
    {

    }

    public int getType()
    {
        return type;
    }

    public MarkerOptions getMarkerOptions()
    {
        MarkerOptions markerOptions = new MarkerOptions();

        if (!Double.isNaN(latitude) && !Double.isNaN(longitude))
            markerOptions.position(new LatLng(latitude, longitude));
        if (title != null)
            markerOptions.title(title);
        if (snippet != null)
            markerOptions.snippet(snippet);
        if (!Float.isNaN(anchorX) && !Float.isNaN(anchorY))
            markerOptions.anchor(anchorX, anchorY);

        markerOptions.visible(visible);

        return markerOptions;
    }

    protected NormalMarker(Parcel in)
    {
        latitude = in.readDouble();
        longitude = in.readDouble();
        title = in.readString();
        snippet = in.readString();
        anchorX = in.readFloat();
        anchorY = in.readFloat();
        visible = in.readInt() != 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeString(title);
        parcel.writeString(snippet);
        parcel.writeFloat(anchorX);
        parcel.writeFloat(anchorY);
        parcel.writeInt(visible ? 1 : 0);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<NormalMarker> CREATOR = new Creator<NormalMarker>()
    {
        @Override
        public NormalMarker createFromParcel(Parcel in)
        {
            return new NormalMarker(in);
        }

        @Override
        public NormalMarker[] newArray(int size)
        {
            return new NormalMarker[size];
        }
    };
}

class ImageMarker extends NormalMarker implements Parcelable
{
    public ImageMessage imageMessage = null;

    public ImageMarker()
    {
        type = NormalMarker.IMAGE_MARKER;
    }

    public MarkerOptions getMarkerOptions()
    {
        MarkerOptions markerOptions = super.getMarkerOptions();

        if (imageMessage.bitmap != null)
        {
            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(imageMessage.bitmap, 64, 64, 0);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(thumbnail));
        }

        return markerOptions;
    }

    protected ImageMarker(Parcel in)
    {
        super(in);
        imageMessage = in.readParcelable(ImageMessage.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags)
    {
        super.writeToParcel(parcel, flags);
        parcel.writeParcelable(imageMessage, flags);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<ImageMarker> CREATOR = new Creator<ImageMarker>()
    {
        @Override
        public ImageMarker createFromParcel(Parcel in)
        {
            return new ImageMarker(in);
        }

        @Override
        public ImageMarker[] newArray(int size)
        {
            return new ImageMarker[size];
        }
    };
}
