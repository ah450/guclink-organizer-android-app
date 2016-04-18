package in.guclink.www.organizer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.guclink.www.organizer.network.AuthService;
import in.guclink.www.organizer.network.NetworkResponseUtility;
import in.guclink.www.organizer.util.ErrorHandler;
import in.guclink.www.organizer.util.State;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class SignupActivity extends AppCompatActivity {
    @Bind(R.id.input_email) EditText email;
    @Bind(R.id.input_password) EditText password;
    @Bind(R.id.input_name) EditText name;
    @Bind(R.id.btn_create) Button createButton;

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
        setContentView(R.layout.activity_signup);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Montserrat-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        ButterKnife.bind(this);
        addEmailTextWatcher();
        addPasswordTextWatcher();
        addNameTextWatcher();
    }


    public void clickCreate(View button) {
        if (validate()) {
            if (!State.isConnected(this)) {
                Toast toast = Toast.makeText(this, R.string.noInternetText, Toast.LENGTH_LONG);
                toast.show();
                return;
            }
            final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_PopupOverlay);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.signupProgressDialogMessage));
            progressDialog.show();
            try {

                Promise signupPromise = AuthService.createAccount(name.getText().toString(),
                        email.getText().toString(), password.getText().toString(), this);
                signupPromise.done(new DoneCallback() {
                    @Override
                    public void onDone(Object result) {
                        JSONObject response = (JSONObject) result;
                        goToWelcomeActivity();

                    }
                }).fail(new FailCallback() {
                    @Override
                    public void onFail(Object result) {
                        if (result instanceof Exception) {
                            ErrorHandler.handleException((Exception) result);
                            Toast.makeText(SignupActivity.this, R.string.applicationError, Toast.LENGTH_SHORT).show();
                        } else {
                            NetworkResponse response = (NetworkResponse) result;
                            switch(response.statusCode) {
                                case 422:
                                    String error = NetworkResponseUtility.parseModelMessages(response.data);
                                    if (error == null) {
                                        ErrorHandler.logError(ErrorHandler.NETWORK_ERROR_PARSE_422, "signup", response);
                                        Toast.makeText(SignupActivity.this, R.string.applicationError, Toast.LENGTH_SHORT).show();
                                    }
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                                    builder.setMessage(error)
                                            .setTitle(R.string.signup_error_dialog_title)
                                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
//                                                    Do nothing
                                                }
                                            });
                                     AlertDialog dialog = builder.create();
                                    dialog.show();
                                    break;
                                default:
                                    ErrorHandler.logError(ErrorHandler.NETWORK_ERROR_CODE, "unexpected signup response status code", response);
                                    Toast.makeText(SignupActivity.this, R.string.applicationError, Toast.LENGTH_SHORT).show();
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
            } catch (Exception e) {
                progressDialog.dismiss();
                ErrorHandler.handleException(e);
            }
        }
    }

    private void goToWelcomeActivity() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
    }

    private boolean validate() {
        return validatePassword() && validateEmail() && validateName();
    }

    private boolean validateEmail() {
        String emailText = email.getText().toString();
        return emailText.matches(AuthService.GUC_EMAIL_PATTERN);
    }

    private boolean validatePassword() {
        String passwordText = password.getText().toString();
        return !passwordText.isEmpty();
    }

    private boolean validateName() {
        String nameText = name.getText().toString();
        return !nameText.isEmpty();
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
                    email.setError(getString(R.string.email_signup_error));
                }

                setCreateButtonEnabled();
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
                    password.setError(getString(R.string.password_signup_error));
                }
                setCreateButtonEnabled();
            }
        });
    }

    private void addNameTextWatcher() {
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//              Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(validateName()) {
                    name.setError(null);
                } else {
                    name.setError(getString(R.string.name_signup_error));
                }
                setCreateButtonEnabled();
            }
        });
    }


    private void setCreateButtonEnabled() {
        if(validate()) {
            createButton.setEnabled(true);
        } else {
            createButton.setEnabled(false);
        }
    }

}
