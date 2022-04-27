package se.mau.aj9191.assignment_1;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class MainViewModel extends ViewModel
{
    // --- DATA ---

    private final ArrayList<Group> joinedGroups = new ArrayList<>();

    private final ArrayList<Group> groups = new ArrayList<>();
    private final ArrayList<String> members = new ArrayList<>();

    private LatLng location = new LatLng(Double.NaN, Double.NaN);

    public int getGroupsSize()
    {
        return joinedGroups.size();
    }
    public Group getGroup(int index)
    {
        return joinedGroups.get(index);
    }
    public Group getGroup(String groupName)
    {
        return joinedGroups.stream().filter(o -> groupName.equals(o.getName())).findFirst().orElse(null);
    }

    public ArrayList<Group> getAllGroups()
    {
        return groups;
    }
    public ArrayList<String> getAllMembers()
    {
        return members;
    }

    public LatLng getLocation()
    {
        return location;
    }

    // --- EVENTS ---

    private final SingleLiveEvent<Group> registerEvent = new SingleLiveEvent<>(); // id, group
    private final SingleLiveEvent<String> unregisterEvent = new SingleLiveEvent<>();

    private final SingleLiveEvent<Boolean> getMembersEvent = new SingleLiveEvent<>();
    private final SingleLiveEvent<Boolean> getGroupsEvent = new SingleLiveEvent<>();

    private final SingleLiveEvent<Location> locationEvent = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> locationsEvent = new SingleLiveEvent<>(); // group, locations

    private final SingleLiveEvent<Group> viewableEvent = new SingleLiveEvent<>();

    private final SingleLiveEvent<SendText> sentTextEvent = new SingleLiveEvent<>();
    private final SingleLiveEvent<SendImage> sentImageEvent = new SingleLiveEvent<>();

    private final SingleLiveEvent<TextMessage> textMessageEvent = new SingleLiveEvent<>();
    private final SingleLiveEvent<ImageMessage> imageMessageEvent = new SingleLiveEvent<>();

    public void postRegister(Group group)
    {
        joinedGroups.add(group);
        registerEvent.postValue(group);
    }
    public void postUnregister(String id)
    {
        joinedGroups.removeIf(group -> id.equals(group.getId()));
        unregisterEvent.postValue(id);
    }

    public void postMembers(Group members)
    {
        this.members.clear();
        this.members.addAll(members.getMembers());

        getMembersEvent.postValue(true);
    }
    public void postGroups(Group[] groups)
    {
        this.groups.clear();
        this.groups.addAll(Arrays.asList(groups));

        getGroupsEvent.postValue(true);
    }

    public void postLocation(Location location)
    {
        this.location = new LatLng(location.getLatitude(), location.getLongitude());
        this.locationEvent.postValue(location);
    }
    public void postLocations(String groupName, Location[] locations)
    {
        Group group = getGroup(groupName);

        if (group != null)
        {
            for (int i = group.getMarkers().size() - 1; i >= 0; --i)
            {
                NormalMarker normalMarker = group.getMarkers().get(i);
                if (normalMarker.getType() == NormalMarker.NORMAL_MARKER)
                    group.removeMarker(i);
            }

            for (Location location : locations)
            {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                if (Double.isNaN(latitude) || Double.isNaN(longitude))
                    continue;

                NormalMarker normalMarker = new NormalMarker();
                normalMarker.latitude = latitude;
                normalMarker.longitude = longitude;
                normalMarker.title = groupName + ": " + location.getMember();
                normalMarker.snippet = location.getMember() + " last recorded location";
                normalMarker.visible = group.viewable;

                group.addMarker(normalMarker);
            }
        }

        this.locationsEvent.postValue(groupName);
    }

    public void postViewable(Group group)
    {
        viewableEvent.postValue(group);
    }

    public void postSentText(SendText sendText)
    {
        this.sentTextEvent.postValue(sendText);
    }
    public void postSentImage(SendImage sendImage)
    {
        this.sentImageEvent.postValue(sendImage);
    }

    public void postTextMessage(TextMessage textMessage)
    {
        Group group = joinedGroups.stream().filter(o -> textMessage.groupName.equals(o.getName())).findFirst().orElse(null);

        if (group != null)
        {
            group.addMessage(textMessage);
            this.textMessageEvent.postValue(textMessage);
        }
    }
    public void postImageMessage(ImageMessage imageMessage)
    {
        Group group = joinedGroups.stream().filter(o -> imageMessage.groupName.equals(o.getName())).findFirst().orElse(null);

        if (group != null)
        {
            ImageMarker imageMarker = new ImageMarker();
            imageMarker.latitude = imageMessage.latitude;
            imageMarker.longitude = imageMessage.longitude;
            imageMarker.title = group.getName() + ": " + imageMessage.username;
            imageMarker.anchorX = 0.5f;
            imageMarker.anchorY = 1.0f;
            imageMarker.imageMessage = imageMessage;

            group.addMarker(imageMarker);
            group.addMessage(imageMessage);

            this.imageMessageEvent.postValue(imageMessage);
        }
    }

    public LiveData<Group> getRegisterLiveData()
    {
        return registerEvent;
    }
    public LiveData<String> getUnregisterLiveData()
    {
        return unregisterEvent;
    }

    public LiveData<Boolean> getMembersLiveData()
    {
        return getMembersEvent;
    }
    public LiveData<Boolean> getGroupsLiveData()
    {
        return getGroupsEvent;
    }

    public LiveData<Location> getLocationLiveData()
    {
        return locationEvent;
    }
    public LiveData<String> getLocationsLiveData()
    {
        return locationsEvent;
    }

    public LiveData<Group> getViewableLiveData()
    {
        return viewableEvent;
    }

    public LiveData<SendText> getSentTextLiveData()
    {
        return sentTextEvent;
    }
    public LiveData<SendImage> getSentImageLiveData()
    {
        return sentImageEvent;
    }

    public LiveData<TextMessage> getTextMessageLiveData()
    {
        return textMessageEvent;
    }
    public LiveData<ImageMessage> getImageMessageLiveData()
    {
        return imageMessageEvent;
    }
}
