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
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity
{
    private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        controller = new Controller(this, viewModel, savedInstanceState);

        String currentLanguage = PreferenceManager.getDefaultSharedPreferences(this).getString(LocaleHelper.SELECTED_LANGUAGE, "en");
        LocaleHelper.setLocale(this, currentLanguage);
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

    @NonNull
    public Controller getController()
    {
        return controller;
    }
}
