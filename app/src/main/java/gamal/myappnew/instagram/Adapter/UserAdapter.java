package gamal.myappnew.instagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
import gamal.myappnew.instagram.Fragment.ProfileFragment;
import gamal.myappnew.instagram.MainActivity;
import gamal.myappnew.instagram.Model.User;
import gamal.myappnew.instagram.R;

import static android.content.Context.MODE_PRIVATE;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
Context mcontext;
List<User> mUser;
FirebaseUser firebaseUser;
private boolean isfragment;

    public UserAdapter(Context context, List<User> mUser,boolean isfragment) {
        this.mcontext = context;
        this.mUser = mUser;
        this.isfragment=isfragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mcontext).inflate(R.layout.user_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
                             firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
                             final User user=mUser.get(position);
                             holder.follow.setVisibility(View.VISIBLE);
                             holder.username.setText(user.getUsername());
                             holder.fullname.setText(user.getFullname());
        Glide.with(mcontext).load(user.getImageurl()).apply(new RequestOptions().placeholder(R.drawable.progressbar)).into(holder.imageprofile);
        isFollowing(user.getId(),holder.follow);
        if (user.getId().equals(firebaseUser.getUid()))
        {
            holder.follow.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isfragment)
              {
                    SharedPreferences.Editor editor = mcontext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profiled", user.getId());
                editor.apply();
                ((FragmentActivity) mcontext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragement_contanier, new ProfileFragment())
                        .commit();
            }else {
                    Intent intent =new Intent(mcontext, MainActivity.class);
                    intent.putExtra("publisher",user.getId());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    mcontext.startActivity(intent);
                }
            }
        });
        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.follow.getText().equals("Follow"))
                {
                    // المتابعين
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                            .child("Followers").child(firebaseUser.getUid())
                            .setValue(true);
                             //الي هيتابعهم
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("Following").child(user.getId())
                            .setValue(true);
                    addnotification(user.getId());
                    addToken(user.getId());

                }
                else {

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("Following").child(user.getId())
                            .removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                            .child("Followers").child(firebaseUser.getUid())
                            .removeValue();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
           TextView username,fullname;
           CircleImageView imageprofile;
           Button follow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.username);
            fullname=itemView.findViewById(R.id.fullname);
            imageprofile=itemView.findViewById(R.id.image_profile);
            follow=itemView.findViewById(R.id.btn_follow);
        }
    }
    private void isFollowing(final String userid, final Button button)
    {
        DatabaseReference reference=
                FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userid).exists())
                {
                    button.setText("Following");
                }
                else {
                    button.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void addnotification(String userid)
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Notification")
                .child(userid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("userid",firebaseUser.getUid());
        hashMap.put("text","Started Following you");
        hashMap.put("postid","");
        hashMap.put("ispost",false);
        reference.push().setValue(hashMap);

    }
    private void addToken(String userid)
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Token")
                .child(userid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("userid",firebaseUser.getUid());
        hashMap.put("text","Started Following you");
        hashMap.put("postid","");
        hashMap.put("ispost",false);
        reference.push().setValue(hashMap);

    }
}
