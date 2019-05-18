package com.app_services.mr_kaushik.chattingapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app_services.mr_kaushik.chattingapp.UserModel.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    EditText name, username, email;
    EditText about;
    CircleImageView profile_pic;
    LinearLayout ll_for_name, ll_for_about, ll_for_username;

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    StorageReference storageReference;

    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        about = findViewById(R.id.user_about);
        email = findViewById(R.id.email);
        profile_pic = findViewById(R.id.user_profile_pic);

        ll_for_name = findViewById(R.id.ll_for_name);
        ll_for_username = findViewById(R.id.ll_for_username);
        ll_for_about = findViewById(R.id.ll_for_about);



        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                Log.i("DATA SNAPSHOT ", "onDataChange: data = "+ dataSnapshot.getValue().toString());
                String fullName = user.getName();
                name.setText(fullName);
                username.setText(user.getUsername());
                about.setText(user.getUser_about());
                email.setText(firebaseUser.getEmail());


                if (user.getImageURL().equals("default")){
                    profile_pic.setImageResource(R.mipmap.ic_default_profile_pic);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_pic);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        ll_for_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String oldName = name.getText().toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Enter Name");


                final EditText input = new EditText(getApplicationContext());
                input.setText(name.getText().toString());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        name.setText(input.getText().toString());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("name", name.getText().toString());
                        reference.updateChildren(hashMap);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        name.setText(oldName);
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_for_name.performClick();
            }
        });

        ll_for_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String oldAbout = about.getText().toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Enter About");


                final EditText input = new EditText(getApplicationContext());
                input.setText(about.getText().toString());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        about.setText(input.getText().toString());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("user_about", about.getText().toString());
                        reference.updateChildren(hashMap);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        about.setText(oldAbout);
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_for_about.performClick();
            }
        });

        ll_for_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String oldUserName = username.getText().toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Enter Username");


                final EditText input = new EditText(getApplicationContext());
                input.setText(username.getText().toString());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        username.setText(input.getText().toString());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("username", username.getText().toString());
                        reference.updateChildren(hashMap);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        username.setText(oldUserName);
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_for_username.performClick();
            }
        });

    }


    private void openImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);

    }

    private String getFileExtension(Uri uri){

        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = getApplicationContext().getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        assert mimeType != null;
        return mimeType.split("/")[1];

    }

    private void uploadImage(){
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading");
        dialog.show();

        if (imageUri != null){
            Log.i("Image", "uploadImage: URL " + imageUri.toString());
            Log.i("Image", "uploadImage: URL " + getFileExtension(imageUri));

            String image_url = System.currentTimeMillis()+ "." + getFileExtension(imageUri);

            Log.i("Image", "uploadImage: URL = " + image_url);

            final StorageReference fileReference = storageReference.child(image_url);
            uploadTask = fileReference.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Log.i("task.isSuccessful()", "onComplete: task.isSuccessful() = " + task.isSuccessful());
                    if (task.isSuccessful()) {
                        Log.i("downloadUri", "onComplete: taskResult = " + task.getResult().toString());
                        Uri downloadUri = task.getResult();
                        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("imageURL", task.getResult().toString());
                        Toast.makeText(ProfileActivity.this, "Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                        reference.updateChildren(hashMap);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Uploading Failed!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

        } else {
            Toast.makeText(this, "No image selected!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(this, "Uploading Image!", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }

    private void Status(){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", "online");
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Status();
    }
}
