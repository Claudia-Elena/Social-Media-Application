package com.example.socialmediaapplication.service.post.fragments;

/**
 * Created by LittleDuck on 3/22/2023
 * Name of project: SocialMediaApplication
 */

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.socialmediaapplication.R;
import com.example.socialmediaapplication.service.board.DashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddBlogFragment extends Fragment {

    private static final String CHANNEL_ID = "1";

    public AddBlogFragment() {
        // Required empty public constructor
    }

    FirebaseAuth firebaseAuth;
    EditText title, des;
    ProgressDialog pd;
    String name, email, uid, dp;
    DatabaseReference databaseReference;
    Button upload;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();
        View view = inflater.inflate(R.layout.fragment_add_blog, container, false);

        title = view.findViewById(R.id.ptitle);
        des = view.findViewById(R.id.pdes);
        upload = view.findViewById(R.id.upload);
        pd = new ProgressDialog(getContext());
        pd.setCanceledOnTouchOutside(false);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        Query query = databaseReference.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    name = Objects.requireNonNull(dataSnapshot1.child("name").getValue()).toString();
                    email = "" + dataSnapshot1.child("email").getValue();
                    dp = "" + Objects.requireNonNull(dataSnapshot1.child("image").getValue()).toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        upload.setOnClickListener(v -> {
            String title = "" + this.title.getText().toString().trim();
            String description = "" + des.getText().toString().trim();

            if (TextUtils.isEmpty(title)) {
                this.title.setError("Title Cant be empty");
                Toast.makeText(getContext(), "Title can't be left empty", Toast.LENGTH_LONG).show();
                return;
            }

            if (TextUtils.isEmpty(description)) {
                des.setError("Description Cant be empty");
                Toast.makeText(getContext(), "Description can't be left empty", Toast.LENGTH_LONG).show();
            } else {
                uploadData(title, description);
            }
        });
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void show_Notification(String email, String title) {

        Intent intent = new Intent(getContext(), AddBlogFragment.class);
        String CHANNEL_ID = "MYCHANNEL";
        NotificationChannel notificationChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ID, "name", NotificationManager.IMPORTANCE_LOW);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 1, intent, PendingIntent.FLAG_IMMUTABLE);
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(getContext(), CHANNEL_ID)
                    .setContentTitle("New post from " + email)
                    .setContentText(title)
                    .setContentIntent(pendingIntent)
                    .setChannelId(CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.sym_action_chat)
                    .build();
        }

        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(1, notification);

    }

    private void uploadData(final String title, final String description) {
        pd.setMessage("Publishing Post");
        pd.show();
        final String timestamp = String.valueOf(System.currentTimeMillis());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("uid", user.getUid());
        hashMap.put("uemail", user.getEmail());
        hashMap.put("title", title);
        hashMap.put("description", description);
        hashMap.put("ptime", timestamp);
        hashMap.put("plike", "0");
        hashMap.put("pcomments", "0");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Posts");
        reference.child(timestamp).setValue(hashMap)
                .addOnSuccessListener(aVoid -> {
                    pd.dismiss();
                    Toast.makeText(getContext(), "Published", Toast.LENGTH_LONG).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        show_Notification(user.getEmail(), this.title.getText().toString());
                    }
                    this.title.setText("");
                    des.setText("");
                    startActivity(new Intent(getContext(), DashboardActivity.class));
                    getActivity().finish();

                }).addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_LONG).show();
                });
    }
}
