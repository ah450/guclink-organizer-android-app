package in.guclink.www.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import in.guclink.www.organizer.network.AuthService;

/**
 * Created by ahi on 18/04/16.
 */
public abstract class PublicActivity extends AppCompatActivity {


    private void launchScheduleIfLoggedIn() {
        if(AuthService.isLoggedIn(getApplicationContext())) {
            Intent intent = new Intent(this, ScheduleActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        launchScheduleIfLoggedIn();
        onCreateActual(savedInstanceState);
    }

    @Override
    protected void onResume() {
        launchScheduleIfLoggedIn();
        onResumeActual();
        super.onResume();
    }

    public abstract void onCreateActual(Bundle savedInstanceState);
    public void onResumeActual() {
    }
}
