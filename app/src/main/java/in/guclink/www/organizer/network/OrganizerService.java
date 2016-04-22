package in.guclink.www.organizer.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.impl.DeferredObject;
import org.jdeferred.multiple.MultipleResults;
import org.jdeferred.multiple.OneReject;
import org.jdeferred.multiple.OneResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import in.guclink.www.organizer.BuildConfig;
import in.guclink.www.organizer.R;
import in.guclink.www.organizer.models.Event;
import in.guclink.www.organizer.models.Exam;
import in.guclink.www.organizer.models.Schedulable;
import in.guclink.www.organizer.models.ScheduleSlot;
import in.guclink.www.organizer.util.ErrorHandler;

/**
 * Service for accesing organizer api.
 */
public class OrganizerService {
    public static final String ORGANIZER_BASE_URL = BuildConfig.organizerBaseUrl;


    public Promise<List<ScheduleSlot>, Object, Object> getScheduleSlots(GUCCredentials creds, final Context ctx) {
        final Deferred<List<ScheduleSlot>, Object, Object>  deferred = new DeferredObject<List<ScheduleSlot>, Object, Object>();
        String url = TextUtils.join("/", new String[] {ORGANIZER_BASE_URL, "schedules.json"});
        JSONObject params = new JSONObject();
        try {
            params.put("guc_password", creds.password);
            params.put("guc_username", creds.name);
            Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray slots = response.getJSONArray("slots");
                        ArrayList<ScheduleSlot> scheduleSlots = new ArrayList<ScheduleSlot>();
                        for(int i = 0; i < slots.length(); i++) {
                            JSONObject slot = slots.getJSONObject(i);
                            scheduleSlots.add(ScheduleSlot.fromJSON(slot));
                        }
                        deferred.resolve(scheduleSlots);
                    } catch (Exception e) {
                        ErrorHandler.handleException(e);
                        deferred.reject(e);
                    }
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        ErrorHandler.logError(error.networkResponse.statusCode, "Error fetching schedule",
                                error.networkResponse);
                        deferred.reject(error.networkResponse);
                    } catch(Exception e) {
                        ErrorHandler.handleException(e);
                        deferred.reject(e);
                    }
                }
            };
            JsonObjectRequest jsonRequest = new AuthenticatedJSONObjectRequest(Request.Method.POST, url,
                    params, successListener, errorListener, ctx);
            VolleyQueueSingleton.getInstance(ctx).addToRequestQueue(jsonRequest);
        } catch (JSONException e) {
            ErrorHandler.handleException(e);
            deferred.reject(e);
        }
        return deferred.promise();
    }

    public Promise<List<Exam>, Object, Object> getExams(GUCCredentials creds, final Context ctx) {
        final Deferred<List<Exam>, Object, Object> deferred = new DeferredObject<List<Exam>, Object, Object>();
        String url = TextUtils.join("/", new String[] {ORGANIZER_BASE_URL, "exams.json"});
        try {
            JSONObject params = new JSONObject();
            params.put("guc_password", creds.password);
            params.put("guc_username", creds.name);
            Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray jsonExams = response.getJSONArray("exams");
                        ArrayList<Exam> exams = new ArrayList<Exam>();
                        for(int i = 0; i < jsonExams.length(); i++) {
                            exams.add(Exam.fromJSON(jsonExams.getJSONObject(i)));
                        }
                        deferred.resolve(exams);
                    } catch(Exception e) {
                        ErrorHandler.handleException(e);
                        deferred.reject(e);
                    }
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        ErrorHandler.logError(error.networkResponse.statusCode, "Error fetching exams",
                                error.networkResponse);
                        deferred.reject(error.networkResponse);
                    } catch(Exception e) {
                        ErrorHandler.handleException(e);
                        deferred.reject(e);
                    }
                }
            };
            JsonObjectRequest jsonRequest = new AuthenticatedJSONObjectRequest(Request.Method.POST,
                    url, params, successListener, errorListener, ctx);
            VolleyQueueSingleton.getInstance(ctx).addToRequestQueue(jsonRequest);
        } catch(JSONException e) {
            ErrorHandler.handleException(e);
            deferred.reject(e);
        }
        return deferred.promise();
    }

    public Promise<List<Event>, Object, Object> getEvents(final Context ctx) {
        final Deferred<List<Event>, Object, Object> deferred = new DeferredObject<List<Event>, Object, Object>();
        String url = TextUtils.join("/", new String[] {ORGANIZER_BASE_URL, "events.json"});
        Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray eventJson = response.getJSONArray("events");
                    ArrayList<Event> events = new ArrayList<Event>();
                    for (int i = 0; i < eventJson.length(); i++) {
                        events.add(Event.fromJSON(eventJson.getJSONObject(i)));
                    }
                    deferred.resolve(events);
                } catch (Exception e) {
                    ErrorHandler.handleException(e);
                    deferred.reject(e);
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    ErrorHandler.logError(error.networkResponse.statusCode, "Error fetching events",
                            error.networkResponse);
                    deferred.reject(error.networkResponse);
                } catch(Exception e) {
                    ErrorHandler.handleException(e);
                    deferred.reject(e);
                }
            }
        };
        JsonObjectRequest request = new AuthenticatedJSONObjectRequest(url, null, successListener, errorListener, ctx);
        VolleyQueueSingleton.getInstance(ctx).addToRequestQueue(request);
        return deferred.promise();
    }

    public Promise<List<Schedulable>, Object, Object> getSchedule(GUCCredentials creds, Context ctx) {
        final Deferred<List<Schedulable>, Object, Object> deferred = new DeferredObject<List<Schedulable>, Object, Object>();
        AndroidDeferredManager manager = new AndroidDeferredManager();
        manager.when(getScheduleSlots(creds, ctx), getExams(creds, ctx),
                getEvents(ctx)).then(new DoneCallback<MultipleResults>() {
            @Override
            public void onDone(MultipleResults results) {
                ArrayList<Schedulable> schedule = new ArrayList<Schedulable>();

                for (OneResult result: results ) {
                    schedule.addAll((Collection<? extends Schedulable>) result.getResult());
                }
                deferred.resolve(schedule);
            }
        }).fail(new FailCallback<OneReject>() {
            @Override
            public void onFail(OneReject result) {
                deferred.reject(result.getReject());
            }
        });
        return deferred.promise();
    }

    public static void storeGUCCredentials(GUCCredentials creds, Context ctx) throws JSONException {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(ctx.getResources().getString(R.string.gucCredsKey), creds.toJSONObject().toString());
        editor.apply();
    }

    public static boolean hasGUCCredentials(Context ctx) {
        return getSharedPreferences(ctx).contains(ctx.getResources().getString(R.string.gucCredsKey));
    }

    public static GUCCredentials getGUCCredentials(Context ctx) {
        if(hasGUCCredentials(ctx)) {
            SharedPreferences sp = getSharedPreferences(ctx);
            try {
                return GUCCredentials.fromJSON(new JSONObject(sp.getString(ctx.getResources().getString(R.string.gucCredsKey), null)));
            } catch (Exception e) {
                ErrorHandler.handleException(e);
                return null;
            }
        } else {
            return null;
        }
    }

    private static SharedPreferences getSharedPreferences(Context ctx) {
        return ctx.getSharedPreferences("ORGANIZER_SHARED_PREFS", Context.MODE_PRIVATE);
    }

    public static void clearSharedPreferences(Context ctx) {
        SharedPreferences sp = getSharedPreferences(ctx);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }
}
