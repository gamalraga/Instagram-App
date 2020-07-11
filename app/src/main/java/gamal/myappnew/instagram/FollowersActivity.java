package gamal.myappnew.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import gamal.myappnew.instagram.Adapter.UserAdapter;
import gamal.myappnew.instagram.Model.User;

public class FollowersActivity extends AppCompatActivity {
String id;
String title;
List<String> idlist;
List<User> musers;
RecyclerView recyclerView;
UserAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        Intent mServiceIntent = new Intent(getApplicationContext(), MyService.class);
        mServiceIntent.putExtra("notification","notification");
        // mServiceIntent.setData(Uri.parse(savedFilePath));
        startService(mServiceIntent);
        Intent intent=getIntent();
        id=intent.getStringExtra("id");
        title=intent.getStringExtra("title");
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        musers=new ArrayList<>();
        recyclerView=findViewById(R.id.followers_recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter=new UserAdapter(getApplicationContext(),musers,false);
        recyclerView.setAdapter(adapter);
            idlist=new ArrayList<>();
            switch (title)
            {
                case "Likes":
                    getLikes();
                    break;
                case "Following":
                    getfollowing();
                    break;
                case "Followers":
                    getfollowers();
                    break;
            }
    }

    private void getLikes() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference()
                .child("Likes").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idlist.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    idlist.add(snapshot.getKey());
                }
                showuser();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getfollowing() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(id).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idlist.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    idlist.add(snapshot.getKey());
                }
                showuser();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getfollowers() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(id).child("Followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idlist.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    idlist.add(snapshot.getKey());
                }
                showuser();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void showuser()
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference()
                .child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                musers.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    User user=snapshot.getValue(User.class);
                    for (String id : idlist)
                    {
                        if (user.getId().equals(id))
                        {
                            musers.add(user);
                        }
                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






    }

}
