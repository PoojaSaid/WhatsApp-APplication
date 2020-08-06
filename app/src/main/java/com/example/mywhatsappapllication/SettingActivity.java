package com.example.mywhatsappapllication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {
    private Button updateAccountSetting;
    private EditText userName, userStatus;
    private CircleImageView userProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        InitializeFields();
    }

    private void InitializeFields() {
        updateAccountSetting = findViewById(R.id.update_setting_button);
        userName = findViewById(R.id.set_profile_name);
        userStatus = findViewById(R.id.set_profile_status);
        userName = findViewById(R.id.set_profile_image);
    }
}