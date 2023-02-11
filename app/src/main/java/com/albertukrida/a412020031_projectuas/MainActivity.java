package com.albertukrida.a412020031_projectuas;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.albertukrida.a412020031_projectuas.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    ProgressDialog pdLoading;
    StorageReference storageReference;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    ImageView squareProfileImage, circleProfileImage;

    public static boolean home = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if(!home){
            replaceFragment(new FragmentHome());
            home = true;
        }
        binding.navigationView.setOnItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.home:
                    replaceFragment(new FragmentHome());
                    break;
                case R.id.transactions:
                    replaceFragment(new FragmentTransactions());
                    break;
                case R.id.about:
                    replaceFragment(new FragmentAbout());
                    break;
                case R.id.profile:
                    replaceFragment(new FragmentProfile());
                    break;
            }
            return true;
        });

        Objects.requireNonNull(getSupportActionBar()).hide();

        pdLoading = new ProgressDialog(MainActivity.this);
        storageReference = FirebaseStorage.getInstance().getReference();
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
    }

    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransactions = fragmentManager.beginTransaction();
        fragmentTransactions.replace(R.id.frameLayout,fragment);
        fragmentTransactions.commit();
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                assert data != null;
                Uri imageUri = data.getData();
                uploadImageToFirebase(imageUri);
            }
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exit Application?")
                .setMessage("Are you sure you want to close this application?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", null);
        AlertDialog dialog = builder.create();
        dialog.show();
        ImageView imageView = dialog.findViewById(android.R.id.icon);
        if (imageView != null)
            imageView.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
    }




    //========================================= Home =========================================//
    public void ShowAllTransactions(){
        binding.navigationView.setSelectedItemId(R.id.transactions);
    }
    public void goToProfile(){
        binding.navigationView.setSelectedItemId(R.id.profile);
    }




    //========================================= Profile =========================================//
    public void CustomProfilePicture(){
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGalleryIntent, 1000);
    }

    public void uploadImageToFirebase(Uri imageUri){
        pdLoading.setMessage("\tLoading...");
        pdLoading.setCancelable(false);
        pdLoading.show();
        fStore.collection("users")
                .document(Objects.requireNonNull(fAuth.getCurrentUser()).getUid())
                .update("Profile Picture", "custom");
        assert fAuth.getCurrentUser() != null;
        StorageReference fileRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        circleProfileImage = findViewById(R.id.circleProfileImage);
                        squareProfileImage = findViewById(R.id.squareProfileImage);
                        Picasso.get().load(uri).into(circleProfileImage);
                        Picasso.get().load(uri).into(squareProfileImage);
                        pdLoading.dismiss();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),
                        "Error! " + e.getMessage(), Toast.LENGTH_LONG).show();
                pdLoading.dismiss();
            }
        });
    }

    public void Logout (View view){
        fAuth.signOut();
        GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build()).signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),
                        "Error! " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}