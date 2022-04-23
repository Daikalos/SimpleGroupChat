package se.mau.aj9191.assignment_1;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;

import java.util.Locale;

public class LocaleHelper
{
    private static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language";

    private static void persist(Context context, String language)
    {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(SELECTED_LANGUAGE, language);
        editor.apply();
    }

    public static void setLocale(Context context, String language)
    {
        persist(context, language);

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);

        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}
