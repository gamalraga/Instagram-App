package gamal.myappnew.instagram.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import gamal.myappnew.instagram.Fragment.DeltiasPostFragment;
import gamal.myappnew.instagram.Model.Post;
import gamal.myappnew.instagram.R;

import static android.content.Context.MODE_PRIVATE;

public class MyFotesAdapter  extends RecyclerView.Adapter<MyFotesAdapter.ViewHolder> {

    Context context;
    List<Post> mfotes;
    FirebaseUser firebaseUser;

    public MyFotesAdapter(Context context, List<Post> mfotes) {
        this.context = context;
        this.mfotes = mfotes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.fotoes_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                  final Post post=mfotes.get(position);
        Glide.with(context).load(post.getPostimage()).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=context.getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                editor.putString("postid", post.getPostid());
                editor.apply();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragement_contanier
                        ,new DeltiasPostFragment()).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mfotes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
           ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.item_post_image);
        }
    }
}
