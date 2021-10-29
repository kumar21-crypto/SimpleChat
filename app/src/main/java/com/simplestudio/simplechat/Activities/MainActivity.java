package com.simplestudio.simplechat.Activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Dialog;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.simplestudio.simplechat.Adaptors.StatusAdaptor;
import com.simplestudio.simplechat.Models.Status;
import com.simplestudio.simplechat.Models.UserStatus;
import com.simplestudio.simplechat.R;
import com.simplestudio.simplechat.Models.User;
import com.simplestudio.simplechat.Adaptors.ViewPageAdaptor;
import com.simplestudio.simplechat.databinding.ActivityChatBinding;
import com.simplestudio.simplechat.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    TextView text;
    Button logout;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    private String titles[] = new String[]{"Chats","Groups","Calls"};
    private Uri statusImageUri;
    private ViewPageAdaptor viewPageAdaptor;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private CircleImageView circleImageView;
    private ArrayList<User> users;
    private StatusAdaptor statusAdaptor;
    private ArrayList<UserStatus> userStatuses;
    private ActivityMainBinding binding;
    private ActivityResultLauncher<String> getImage;
    private Dialog dialog;
    private User user;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // for status bar content visibility
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

    tabLayout = (TabLayout) findViewById(R.id.tabLayout);
    viewPager2 = (ViewPager2) findViewById(R.id.viewPager);
    viewPageAdaptor = new ViewPageAdaptor(this);
    userStatuses = new ArrayList<>();
    statusAdaptor = new StatusAdaptor(this,userStatuses);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    binding.statusRecyclerView.setLayoutManager(layoutManager);
    binding.statusRecyclerView.setNestedScrollingEnabled(false);
    binding.statusRecyclerView.setAdapter(statusAdaptor);


    auth = FirebaseAuth.getInstance();
    database = FirebaseDatabase.getInstance();
    storage = FirebaseStorage.getInstance();
    circleImageView = findViewById(R.id.mainActivityProfileImage);

    dialog = new Dialog(this);
    dialog.setContentView(R.layout.progress_dialoge);
    dialog.setCancelable(false);

    database.getReference().child("users").child(auth.getUid()).addValueEventListener(
            new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user = snapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            }
    );

    database.getReference().child("stories").addValueEventListener(
            new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                    {
                        userStatuses.clear();

                        for (DataSnapshot snapshot1 : snapshot.getChildren())
                        {
                                UserStatus userStatus = new UserStatus();
                                userStatus.setName(snapshot1.child("name").getValue(String.class));
                                userStatus.setProfileImage(snapshot1.child("profileImage").getValue(String.class));
                                userStatus.setLastUpdated(snapshot1.child("lastUpdated").getValue(Long.class));


                                ArrayList<Status> statuses1 = new ArrayList<>();

                            for (DataSnapshot snapshot2 : snapshot1.child("statuses").getChildren())
                            {
                                Status sampleStatus = snapshot2.getValue(Status.class);
                                statuses1.add(sampleStatus);
                            }
                            userStatus.setStatuses(statuses1);

                            userStatuses.add(userStatus);
                        }

                        statusAdaptor.notifyDataSetChanged();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            }
    );

    // setting status adaptor
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.statusRecyclerView.setLayoutManager(linearLayoutManager);
        binding.statusRecyclerView.setAdapter(statusAdaptor);

        getImage = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result!=null) {
                            statusImageUri = result;
                            Toast.makeText(MainActivity.this, "image set successfully", Toast.LENGTH_SHORT).show();

                            statusUploadToDataBase();
                        }
                    }
                });

        // viewpager adaptor
    viewPager2.setAdapter(viewPageAdaptor);

    new TabLayoutMediator(tabLayout,viewPager2,((tab, position) -> tab.setText(titles[position]))).attach();

    setMainProfileImage();

    binding.statusAddButton.setOnClickListener(v -> {

        getImage.launch("image/*");




    });

//    binding.statusImageView.setOnClickListener(v -> {
//        statusUploadToDataBase();
//    });


    }

    private void statusUploadToDataBase() {

        Date date = new Date();

        final StorageReference reference = storage.getReference().child("status").child(auth.getUid())
                .child(date.getTime() + "");

        reference.putFile(statusImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful())
                {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            UserStatus userStatus = new UserStatus();
                            userStatus.setName(user.getName());
                            userStatus.setProfileImage(user.getProfileImage());
                            userStatus.setLastUpdated(date.getTime());

                            HashMap<String , Object> objectsHashMap = new HashMap<>();
                            objectsHashMap.put("name",userStatus.getName());
                            objectsHashMap.put("profileImage",userStatus.getProfileImage());
                            objectsHashMap.put("lastUpdated",userStatus.getLastUpdated());

                            String imageURL = uri.toString();


                            Status status = new Status(imageURL,userStatus.getLastUpdated());

                            database.getReference().child("stories")
                                    .child(auth.getUid())
                                    .updateChildren(objectsHashMap);

                            database.getReference().child("stories")
                                    .child(auth.getUid())
                                    .child("statuses")
                                    .push()
                                    .setValue(status);

                            Toast.makeText(MainActivity.this, "status upload successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentUID = auth.getUid();
        if (currentUID != null) {
            database.getReference().child("connection").child(currentUID).setValue("online");

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        String currentUID = auth.getUid();
        if (currentUID != null) {
            database.getReference().child("connection").child(currentUID).setValue("offline");
        }

    }

    private void setMainProfileImage() {

        database.getReference().child("users").child(Objects.requireNonNull(auth.getUid()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        User user = snapshot.getValue(User.class);

                        if (user != null) {
                            Glide.with(MainActivity.this)
                                    .load(user.getProfileImage())
                                    .placeholder(R.drawable.user)
                                    .into(circleImageView);

                            Glide.with(MainActivity.this)
                                    .load(user.getProfileImage())
                                    .placeholder(R.mipmap.ic_launcher)
                                    .into(binding.statusImageView);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


}
