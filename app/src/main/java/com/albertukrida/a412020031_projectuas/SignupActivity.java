package com.albertukrida.a412020031_projectuas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

public class SignupActivity extends AppCompatActivity {

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    public static final int GOOGLE_SIGN_IN_CODE = 10005;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    private EditText editTextFullname, editTextEmail, editTextPassword, editTextConfirmPassword;
    ProgressDialog pdLoading;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        MainActivity.home = false;

        Objects.requireNonNull(getSupportActionBar()).hide();

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        pdLoading = new ProgressDialog(SignupActivity.this);

        editTextFullname = findViewById(R.id.txtFullname);
        editTextEmail = findViewById(R.id.txtEmail);
        editTextPassword = findViewById(R.id.txtPassword);
        editTextConfirmPassword = findViewById(R.id.txtConfirmPassword);
    }


    //================================== Go to Login Activity ==================================//
    public void LoginPage(View view){
        startActivity(new Intent(SignupActivity.this,LoginActivity.class));
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();
    }

    //===================================== Register Email =====================================//
    public void SubmitSignup(View view){
        String txtUsername = editTextFullname.getText().toString();
        String txtEmail = editTextEmail.getText().toString();
        String txtPassword = editTextPassword.getText().toString();
        String txtConfirmPassword = editTextConfirmPassword.getText().toString();

        if(txtUsername.length() == 0){
            editTextFullname.setError("Username is required");
            editTextFullname.requestFocus();
        }else if(txtUsername.length() < 4){
            editTextFullname.setError("Username is too short");
            editTextFullname.requestFocus();
        }else if(txtUsername.length() > 32){
            editTextFullname.setError("Username is too long");
            editTextFullname.requestFocus();
        }

        else if(txtEmail.length() == 0){
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
        }else if(!Patterns.EMAIL_ADDRESS.matcher(txtEmail).matches()){
            editTextEmail.setError("Valid email is required");
            editTextEmail.requestFocus();
        }

        else if(txtPassword.length() == 0){
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
        } else if(txtPassword.length() < 6){
            editTextPassword.setError("Password too weak");
            editTextPassword.requestFocus();
        }

        else if(txtConfirmPassword.length() == 0){
            editTextConfirmPassword.setError("Password confirmation is required");
            editTextConfirmPassword.requestFocus();
        }else if(!txtPassword.equals(txtConfirmPassword)){
            editTextConfirmPassword.setError("Different password, please try again!");
            editTextConfirmPassword.requestFocus();
        }

        else{
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
            registerUser(txtUsername, txtEmail, txtPassword);
        }
    }

    private void registerUser(String username, String email, String password){
        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignupActivity.this, task -> {
            if(task.isSuccessful()) {
                Objects.requireNonNull(fAuth.getCurrentUser()).sendEmailVerification().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        // Get current date
                        Date dateJoined = Calendar.getInstance().getTime();
                        SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                        String formattedDate = df.format(dateJoined);
                        // Add to fireStore
                        Map<String, Object> user = new HashMap<>();
                        user.put("Profile Picture", "profile_icon1.png");
                        user.put("Name", username);
                        user.put("Email", email);
                        user.put("Password", password);
                        user.put("EmailSignIn", "True");
                        user.put("Date Joined", formattedDate);
                        userID = fAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = fStore.collection("users").document(userID);
                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                final AlertDialog verifyEmailDialog = new AlertDialog.Builder(SignupActivity.this)
                                        .setView(R.layout.dialog_signup)
                                        .setTitle("Verify Your Email")
                                        .create();
                                verifyEmailDialog.setCancelable(false);
                                verifyEmailDialog.setCanceledOnTouchOutside(false);
                                pdLoading.dismiss();
                                verifyEmailDialog.show();
                                TextView btnNext = verifyEmailDialog.findViewById(R.id.btnNext);
                                btnNext.setOnClickListener(view -> {
                                    verifyEmailDialog.dismiss();
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                                    finish();
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pdLoading.dismiss();
                                Toast.makeText(getApplicationContext(),
                                        "Error! " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }else{
                        pdLoading.dismiss();
                        Toast.makeText(SignupActivity.this,
                                "Error! " + Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }else{
                pdLoading.dismiss();
                Toast.makeText(SignupActivity.this,
                        "Error! " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    //====================================== Sign In Google ======================================//
    public void SignInGoogle(View view){
        pdLoading.setMessage("\tLoading...");
        pdLoading.setCancelable(false);
        pdLoading.show();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("328898353463-jjsr04n970kcht6f8s2mvpfbjkem0po8.apps.googleusercontent.com")
                .requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);
        Intent sign = gsc.getSignInIntent();
        startActivityForResult(sign, GOOGLE_SIGN_IN_CODE);
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GOOGLE_SIGN_IN_CODE){
            Task<GoogleSignInAccount> signInTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                Map<String, Object> user = new HashMap<>();
                GoogleSignInAccount signInAcc = signInTask.getResult(ApiException.class);
                AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAcc.getIdToken(), null);
                fAuth.signInWithCredential(authCredential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // Get current date
                        Date dateJoined = Calendar.getInstance().getTime();
                        SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                        String formattedDate = df.format(dateJoined);
                        // Add to fireStore
                        user.put("Profile Picture", "profile_icon1.png");
                        user.put("Name", fAuth.getCurrentUser().getDisplayName());
                        user.put("Email", fAuth.getCurrentUser().getEmail());
                        user.put("GoogleSignIn", "True");
                        user.put("Date Joined", formattedDate);
                        if (authResult.getAdditionalUserInfo().isNewUser()){
                            fStore.collection("users")
                                    .document(fAuth.getCurrentUser().getUid())
                                    .set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getApplicationContext(),
                                                    "Success! Your google account is now connected to RoboCash", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                                            pdLoading.dismiss();
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(),
                                                    "Error! " + e.getMessage(), Toast.LENGTH_LONG).show();
                                            pdLoading.dismiss();
                                        }
                                    });
                        }else {
                            fStore.collection("users")
                                    .document(fAuth.getCurrentUser().getUid())
                                    .update("GoogleSignIn", "True");
                            Toast.makeText(getApplicationContext(),
                                    "Success! Your google account is now connected to RoboCash", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                            pdLoading.dismiss();
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),
                                "Error! " + e.getMessage(), Toast.LENGTH_LONG).show();
                        pdLoading.dismiss();
                    }
                });
            } catch (ApiException e){
                e.printStackTrace();
                pdLoading.dismiss();
            }
        }
    }


    //====================================== Sign In Facebook ======================================//
    public void SignInFacebook(View view){
        Toast.makeText(getApplicationContext(),
                "Sorry, we are still working for this feature.", Toast.LENGTH_SHORT).show();
    }


    //====================================== Sign In Twitter ======================================//
    public void SignInTwitter(View view){
        Toast.makeText(getApplicationContext(),
                "Sorry, we are still working for this feature.", Toast.LENGTH_SHORT).show();
    }
}