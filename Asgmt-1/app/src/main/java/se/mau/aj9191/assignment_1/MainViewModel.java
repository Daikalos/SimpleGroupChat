package se.mau.aj9191.assignment_1;

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

    private final MutableLiveData<ArrayList<String>> groups = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<String>> members = new MutableLiveData<>();

    private final MutableLiveData<User> location = new MutableLiveData<>();

    private final HashMap<String, String> enteredGroups = new HashMap<>();

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

    public void showMembers(ArrayList<String> members)
    {
        this.members.postValue(members);
    }
    public void showGroups(ArrayList<String> groups)
    {
        this.groups.postValue(groups);
    }

    public LiveData<String> getRegisterLiveData()   { return register; }
    public LiveData<String> getUnregisterLiveData() { return unregister; }

    public LiveData<ArrayList<String>> getGroupsLiveData()  { return groups; }
    public LiveData<ArrayList<String>> getMembersLiveData() { return members; }
}
