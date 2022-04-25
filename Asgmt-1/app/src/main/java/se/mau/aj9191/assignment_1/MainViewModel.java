package se.mau.aj9191.assignment_1;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class MainViewModel extends ViewModel
{
    private final SingleLiveEvent<Group> register = new SingleLiveEvent<>(); // id, group
    private final SingleLiveEvent<String> unregister = new SingleLiveEvent<>();

    private final SingleLiveEvent<String[]> groups = new SingleLiveEvent<>();
    private final SingleLiveEvent<Pair<String, String[]>> members = new SingleLiveEvent<>(); // group, members

    private final SingleLiveEvent<Location> location = new SingleLiveEvent<>();
    private final SingleLiveEvent<Pair<String, Location[]>> locations = new SingleLiveEvent<>(); // group, locations

    private final SingleLiveEvent<Group> viewable = new SingleLiveEvent<>();

    private final SingleLiveEvent<SendText> sentText = new SingleLiveEvent<>();
    private final SingleLiveEvent<SendImage> sentImage = new SingleLiveEvent<>();

    private final SingleLiveEvent<TextMessage> textMessage = new SingleLiveEvent<>();
    private final SingleLiveEvent<ImageMessage> imageMessage = new SingleLiveEvent<>();

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

    public void postSentText(SendText sendText)
    {
        this.sentText.postValue(sendText);
    }
    public void postSentImage(SendImage sendImage)
    {
        this.sentImage.postValue(sendImage);
    }

    public void postTextMessage(TextMessage textMessage)
    {
        Group group = joinedGroups.stream().filter(o -> textMessage.groupName.equals(o.getName())).findFirst().orElse(null);

        if (group != null)
        {
            group.addMessage(textMessage);
            this.textMessage.postValue(textMessage);
        }
    }
    public void postImageMessage(ImageMessage imageMessage)
    {
        Group group = joinedGroups.stream().filter(o -> imageMessage.groupName.equals(o.getName())).findFirst().orElse(null);

        if (group != null)
        {
            group.addMessage(imageMessage);
            this.imageMessage.postValue(imageMessage);
        }
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

    public LiveData<SendText> getSentTextLiveData()
    {
        return sentText;
    }
    public LiveData<SendImage> getSentImage()
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
