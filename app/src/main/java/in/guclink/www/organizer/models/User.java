package in.guclink.www.organizer.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ahi on 22/04/16.
 */
public class User {

    private String name, fullName, email;
    private boolean student, superUser;
    private User(String name, String fullName, String email, boolean student, boolean superUser) {
        this.name = name;
        this.fullName = fullName;
        this.email = email;
        this.student = student;
        this.superUser = superUser;
    }

    public boolean isStudent() {
        return student;
    }

    public boolean isTeacher() {
        return !student;
    }

    public boolean isSuperUser() {
        return superUser;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }


    public JSONObject toJSONObject() throws JSONException {
        JSONObject data = new JSONObject();
        data.put("name", name);
        data.put("full_name", fullName);
        data.put("email", email);
        data.put("student", student);
        data.put("super_user", superUser);
        return data;
    }

    public static User fromJSON(JSONObject data) throws JSONException {
        String name = data.getString("name");
        String fullName = data.getString("full_name");
        String email = data.getString("email");
        boolean student = data.getBoolean("student");
        boolean superUser = data.getBoolean("super_user");
        return new User(name, fullName, email, student, superUser);
    }
}
