package in.guclink.www.organizer.network;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * JSON Object request with Authorization header.
 */
public class AuthenticatedJSONObjectRequest extends JsonObjectRequest {
    Context ctx;
    public AuthenticatedJSONObjectRequest(int method, String url, JSONObject params, Response.Listener<JSONObject> successListener, Response.ErrorListener errorListener, Context ctx) {
        super(method, url, params, successListener, errorListener);
        this.ctx = ctx;
    }

    public AuthenticatedJSONObjectRequest(String url, JSONObject params, Response.Listener<JSONObject> successListener, Response.ErrorListener errorListener, Context ctx) {
        this(Method.GET, url, params, successListener, errorListener, ctx);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();
        AuthService.addTokenHeader(headers, ctx);
        return headers;
    }
}
