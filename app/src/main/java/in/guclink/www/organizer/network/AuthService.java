package in.guclink.www.organizer.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

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
import java.util.Map;

import in.guclink.www.organizer.BuildConfig;
import in.guclink.www.organizer.R;
import in.guclink.www.organizer.models.User;
import in.guclink.www.organizer.util.ErrorHandler;

/**
 * Service for accesing AUTH api
 */
public class AuthService {
    public static final String AUTH_API_BASE_URL = BuildConfig.authBaseUrl;
    public static final String GUC_EMAIL_PATTERN = "\\A[a-zA-Z\\.\\-]+@(student.)?guc.edu.eg\\z";
    public static final int DEFAULT_EXPIRATION = 3840;

    public static Promise createAccount(String name, String email, String password, Context ctx) throws JSONException {
        final Deferred deferred = new DeferredObject();
        String url = TextUtils.join("/", new String[] {AUTH_API_BASE_URL, "users.json"});
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
                    deferred.reject(error.networkResponse);
                } catch(Exception e) {
                    deferred.reject(e);
                }
            }
        };
        JSONObject params = new JSONObject();
        params.put("password", password);
        params.put("email", email);
        params.put("name", name);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                successListener, errorListener);

        VolleyQueueSingleton.getInstance(ctx).addToRequestQueue(jsonRequest);
        return deferred.promise();
    }


    public static boolean isLoggedIn(Context ctx) {
        SharedPreferences sp = getSharedPreferences(ctx);
        return hasToken(sp, ctx.getResources()) && notExpired(getToken(ctx), ctx);
    }

    public static void logout(Context ctx) {
        SharedPreferences sp = getSharedPreferences(ctx);
        if (isLoggedIn(ctx)) {
            SharedPreferences.Editor editor = sp.edit();
            editor.remove(ctx.getResources().getString(R.string.authTokenKey));
            editor.remove(ctx.getResources().getString(R.string.authTokenCreationDateKey));
        }
    }

    public static void setToken(String token, Context ctx) {
        SharedPreferences sp = getSharedPreferences(ctx);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(ctx.getResources().getString(R.string.authTokenKey), token);
        setTokenCreationDate(ctx, editor);
        editor.apply();
    }

    public static void setUser(JSONObject user, Context ctx) {
        SharedPreferences sp = getSharedPreferences(ctx);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(ctx.getResources().getString(R.string.currentUserSPKey), user.toString());
        editor.apply();
    }

    public static User getUser(Context ctx) {
        SharedPreferences sp = getSharedPreferences(ctx);
        String key = ctx.getResources().getString(R.string.currentUserSPKey);
        if (!sp.contains(key)) {
            return null;
        }
        try {
            return User.fromJSON(new JSONObject(sp.getString(key, null)));
        } catch (JSONException e) {
            ErrorHandler.handleException(e);
            return null;
        }
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
        return sp.getString(ctx.getResources().getString(R.string.authTokenKey), null);
    }

    private static Date getTokenCreationDate(Context ctx) throws ParseException {
        SharedPreferences sp = getSharedPreferences(ctx);
        DateFormat df = DateFormat.getDateTimeInstance();
        return df.parse(sp.getString(ctx.getResources().getString(R.string.authTokenCreationDateKey), null));
    }

    private static void setTokenCreationDate(Context ctx, SharedPreferences.Editor editor) {
        DateFormat df = DateFormat.getDateTimeInstance();
        editor.putString(ctx.getResources().getString(R.string.authTokenCreationDateKey), df.format(new Date()));
    }

    private static SharedPreferences getSharedPreferences(Context ctx) {
        return ctx.getSharedPreferences("AUTH_SHARED_PREFS", Context.MODE_PRIVATE);
    }

    private static boolean hasToken(SharedPreferences sp, Resources res) {
        return sp.contains(res.getString(R.string.authTokenKey));
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
                    deferred.reject(error.networkResponse);
                } catch(Exception e) {
                    deferred.reject(e);
                }
            }
        };
        JSONObject token = new JSONObject();
        token.put("password", password);
        token.put("email", email);
        token.put("expiration", expiration);
        JSONObject params = new JSONObject();
        params.put("token", token);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                successListener, errorListener);

        VolleyQueueSingleton.getInstance(ctx).addToRequestQueue(jsonRequest);
        return deferred.promise();
    }

    /**
     * Resend verification email
     * @param email
     */
    public static void resendVerify(String email, Context ctx) {
        try {

            Response.Listener<JSONObject> success = new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                }
            };
            Response.ErrorListener failure = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    ErrorHandler.reportGenericVolleyError(error);
                }
            };
            String emailParam = Base64.encodeToString(email.getBytes(), Base64.URL_SAFE).trim();
            String [] urlArray = new String[] {AUTH_API_BASE_URL, "users", emailParam, "resend_verify.json"};
            String url = TextUtils.join("/", urlArray);
            JsonObjectRequest request = new JsonObjectRequest(url,
                    null, success, failure);
            VolleyQueueSingleton.getInstance(ctx).addToRequestQueue(request);
        } catch (Exception e) {
            ErrorHandler.handleException(e);
        }
    }

    public static void addTokenHeader(Map<String, String> headers, Context ctx) {
        if(isLoggedIn(ctx)) {
            String value = "Bearer " + getToken(ctx);
            headers.put("Authorization", value);
        }
    }
}
