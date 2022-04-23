package se.mau.aj9191.assignment_1;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;

public class MainViewModel extends ViewModel
{
    private MutableLiveData<Group> groupCreate = new MutableLiveData<>();
    private MutableLiveData<User> userRegister = new MutableLiveData<>();

    private ArrayList<Group> groups = new ArrayList<>();

    public ArrayList<Group> getGroups()
    {
        return groups;
    }

    public void updateGroups(ArrayList<String> groups)
    {
        this.groups = new ArrayList<>(groups.size());

        for (String group : groups)
            this.groups.add(new Group(group));
    }
}
