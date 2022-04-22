package se.mau.aj9191.assignment_1;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkService extends Service
{
    public static final String IP = "195.178.227.53", PORT = "7117";
    private final IBinder binder = new NetworkBinder();

    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private InetAddress address;

    private boolean connected = false;

    private ExecutorService executorService = Executors.newFixedThreadPool(6);

    public boolean isConnected()
    {
        return connected;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);
        connect();
        return START_STICKY;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        disconnect();
    }

    public void connect()
    {
        executorService.execute(new Connect());
    }
    public void disconnect()
    {
        executorService.execute(new Disconnect());
    }

    public class NetworkBinder extends Binder
    {
        NetworkService getService()
        {
            return NetworkService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return binder;
    }
    @Override
    public boolean onUnbind(Intent intent)
    {
        return super.onUnbind(intent);
    }


    private class Connect implements Runnable
    {
        @Override
        public void run()
        {
            try
            {
                address = InetAddress.getByName(IP);
                socket = new Socket(address, Integer.parseInt(PORT));
                input = new ObjectInputStream(socket.getInputStream());
                output = new ObjectOutputStream(socket.getOutputStream());
                output.flush();

                connected = true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    private class Disconnect implements Runnable
    {
        @Override
        public void run()
        {
            try
            {
                if (input != null)
                    input.close();
                if (output != null)
                    output.close();
                if (socket != null)
                    socket.close();

                connected = false;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
