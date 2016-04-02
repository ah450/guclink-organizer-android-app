package in.guclink.www.organizer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.EditText;


import butterknife.Bind;
import butterknife.ButterKnife;
import in.guclink.www.organizer.network.AuthService;

/**
 * Created by ahi on 02/04/16.
 */
public class ResendVerifyDialog  extends DialogFragment {
    @Bind(R.id.verifyEmailInput) EditText email;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_resend_verify, null)
        ).setPositiveButton(R.string.resend, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (validateEmail()) {
                    dialog.dismiss();
                    AuthService.resendVerify(getEmailText(), getContext());
                } else {
                    addEmailError();
                }

            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        Dialog dialog = builder.create();
        ButterKnife.bind(dialog);
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(validateEmail()) {
                    clearEmailError();
                } else {
                    addEmailError();
                }
            }
        });
        return dialog;
    }

    private void clearEmailError() {
        email.setError(null);
    }
    private void addEmailError() {
        email.setError(getString(R.string.login_email_error));
    }

    private String getEmailText() {
        return email.getText().toString();
    }

    private boolean validateEmail() {
        String emailText = email.getText().toString();
        return emailText.matches(AuthService.GUC_EMAIL_PATTERN);
    }
}
