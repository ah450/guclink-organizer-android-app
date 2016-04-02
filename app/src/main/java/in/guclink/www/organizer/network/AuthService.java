package in.guclink.www.organizer.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.jdeferred.Deferred;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import in.guclink.www.organizer.R;
import in.guclink.www.organizer.util.ErrorHandler;

/**
 * Created by ahi on 30/03/16.
 */
public class AuthService {
    public static final String AUTH_API_BASE_URL = Resources.getSystem().getString(R.string.authBaseUrl);
    public static final String GUC_EMAIL_PATTERN = "\\A[a-zA-Z\\.\\-]+@(student.)?guc.edu.eg\\z";
    public static final int DEFAULT_EXPIRATION = 3840;

    public static Promise createAccount(String name, String email, String password) {
        Deferred deferred = new DeferredObject();

        return deferred.promise();
    }


    public static boolean isLoggedIn(Context ctx) {
        SharedPreferences sp = getSharedPreferences(ctx);
        return hasToken(sp) && notExpired(getToken(ctx), ctx);
    }

    public static void logout(Context ctx) {
        SharedPreferences sp = getSharedPreferences(ctx);
        if (isLoggedIn(ctx)) {
            SharedPreferences.Editor editor = sp.edit();
            editor.remove(Resources.getSystem().getString(R.string.authTokenKey));
            editor.remove(Resources.getSystem().getString(R.string.authTokenCreationDateKey));
        }
    }

    public static void setToken(String token, Context ctx) {
        SharedPreferences sp = getSharedPreferences(ctx);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Resources.getSystem().getString(R.string.authTokenKey), token);
        setTokenCreationDate(ctx);
    }

    public static boolean notExpired(String token, Context ctx) {
        if (token == null) {
            return false;
        }
        try {
            Date createdAt = getTokenCreationDate(ctx);
            createdAt.setTime(createdAt.getTime() + DEFAULT_EXPIRATION * 60 * 60 * 1000);
            return new Date().before(createdAt);
        } catch (ParseException e) {
            ErrorHandler.handleException(e);
            return false;
        }

    }

    public static String getToken(Context ctx) {
        SharedPreferences sp = getSharedPreferences(ctx);
        return sp.getString(Resources.getSystem().getString(R.string.authTokenKey), null);
    }

    private static Date getTokenCreationDate(Context ctx) throws ParseException {
        SharedPreferences sp = getSharedPreferences(ctx);
        DateFormat df = DateFormat.getDateTimeInstance();
        return df.parse(sp.getString(Resources.getSystem().getString(R.string.authTokenCreationDateKey), null));
    }

    private static void setTokenCreationDate(Context ctx) {
        SharedPreferences sp = getSharedPreferences(ctx);
        SharedPreferences.Editor editor = sp.edit();
        DateFormat df = DateFormat.getDateTimeInstance();
        editor.putString(Resources.getSystem().getString(R.string.authTokenCreationDateKey), df.format(new Date()));
    }

    private static SharedPreferences getSharedPreferences(Context ctx) {
        return ctx.getSharedPreferences("AUTH_SHARED_PREFS", Context.MODE_PRIVATE);
    }

    private static boolean hasToken(SharedPreferences sp) {
        return sp.contains(Resources.getSystem().getString(R.string.authTokenKey));
    }

    /**
     * @param email login email
     * @param password guclink password
     * @param expiration in hours
     */
    public static Promise login(String email, String password, int expiration, Context ctx) throws JSONException {
        final Deferred deferred = new DeferredObject();
        String url = TextUtils.join("/", new String[] {AUTH_API_BASE_URL, "tokens.json"});
        Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                deferred.resolve(response);
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject jsonObject = new JSONObject(responseBody);
                    deferred.reject(jsonObject);
                } catch(Exception e) {
                    deferred.reject(e);
                }
            }
        };
        JSONObject params = new JSONObject();
        params.put("password", password);
        params.put("email", email);
        params.put("expiration", expiration);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                successListener, errorListener);

        VolleyQueueSingleton.getInstance(ctx).addToRequestQueue(jsonRequest);
        return deferred.promise();
    }
}
