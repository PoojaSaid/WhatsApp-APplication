package com.example.mywhatsappapllication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {
    private Button updateAccountSetting;
    private EditText userName, userStatus;
    private CircleImageView userProfileImage;
    private String currentuserId;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private static final int galleryPick = 1;
    private StorageReference userProfileImagesRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mAuth = FirebaseAuth.getInstance();
        currentuserId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        userProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        InitializeFields();
        userName.setVisibility(View.INVISIBLE);

        updateAccountSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSettings();
            }
        });
        RetrieveUserInfo();

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallaryIntent = new Intent();
                gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                gallaryIntent.setType("image/*");
                startActivityForResult(gallaryIntent, galleryPick);
            }
        });
    }


    private void InitializeFields() {
        updateAccountSetting = findViewById(R.id.update_setting_button);
        userName = findViewById(R.id.set_profile_name);
        userStatus = findViewById(R.id.set_profile_status);
        userProfileImage = findViewById(R.id.set_profile_image);
        loadingBar = new ProgressDialog(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == galleryPick && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }


        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){
                loadingBar.setTitle("Set Profile Image");
                loadingBar.setMessage("Please wait, your profile image is uploading...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                Uri resultUri = result.getUri();
                StorageReference filePath = userProfileImagesRef.child(currentuserId+".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(SettingActivity.this, "Profile Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        final  String downloadUrl = task.getResult().getMetadata().getReference().getDownloadUrl().toString();
                        Log.d("SettingActivityImage",downloadUrl);
                        rootRef.child(currentuserId).child("image")
                                .setValue(downloadUrl)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(SettingActivity.this, "Image saved in database", Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                        }else{
                                            String message = task.getException().toString();
                                            Toast.makeText(SettingActivity.this, "Errror Message: " + message, Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                        }
                                    }
                                });
                    }else {
                        String message = task.getException().toString();
                        Toast.makeText(SettingActivity.this, "Errror Message: " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                    }
                });
            }

        }

        
    }

    private void updateSettings() {
        String setProfileName = userName.getText().toString();
        String setProfileStatus = userStatus.getText().toString();

        //Profile picture functionality is remaining
        if (setProfileName.isEmpty()) {
            Toast.makeText(this, "Please write your user name", Toast.LENGTH_SHORT).show();
        }
        if (setProfileStatus.isEmpty()) {
            Toast.makeText(this, "Please write your status", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uid", currentuserId);
            profileMap.put("name", setProfileName);
            profileMap.put("status", setProfileStatus);
            rootRef.child("Users").child(currentuserId).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sendUserToMainActivity();
                                Toast.makeText(SettingActivity.this, "Profile update successfully........", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = task.getException().toString();
                                Toast.makeText(SettingActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }

    private void RetrieveUserInfo() {
        rootRef.child("Users").child(currentuserId).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if ((snapshot.exists()) && (snapshot.hasChild("name")) && (snapshot.hasChild("image"))) {
                            String retriewProfileName = snapshot.child("name").getValue().toString();
                            String retriewProfileStatus = snapshot.child("status").getValue().toString();
                            String retriewUserProfileImage = snapshot.child("image").getValue().toString();
                            userName.setText(retriewProfileName);
                            userStatus.setText(retriewProfileStatus);
                            
                            //For retrieing image
                            Picasso.get().load(retriewUserProfileImage).into(userProfileImage);

                        } else if ((snapshot.exists()) && (snapshot.hasChild("name"))) {
                            String retriewProfileName = snapshot.child("name").getValue().toString();
                            String retriewProfileStatus = snapshot.child("status").getValue().toString();
                            userName.setText(retriewProfileName);
                            userStatus.setText(retriewProfileStatus);
                        } else {
                            userName.setVisibility(View.VISIBLE);

                            Toast.makeText(SettingActivity.this, "Please set and update your profile information", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }

    private void sendUserToMainActivity() {
        Intent settingToMainIntent = new Intent(SettingActivity.this, MainActivity.class);
        //not go back to login screen after successful login
        settingToMainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingToMainIntent);
        finish();
    }
}