package com.smol.inz.pojednejnutce.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.smol.inz.pojednejnutce.R;
import com.smol.inz.pojednejnutce.model.UserGenreGussedSongsPOJO;
import com.smol.inz.pojednejnutce.model.UserPOJO;
import com.smol.inz.pojednejnutce.utils.DialogUtils;
import com.smol.inz.pojednejnutce.utils.FormValidationUtils;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnSignup;
    TextView btnLogin,btnForgotPass;
    EditText mEditEmail;
    EditText mEditPassword;
    RelativeLayout activity_sign_up;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //View
        btnSignup = (Button)findViewById(R.id.signup_btn_register);
        btnLogin = (TextView)findViewById(R.id.signup_btn_login);
        btnForgotPass = (TextView)findViewById(R.id.signup_btn_forgot_pass);
        mEditEmail = (EditText)findViewById(R.id.signup_email);
        mEditPassword = (EditText)findViewById(R.id.signup_password);
        activity_sign_up = (RelativeLayout)findViewById(R.id.activity_sign_up);

        btnSignup.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnForgotPass.setOnClickListener(this);

        //Init Firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.signup_btn_login){
            startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
            finish();
        }
        else if(view.getId() == R.id.signup_btn_forgot_pass){
            startActivity(new Intent(SignUpActivity.this,ForgotPasswordActivity.class));
            finish();
        }
        else if(view.getId() == R.id.signup_btn_register){

            FormValidationUtils.clearErrors(mEditEmail, mEditPassword);

            if (FormValidationUtils.isBlank(mEditEmail)) {
                mEditEmail.setError("Please enter email");
                return;
            }

            if (!FormValidationUtils.isEmailValid(mEditEmail)) {
                mEditEmail.setError("Please enter valid email");
                return;
            }

            if (TextUtils.isEmpty(mEditPassword.getText())) {
                mEditPassword.setError("Please enter password");
                return;
            }

            signUpUser(mEditEmail.getText().toString(), mEditPassword.getText().toString());
        }
    }

    private void signUpUser(String email, String password) {
        closeKeyboard();
        DialogUtils.showProgressDialog(this, "", getString(R.string.str_creating_account), false);

        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {
                            Snackbar.make(activity_sign_up, task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
                            DialogUtils.dismissProgressDialog();
                        }
                        else{
                            Snackbar.make(activity_sign_up,"Register success! ",Snackbar.LENGTH_SHORT).show();
                            DialogUtils.dismissProgressDialog();

                            //TODO Add progress dialog here!
                            initDatabaseDataForNewUser();
                            //TODO Stop progress dialog here
                            startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                        }
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

    private void initDatabaseDataForNewUser() {
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        addDataUsersTable();
        //addDataAllGenresUserGuessedSongsCount();
        addDataUserSpecificGenreGuessedSongsCount();
    }

    private void addDataUsersTable() {

        UserPOJO mUser = new UserPOJO(mFirebaseUser.getEmail(), 0);
        mDatabaseReference.child("Users").child(mFirebaseUser.getUid()).setValue(mUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("addDataUsersTable: ", "SUCCESSFUL!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("addDataUsersTable", "WENT WRONG!");
                    }
                });

    }

//    private void addDataAllGenresUserGuessedSongsCount() {
//
//        //TODO maybe not static genres but read from somewhere so that it is more flexible?
//        UserGuessedSongsAllGenresPOJO mGuessedSongs = new UserGuessedSongsAllGenresPOJO(0);
//        mDatabaseReference.child("UserGuessedSongsAllGenres").child(mFirebaseUser.getUid()).setValue(mGuessedSongs)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d("initAllGuessedSong: ", "SUCCESSFUL!");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d("initAllGuessedSong", "WENT WRONG!");
//                    }
//                });
//    }

    private void addDataUserSpecificGenreGuessedSongsCount() {

        //TODO make it flexible depending on genres available

        UserGenreGussedSongsPOJO mGuessedSongs = new UserGenreGussedSongsPOJO(0, 0, 0);
        mDatabaseReference.child("popUserGuessedSongsCount").child(mFirebaseUser.getUid()).setValue(mGuessedSongs)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("initPopGuessedSong: ", "SUCCESSFUL!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("initPopGuessedSong", "WENT WRONG!");
                    }
                });

        //TODO ADD OTHER CATEGORIES
    }
}

