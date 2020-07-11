package gamal.myappnew.instagram.Fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import gamal.myappnew.instagram.Adapter.MyFotesAdapter;
import gamal.myappnew.instagram.EditProfileActivity;
import gamal.myappnew.instagram.FollowersActivity;
import gamal.myappnew.instagram.MainActivity;
import gamal.myappnew.instagram.Notification.NotificationHelper;
import gamal.myappnew.instagram.Model.Post;
import gamal.myappnew.instagram.Model.User;
import gamal.myappnew.instagram.OptionActivity;
import gamal.myappnew.instagram.R;

public class ProfileFragment extends Fragment {
ImageView imageprofile,options;
TextView posts,followers,following,username,fullname,bio;
Button editprofile;

FirebaseUser firebaseUser;
String profileid;
ImageButton myfotos,savefotoes;
//my posts
RecyclerView recyclerView;
MyFotesAdapter adapter;
List<Post> mfotes;
//saveposts
RecyclerView recyclerViewsavepost;
List<Post> mfotes_save;
MyFotesAdapter adapter_save;
List<String> saves;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_profile, container, false);
        //..........

        SharedPreferences preferences=getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid=preferences.getString("profiled","none");





        imageprofile=view.findViewById(R.id.profile_imageprofile);
        options=view.findViewById(R.id.option);
        posts=view.findViewById(R.id.proflie_post);
        followers=view.findViewById(R.id.proflie_followers);
        following=view.findViewById(R.id.proflie_following);
        username=view.findViewById(R.id.profile_username);
        fullname=view.findViewById(R.id.fullname);
        bio=view.findViewById(R.id.bio);
        editprofile=view.findViewById(R.id.edit_profile);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        myfotos=view.findViewById(R.id.my_fotos);
        savefotoes=view.findViewById(R.id.save_fotos);

        //recycle posts
        recyclerView=view.findViewById(R.id.profile_recycle_view);
        mfotes=new ArrayList<>();
        adapter=new MyFotesAdapter(getContext(),mfotes);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        readposts();
        //saveposts
        recyclerViewsavepost=view.findViewById(R.id.profile_recycle_view_save);
        mfotes_save=new ArrayList<>();
        adapter_save=new MyFotesAdapter(getContext(),mfotes_save);
        recyclerViewsavepost.setLayoutManager(new GridLayoutManager(getContext(),3));
        recyclerViewsavepost.setHasFixedSize(true);
        recyclerViewsavepost.setAdapter(adapter_save);
        //............
        recyclerViewsavepost.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        userprofile();
        getfollowers();
        getnrpost();
        mysaves();
        if (profileid.equals(firebaseUser.getUid()))
        {
            editprofile.setText("Edit Profile");
        }
      else {
                      checkfollow();
                      savefotoes.setVisibility(View.GONE);
            }
        editprofile.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String btn=editprofile.getText().toString();
                if (btn.equals("Edit Profile"))
                {
                    //go to profile
                    startActivity(new Intent(getContext(), EditProfileActivity.class));

                }
                else if (btn.equals("follow"))
                {
                    // المتابعين
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("Followers").child(firebaseUser.getUid())
                            .setValue(true);
                    //الي هيتابعهم
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("Following").child(profileid)
                            .setValue(true);
                    addnotification();
                    addToken();
                }  else if (btn.equals("following"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("Following").child(profileid)
                            .removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("Followers").child(firebaseUser.getUid())
                            .removeValue();
                }
            }
        });
      savefotoes.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

              recyclerViewsavepost.setVisibility(View.VISIBLE);
              recyclerView.setVisibility(View.GONE);
          }
      });
      myfotos.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

              recyclerViewsavepost.setVisibility(View.GONE);
              recyclerView.setVisibility(View.VISIBLE);
          }
      });

      followers.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent=new Intent(getContext(), FollowersActivity.class);
              intent.putExtra("id",profileid);
              intent.putExtra("title","Followers");
              startActivity(intent);
          }
      });
      following.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent=new Intent(getContext(), FollowersActivity.class);
              intent.putExtra("id",profileid);
              intent.putExtra("title","Following");
              startActivity(intent);
          }
      });
      options.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent=new Intent(getContext(), OptionActivity.class);
              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
              startActivity(intent);

          }
      });
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addToken() {

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Token")
                .child(profileid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("userid",firebaseUser.getUid());
        hashMap.put("text","Started Following you");
        hashMap.put("postid","");
        hashMap.put("ispost",false);
        reference.push().setValue(hashMap);
        NotificationHelper notificationHelper=new NotificationHelper(getContext());
        notificationHelper.sendhightproirityNotification("Title","Started Following you ",MainActivity.class);
    }

    private void readpostsave() {

        DatabaseReference reference=   FirebaseDatabase.getInstance().getReference().child("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mfotes_save.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Post post=snapshot.getValue(Post.class);
                    for (String id:saves)
                    {
                        if (post.getPostid().equals(id))
                        {
                            mfotes_save.add(post);
                        }
                    }
                }
                adapter_save.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void mysaves()
    {
        saves=new ArrayList<>();
DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Saves")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    saves.add(snapshot.getKey());
                }
                readpostsave();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readposts() {
     DatabaseReference reference=   FirebaseDatabase.getInstance().getReference().child("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mfotes.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Post post=snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)) {
                        mfotes.add(post);
                    }
                }
                Collections.reverse(mfotes);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void userprofile()
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Users")
                .child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(getContext()==null)
                {
                    return;
                }
                User user=dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                fullname.setText(user.getFullname());
                bio.setText(user.getBio());
                Glide.with(getContext()).load(user.getImageurl()).into(imageprofile);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void checkfollow()
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(profileid).exists())
                {
                    editprofile.setText("following");
                }else {
                    editprofile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
private void getfollowers()
{
    DatabaseReference reference=FirebaseDatabase.getInstance().getReference()
            .child("Follow").child(profileid).child("Followers");
    reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            followers.setText(""+dataSnapshot.getChildrenCount());
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
        DatabaseReference reference1=FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("Following");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                following.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getnrpost()
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference()
                .child("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i=0;
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Post post=snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(firebaseUser.getUid()))
                    {
                        i++;
                    }
                }
                posts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addnotification()
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Notification")
                .child(profileid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("userid",firebaseUser.getUid());
        hashMap.put("text","Started Following you");
        hashMap.put("postid","");
        hashMap.put("ispost",false);
        reference.push().setValue(hashMap);
        NotificationHelper notificationHelper=new NotificationHelper(getContext());
        notificationHelper.sendhightproirityNotification("Title","Started Following you ",MainActivity.class);

    }
}
