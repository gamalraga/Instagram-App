package gamal.myappnew.instagram.Fragment;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gamal.myappnew.instagram.Adapter.NotificationAdapter;
import gamal.myappnew.instagram.Model.Notification;
import gamal.myappnew.instagram.Notification.NotificationHelper;
import gamal.myappnew.instagram.R;

public class NotificationFragment extends Fragment {


RecyclerView recyclerView;
List<Notification> notifications;
NotificationAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         View view =inflater.inflate(R.layout.fragment_notification, container, false);
         recyclerView=view.findViewById(R.id.notification_recycleview);
         recyclerView.setHasFixedSize(true);
         notifications=new ArrayList<>();
         adapter=new NotificationAdapter(getContext(),notifications);
         recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
         recyclerView.setAdapter(adapter);
         readnotification();

         return view;
    }

    private void readnotification() {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference()
                .child("Notification").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notifications.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Notification notification=snapshot.getValue(Notification.class);
                    notifications.add(notification);
                }
                Collections.reverse(notifications);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
