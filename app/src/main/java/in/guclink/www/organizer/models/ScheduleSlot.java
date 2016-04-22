package in.guclink.www.organizer.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a schedule slot (lecture/tutorial/lab/etc.).
 */
public class ScheduleSlot implements Schedulable, Followable {
    private int slotNumber, dayNumber;
    private String ID, location, group, name, topicID, group_topic_id;
    private boolean lab, lecture, tutorial;
    private Course course;

    private ScheduleSlot(int slot_num, int day, String id, String location, String group,
                         String name, String topic_id, String group_topic_id, boolean lab,
                         boolean lecture, boolean tutorial, Course course) {
        this.slotNumber = slot_num;
        this.dayNumber = day;
        this.ID = id;
        this.location = location;
        this.group = group;
        this.name = name;
        this.topicID = topic_id;
        this.group_topic_id = group_topic_id;
        this.lab = lab;
        this.lecture = lecture;
        this.tutorial = tutorial;
        this.course = course;
    }


    public int getSlotNumber() {
        return slotNumber;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public String getID() {
        return ID;
    }

    public String getLocation() {
        return location;
    }

    public String getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public String getTopicID() {
        return topicID;
    }

    public String getGroup_topic_id() {
        return group_topic_id;
    }

    public boolean isLab() {
        return lab;
    }

    public boolean isLecture() {
        return lecture;
    }

    public boolean isTutorial() {
        return tutorial;
    }

    public Course getCourse() {
        return course;
    }

    public static ScheduleSlot fromJSON(JSONObject data) throws JSONException {
        Course course = Course.fromJSON(data.getJSONObject("course"));
        return new ScheduleSlot(data.getInt("slot_num"), data.getInt("day"),
                data.getString("id"), data.getString("location"),
                data.getString("group"),
                data.getString("name"), data.getString("topic_id"),
                data.getString("group_topic_id"), data.getBoolean("lab"),
                data.getBoolean("lecture"), data.getBoolean("tutorial"),
                course);
    }

}
