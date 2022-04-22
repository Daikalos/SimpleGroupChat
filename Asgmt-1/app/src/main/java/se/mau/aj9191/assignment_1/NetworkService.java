package se.mau.aj9191.assignment_1;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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
    private InetAddress address;

    private InputStream inputStream;
    private OutputStream outputStream;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    private Receive receive;
    private String receiveString;
    private boolean isReceiving = false;

    private boolean connected = false;

    private ExecutorService executorService = Executors.newFixedThreadPool(6);

    public boolean getIsConnected()
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

    public void sendMessage(String message)
    {
        executorService.execute(new Send(message));
    }
    public String receive()
    {
        return receiveString;
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

                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();

                dataInputStream = new DataInputStream(inputStream);
                dataOutputStream = new DataOutputStream(outputStream);

                isReceiving = true;
                connected = true;

                receive = new Receive();
                receive.start();
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
                if (inputStream != null)
                    inputStream.close();
                if (outputStream != null)
                    outputStream.close();

                if (dataInputStream != null)
                    dataInputStream.close();
                if (dataOutputStream != null)
                    dataOutputStream.close();

                if (socket != null)
                    socket.close();

                isReceiving = false;
                connected = false;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private class Receive extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                while (isReceiving)
                {
                    receiveString = dataInputStream.readUTF();
                }
            }
            catch (Exception e)
            {
                isReceiving = false;
            }
        }
    }
    private class Send implements Runnable
    {
        private String jsonMessage;

        public Send(String jsonMessage)
        {
            this.jsonMessage = jsonMessage;
        }

        @Override
        public void run()
        {
            try
            {
                dataOutputStream.writeUTF(jsonMessage);
                dataOutputStream.flush();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
