package gamal.myappnew.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import gamal.myappnew.instagram.Fragment.HomeFragment;
import gamal.myappnew.instagram.Fragment.NotificationFragment;
import gamal.myappnew.instagram.Fragment.ProfileFragment;
import gamal.myappnew.instagram.Fragment.SearchFragment;

public class MainActivity extends AppCompatActivity {
    //widget
 BottomNavigationView bottomNavigationView;
 Fragment selectfragement=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView=findViewById(R.id.bottom_navagation);

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedLintener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragement_contanier
                ,new HomeFragment()).commit();
        Bundle bundle=getIntent().getExtras();


        if (bundle!=null)
        {
            String publisher= bundle.getString("publisher");
            SharedPreferences.Editor editor=getSharedPreferences("PREFS",MODE_PRIVATE).edit();
            editor.putString("profiled", publisher);
            editor.apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragement_contanier
                    ,new ProfileFragment()).commit();
        }
        else  {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragement_contanier
                    ,new HomeFragment()).commit();
        }
    }
    // to initilaze menu
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedLintener=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectfragement = new HomeFragment();
                            break;

                        case R.id.nav_search:
                            selectfragement = new SearchFragment();
                            break;
                        case R.id.nav_add:
                            selectfragement = null;
                            startActivity(new Intent(MainActivity.this, PostActivity.class));
                            break;
                        case R.id.nav_heart:
                            selectfragement = new NotificationFragment();
                            break;
                        case R.id.nav_profile:
                            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                            editor.putString("profiled", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            editor.apply();
                            selectfragement = new ProfileFragment();
                            break;
                    }
                    if (selectfragement != null) {
                        // replace framelayout with new fragment
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragement_contanier
                                , selectfragement).commit();
                    }
                    /*else{
                     getSupportFragmentManager().beginTransaction().replace(R.id.fragement_contanier
                                , new HomeFragment()).commit();
                }*/
                    Intent mServiceIntent = new Intent(getApplicationContext(), MyService.class);
                    mServiceIntent.putExtra("notification","notification");
                    // mServiceIntent.setData(Uri.parse(savedFilePath));
                    startService(mServiceIntent);
                    return true;
                }
            };

    @Override
    protected void onResume() {
        super.onResume();
        Intent mServiceIntent = new Intent(getApplicationContext(), MyService.class);
        mServiceIntent.putExtra("notification","notification");
        // mServiceIntent.setData(Uri.parse(savedFilePath));
        startService(mServiceIntent);

    }
}
