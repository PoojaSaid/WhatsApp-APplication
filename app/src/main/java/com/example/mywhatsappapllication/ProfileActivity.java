package com.example.mywhatsappapllication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String receiverUserId, current_state, senderUserId;
    private CircleImageView userProfileImage;
    private TextView userProfileNamePA, userProfileStatus;
    private Button sendMessageButton, declineRequestButton;
    private DatabaseReference userRef, chatRequestRef, contactRef;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
        /*Toast.makeText(this, "User ID: " + receiverUserId, Toast.LENGTH_SHORT).show();
     Log.d("ProfileActivity", receiverUserId);*/
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        contactRef = FirebaseDatabase.getInstance().getReference().child("contacts");
        senderUserId = mAuth.getCurrentUser().getUid();

        userProfileImage = findViewById(R.id.visit_profile_image);
        userProfileNamePA = findViewById(R.id.visit_user_name);
        userProfileStatus = findViewById(R.id.visit_profile_status);
        sendMessageButton = findViewById(R.id.send_message_request_button);
        declineRequestButton = findViewById(R.id.decline_message_request_button);
        current_state = "new";

        retrieveProfileInformation();
    }

    private void retrieveProfileInformation() {
        userRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("image"))) {
                    String userImage = snapshot.child("image").getValue().toString();
                    String userName = snapshot.child("name").getValue().toString();
                    String userStatus = snapshot.child("status").getValue().toString();

                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(userProfileImage);

                    userProfileNamePA.setText(userName);
                    Log.d("ProfileActivity", userImage);
                    userProfileStatus.setText(userStatus);

                    manageChatRequests();

                } else {
                    String userName = snapshot.child("name").getValue().toString();
                    String userStatus = snapshot.child("status").getValue().toString();
                    userProfileNamePA.setText(userName);
                    userProfileStatus.setText(userStatus);
                    manageChatRequests();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void manageChatRequests() {
        chatRequestRef.child(senderUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(receiverUserId)) {
                            String request_type = snapshot.child(receiverUserId).child("request_type").getValue().toString();

                            if (request_type.equals("sent")) {

                                current_state = "request_sent";
                                sendMessageButton.setText("Cancel Chat Request");
                            } else if (request_type.equals("received")) {
                                current_state = "request_received";
                                sendMessageButton.setText("Accept Chat Request");

                                declineRequestButton.setVisibility(View.VISIBLE);
                                declineRequestButton.setEnabled(true);
                                declineRequestButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        chancelChatRequest();
                                    }
                                });
                            }
                        } else {
                            contactRef.child(senderUserId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.hasChild(receiverUserId)) {
                                                current_state = "friends";
                                                sendMessageButton.setText("Remove this contacts");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //if the send and receiver is not one person
        if (!senderUserId.equals(receiverUserId)) {
            sendMessageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendMessageButton.setEnabled(false);

                    //If the user is new, then send them chat request
                    if (current_state.equals("new")) {
                        sendChatRequest();
                    }

                    //if the user want to cancel the request
                    if (current_state.equals("request_sent")) {
                        chancelChatRequest();
                    }

                    if (current_state.equals("request_received")) {
                        acceptChatRequest();
                    }

                    if (current_state.equals("friends")) {
                       removeSpecificContact();
                    }


                }
            });
        } else {
            sendMessageButton.setVisibility(View.VISIBLE);
        }
    }


    private void acceptChatRequest() {

        contactRef.child(senderUserId).child(receiverUserId)
                .child("contacts").setValue("saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            contactRef.child(receiverUserId).child(senderUserId)
                                    .child("contacts").setValue("saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                chatRequestRef.child(senderUserId).child(receiverUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    chatRequestRef.child(receiverUserId).child(senderUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    sendMessageButton.setEnabled(true);
                                                                                    current_state = "friends";
                                                                                    sendMessageButton.setText("Remove contact");
                                                                                    declineRequestButton.setVisibility(View.INVISIBLE);
                                                                                    declineRequestButton.setEnabled(false);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    private void chancelChatRequest() {

        //For cancelling chat request, by changing current_state and button design
        chatRequestRef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            chatRequestRef.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendMessageButton.setEnabled(true);
                                                current_state = "new";
                                                sendMessageButton.setText("Send Message");

                                                declineRequestButton.setVisibility(View.INVISIBLE);
                                                declineRequestButton.setEnabled(false);
                                            }
                                        }
                                    });

                        }
                    }
                });

    }

    private void sendChatRequest() {
        chatRequestRef.child(senderUserId).child(receiverUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            chatRequestRef.child(receiverUserId).child(senderUserId)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendMessageButton.setEnabled(true);
                                                current_state = "request_sent";
                                                sendMessageButton.setText("Cancel Chat Request");
                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    private void removeSpecificContact() {

        contactRef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            contactRef.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendMessageButton.setEnabled(true);
                                                current_state = "new";
                                                sendMessageButton.setText("Send Message");

                                                declineRequestButton.setVisibility(View.INVISIBLE);
                                                declineRequestButton.setEnabled(false);
                                            }
                                        }
                                    });

                        }
                    }
                });


    }
}