package in.guclink.www.organizer;

import android.support.v4.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.json.JSONException;
import org.json.JSONObject;

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
    protected void onResume() {
       if(AuthService.isLoggedIn(getApplicationContext())) {
           Intent intent = new Intent(this, ScheduleActivity.class);
           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
           startActivity(intent);
           finish();
       }
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(AuthService.isLoggedIn(getApplicationContext())) {
            Intent intent = new Intent(this, ScheduleActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_login);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Montserrat-Regular.ttf")
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
     * Handle resend verification
     * @param view
     */
    public void buttonClickResendVerify(View view) {
        DialogFragment fragment = new ResendVerifyDialog();
        fragment.show(getSupportFragmentManager(), "resend_verify");
    }

    /**
     * Handle Signup
     * @param view
     */
    public void buttonClickSignup(View view) {
        Intent intent  = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    /**
     * Handle login click
     * @param view
     */
    public void buttonClickLogin(View view) {
        if (validate()) {
            if (!State.isConnected(this)) {
                Toast toast = Toast.makeText(this, R.string.noInternetText, Toast.LENGTH_LONG);
                toast.show();
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
                        try {
                            JSONObject response = (JSONObject) result;
                            String token = response.getString("token");
                            AuthService.setToken(token, LoginActivity.this);
                            launchScheduleActivity();
                        } catch (Exception e) {
                            Toast.makeText(LoginActivity.this, R.string.applicationError, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).fail(new FailCallback() {
                    @Override
                    public void onFail(Object result) {
                        if (result instanceof Exception) {
                            ErrorHandler.handleException((Exception) result);
                            Toast.makeText(LoginActivity.this, R.string.applicationError, Toast.LENGTH_SHORT).show();
                        } else {
                            NetworkResponse response = (NetworkResponse) result;
                            switch (response.statusCode) {
                                case 401:
                                    Toast.makeText(LoginActivity.this, "Incorrect Username or password", Toast.LENGTH_SHORT).show();
                                    break;
                                case 403:
                                    Toast.makeText(LoginActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    ErrorHandler.logError(ErrorHandler.NETWORK_ERROR_CODE, "unexpected login response status code", response);
                                    Toast.makeText(LoginActivity.this, R.string.applicationError, Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
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
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
//                Do Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//              Do Nothing
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
//                Do Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//              Do nothing
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
