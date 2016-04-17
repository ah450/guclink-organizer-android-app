package in.guclink.www.organizer.network;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import in.guclink.www.organizer.util.ErrorHandler;

/**
 * Created by ahi on 17/04/16.
 */
public class NetworkResponseUtility {

    public static String parseModelMessages(byte[] data) {
        String parsed = null;


        if (data != null) {
            String jsonStr = new String(data);
            try {
                JSONObject json = new JSONObject(jsonStr);
                parsed = "";
                final Iterator<String> iter = json.keys();
                Iterable<String> adapter = new Iterable<String>() {
                    @Override
                    public Iterator<String> iterator() {
                        return iter;
                    }
                };
                for (String key: adapter) {
                    if(key != null) {
                        parsed += TextUtils.join(" ", key.split("_"));
                        parsed += " " + json.get(key) + ". ";
                    }
                }
            } catch (JSONException e) {
                ErrorHandler.handleException(e);
                return null;
            }
        }

        return parsed;
    }
}
