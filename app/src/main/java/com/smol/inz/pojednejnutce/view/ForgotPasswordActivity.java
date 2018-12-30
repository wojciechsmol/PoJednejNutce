package com.smol.inz.pojednejnutce.view;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.smol.inz.pojednejnutce.R;
import com.smol.inz.pojednejnutce.utils.DialogUtils;
import com.smol.inz.pojednejnutce.utils.FormValidationUtils;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEditEmail;
    private Button btnResetPass;
    private TextView btnBack;
    private RelativeLayout activity_forgot;

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //View
        mEditEmail = (EditText)findViewById(R.id.forgot_email);
        btnResetPass = (Button)findViewById(R.id.forgot_btn_reset);
        btnBack = (TextView)findViewById(R.id.forgot_btn_back);
        activity_forgot = (RelativeLayout)findViewById(R.id.activity_forgot_password);

        btnResetPass.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        //Init Firebase
        mFirebaseAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.forgot_btn_back)
        {
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }
        else  if(view.getId() == R.id.forgot_btn_reset)
        {
            if (FormValidationUtils.isBlank(mEditEmail)) {
                FormValidationUtils.setError(null, mEditEmail, "Please enter email");
                return;
            }

            if (!FormValidationUtils.isEmailValid(mEditEmail)) {
                FormValidationUtils.setError(null, mEditEmail, "Please enter valid email");
                return;
            }

            resetPassword(mEditEmail.getText().toString());
        }
    }

    private void resetPassword(final String email) {
        DialogUtils.showProgressDialog(this, "", "Please wait...", false);

        mFirebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        DialogUtils.dismissProgressDialog();
                        if(task.isSuccessful())
                        {
                            Snackbar.make(activity_forgot,"We have sent password to email: "+email,Snackbar.LENGTH_SHORT).show();
                        }
                        else{
                            Snackbar.make(activity_forgot, task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}