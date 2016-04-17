package in.guclink.www.organizer.util;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

/**
 * Created by ahi on 01/04/16.
 */
public class ErrorHandler {

    public static final int NETWORK_ERROR_CODE = 0;
    public static final int NETWORK_ERROR_PARSE_422 = 1;

    public static void handleException(Exception e) {
//    TODO: Implement
    }

    public static void reportGenericVolleyError(VolleyError error) {
//        TODO: Implement
    }

    public static void logError(int networkErrorCode, String s, NetworkResponse response) {
//        TODO: Implement
    }
}
