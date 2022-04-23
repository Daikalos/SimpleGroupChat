package se.mau.aj9191.assignment_1;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity
{
    private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        controller = new Controller(this, new ViewModelProvider(this).get(MainViewModel.class), savedInstanceState);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        controller.onResume();

        checkPermissions();
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        controller.onDestroy();
    }

    private void checkPermissions()
    {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case 1:
                if (grantResults.length == 0 || grantResults[0] == PackageManager.PERMISSION_DENIED)
                {
                    ActivityCompat.finishAffinity(this);
                }
                break;
        }
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
