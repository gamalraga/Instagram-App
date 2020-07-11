package gamal.myappnew.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
EditText username,fullname,email,password;
Button register;
TextView text_login;
FirebaseAuth auth;
DatabaseReference reference;
ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username=findViewById(R.id.register_username);
        fullname=findViewById(R.id.register_fullname);
        email=findViewById(R.id.register_email);
        password=findViewById(R.id.register_password);
        register=findViewById(R.id.register_btn);
        text_login=findViewById(R.id.register_text_login);

        auth=FirebaseAuth.getInstance();
        pd=new ProgressDialog(this);
        text_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setMessage("Please Wait...");
                pd.setCanceledOnTouchOutside(false);
                pd.show();
                String str_username=username.getText().toString();
                String str_fullname=fullname.getText().toString();
                String str_email=email.getText().toString();
                String str_password=password.getText().toString();
                if (TextUtils.isEmpty(str_email) ||TextUtils.isEmpty(str_fullname)
                        ||TextUtils.isEmpty(str_password) ||TextUtils.isEmpty(str_username) )
                {
                    Toast.makeText(RegisterActivity.this, "All Fileds are reqired", Toast.LENGTH_SHORT).show();
                }
                else if (str_email.isEmpty())
                {
                    Toast.makeText(RegisterActivity.this, "Please, Write Email...", Toast.LENGTH_SHORT).show();
                }
                else if (str_username.isEmpty())
                {
                    Toast.makeText(RegisterActivity.this, "Please, Write Username...", Toast.LENGTH_SHORT).show();
                }else if (str_fullname.isEmpty())
                {
                    Toast.makeText(RegisterActivity.this, "Please, Write Fullname...", Toast.LENGTH_SHORT).show();
                }else if (password.length()<6)
                {
                    Toast.makeText(RegisterActivity.this, "Password is short", Toast.LENGTH_SHORT).show();
                } else if (str_password.isEmpty())
                {
                    Toast.makeText(RegisterActivity.this, "Please, Write Password...", Toast.LENGTH_SHORT).show();
                }
                else {
                          register(str_username,str_fullname,str_email,str_password);
                }
            }
        });
        text_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
    }
    private void register(final String username, final String fullname, String email, String password)
    {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    FirebaseUser firebaseUser=auth.getCurrentUser();
                    String userid=firebaseUser.getUid();
                    reference= FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
                    HashMap<String,Object> hashMap=new HashMap<>();
                    hashMap.put("id",userid);
                    hashMap.put("username",username);
                    hashMap.put("fullname",fullname);
                    hashMap.put("bio","");
                    hashMap.put("imageurl","https://firebasestorage.googleapis.com/v0/b/gamaldox-15260.appspot.com/o/placeprofile.png?alt=media&token=555836cd-4ac5-4772-8cde-c53b240ac51a");
                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            pd.dismiss();
                            Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });



                }else {
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this, "you can't register with email or password..", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
