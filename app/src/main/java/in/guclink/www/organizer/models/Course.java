package in.guclink.www.organizer.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Course implements Followable, JSONSerializable {
    private String name, ID, topicID;
    private Course(String name, String id, String topicID) {
        this.name = name;
        this.ID = id;
        this.topicID = topicID;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getTopicID() {
        return topicID;
    }

    public static Course fromJSON(JSONObject data) throws JSONException {
        return new Course(data.getString("name"), data.getString("id"),
                data.getString("topic_id"));
    }



    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject data = new JSONObject();
        data.put("name", getName());
        data.put("id", getID());
        data.put("topic_id", getTopicID());
        return data;
    }

    @Override
    public String getGCMTopic() {
        return getTopicID();
    }

    @Override
    public boolean hasParentTopic() {
        return false;
    }

    @Override
    public String getGCMParentTopic() {
        return null;
    }
}
