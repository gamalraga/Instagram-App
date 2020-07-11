package gamal.myappnew.instagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class PostActivity extends AppCompatActivity {
Uri imageuri;
String myuri="";
StorageTask uploadtask;
StorageReference storageReference;
ImageView close,image_added;
ImageView post;
EditText descripition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Intent mServiceIntent = new Intent(getApplicationContext(), MyService.class);
        mServiceIntent.putExtra("notification","notification");
        // mServiceIntent.setData(Uri.parse(savedFilePath));
        startService(mServiceIntent);
        close=findViewById(R.id.close);
        image_added=findViewById(R.id.image_added);
        post=findViewById(R.id.post);
        descripition=findViewById(R.id.descripition);
        storageReference= FirebaseStorage.getInstance().getReference("Posts");
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this,MainActivity.class));
                finish();
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uploadimage();
            }
        });
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.
                    READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                , Manifest.permission.WRITE_EXTERNAL_STORAGE}
                        , 1);
            } else {
                CropImage.activity().setAspectRatio(1, 1)
                        .start(PostActivity.this);
            }
        }
    }


    private String getfileExension(Uri uri)
    {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap map=MimeTypeMap.getSingleton();
        return map.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void Uploadimage() {

        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Posting...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if (imageuri!=null)
        {
            final StorageReference filereference=storageReference.child(System.currentTimeMillis()+"."+getfileExension(imageuri));
            uploadtask=filereference.putFile(imageuri);
            uploadtask.continueWithTask(new Continuation() {
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
                        myuri=downloaduri.toString();
                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Posts");
                        String postid=reference.push().getKey();
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("postid",postid);
                        hashMap.put("postimage",myuri);
                        hashMap.put("descripition",descripition.getText().toString());
                        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        reference.child(postid).setValue(hashMap);
                        progressDialog.dismiss();
                        Toast.makeText(PostActivity.this, "Public is Done", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(PostActivity.this,MainActivity.class));
                        finish();
                    }else {
                        Toast.makeText(PostActivity.this, "Failed ! ", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
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
            imageuri=result.getUri();
            image_added.setImageURI(imageuri);
        }else {
            Toast.makeText(this, "Error,Try Again !", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this,MainActivity.class));
            finish();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    CropImage.activity().setAspectRatio(1, 1)
                            .start(PostActivity.this);

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
