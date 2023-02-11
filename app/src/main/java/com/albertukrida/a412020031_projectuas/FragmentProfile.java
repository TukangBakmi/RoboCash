package com.albertukrida.a412020031_projectuas;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentProfile extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public FragmentProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentProfile newInstance(String param1, String param2) {
        FragmentProfile fragment = new FragmentProfile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // TODO: Rename and change types of parameters
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private TextView txtProfileName, txtName, txtEmail, txtJoined, txtConnectedGoogle;
    private Button btnChangePassword, btnLogout;
    private ImageView circleProfileImage, squareProfileImage, btnEdit;
    StorageReference storageReference;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, null);

        ProgressDialog pdLoading = new ProgressDialog(getActivity());
        pdLoading.setMessage("\tLoading...");
        pdLoading.setCancelable(false);
        pdLoading.show();

        storageReference = FirebaseStorage.getInstance().getReference();
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        txtProfileName = root.findViewById(R.id.txtProfileName);
        txtName = root.findViewById(R.id.txtName);
        txtEmail = root.findViewById(R.id.txtEmail);
        txtJoined = root.findViewById(R.id.txtJoined);
        txtConnectedGoogle = root.findViewById(R.id.txtConnectedGoogle);

        circleProfileImage = root.findViewById(R.id.circleProfileImage);
        squareProfileImage = root.findViewById(R.id.squareProfileImage);
        btnEdit = root.findViewById(R.id.btnEdit);
        btnChangePassword = root.findViewById(R.id.btnChangePassword);
        btnLogout = root.findViewById(R.id.btnLogout);

        FirebaseUser user = fAuth.getCurrentUser();

        // Mengambil semua data dari seorang user dan menampilkannya
        assert user != null;
        DocumentReference documentReference = fStore.collection("users").document(user.getUid());
        documentReference.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot documentSnapshot = task.getResult();
                if(documentSnapshot != null &&documentSnapshot.exists()){
                    txtProfileName.setText(documentSnapshot.getString("Name"));
                    txtName.setText(documentSnapshot.getString("Name"));
                    txtEmail.setText(documentSnapshot.getString("Email"));
                    txtJoined.setText(String.format("Date Joined: %s", documentSnapshot.getString("Date Joined")));
                    if(Objects.equals(documentSnapshot.getString("EmailSignIn"), "True")){
                        btnChangePassword.setVisibility(View.VISIBLE);
                    }else{
                        btnChangePassword.setVisibility(View.GONE);
                    }
                    if(Objects.equals(documentSnapshot.getString("GoogleSignIn"), "True")){
                        txtConnectedGoogle.setVisibility(View.VISIBLE);
                    }else{
                        txtConnectedGoogle.setVisibility(View.GONE);
                    }
                    // profile picture
                    UpdateProfilePict();
                    pdLoading.dismiss();
                }
            }
        });

        circleProfileImage.setOnClickListener(this::ChangeProfilePicture);
        squareProfileImage.setOnClickListener(this::ChangeProfilePicture);
        btnEdit.setOnClickListener(this::EditProfile);
        btnChangePassword.setOnClickListener(this::ShowOldPasswordDialog);
        btnLogout.setOnClickListener(view -> ((MainActivity)getActivity()).Logout(view));

        // Inflate the layout for this fragment
        return root;
    }




    //===============================================================================================//
    //======================================= Profile Picture =======================================//
    //===============================================================================================//
    public void ChangeProfilePicture(View view){
        final AlertDialog changeProfPictDialog = new AlertDialog.Builder(view.getContext())
                .setView(R.layout.dialog_change_profile_picture)
                .setTitle("Change Profile Picture?")
                .create();
        changeProfPictDialog.show();

        assert fAuth.getCurrentUser() != null;

        ImageView profileIcon1 = changeProfPictDialog.findViewById(R.id.profileIcon1);
        ImageView profileIcon2 = changeProfPictDialog.findViewById(R.id.profileIcon2);
        ImageView profileIcon3 = changeProfPictDialog.findViewById(R.id.profileIcon3);
        ImageView profileIcon4 = changeProfPictDialog.findViewById(R.id.profileIcon4);
        ImageView profileIcon5 = changeProfPictDialog.findViewById(R.id.profileIcon5);
        ImageView profileIcon6 = changeProfPictDialog.findViewById(R.id.profileIcon6);
        ImageView profileIcon7 = changeProfPictDialog.findViewById(R.id.profileIcon7);
        ImageView profileIcon8 = changeProfPictDialog.findViewById(R.id.profileIcon8);
        ImageView profileIcon9 = changeProfPictDialog.findViewById(R.id.profileIcon9);

        TextView btnNext = changeProfPictDialog.findViewById(R.id.btnNext);
        TextView btnCancel = changeProfPictDialog.findViewById(R.id.btnCancel);

        profileIcon1.setOnClickListener(view13 -> {
            fStore.collection("users")
                    .document(fAuth.getCurrentUser().getUid())
                    .update("Profile Picture", "profile_icon1.png");
            UpdateProfilePict();
            changeProfPictDialog.dismiss();
        });
        profileIcon2.setOnClickListener(view13 -> {
            fStore.collection("users")
                    .document(fAuth.getCurrentUser().getUid())
                    .update("Profile Picture", "profile_icon2.png");
            UpdateProfilePict();
            changeProfPictDialog.dismiss();
        });
        profileIcon3.setOnClickListener(view13 -> {
            fStore.collection("users")
                    .document(fAuth.getCurrentUser().getUid())
                    .update("Profile Picture", "profile_icon3.png");
            UpdateProfilePict();
            changeProfPictDialog.dismiss();
        });
        profileIcon4.setOnClickListener(view13 -> {
            fStore.collection("users")
                    .document(fAuth.getCurrentUser().getUid())
                    .update("Profile Picture", "profile_icon4.png");
            UpdateProfilePict();
            changeProfPictDialog.dismiss();
        });
        profileIcon5.setOnClickListener(view13 -> {
            fStore.collection("users")
                    .document(fAuth.getCurrentUser().getUid())
                    .update("Profile Picture", "profile_icon5.png");
            UpdateProfilePict();
            changeProfPictDialog.dismiss();
        });
        profileIcon6.setOnClickListener(view13 -> {
            fStore.collection("users")
                    .document(fAuth.getCurrentUser().getUid())
                    .update("Profile Picture", "profile_icon6.png");
            UpdateProfilePict();
            changeProfPictDialog.dismiss();
        });
        profileIcon7.setOnClickListener(view13 -> {
            fStore.collection("users")
                    .document(fAuth.getCurrentUser().getUid())
                    .update("Profile Picture", "profile_icon7.png");
            UpdateProfilePict();
            changeProfPictDialog.dismiss();
        });
        profileIcon8.setOnClickListener(view13 -> {
            fStore.collection("users")
                    .document(fAuth.getCurrentUser().getUid())
                    .update("Profile Picture", "profile_icon8.png");
            UpdateProfilePict();
            changeProfPictDialog.dismiss();
        });
        profileIcon9.setOnClickListener(view13 -> {
            fStore.collection("users")
                    .document(fAuth.getCurrentUser().getUid())
                    .update("Profile Picture", "profile_icon9.png");
            UpdateProfilePict();
            changeProfPictDialog.dismiss();
        });
        btnNext.setOnClickListener(view12 -> {
            ((MainActivity)getActivity()).CustomProfilePicture();
            changeProfPictDialog.dismiss();
        });
        btnCancel.setOnClickListener(view1 -> changeProfPictDialog.dismiss());
    }

    public void UpdateProfilePict(){
        assert fAuth.getCurrentUser() != null;
        DocumentReference documentReference = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    if (Objects.equals(documentSnapshot.getString("Profile Picture"), "profile_icon1.png")) {
                        circleProfileImage.setImageResource(R.mipmap.profile_icon1);
                        squareProfileImage.setImageResource(R.mipmap.profile_icon1);
                    } else if (Objects.equals(documentSnapshot.getString("Profile Picture"), "profile_icon2.png")) {
                        circleProfileImage.setImageResource(R.mipmap.profile_icon2);
                        squareProfileImage.setImageResource(R.mipmap.profile_icon2);
                    } else if (Objects.equals(documentSnapshot.getString("Profile Picture"), "profile_icon3.png")) {
                        circleProfileImage.setImageResource(R.mipmap.profile_icon3);
                        squareProfileImage.setImageResource(R.mipmap.profile_icon3);
                    } else if (Objects.equals(documentSnapshot.getString("Profile Picture"), "profile_icon4.png")) {
                        circleProfileImage.setImageResource(R.mipmap.profile_icon4);
                        squareProfileImage.setImageResource(R.mipmap.profile_icon4);
                    } else if (Objects.equals(documentSnapshot.getString("Profile Picture"), "profile_icon5.png")) {
                        circleProfileImage.setImageResource(R.mipmap.profile_icon5);
                        squareProfileImage.setImageResource(R.mipmap.profile_icon5);
                    } else if (Objects.equals(documentSnapshot.getString("Profile Picture"), "profile_icon6.png")) {
                        circleProfileImage.setImageResource(R.mipmap.profile_icon6);
                        squareProfileImage.setImageResource(R.mipmap.profile_icon6);
                    } else if (Objects.equals(documentSnapshot.getString("Profile Picture"), "profile_icon7.png")) {
                        circleProfileImage.setImageResource(R.mipmap.profile_icon7);
                        squareProfileImage.setImageResource(R.mipmap.profile_icon7);
                    } else if (Objects.equals(documentSnapshot.getString("Profile Picture"), "profile_icon8.png")) {
                        circleProfileImage.setImageResource(R.mipmap.profile_icon8);
                        squareProfileImage.setImageResource(R.mipmap.profile_icon8);
                    } else if (Objects.equals(documentSnapshot.getString("Profile Picture"), "profile_icon9.png")) {
                        circleProfileImage.setImageResource(R.mipmap.profile_icon9);
                        squareProfileImage.setImageResource(R.mipmap.profile_icon9);
                    } else {
                        StorageReference profilePictureRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
                        profilePictureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri).into(circleProfileImage);
                                Picasso.get().load(uri).into(squareProfileImage);
                            }
                        });
                    }
                }
            }
        });
    }




    //===============================================================================================//
    //====================================== Edit Display Name ======================================//
    //===============================================================================================//
    public void EditProfile(View view){
        final AlertDialog changeDispNameDial = new AlertDialog.Builder(view.getContext())
                .setView(R.layout.dialog_edit_profile)
                .setTitle("Change Display Name?")
                .create();
        changeDispNameDial.show();

        assert fAuth.getCurrentUser() != null;

        EditText ChangeName = changeDispNameDial.findViewById(R.id.txtChangeName);
        TextView btnNext = changeDispNameDial.findViewById(R.id.btnNext);
        TextView btnCancel = changeDispNameDial.findViewById(R.id.btnCancel);

        btnNext.setOnClickListener(view12 -> {
            String txtChangeName = ChangeName.getText().toString();
            if(txtChangeName.length() == 0){
                ChangeName.setError("Username is required");
                ChangeName.requestFocus();
            }else if(txtChangeName.length() < 4){
                ChangeName.setError("Username is too short");
                ChangeName.requestFocus();
            }else if(txtChangeName.length() > 32){
                ChangeName.setError("Username is too long");
                ChangeName.requestFocus();
            }else {
                txtProfileName.setText(txtChangeName);
                txtName.setText(txtChangeName);
                fStore.collection("users")
                    .document(fAuth.getCurrentUser().getUid())
                    .update("Name", txtChangeName);
                Toast.makeText(getContext(),
                    "Your name has been successfully changed", Toast.LENGTH_LONG).show();
                changeDispNameDial.dismiss();
            }
        });
        btnCancel.setOnClickListener(view1 -> changeDispNameDial.dismiss());
    }




    //===============================================================================================//
    //======================================= Change Password =======================================//
    //===============================================================================================//
    public void ShowOldPasswordDialog(View view){
        final AlertDialog oldPasswordDialog = new AlertDialog.Builder(view.getContext())
                .setView(R.layout.dialog_change_password_old)
                .setTitle("Change Password?")
                .create();
        oldPasswordDialog.show();

        EditText oldPassword = oldPasswordDialog.findViewById(R.id.txtOldPassword);
        TextView btnNext = oldPasswordDialog.findViewById(R.id.btnNext);
        TextView btnCancel = oldPasswordDialog.findViewById(R.id.btnCancel);

        btnNext.setOnClickListener(view12 -> {
            String txtOldPassword = oldPassword.getText().toString();
            if (txtOldPassword.length() == 0) {
                oldPassword.setError("Enter your old password");
                oldPassword.requestFocus();
            } else {
                // Ambil password dari database
                DocumentReference documentReference = fStore.collection("users").document(Objects.requireNonNull(fAuth.getCurrentUser()).getUid());
                documentReference.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            //di cek sama/beda
                            if (Objects.equals(documentSnapshot.getString("Password"), txtOldPassword)) {
                                oldPasswordDialog.dismiss();
                                ShowNewPasswordDialog(view12);
                            } else {
                                oldPassword.setError("Error! Wrong password");
                                oldPassword.requestFocus();
                            }
                        }
                    }
                });
            }
        });
        btnCancel.setOnClickListener(view1 -> oldPasswordDialog.dismiss());
    }

    private void ShowNewPasswordDialog(View view){
        final AlertDialog newPasswordDialog = new AlertDialog.Builder(view.getContext())
                .setView(R.layout.dialog_change_password_new)
                .setTitle("Change Password?")
                .create();
        newPasswordDialog.show();

        EditText newPassword = newPasswordDialog.findViewById(R.id.txtNewPassword);
        EditText confirmNewPassword = newPasswordDialog.findViewById(R.id.txtConfirmNewPassword);
        TextView btnNext = newPasswordDialog.findViewById(R.id.btnNext);
        TextView btnCancel = newPasswordDialog.findViewById(R.id.btnCancel);

        btnNext.setOnClickListener(view1 -> {
            String txtNewPassword = newPassword.getText().toString();
            String txtConfirmNewPassword = confirmNewPassword.getText().toString();
            if(txtNewPassword.length() == 0){
                newPassword.setError("Enter your new password");
                newPassword.requestFocus();
            }else if(txtConfirmNewPassword.length() == 0){
                confirmNewPassword.setError("Confirm your new password");
                confirmNewPassword.requestFocus();
            }else if(!txtNewPassword.equals(txtConfirmNewPassword)) {
                confirmNewPassword.setError("Different password, please try again!");
                confirmNewPassword.requestFocus();
            }else{
                Objects.requireNonNull(fAuth.getCurrentUser()).updatePassword(txtNewPassword).addOnSuccessListener(unused -> {
                    fStore.collection("users")
                            .document(fAuth.getCurrentUser().getUid())
                            .update("Password", txtNewPassword);
                    Toast.makeText(getContext(),
                            "Your password has been successfully changed", Toast.LENGTH_LONG).show();
                }).addOnFailureListener(e -> Toast.makeText(getContext(),
                        "Error! " + e.getMessage(), Toast.LENGTH_LONG).show());
                newPasswordDialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(view12 -> newPasswordDialog.dismiss());
    }
}