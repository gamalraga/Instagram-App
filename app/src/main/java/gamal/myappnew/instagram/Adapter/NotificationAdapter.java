package gamal.myappnew.instagram.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import gamal.myappnew.instagram.Fragment.DeltiasPostFragment;
import gamal.myappnew.instagram.Fragment.ProfileFragment;
import gamal.myappnew.instagram.Model.Notification;
import gamal.myappnew.instagram.Model.Post;
import gamal.myappnew.instagram.Model.User;
import gamal.myappnew.instagram.R;

import static android.content.Context.MODE_PRIVATE;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    Context context;
    List<Notification> mnotification;


    public NotificationAdapter(Context context, List<Notification> mnotification) {
        this.context = context;
        this.mnotification = mnotification;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.notification_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Notification notification=mnotification.get(position);
        holder.text.setText(notification.getText());
        if (notification.isIspost()) {
            holder.imagepost.setVisibility(View.VISIBLE);
            getpostimage(notification.getPostid(), holder.imagepost);
        }else {
            holder.imagepost.setVisibility(View.GONE);
        }
        getuserinfo(notification.getUserid(),holder.imageprofile,holder.username);

           holder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if (notification.isIspost()==true) {
            SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("postid", notification.getPostid());
            editor.apply();
            ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragement_contanier
                    ,new DeltiasPostFragment()).commit();
        }else {
            SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profiled", notification.getUserid());
            editor.apply();
            ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragement_contanier
                    ,new ProfileFragment()).commit();
        }
    }
});
    }

    @Override
    public int getItemCount() {
        return mnotification.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
                 ImageView imagepost;
                 CircleImageView imageprofile;
                 TextView username,text;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagepost=itemView.findViewById(R.id.notification_postimage);
            imageprofile=itemView.findViewById(R.id.notification_imageprofile);
            username=itemView.findViewById(R.id.notification_username);
            text=itemView.findViewById(R.id.notification_comment);
        }
    }
    private void getuserinfo(String userid, final CircleImageView imageView, final TextView username)
    {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                Glide.with(context).load(user.getImageurl()).into(imageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void getpostimage(String postid, final ImageView postimage)
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference()
                .child("Posts").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post=dataSnapshot.getValue(Post.class);
                Glide.with(context).load(post.getPostimage()).apply(new RequestOptions().placeholder(R.drawable.progressbar)).into(postimage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
