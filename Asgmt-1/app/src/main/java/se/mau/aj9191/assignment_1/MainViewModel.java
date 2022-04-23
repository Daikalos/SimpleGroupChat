package se.mau.aj9191.assignment_1;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MainViewModel extends ViewModel
{
    private final MutableLiveData<String> register = new MutableLiveData<>();
    private final MutableLiveData<String> unregister = new MutableLiveData<>();

    private final MutableLiveData<String[]> groups = new MutableLiveData<>();
    private final MutableLiveData<String[]> members = new MutableLiveData<>();

    private final MutableLiveData<Pair<String, Location[]>> locations = new MutableLiveData<>(); // group, locations

    private final HashMap<String, String> enteredGroups = new HashMap<>(); // id, group name

    public void register(String groupName, String id)
    {
        enteredGroups.put(id, groupName);
        register.postValue(groupName);
    }
    public void unregister(String id)
    {
        enteredGroups.remove(id);
        unregister.postValue(id);
    }

    public void showMembers(String[] members)
    {
        this.members.postValue(members);
    }
    public void showGroups(String[] groups)
    {
        this.groups.postValue(groups);
    }
    public void setLocation(String id, double longitude, double latitude)
    {

    }
    public void updateLocations(Pair<String, Location[]> locations)
    {
        this.locations.postValue(locations);
    }

    public boolean enteredGroup(String groupName)
    {
        return enteredGroups.containsValue(groupName);
    }

    public LiveData<String> getRegisterLiveData()
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
    public LiveData<String[]> getMembersLiveData()
    {
        return members;
    }

    public LiveData<Pair<String, Location[]>> getLocationsLiveData()
    {
        return locations;
    }
}
