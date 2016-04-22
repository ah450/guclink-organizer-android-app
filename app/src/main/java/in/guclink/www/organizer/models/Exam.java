package in.guclink.www.organizer.models;


import org.json.JSONException;
import org.json.JSONObject;

public class Exam implements Schedulable {
    @Override
    public JSONObject toJSONObject() throws JSONException {
        return null;
    }

    public static Exam fromJSON(JSONObject data) throws JSONException {
        return null;
    }
}
