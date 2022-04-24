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
    private final MutableLiveData<Pair<String, String>> register = new MutableLiveData<>();
    private final MutableLiveData<String> unregister = new MutableLiveData<>();

    private final MutableLiveData<String[]> groups = new MutableLiveData<>();
    private final MutableLiveData<Pair<String, String[]>> members = new MutableLiveData<>(); // group, members

    private final MutableLiveData<Pair<String, Location[]>> locations = new MutableLiveData<>(); // group, locations
    private final MutableLiveData<Group> viewable = new MutableLiveData<>();

    private final ArrayList<Group> joinedGroups = new ArrayList<>();

    public void register(String id, String groupName)
    {
        joinedGroups.add(new Group(id, groupName));
        register.postValue(new Pair<>(id, groupName));
    }
    public void unregister(String id)
    {
        joinedGroups.removeIf(group -> group.getId().equals(id));
        unregister.postValue(id);
    }

    public void showMembers(Pair<String, String[]> members)
    {
        this.members.postValue(members);
    }
    public void showGroups(String[] groups)
    {
        this.groups.postValue(groups);
    }
    public void updateLocations(Pair<String, Location[]> locations)
    {
        this.locations.postValue(locations);
    }
    public void updateViewable(Group group)
    {
        viewable.postValue(group);
    }

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

    public LiveData<Pair<String, String>> getRegisterLiveData()
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

    public LiveData<Pair<String, Location[]>> getLocationsLiveData()
    {
        return locations;
    }
    public LiveData<Group> getViewableLiveData()
    {
        return viewable;
    }
}
