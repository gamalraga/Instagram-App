package gamal.myappnew.instagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import gamal.myappnew.instagram.Model.User;

public class EditProfileActivity extends AppCompatActivity {
 ImageView close,done;
 TextView changephoto;
 MaterialEditText fullname,username,bio;
 CircleImageView imageprofile;
 FirebaseUser firebaseUser;
 Uri mImageUri;
 StorageTask uploadTask;
 StorageReference storageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        close=findViewById(R.id.close);
        Intent mServiceIntent = new Intent(getApplicationContext(), MyService.class);
        mServiceIntent.putExtra("notification","notification");
        // mServiceIntent.setData(Uri.parse(savedFilePath));
        startService(mServiceIntent);
        done=findViewById(R.id.save);
        changephoto=findViewById(R.id.edit_changephoto);
        fullname=findViewById(R.id.edit_fullname);
        username=findViewById(R.id.edit_username);
        bio=findViewById(R.id.edit_bio);
        imageprofile=findViewById(R.id.edit_imageprofile);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        storageRef= FirebaseStorage.getInstance().getReference().child("uploads");
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference()
        .child("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageurl()).apply(new RequestOptions().placeholder(R.drawable.progressbar)).into(imageprofile);
                fullname.setText(user.getFullname());
                username.setText(user.getUsername());
                bio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        changephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.
                            READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                        , Manifest.permission.WRITE_EXTERNAL_STORAGE}
                                , 1);
                    } else {
                        CropImage.activity().setAspectRatio(1, 1)
                                .setCropShape(CropImageView.CropShape.OVAL)
                                .start(EditProfileActivity.this);
                    }
                }
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateprofile(fullname.getText().toString(),username.getText().toString(),bio.getText().toString());
            }
        });
    }

    private void updateprofile(String fullname, String username, String bio) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("username",username);
        hashMap.put("fullname",fullname);
        hashMap.put("bio",bio);
        reference.updateChildren(hashMap);
        Toast.makeText(this, "Update done...", Toast.LENGTH_SHORT).show();
    finish();
    }
    private String getfileExension(Uri uri)
    {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap map=MimeTypeMap.getSingleton();
        return map.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void Uploadimage() {

        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if (mImageUri!=null)
        {
            final StorageReference filereference=storageRef.child(System.currentTimeMillis()+"."+getfileExension(mImageUri));
            uploadTask=filereference.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful())
                    {
                        throw  task.getException();
                    }
                    return filereference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful())
                    {
                        Uri downloaduri=  task.getResult();
                        String myuri=downloaduri.toString();
                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("imageurl",""+myuri);
                        reference.updateChildren(hashMap);
                        progressDialog.dismiss();

                    }else {
                        Toast.makeText(EditProfileActivity.this, "Failed ! ", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfileActivity.this, "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(this, "No Image Select", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&&resultCode==RESULT_OK)
        {
            CropImage.ActivityResult result= CropImage.getActivityResult(data);
            mImageUri=result.getUri();
            Uploadimage();
        }else {
            Toast.makeText(this, "Error,Try Again !", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(EditProfileActivity.this,MainActivity.class));
            finish();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    CropImage.activity().setAspectRatio(1,1)
                            .setCropShape(CropImageView.CropShape.OVAL)
                            .start(EditProfileActivity.this);

                } else {
                    // Permission Denied
                    Toast.makeText(getApplicationContext(), "Can't change image profile..", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
