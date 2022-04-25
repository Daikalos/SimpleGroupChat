package se.mau.aj9191.assignment_1;

import android.util.JsonWriter;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringWriter;

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
                    parseTextMessage(viewModel, obj);
                    break;
                case "upload":
                    parseEnterImage(viewModel, obj);
                    break;
                case "imagechat":
                    parseImageMessage(viewModel, obj);
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
            e.printStackTrace();
            Log.d("error", e.getMessage() + "\n");
        }
    }

    public static void parseRegister(MainViewModel viewModel, JSONObject jsonObject) throws JSONException
    {
        String id = jsonObject.getString("id");
        String groupName = jsonObject.getString("group");

        Group group = new Group(id, groupName);

        viewModel.postRegister(group);
    }
    public static void parseUnregister(MainViewModel viewModel, JSONObject jsonObject) throws JSONException
    {
        String id = jsonObject.getString("id");
        viewModel.postUnregister(id);
    }
    public static void parseMembers(MainViewModel viewModel, JSONObject jsonObject) throws JSONException
    {
        JSONArray arr = jsonObject.getJSONArray("members");
        String groupName = jsonObject.getString("group");

        Group result = new Group(null, groupName);

        for (int i = 0; i < arr.length(); ++i)
        {
            JSONObject obj = (JSONObject)arr.get(i);
            result.addMember(obj.getString("member"));
        }

        viewModel.postMembers(result);
    }
    public static void parseGroups(MainViewModel viewModel, JSONObject jsonObject) throws JSONException
    {
        JSONArray arr = jsonObject.getJSONArray("groups");
        Group[] result = new Group[arr.length()];

        for (int i = 0; i < arr.length(); ++i)
        {
            JSONObject obj = (JSONObject)arr.get(i);
            result[i] = new Group(null, obj.getString("group"));
        }

        viewModel.postGroups(result);
    }
    public static void parseSetLocation(MainViewModel viewModel, JSONObject jsonObject) throws JSONException
    {
        String id = jsonObject.getString("id");
        double longitude = Double.parseDouble(jsonObject.getString("longitude"));
        double latitude = Double.parseDouble(jsonObject.getString("latitude"));

        Location location = new Location(id, longitude, latitude);

        viewModel.postLocation(location);
    }
    public static void parseLocations(MainViewModel viewModel, JSONObject jsonObject) throws JSONException
    {
        JSONArray arr = jsonObject.getJSONArray("location");
        String groupName = jsonObject.getString("group");

        Location[] locations = new Location[arr.length()];

        for (int i = 0; i < arr.length(); ++i)
        {
            JSONObject obj = (JSONObject)arr.get(i);

            String member = obj.getString("member");
            double longitude = Double.parseDouble(obj.getString("longitude"));
            double latitude = Double.parseDouble(obj.getString("latitude"));

            locations[i] = new Location(member, longitude, latitude);
        }

        viewModel.postLocations(groupName, locations);
    }
    public static void parseEnterText(MainViewModel viewModel, JSONObject jsonObject) throws JSONException
    {
        if (jsonObject.isNull("id"))
            return;

        String id = jsonObject.getString("id");
        String text = jsonObject.getString("text");

        SendText sendText = new SendText(id, text);

        viewModel.postSentText(sendText);
    }
    public static void parseEnterImage(MainViewModel viewModel, JSONObject jsonObject) throws JSONException
    {
        String imageid = jsonObject.getString("imageid");
        String port = jsonObject.getString("port");

        SendImage sendImage = new SendImage(imageid, port);

        viewModel.postSentImage(sendImage);
    }
    public static void parseTextMessage(MainViewModel viewModel, JSONObject jsonObject) throws JSONException
    {
        if (jsonObject.isNull("group"))
            return;

        String group = jsonObject.getString("group");
        String member = jsonObject.getString("member");
        String text = jsonObject.getString("text");

        TextMessage result = new TextMessage(group, member, text);

        viewModel.postTextMessage(result);
    }
    public static void parseImageMessage(MainViewModel viewModel, JSONObject jsonObject) throws JSONException
    {
        String group = jsonObject.getString("group");
        String member = jsonObject.getString("member");
        String text = jsonObject.getString("text");
        double longitude = Double.parseDouble(jsonObject.getString("longitude"));
        double latitude = Double.parseDouble(jsonObject.getString("latitude"));
        String imageid = jsonObject.getString("imageid");
        String port = jsonObject.getString("port");

        ImageMessage result = new ImageMessage(group, member, text, longitude, latitude, imageid, port);

        viewModel.postTextMessage(result);
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
    public static String sendLocation(String id, double longitude, double latitude)
    {
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = new JsonWriter(stringWriter);

        try
        {
            writer.beginObject()
                    .name("type").value("location")
                    .name("id").value(id)
                    .name("longitude").value(String.valueOf(longitude))
                    .name("latitude").value(String.valueOf(latitude)).endObject();
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
                    .name("longitude").value(String.valueOf(longitude))
                    .name("latitude").value(String.valueOf(latitude)).endObject();
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }

        return stringWriter.toString();
    }
}
