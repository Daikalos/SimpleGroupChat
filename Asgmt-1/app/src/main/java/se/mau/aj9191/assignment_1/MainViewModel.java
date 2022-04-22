package se.mau.aj9191.assignment_1;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel
{
    private MutableLiveData<Group> groupCreate = new MutableLiveData<>();
    private MutableLiveData<User> userRegister = new MutableLiveData<>();


}
