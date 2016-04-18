package in.guclink.www.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import in.guclink.www.organizer.network.AuthService;

/**
 * Redirects to login if not logged in on resume, restart and create.
 */
public abstract class LoggedInActivity  extends AppCompatActivity {


    private void launchLoginIfNotLoggedIn() {
        if(!AuthService.isLoggedIn(getApplicationContext())) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        launchLoginIfNotLoggedIn();
        onCreateActual(savedInstanceState);
    }

    @Override
    protected void onResume() {
        onResumeActual();
        launchLoginIfNotLoggedIn();
        super.onResume();
    }


    public abstract void onCreateActual(Bundle savedInstanceState);
    public void onResumeActual() {

    }

}
