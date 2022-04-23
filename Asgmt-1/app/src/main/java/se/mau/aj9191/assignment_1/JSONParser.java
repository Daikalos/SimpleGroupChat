package se.mau.aj9191.assignment_1;

import android.util.JsonWriter;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

public class JSONParser
{
    public static void parseType(MainViewModel viewModel, String jsonMessage) throws JSONException
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

    public static void parseRegister(MainViewModel viewModel, JSONObject jsonObject) throws JSONException
    {
        String id = jsonObject.getString("id");


    }
    public static void parseUnregister(MainViewModel viewModel, JSONObject jsonObject) throws JSONException
    {
        String id = jsonObject.getString("id");


    }
    public static void parseMembers(MainViewModel viewModel, JSONObject jsonObject) throws JSONException
    {
        JSONArray arr = jsonObject.getJSONArray("members");
        ArrayList<String> result = new ArrayList<>(arr.length());

        for (int i = 0; i < arr.length(); ++i)
            result.add(arr.getString(i));


    }
    public static void parseGroups(MainViewModel viewModel, JSONObject jsonObject) throws JSONException
    {
        JSONArray arr = jsonObject.getJSONArray("groups");
        ArrayList<String> result = new ArrayList<>(arr.length());

        for (int i = 0; i < arr.length(); ++i)
            result.add(arr.getString(i));

        viewModel.updateGroups(result);
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
        ArrayList<String> result = new ArrayList<>(arr.length());

        String group = jsonObject.getString("group");

        for (int i = 0; i < arr.length(); ++i)
        {
            String member = jsonObject.getString("member");
            double longitude = jsonObject.getDouble("longitude");
            double latitude = jsonObject.getDouble("latitude");
        }


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

    public static String register(String group, String member) throws IOException
    {
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = new JsonWriter(stringWriter);

        writer.beginObject()
                .name("type").value("register")
                .name("group").value(group)
                .name("member").value(member);

        return stringWriter.toString();
    }
    public static String unregister(String id) throws IOException
    {
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = new JsonWriter(stringWriter);

        writer.beginObject()
                .name("type").value("unregister")
                .name("id").value(id);

        return stringWriter.toString();
    }
    public static String members(String groupName) throws IOException
    {
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = new JsonWriter(stringWriter);

        writer.beginObject()
                .name("type").value("members")
                .name("group").value(groupName).endObject();

        return stringWriter.toString();
    }
    public static String groups() throws IOException
    {
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = new JsonWriter(stringWriter);

        writer.beginObject()
                .name("type").value("groups").endObject();

        return stringWriter.toString();
    }
    public static String setLocation(String id, double longitude, double latitude) throws IOException
    {
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = new JsonWriter(stringWriter);

        writer.beginObject()
                .name("type").value("location")
                .name("id").value(id)
                .name("longitude").value(longitude)
                .name("latitude").value(latitude).endObject();

        return stringWriter.toString();
    }
    public static String enterText(String id, String text) throws IOException
    {
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = new JsonWriter(stringWriter);

        writer.beginObject()
                .name("type").value("textchat")
                .name("id").value(id)
                .name("text").value(text).endObject();

        return stringWriter.toString();
    }
    public static String enterImage(String id, String text, double longitude, double latitude) throws IOException
    {
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = new JsonWriter(stringWriter);

        writer.beginObject()
                .name("type").value("imagechat")
                .name("id").value(id)
                .name("text").value(text)
                .name("longitude").value(longitude)
                .name("latitude").value(latitude).endObject();

        return stringWriter.toString();
    }
}
