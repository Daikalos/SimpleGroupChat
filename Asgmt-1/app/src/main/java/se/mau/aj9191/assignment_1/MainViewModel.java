package se.mau.aj9191.assignment_1;

import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Predicate;

public class MainViewModel extends ViewModel
{
    private final MutableLiveData<Group> register = new MutableLiveData<>(); // id, group
    private final MutableLiveData<String> unregister = new MutableLiveData<>();

    private final MutableLiveData<String[]> groups = new MutableLiveData<>();
    private final MutableLiveData<Pair<String, String[]>> members = new MutableLiveData<>(); // group, members

    private final MutableLiveData<Location> location = new MutableLiveData<>();
    private final MutableLiveData<Pair<String, Location[]>> locations = new MutableLiveData<>(); // group, locations

    private final MutableLiveData<Group> viewable = new MutableLiveData<>();

    private final MutableLiveData<SentText> sentText = new MutableLiveData<>();
    private final MutableLiveData<SentImage> sentImage = new MutableLiveData<>();

    private final MutableLiveData<TextMessage> textMessage = new MutableLiveData<>();
    private final MutableLiveData<ImageMessage> imageMessage = new MutableLiveData<>();

    private final ArrayList<Group> joinedGroups = new ArrayList<>();

    public int getGroupsSize()
    {
        return joinedGroups.size();
    }
    public Group getGroup(int index)
    {
        return joinedGroups.get(index);
    }
    public Group joinedGroup(String groupName)
    {
        if (groupName == null || groupName.isEmpty())
            return null;

        return joinedGroups.stream().filter(o -> groupName.equals(o.getName())).findFirst().orElse(null);
    }

    public void postRegister(Group group)
    {
        joinedGroups.add(group);
        register.postValue(group);
    }
    public void postUnregister(String id)
    {
        joinedGroups.removeIf(group -> id.equals(group.getId()));
        unregister.postValue(id);
    }

    public void postMembers(Pair<String, String[]> members)
    {
        this.members.postValue(members);
    }
    public void postGroups(String[] groups)
    {
        this.groups.postValue(groups);
    }

    public void postLocation(Location location)
    {
        this.location.postValue(location);
    }
    public void postLocations(Pair<String, Location[]> locations)
    {
        this.locations.postValue(locations);
    }

    public void postViewable(Group group)
    {
        viewable.postValue(group);
    }

    public void postSentText(SentText sentText)
    {
        this.sentText.postValue(sentText);
    }
    public void postSentImage(SentImage sentImage)
    {
        this.sentImage.postValue(sentImage);
    }

    public void postTextMessage(TextMessage textMessage)
    {
        this.textMessage.postValue(textMessage);
    }
    public void postImageMessage(ImageMessage imageMessage)
    {
        this.imageMessage.postValue(imageMessage);
    }

    public LiveData<Group> getRegisterLiveData()
    {
        return register;
    }
    public LiveData<String> getUnregisterLiveData()
    {
        return unregister;
    }

    public LiveData<String[]> getGroupsLiveData()
    {
        return groups;
    }
    public LiveData<Pair<String, String[]>> getMembersLiveData()
    {
        return members;
    }

    public LiveData<Location> getLocationLiveData()
    {
        return location;
    }
    public LiveData<Pair<String, Location[]>> getLocationsLiveData()
    {
        return locations;
    }

    public LiveData<Group> getViewableLiveData()
    {
        return viewable;
    }

    public LiveData<SentText> getSentTextLiveData()
    {
        return sentText;
    }
    public LiveData<SentImage> getSentImage()
    {
        return sentImage;
    }

    public LiveData<TextMessage> getTextMessageLiveData()
    {
        return textMessage;
    }
    public LiveData<ImageMessage> getImageMessageLiveData()
    {
        return imageMessage;
    }
}
