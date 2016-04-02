package in.guclink.www.organizer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.json.JSONException;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.guclink.www.organizer.network.AuthService;
import in.guclink.www.organizer.util.ErrorHandler;
import in.guclink.www.organizer.util.State;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class LoginActivity extends AppCompatActivity {
    @Bind(R.id.input_email) EditText email;
    @Bind(R.id.input_password) EditText password;
    @Bind(R.id.btn_login) Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/montserrat/Montserrat-Regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        ButterKnife.bind(this);
        addEmailTextWatcher();
        addPasswordTextWatcher();
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    /**
     * Handle login click
     * @param view
     */
    public void buttonClickLogin(View view) {
        if (validate()) {
            if (!State.isConnected(this)) {
//                TODO: Handle No connection
                return;
            }
            final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_PopupOverlay);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.loginProgressDialogMessage));
            progressDialog.show();
            try {
                Promise loginPromise = AuthService.login(email.getText().toString(),
                        password.getText().toString(), AuthService.DEFAULT_EXPIRATION, this);

                loginPromise.done(new DoneCallback() {
                    @Override
                    public void onDone(Object result) {
                        launchScheduleActivity();
                    }
                }).fail(new FailCallback() {
                    @Override
                    public void onFail(Object result) {

                    }
                }).always(new AlwaysCallback() {
                    @Override
                    public void onAlways(Promise.State state, Object resolved, Object rejected) {
                        progressDialog.dismiss();
                    }
                });
            } catch (JSONException e) {
                progressDialog.dismiss();
                ErrorHandler.handleException(e);
            }
        }
    }

    private void launchScheduleActivity() {
        Intent intent = new Intent(this, ScheduleActivity.class);
        startActivity(intent);
    }
    private boolean validateEmail() {
        String emailText = email.getText().toString();
        return emailText.matches(AuthService.GUC_EMAIL_PATTERN);
    }

    private boolean validatePassword() {
        String passwordText = password.getText().toString();
        return !passwordText.isEmpty();
    }

    private boolean validate() {
        return validatePassword() && validateEmail();
    }

    private void addEmailTextWatcher() {
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (validateEmail()) {
                    email.setError(null);
                } else {
                    email.setError(getString(R.string.login_email_error));
                }

                setLoginButtonEnabled();
            }
        });
    }

    private void addPasswordTextWatcher() {
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (validatePassword()) {
                    password.setError(null);
                } else {
                    password.setError(getString(R.string.password_login_error));
                }
                setLoginButtonEnabled();
            }
        });
    }

    private void setLoginButtonEnabled() {
        if(validate()) {
            loginBtn.setEnabled(true);
        } else {
            loginBtn.setEnabled(false);
        }
    }
}
