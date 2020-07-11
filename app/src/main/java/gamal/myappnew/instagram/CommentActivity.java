package gamal.myappnew.instagram;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import gamal.myappnew.instagram.Adapter.CommentAdapter;
import gamal.myappnew.instagram.Model.Comment;
import gamal.myappnew.instagram.Notification.NotificationHelper;
import gamal.myappnew.instagram.Model.User;

public class CommentActivity extends AppCompatActivity {
EditText addcomment;
CircleImageView imageprofile;
ImageView post;
String postid,publishid;
FirebaseUser firebaseUser;
RecyclerView recyclerView;
List<Comment> mcomment;
CommentAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        addcomment=findViewById(R.id.add_comment);
        imageprofile=findViewById(R.id.comment_imageprofile);
        post=findViewById(R.id.comment_post);
        Toolbar toolbar=findViewById(R.id.toolbar);
        Intent mServiceIntent = new Intent(getApplicationContext(), MyService.class);
        mServiceIntent.putExtra("notification","notification");
        // mServiceIntent.setData(Uri.parse(savedFilePath));
        startService(mServiceIntent);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent=getIntent();
        mcomment=new ArrayList<>();
        postid=intent.getStringExtra("postid");
        publishid=intent.getStringExtra("publisherid");
        getimage();
        post.setVisibility(View.GONE);
        if (addcomment.getText().toString().isEmpty())
        {
            post.setVisibility(View.GONE);

        }

        addcomment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                post.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                post.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {
                post.setVisibility(View.VISIBLE);

            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addcomment.getText().toString().equals(""))
                {
                    Toast.makeText(CommentActivity.this, "Can't send empty comment", Toast.LENGTH_SHORT).show();
                }
                else {
                    addcomments();
                }
            }
        });

        recyclerView=findViewById(R.id.comment_recycleview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
       // linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new CommentAdapter(mcomment,CommentActivity.this);
        recyclerView.setAdapter(adapter);
        readcomment();
    }

    private void addcomments() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Comments")
                .child(postid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("comment",addcomment.getText().toString());
        hashMap.put("publiser",publishid);
        reference.push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                addnotification(postid);
                addToken(postid);
                if (task.isSuccessful())
                {
                    Toast.makeText(CommentActivity.this, "Public comment is done", Toast.LENGTH_SHORT).show();
                    addcomment.setText("");
                }
            }
        });

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addnotification(String postid)
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Notification")
                .child(publishid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("userid",firebaseUser.getUid());
        hashMap.put("text","Commented in your post " +addcomment.getText().toString());
        hashMap.put("postid",postid);
        hashMap.put("ispost",true);
        reference.push().setValue(hashMap);


    }
    private void addToken(String postid)
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Token")
                .child(publishid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("userid",firebaseUser.getUid());
        hashMap.put("text","Commented in your post " +addcomment.getText().toString());
        hashMap.put("postid",postid);
        hashMap.put("ispost",true);
        reference.push().setValue(hashMap);

    }
    public void getimage()
    {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=
                FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageurl())
                        .into(imageprofile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void readcomment()
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Comments")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mcomment.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Comment comment=snapshot.getValue(Comment.class);
                    mcomment.add(comment);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
