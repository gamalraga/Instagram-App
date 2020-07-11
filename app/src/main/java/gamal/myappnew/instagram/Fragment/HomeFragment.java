package gamal.myappnew.instagram.Fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import gamal.myappnew.instagram.Adapter.PostAdapter;
import gamal.myappnew.instagram.Model.Post;
import gamal.myappnew.instagram.Model.Story;
import gamal.myappnew.instagram.PostActivity;
import gamal.myappnew.instagram.R;

public class HomeFragment extends Fragment {


RecyclerView recyclerView;
PostAdapter adapter;
List<Post> mpost;
List<String> followinglist;
ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

                View view=inflater.inflate(R.layout.fragment_home, container, false);
                ImageView addpost=view.findViewById(R.id.posts);
                addpost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(),PostActivity.class));
                    }
                });
                recyclerView=view.findViewById(R.id.post_recycleview);
               progressBar=view.findViewById(R.id.progressbar);
                  recyclerView.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                recyclerView.setLayoutManager(linearLayoutManager);
                mpost=new ArrayList<>();
                adapter=new PostAdapter(mpost,getContext());
                recyclerView.setAdapter(adapter);
        ImageView posts=view.findViewById(R.id.posts);
        posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PostActivity.class));
            }
        });
                ////.....................

                    Checkfollowing();

                return view;
    }
    private void Checkfollowing()
    {
        followinglist=new ArrayList<>();
        DatabaseReference reference
                =FirebaseDatabase.getInstance().getReference("Follow").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followinglist.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    followinglist.add(snapshot.getKey());
                }
                readposts();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void readposts()
    {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mpost.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Post post=snapshot.getValue(Post.class);
                    for (String id:followinglist)
                    {
                        if (post.getPublisher().equals(id))
                        {
                            mpost.add(post);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
