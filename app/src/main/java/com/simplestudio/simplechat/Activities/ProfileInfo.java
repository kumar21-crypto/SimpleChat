package com.simplestudio.simplechat.Activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.simplestudio.simplechat.R;
import com.simplestudio.simplechat.Models.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileInfo extends AppCompatActivity {

    private ImageView pencilImageView;
    private EditText editName;
    private Button uploadInfo;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private ActivityResultLauncher<String> getContent;
    private Uri selectedImage;
    private String creationTime;
    private CircleImageView imageView;
    private Dialog dialog;
    private TextView fire,local;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);


        pencilImageView = findViewById(R.id.selectImage);
        editName = findViewById(R.id.name);
        uploadInfo = findViewById(R.id.uploadButton);
        imageView = findViewById(R.id.uploadedImageView);
        fire = findViewById(R.id.fireBaseImage);
        local = findViewById(R.id.profileImageString);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        creationTime = getIntent().getStringExtra("time");

        //for dialog showing
        dialog = new Dialog(ProfileInfo.this);
        dialog.setContentView(R.layout.progress_dialoge);
        dialog.setCancelable(false);

        // show dialog on ui
        showProgressDialog();

        // check if old or new user
        checkUserExistence();

        // image set in profile
        pencilImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContent.launch("image/*");
            }
        });

        // upload profile information
        uploadInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editName.getText().toString().isEmpty())
                {
                    Toast.makeText(ProfileInfo.this, "please enter your name", Toast.LENGTH_SHORT).show();
                }
                else {
                    uploadImageToFireBase();
                }
            }
        });

        getContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {

                        imageView.setImageURI(result);

                        selectedImage = result;
                    }
                });

    }


    private void checkUserExistence() {

        if (creationTime.equals("new"))
        {
            Toast.makeText(this, "welcome new user", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
        else {
            Toast.makeText(this, "welcome our old user", Toast.LENGTH_SHORT).show();

            ExecutorService service = Executors.newSingleThreadExecutor();
            service.execute(new Runnable() {
                @Override
                public void run() {
                    downloadOldUserInfo();
                }
            });

        }
    }

    private void showProgressDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });
    }


    private void downloadOldUserInfo() {
        database.getReference().child("users").child(auth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user2 = snapshot.getValue(User.class);

                        try {

                            Glide.with(ProfileInfo.this)
                                    .load(user2.getProfileImage())
                                    .placeholder(R.drawable.user)
                                    .into(imageView);
                            editName.setText(user2.getName());
                            dialog.dismiss();
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(ProfileInfo.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void uploadImageToFireBase() {

            StorageReference reference = storage.getReference().child("profileImages").child(auth.getUid());

            reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(ProfileInfo.this, "profile photo upload successfully", Toast.LENGTH_SHORT).show();

                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUri = uri.toString();
                                String uid = auth.getUid();
                                String phone = auth.getCurrentUser().getPhoneNumber();
                                String name = editName.getText().toString();

                                User user = new User(uid, name, phone, imageUri);

                                database.getReference()
                                        .child("users")
                                        .child(auth.getUid())
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                Toast.makeText(ProfileInfo.this, "your profile information is updated successfully", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(ProfileInfo.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ProfileInfo.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileInfo.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

    }


}

