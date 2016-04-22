package in.guclink.www.organizer;

import android.app.Application;
import android.test.ApplicationTestCase;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import in.guclink.www.organizer.network.AuthService;
/**
 * Created by ahi on 22/04/16.
 */
public class AuthServiceTest extends ApplicationTestCase<Application> {
    public AuthServiceTest() {
        super(Application.class);
    }
    @Test
    public void testSignup() {
        try {
            Promise promise = AuthService.createAccount("test android", "testandroid@student.guc.edu.eg", "password",
                    getSystemContext());
            promise.done(new DoneCallback() {
                @Override
                public void onDone(Object result) {
                    assertTrue(result instanceof JSONObject);
                }
            });
            promise.fail(new FailCallback() {
                @Override
                public void onFail(Object result) {
                    fail("Signup promise failed");
                }
            });
            promise.waitSafely();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown");
        }
    }

    @Test
    public void testLogin() {
        try {
            Promise promise = createLoginPromise();
            promise.fail(new FailCallback() {
                @Override
                public void onFail(Object result) {
                    fail("Login promise Failed");
                }
            });
            promise.done(new DoneCallback() {
                @Override
                public void onDone(Object result) {
                    assertTrue(result instanceof JSONObject);
                    JSONObject response = (JSONObject) result;
                    assertTrue(response.has("token"));
                    assertTrue(response.has("user"));
                    try {
                        String token = response.getString("token");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        fail("Failed to get keys");
                    }
                }
            });
        } catch (Exception e) {
            fail("Exception thrown");
        }
    }


    private Promise createLoginPromise() throws JSONException {
        return AuthService.login("android@student.guc.edu.eg", "password", AuthService.DEFAULT_EXPIRATION, getSystemContext());
    }
}
