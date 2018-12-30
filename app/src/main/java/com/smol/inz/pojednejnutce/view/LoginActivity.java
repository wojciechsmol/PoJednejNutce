package com.smol.inz.pojednejnutce.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.smol.inz.pojednejnutce.R;
import com.smol.inz.pojednejnutce.utils.DialogUtils;
import com.smol.inz.pojednejnutce.utils.FormValidationUtils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnLogin;
    EditText mEditEmail, mEditPassword;
    TextView btnSignup, btnForgotPass;

    RelativeLayout activity_login;

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //View
        btnLogin = findViewById(R.id.login_btn_login);
        mEditEmail = findViewById(R.id.login_email);
        mEditPassword = findViewById(R.id.login_password);
        btnSignup = findViewById(R.id.login_btn_signup);
        btnForgotPass = findViewById(R.id.login_btn_forgot_password);
        activity_login = findViewById(R.id.activity_login);

        btnSignup.setOnClickListener(this);
        btnForgotPass.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        //Init Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();


        //Check already session , if ok-> DashBoard
        if(mFirebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        if (firebaseUser != null)
            startActivity(new Intent(this, HomeActivity.class));
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.login_btn_forgot_password)
        {
            startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
            finish();
        }
        else if(view.getId() == R.id.login_btn_signup)
        {
            startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            finish();
        }
        else if(view.getId() == R.id.login_btn_login)
        {
            FormValidationUtils.clearErrors(mEditEmail, mEditPassword);

            if (FormValidationUtils.isBlank(mEditEmail)) {
                FormValidationUtils.setError(null, mEditEmail, "Please enter email");
                return;
            }

            if (!FormValidationUtils.isEmailValid(mEditEmail)) {
                FormValidationUtils.setError(null, mEditEmail, "Please enter valid email");
                return;
            }

            if (TextUtils.isEmpty(mEditPassword.getText())) {
                FormValidationUtils.setError(null, mEditPassword, "Please enter password");
                return;
            }

            loginUser(mEditEmail.getText().toString(), mEditPassword.getText().toString());
        }
    }

    private void loginUser(String email, final String password) {
        closeKeyboard();
        DialogUtils.showProgressDialog(this, "", getString(R.string.sign_in), false);
        mFirebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        DialogUtils.dismissProgressDialog();

                        if(task.isSuccessful())
                        {
                                Snackbar.make(activity_login,"Login Successful",Snackbar.LENGTH_SHORT).show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                        finish();
                                    }
                                }, 1000);
                        }
                        else {
                            Snackbar.make(activity_login, task.getException().getMessage(), Toast.LENGTH_SHORT).show();                        }
                    }
                });
    }

    //TODO TAKE THIS SOMEWHERE ELSE!
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}