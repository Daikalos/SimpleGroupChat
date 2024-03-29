package se.mau.aj9191.assignment_1;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class Controller
{
    private final MainActivity mainActivity;
    private final MainViewModel viewModel;

    private NetworkService networkService;
    private boolean bound = false;

    private Listener listener;

    public Controller(MainActivity mainActivity, MainViewModel viewModel, Bundle savedInstanceState)
    {
        this.mainActivity = mainActivity;
        this.viewModel = viewModel;

        Intent intent = new Intent(mainActivity, NetworkService.class);

        if (savedInstanceState == null)
            mainActivity.startService(intent);

        mainActivity.bindService(intent, serviceConnection, 0);
    }

    public void onDestroy()
    {
        if (bound)
        {
            mainActivity.unbindService(serviceConnection);
            listener.shutdown();

            bound = false;
        }
    }

    public void sendMessage(String message)
    {
        if (bound)
            networkService.sendMessage(message);
    }

    private ServiceConnection serviceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder)
        {
            NetworkService.NetworkBinder binder = (NetworkService.NetworkBinder)iBinder;
            networkService = binder.getService();
            bound = true;

            listener = new Listener();
            listener.start();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName)
        {
            bound = false;
        }
    };

    private class Listener extends Thread
    {
        private boolean isRunning = true;

        public void shutdown()
        {
            interrupt();
        }

        @Override
        public void run()
        {
            try
            {
                while (isRunning)
                {
                    String message = networkService.getMessage();
                    JsonHelper.parseType(viewModel, message);
                }
            }
            catch (Exception e)
            {
                isRunning = false;
            }
        }
    }
}
