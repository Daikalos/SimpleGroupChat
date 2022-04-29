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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkService extends Service
{
    public static final String IP = "192.168.0.9", PORT = "7117"; //= "195.178.227.53", PORT = "7117";
    private final IBinder binder = new NetworkBinder();

    private Socket socket;

    private InputStream inputStream;
    private OutputStream outputStream;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    private Receive receive;
    private String receiveString;
    private boolean isReceiving = false;

    private final Object object = new Object();

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);
        connect();
        return START_STICKY;
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
    public String getMessage() throws InterruptedException
    {
        while (receiveString == null || receiveString.isEmpty())
        {
            synchronized (object)
            {
                object.wait();
            }
        }

        String result = receiveString;
        receiveString = "";

        return result;
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
                InetSocketAddress socketAddress = new InetSocketAddress(IP, Integer.parseInt(PORT));

                socket = new Socket();
                socket.connect(socketAddress, 5000);

                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();

                dataInputStream = new DataInputStream(inputStream);
                dataOutputStream = new DataOutputStream(outputStream);

                isReceiving = true;

                receive = new Receive();
                receive.start();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.d("error", e.getMessage());
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

                receive.interrupt();
                isReceiving = false;
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
                    synchronized (object)
                    {
                        object.notifyAll();
                    }
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
            catch (Exception e)
            {
                Log.d("error", e.getMessage());
            }
        }
    }
}
