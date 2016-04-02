package in.guclink.www.organizer.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by ahi on 01/04/16.
 */
public class VolleyQueueSingleton {
    private static VolleyQueueSingleton instance;
    private RequestQueue rq;
    private static Context ctx;


    private VolleyQueueSingleton(Context ctx) {
        this.ctx = ctx;
    }


    public synchronized RequestQueue getRequestQueue() {
        if (rq == null) {
            rq = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return rq;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public static synchronized VolleyQueueSingleton getInstance(Context context) {
        if(instance == null) {
            instance = new VolleyQueueSingleton(context);
        }
        return instance;
    }
}
