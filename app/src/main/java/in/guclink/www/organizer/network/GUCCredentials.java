package in.guclink.www.organizer.network;

import org.json.JSONException;
import org.json.JSONObject;

import in.guclink.www.organizer.models.JSONSerializable;

public class GUCCredentials implements JSONSerializable {
    public final String name, password;

    public GUCCredentials(String name, String password) {
        this.name = name;
        this.password = password;
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject data = new JSONObject();
        data.put("name", name);
        data.put("password", password);
        return data;
    }

    public static GUCCredentials fromJSON(JSONObject data) throws JSONException {
        return new GUCCredentials(data.getString("name"), data.getString("password"));
    }
}
