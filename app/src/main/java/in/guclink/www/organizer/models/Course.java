package in.guclink.www.organizer.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ahi on 22/04/16.
 */
public class Course implements Followable {
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
}
