package com.example.ramu.chatfirebase;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    EditText name,phone,password,email;
    Button register,get;
    private String TAG ="Register";

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference firebaseDatabase;

    String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mAuth   = FirebaseAuth.getInstance();
        progressDialog  =   new ProgressDialog(this);
        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        userType    =   getIntent().getStringExtra("usertype");


        AppPreferences appPreferences = new AppPreferences(this);
        appPreferences.setUserType(""+userType);


        name = (EditText) findViewById(R.id.name);
        phone = (EditText)findViewById(R.id.phone);
        password = (EditText)findViewById(R.id.password);
        register = (Button)findViewById(R.id.register);
        email   =   (EditText)findViewById(R.id.email);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String sPhone         = phone.getText().toString();
                final String sName          = name.getText().toString();
                String sPassword            = password.getText().toString();
                String sEmail               = email.getText().toString();


                if(sPhone.length()>0 && sName.length()>0 && sPassword.length()>0 && sEmail.length() >0 ) {
                    progressDialog.setMessage("Signing up...");
                    progressDialog.show();
                    mAuth.createUserWithEmailAndPassword(sEmail, sPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String userid = mAuth.getCurrentUser().getUid();
                                DatabaseReference currentuserdb = firebaseDatabase.child(userid);


                                /*
                                HashMap hashMap = new HashMap();
                                hashMap.put("name", sName);
                                hashMap.put("phone", sPhone);
                                hashMap.put("userid",userid);
                                currentuserdb.setValue(hashMap);
                                */

                                RegisterModel registerModel = new RegisterModel();
                                registerModel.setName(sName);
                                registerModel.setPhone(sPhone);
                                registerModel.setUserid(userid);
                                registerModel.setImgurl("");
                                HashMap<String,String> hashMap = new HashMap<>();
                                registerModel.setScannedUsers(hashMap);
                                registerModel.setUserType(userType);
                                currentuserdb.setValue(registerModel);
                                currentuserdb.keepSynced(true);

                                progressDialog.dismiss();
                                firebaseDatabase.keepSynced(true);
                                Intent intent = new Intent(Register.this, UploadImage.class);
                                intent.putExtra("usertype",""+userType);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(Register.this,"Sorry !! Please enter proper details.",Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(Register.this,"Please fill mandatory fields",Toast.LENGTH_LONG).show();
                }

            }
        });

    }

}
