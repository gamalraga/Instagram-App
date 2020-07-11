package gamal.myappnew.instagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import gamal.myappnew.instagram.MainActivity;
import gamal.myappnew.instagram.Model.Comment;
import gamal.myappnew.instagram.Model.User;
import gamal.myappnew.instagram.R;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.Viewholder> {
     List<Comment> mcomment;
     Context context;
          FirebaseUser firebaseUser;
    public CommentAdapter(List<Comment> mcomment, Context context) {
        this.mcomment = mcomment;
        this.context = context;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.comment_item,parent,false);
        return new CommentAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        final Comment comment=mcomment.get(position);
        holder.comment.setText(comment.getComment());
        readinfouser(holder.username,holder.imageprofile);
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, MainActivity.class);
                intent.putExtra("publisher",comment.getPubliser());
                context.startActivity(intent);
            }
        });
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, MainActivity.class);
                intent.putExtra("publisher",comment.getPubliser());
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mcomment.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder
    {
        CircleImageView imageprofile;
        TextView username,comment;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            imageprofile=itemView.findViewById(R.id.item_profileimage);
            username=itemView.findViewById(R.id.item_username);
            comment=itemView.findViewById(R.id.item_comment);
        }
    }
    private void readinfouser(final TextView username, final CircleImageView image)
    {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                Glide.with(context).load(user.getImageurl()).into(image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
