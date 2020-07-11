package gamal.myappnew.instagram;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import gamal.myappnew.instagram.Fragment.HomeFragment;
import gamal.myappnew.instagram.Model.Notification;
import gamal.myappnew.instagram.Model.User;
import gamal.myappnew.instagram.Notification.NotificationHelper;

public class MyService extends Service implements ChildEventListener {
    DatabaseReference notificationref;
    User user;
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationref= FirebaseDatabase.getInstance().getReference("Token")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationref.addChildEventListener(this);
        return super.onStartCommand(intent, flags, startId);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        Notification notification=dataSnapshot.getValue(Notification.class);
        NotificationHelper notificationHelper=new NotificationHelper(getApplicationContext());


        notificationHelper.sendhightproirityNotification("new Notification",notification.getText(),MainActivity.class);
        notificationref.removeValue();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
       /* Notification notification=dataSnapshot.getValue(Notification.class);
        NotificationHelper notificationHelper=new NotificationHelper(getApplicationContext());
        notificationHelper.sendhightproirityNotification("new Notification",notification.getText(),MainActivity.class);
        notificationref.removeValue(); */   }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
