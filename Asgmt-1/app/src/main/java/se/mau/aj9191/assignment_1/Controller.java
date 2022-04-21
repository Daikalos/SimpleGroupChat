package se.mau.aj9191.assignment_1;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class Controller
{
    private NetworkService networkService;
    private boolean bound = false;

    private MainActivity mainActivity;

    public Controller(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;

        Intent intent = new Intent(mainActivity, NetworkService.class);
        mainActivity.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void onResume()
    {

    }
    public void onDestroy()
    {
        if (bound)
        {
            mainActivity.unbindService(serviceConnection);
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder)
        {
            NetworkService.NetworkBinder binder = (NetworkService.NetworkBinder)iBinder;
            networkService = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName)
        {
            bound = false;
        }
    };
}
