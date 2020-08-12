package com.example.mywhatsappapllication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private RecyclerView findFriendsRecyclerList;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        findFriendsRecyclerList = findViewById(R.id.find_friend_recyclear_view);
        findFriendsRecyclerList.setLayoutManager(new LinearLayoutManager(this));


        mToolBar = findViewById(R.id.find_friends_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(userRef, Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull Contacts model) {
                        holder.userName.setText(model.getName());
                        holder.userStatus.setText(model.getStatus());
                        //For loading image
                        Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.profileImage);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String visit_user_id = getRef(position).getKey();
                                Log.d("FindFriendActivity", visit_user_id);
                                Intent profileIntent = new Intent(FindFriendActivity.this, ProfileActivity.class);
                                profileIntent.putExtra("visit_user_id", visit_user_id);
                                startActivity(profileIntent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display_layout, parent, false);
                        FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);
                        return viewHolder;

                    }
                };

        findFriendsRecyclerList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class FindFriendViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userStatus;
        CircleImageView profileImage;

        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.ff_user_profile_name);
            userStatus = itemView.findViewById(R.id.ff_user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
        }
    }
}