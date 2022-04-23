package se.mau.aj9191.assignment_1;

import android.util.JsonWriter;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class JsonHelper
{
    public static void parseType(MainViewModel viewModel, String jsonMessage)
    {
        try
        {
            JSONObject obj = new JSONObject(jsonMessage);
            String type = obj.get("type").toString();

            switch (type)
            {
                case "register":
                    parseRegister(viewModel, obj);
                    break;
                case "unregister":
                    parseUnregister(viewModel, obj);
                    break;
                case "members":
                    parseMembers(viewModel, obj);
                    break;
                case "groups":
                    parseGroups(viewModel, obj);
                    break;
                case "location":
                    parseSetLocation(viewModel, obj);
                    break;
                case "locations":
                    parseLocations(viewModel, obj);
                    break;
                case "textchat":
                    parseEnterText(viewModel, obj);
                    break;
                case "upload":
                    parseEnterImage(viewModel, obj);
                    break;
                case "imagechat":
                    parseReceiveImage(viewModel, obj);
                    break;
                case "exception":
                    parseException(obj);
                    break;
                default:
                    Log.d("error", "unknown type: " + type);
                    break;
            }
        }
        catch (JSONException e)
        {
            Log.d("error", e.getMessage());
        }
    }

    public static void parseRegister(MainViewModel viewModel, JSONObject jsonObject) throws JSONException
    {
        String group = jsonObject.getString("group");
        String id = jsonObject.getString("id");

        viewModel.register(group, id);
    }
    public static void parseUnregister(MainViewModel viewModel, JSONObject jsonObject) throws JSONException
    {
        String id = jsonObject.getString("id");
        viewModel.unregister(id);
    }
    public static void parseMembers(MainViewModel viewModel, JSONObject jsonObject) throws JSONException
    {
        JSONArray arr = jsonObject.getJSONArray("members");
        String[] result = new String[arr.length()];

        for (int i = 0; i < arr.length(); ++i)
        {
            JSONObject obj = (JSONObject)arr.get(i);
            result[i] = obj.getString("member");
        }

        viewModel.showMembers(result);
    }
    public static void parseGroups(MainViewModel viewModel, JSONObject jsonObject) throws JSONException
    {
        JSONArray arr = jsonObject.getJSONArray("groups");
        String[] result = new String[arr.length()];

        for (int i = 0; i < arr.length(); ++i)
        {
            JSONObject obj = (JSONObject)arr.get(i);
            result[i] = obj.getString("group");
        }

        viewModel.showGroups(result);
    }
    public static void parseSetLocation(MainViewModel viewModel, JSONObject jsonObject) throws JSONException
    {
        String id = jsonObject.getString("id");
        double longitude = jsonObject.getDouble("longitude");
        double latitude = jsonObject.getDouble("latitude");


    }
    public static void parseLocations(MainViewModel viewModel, JSONObject jsonObject) throws JSONException
    {
        JSONArray arr = jsonObject.getJSONArray("location");
        String group = jsonObject.getString("group");

        Pair<String, Location[]> result = new Pair<>(group, new Location[arr.length()]);

        for (int i = 0; i < arr.length(); ++i)
        {
            String member = jsonObject.getString("member");
            double longitude = jsonObject.getDouble("longitude");
            double latitude = jsonObject.getDouble("latitude");

            result.second[i] = new Location(member, new Coordinate(longitude, latitude));
        }

        viewModel.updateLocations(result);
    }
    public static void parseEnterText(MainViewModel viewModel, JSONObject jsonObject) throws JSONException
    {
        String id = jsonObject.getString("id");
        String text = jsonObject.getString("text");


    }
    public static void parseEnterImage(MainViewModel viewModel, JSONObject jsonObject) throws JSONException
    {
        String imageid = jsonObject.getString("imageid");
        String port = jsonObject.getString("port");


    }
    public static void parseReceiveText(MainViewModel viewModel, JSONObject jsonObject) throws JSONException
    {
        String group = jsonObject.getString("group");
        String member = jsonObject.getString("member");
        String text = jsonObject.getString("text");


    }
    public static void parseReceiveImage(MainViewModel viewModel, JSONObject jsonObject) throws JSONException
    {
        String group = jsonObject.getString("group");
        String member = jsonObject.getString("member");
        String text = jsonObject.getString("text");
        double longitude = jsonObject.getDouble("longitude");
        double latitude = jsonObject.getDouble("latitude");
        String imageid = jsonObject.getString("imageid");
        String port = jsonObject.getString("port");


    }
    public static void parseException(JSONObject jsonObject) throws JSONException
    {
        String exception = jsonObject.get("message").toString();
        Log.d("error", exception);
    }

    public static String sendRegister(String group, String member)
    {
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = new JsonWriter(stringWriter);

        try
        {
            writer.beginObject()
                    .name("type").value("register")
                    .name("group").value(group)
                    .name("member").value(member).endObject();
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }
        return stringWriter.toString();
    }
    public static String sendUnregister(String id)
    {
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = new JsonWriter(stringWriter);

        try
        {
            writer.beginObject()
                    .name("type").value("unregister")
                    .name("id").value(id).endObject();
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }

        return stringWriter.toString();
    }
    public static String sendGetMembers(String groupName)
    {
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = new JsonWriter(stringWriter);

        try
        {
            writer.beginObject()
                    .name("type").value("members")
                    .name("group").value(groupName).endObject();
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }

        return stringWriter.toString();
    }
    public static String sendGetGroups()
    {
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = new JsonWriter(stringWriter);

        try
        {
            writer.beginObject()
                    .name("type").value("groups").endObject();
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }

        return stringWriter.toString();
    }
    public static String sendSetLocation(String id, double longitude, double latitude)
    {
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = new JsonWriter(stringWriter);

        try
        {
            writer.beginObject()
                    .name("type").value("location")
                    .name("id").value(id)
                    .name("longitude").value(longitude)
                    .name("latitude").value(latitude).endObject();
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }

        return stringWriter.toString();
    }
    public static String sendEnterText(String id, String text)
    {
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = new JsonWriter(stringWriter);

        try
        {
            writer.beginObject()
                    .name("type").value("textchat")
                    .name("id").value(id)
                    .name("text").value(text).endObject();
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }

        return stringWriter.toString();
    }
    public static String sendEnterImage(String id, String text, double longitude, double latitude)
    {
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = new JsonWriter(stringWriter);

        try
        {
            writer.beginObject()
                    .name("type").value("imagechat")
                    .name("id").value(id)
                    .name("text").value(text)
                    .name("longitude").value(longitude)
                    .name("latitude").value(latitude).endObject();
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }

        return stringWriter.toString();
    }
}
