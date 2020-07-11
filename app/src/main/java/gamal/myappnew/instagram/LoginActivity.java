package gamal.myappnew.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
EditText email,password;
Button login;
TextView txt_register;
FirebaseAuth auth;
ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email=findViewById(R.id.login_email);
        password=findViewById(R.id.login_password);
        login=findViewById(R.id.login_btn);
        txt_register=findViewById(R.id.login_text_register);
        auth=FirebaseAuth.getInstance();
        pd=new ProgressDialog(this);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setMessage("Please Wait...");
                pd.setCanceledOnTouchOutside(false);
                pd.show();
                String str_email=email.getText().toString();
                String str_password=password.getText().toString();
                if (str_email.isEmpty() || str_password.isEmpty())
                {
                    Toast.makeText(LoginActivity.this, "All Fileds are required ! ", Toast.LENGTH_SHORT).show();
                }
                else if (str_email.isEmpty())
                {
                    Toast.makeText(LoginActivity.this, "Write Email", Toast.LENGTH_SHORT).show();
                }else if (str_password.isEmpty())
                {
                    Toast.makeText(LoginActivity.this, "Write Password", Toast.LENGTH_SHORT).show();
                }
                else {
                    auth.signInWithEmailAndPassword(str_email,str_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                DatabaseReference reference=
                                        FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid());
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        pd.dismiss();

                                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                              pd.dismiss();
                                    }
                                });
                            }else {
                                pd.dismiss();
                                Toast.makeText(LoginActivity.this, "Authentication Failed !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        txt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

    }
}
