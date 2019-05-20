package com.barebrains.where;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.barebrains.where.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UploadAvatar extends AppCompatActivity {
    private  int PICK_IMAGE_REQUEST  = 1;
    ImageView mi;
    Button Upload;
    Button Choose;
    private ProgressBar pb;
    private Uri mImageuri;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_avatar);
        Choose = findViewById(R.id.choose);
        Upload = findViewById(R.id.upload);
        mi = findViewById(R.id.imageView);
        pb = findViewById(R.id.progressBar);
        mStorage = FirebaseStorage.getInstance().getReference("avatars");
        mDatabase = FirebaseDatabase.getInstance().getReference("avatars");
        Choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });



    }
    private String getFileExt(Uri uri)
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadFile()
    {
        if(mImageuri!=null)
        {

            final StorageReference fileref = mStorage.child(FirebaseAuth.getInstance()
                    .getCurrentUser().getUid()+"."+getFileExt(mImageuri));
            /*fileref.putFile(mImageuri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fileref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful())
                        {
                            Uri downloaduri = task.getResult();
                            mDatabase.child(FirebaseAuth.getInstance()
                                    .getCurrentUser().getUid()).setValue(downloaduri.toString());
                        }
                }
            });*/

            fileref.putFile(mImageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pb.setProgress(100,true);
                        }
                    },2000);

                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while(!urlTask.isSuccessful());
                    Uri download = urlTask.getResult();
                    String downurl = String.valueOf(download);
                    mDatabase.child(FirebaseAuth.getInstance()
                            .getCurrentUser().getUid()).setValue(downurl);


                    Toast.makeText(UploadAvatar.this,"file uploaded",Toast.LENGTH_LONG).show();



                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadAvatar.this,"Try again upload Failed",Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double prog = (100.0 *taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    pb.setProgress((int)prog,true);
                }
            });


        }
        else
            Toast.makeText(UploadAvatar.this,"no File Selected",Toast.LENGTH_SHORT).show();
    }

    private void openFileChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null &&data.getData()!=null)
        {
            mImageuri = data.getData();
            Picasso.get().load(mImageuri).into(mi);

        }


    }
    class Upload
    {
        String mName,mImageUrl;
        Upload()
        {

        }
        Upload(String Name,String ImageUrl)
        {
            mName = Name;
            mImageUrl = ImageUrl;

        }
        public String getName()
        {
            return mName;
        }

        public void setName(String name) {
            mName = name;
        }

        public String getImageUrl(){
        return mImageUrl;
    }
        public void setImageUrl(String imageUrl)
        {
            mImageUrl = imageUrl;
        }
    }
}
