package in.guclink.www.organizer.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents an event that isn't a schedule slot nor an exam
 */
public class Event implements Schedulable, Followable {
    @Override
    public String getGCMTopic() {
        return null;
    }

    @Override
    public boolean hasParentTopic() {
        return false;
    }

    @Override
    public String getGCMParentTopic() {
        return null;
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        return null;
    }
}
