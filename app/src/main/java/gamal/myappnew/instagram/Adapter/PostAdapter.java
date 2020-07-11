package gamal.myappnew.instagram.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import gamal.myappnew.instagram.CommentActivity;
import gamal.myappnew.instagram.FollowersActivity;
import gamal.myappnew.instagram.Fragment.DeltiasPostFragment;
import gamal.myappnew.instagram.Fragment.ProfileFragment;
import gamal.myappnew.instagram.Model.Post;
import gamal.myappnew.instagram.Model.User;
import gamal.myappnew.instagram.R;

import static android.content.Context.MODE_PRIVATE;

public class PostAdapter  extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    List<Post> mpost;
    Context context;
    FirebaseUser firebaseUser;

    public PostAdapter(List<Post> mpost, Context context) {
        this.mpost = mpost;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.post_item,parent,false);

        return new PostAdapter.ViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        final Post post=mpost.get(position);
        Glide.with(context).load(post.getPostimage()).apply(new RequestOptions().placeholder(R.drawable.progressbar)).into(holder.postimage);
        if (post.getDescripition().equals(""))
        {
            holder.description.setVisibility(View.GONE);
        }else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescripition()+"  ");
       }
        Publisherinfo(holder.imageprofile,holder.username,post.getPublisher());
          islike(post.getPostid(),holder.like);
          nerlike(holder.likes,post.getPostid());
          getcomments(post.getPostid(),holder.comments);
          issaved(post.getPostid(),holder.save);
          holder.imageprofile.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  SharedPreferences.Editor editor=context.getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                  editor.putString("profiled", post.getPublisher());
                  editor.apply();
                  ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragement_contanier
                          ,new ProfileFragment()).commit();
              }
          });
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=context.getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                editor.putString("profiled", post.getPublisher());
                editor.apply();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragement_contanier
                        ,new ProfileFragment()).commit();
            }
        });
        holder.postimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=context.getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                editor.putString("postid", post.getPostid());
                editor.apply();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragement_contanier
                        ,new DeltiasPostFragment()).commit();
            }
        });
          holder.like.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  if (holder.like.getTag().equals("Like"))
                  {
                      FirebaseDatabase.getInstance().getReference().child("Likes")
                              .child(post.getPostid()).child(firebaseUser.getUid())
                              .setValue(true);
                      addnotification(post.getPublisher(),post.getPostid());
                      addToken(post.getPublisher(),post.getPostid());
                  }else {
                      FirebaseDatabase.getInstance().getReference().child("Likes")
                              .child(post.getPostid()).child(firebaseUser.getUid())
                              .removeValue();
                  }
              }
          });
          holder.comment.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Intent intent=new Intent(context, CommentActivity.class);
                  intent.putExtra("postid",post.getPostid());
                  intent.putExtra("publisherid",post.getPublisher());
                  context.startActivity(intent);

              }
          });
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, CommentActivity.class);
                intent.putExtra("postid",post.getPostid());
                intent.putExtra("publisherid",post.getPublisher());
                context.startActivity(intent);

            }
        });
        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Saved is Done",Toast.LENGTH_LONG).show();
                if (holder.save.getTag().equals("save"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Saves")
                            .child(firebaseUser.getUid()).child(post.getPostid())
                            .setValue(true);
                }else {

                    FirebaseDatabase.getInstance().getReference().child("Saves")
                            .child(firebaseUser.getUid()).child(post.getPostid())
                            .removeValue();
                }
            }
        });
        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                        Intent intent=new Intent(context, FollowersActivity.class);
                        intent.putExtra("id",post.getPostid());
                        intent.putExtra("title","Likes");
                       context. startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mpost.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder
    {
ImageView postimage,like,comment,save;
TextView likes,username,description,comments;
CircleImageView imageprofile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
              postimage=itemView.findViewById(R.id.postimage);
              like=itemView.findViewById(R.id.like);
              likes=itemView.findViewById(R.id.likes);
              comment=itemView.findViewById(R.id.comment);
              comments=itemView.findViewById(R.id.comments);
              save=itemView.findViewById(R.id.save);
              username=itemView.findViewById(R.id.username);
              description=itemView.findViewById(R.id.descripition);
              imageprofile=itemView.findViewById(R.id.imageprofile);
        }
    }
    private void Publisherinfo(final CircleImageView imageprofile, final TextView username, String userid)
    {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users")
                .child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                Glide.with(context).load(user.getImageurl()).into(imageprofile);
                username.setText(user.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void islike(String postid, final ImageView like)
    {
        final FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Likes")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists())
                {
                    like.setImageResource(R.drawable.heartlike);
                    like.setTag("Liked");
                }
                else {
                    like.setImageResource(R.drawable.dislike);
                    like.setTag("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void nerlike(final TextView likes, String postid)
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Likes")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount()+" Likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getcomments(String postid, final TextView comments)
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference()
                .child("Comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comments.setText("View all "+dataSnapshot.getChildrenCount()+" Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void issaved(final String Postid, final ImageView save)
    {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=
                FirebaseDatabase.getInstance().getReference().child("Saves")
                .child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Postid).exists())
                {
                    save.setImageResource(R.drawable.saved);
                    save.setTag("saved");
                }
                else {
                    save.setImageResource(R.drawable.save);
                    save.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }
    private void addnotification(String userid,String postid)
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Notification")
                .child(userid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("userid",firebaseUser.getUid());
        hashMap.put("text","Liked your post");
        hashMap.put("postid",postid);
        hashMap.put("ispost",true);
        reference.push().setValue(hashMap);

    }
    private void addToken(String userid,String postid)
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Token")
                .child(userid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("userid",firebaseUser.getUid());
        hashMap.put("text","Liked your post");
        hashMap.put("postid",postid);
        hashMap.put("ispost",true);
        reference.push().setValue(hashMap);

    }
}
