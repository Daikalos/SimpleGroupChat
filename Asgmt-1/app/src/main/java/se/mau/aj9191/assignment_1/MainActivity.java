package se.mau.aj9191.assignment_1;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{
    private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        controller = new Controller(this, savedInstanceState);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        controller.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0)
            super.onBackPressed();
        else
            getSupportFragmentManager().popBackStack();
    }
}
