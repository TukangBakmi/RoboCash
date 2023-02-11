package com.albertukrida.a412020031_projectuas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

public class LoginActivity extends AppCompatActivity {

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    public static final int GOOGLE_SIGN_IN_CODE = 10005;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    private TextView txtResendEmail;
    private EditText editTextEmail, editTextPassword;
    ProgressDialog pdLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MainActivity.home = false;

        Objects.requireNonNull(getSupportActionBar()).hide();

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        if(fAuth.getCurrentUser() != null && fAuth.getCurrentUser().isEmailVerified()){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            finish();
        }

        pdLoading = new ProgressDialog(LoginActivity.this);

        editTextEmail = findViewById(R.id.txtEmail);
        editTextPassword = findViewById(R.id.txtPassword);

        txtResendEmail = findViewById(R.id.textView5);
        txtResendEmail.setVisibility(View.GONE);
    }


    //================================ Send Request Change Pass ================================//
    public void ForgotPassword(View view) {
        final EditText resetMail = new EditText(view.getContext());
        resetMail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
        passwordResetDialog.setTitle("Reset Password?");
        passwordResetDialog.setMessage("Enter your email to receive a reset link");
        passwordResetDialog.setView(resetMail);

        passwordResetDialog.setPositiveButton("Yes", (dialogInterface, i) -> {
            String mail = resetMail.getText().toString();
            if(mail.length() == 0){
                Toast.makeText(getApplicationContext(),
                        "Please enter your email", Toast.LENGTH_SHORT).show();
            }else{
                pdLoading.setMessage("\tLoading...");
                pdLoading.setCancelable(false);
                pdLoading.show();
                fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(unused -> {
                    pdLoading.dismiss();
                    Toast.makeText(getApplicationContext(),
                            "reset link has been sent to your email!", Toast.LENGTH_LONG).show();
                }).addOnFailureListener(e -> {
                    pdLoading.dismiss();
                    Toast.makeText(getApplicationContext(),
                            "Error! " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });

        passwordResetDialog.setNegativeButton("No", (dialogInterface, i) -> {
            // Close dialog
        });

        passwordResetDialog.show();
    }


    //================================ Resend Email Verification ================================//
    public void ResendEmail(View view){
        pdLoading.setMessage("\tLoading...");
        pdLoading.setCancelable(false);
        pdLoading.show();
        Objects.requireNonNull(fAuth.getCurrentUser()).sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pdLoading.dismiss();
                txtResendEmail.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),
                            "Verification email has been sent to " + fAuth.getCurrentUser().getEmail() + "!",
                            Toast.LENGTH_LONG).show();
                    editTextEmail.setText("");
                    editTextPassword.setText("");
                }else{
                    Toast.makeText(LoginActivity.this,
                            "Error! " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    //================================== Go to SignUp Activity ==================================//
    public void SignupPage(View view) {
        startActivity(new Intent(LoginActivity.this,SignupActivity.class));
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();
    }


    //====================================== Sign In Email ======================================//
    public void SubmitLogin(View view){
        txtResendEmail.setVisibility(View.GONE);
        String txtEmail = editTextEmail.getText().toString();
        String txtPassword = editTextPassword.getText().toString();

        if(txtEmail.length() == 0){
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
        }else if(txtPassword.length() == 0){
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
        }else{
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
            checkUser(txtEmail, txtPassword);
        }
    }
    private void checkUser(String email, String password){
        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            pdLoading.dismiss();
            if(task.isSuccessful()){
                if(Objects.requireNonNull(fAuth.getCurrentUser()).isEmailVerified()){
                    fStore.collection("users")
                            .document(fAuth.getCurrentUser().getUid())
                            .update("Password", password);
                    Toast.makeText(LoginActivity.this,
                            "Logged in Successfully", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                    finish();
                }else{
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            txtResendEmail.setVisibility(View.VISIBLE);
                        }
                    },5000);
                    Toast.makeText(LoginActivity.this,
                            "Please verifiy your email address first! Check your spam or trash folder",
                            Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(LoginActivity.this,
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