package com.example.mywhatsappapllication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestsFragment extends Fragment {
    private View requestFragmentView;
    private RecyclerView myRequestList;
    private String currentUserId;
    private FirebaseAuth mAuth;

    private DatabaseReference chatRequestRef;


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        requestFragmentView =  inflater.inflate(R.layout.fragment_requests, container, false);

        myRequestList = requestFragmentView.findViewById(R.id.chat_requests_list);

        myRequestList.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();

        currentUserId = mAuth.getCurrentUser().getUid();

        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");



        return requestFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<com.example.mywhatsappapllication.Contacts> options = new FirebaseRecyclerOptions.Builder<com.example.mywhatsappapllication.Contacts>()
                .setQuery(chatRequestRef.child(currentUserId), com.example.mywhatsappapllication.Contacts.class)
                .build();

        FirebaseRecyclerAdapter<com.example.mywhatsappapllication.Contacts, RequestViewHolder> adapter = new FirebaseRecyclerAdapter<com.example.mywhatsappapllication.Contacts, RequestViewHolder>() {
            @Override
            protected void onBindViewHolder(@NonNull RequestViewHolder holder, int position, @NonNull com.example.mywhatsappapllication.Contacts model) {

            }

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }
        };



    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder{

        private TextView userName, userProfileStatus;
        private CircleImageView userProfileImage;
        private Button acceptBtn, cancelBtn;



        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.ff_user_profile_name);
            userProfileStatus = itemView.findViewById(R.id.ff_user_status);
            userProfileImage = itemView.findViewById(R.id.users_profile_image);
            acceptBtn = itemView.findViewById(R.id.req_accept_btn);
            cancelBtn = itemView.findViewById(R.id.req_cancel_btn);
        }
    }
}