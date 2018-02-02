package c.com.phoneauthfirebase;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.ButterKnife;
import butterknife.InjectView;
import c.com.phoneauthfirebase.models.GroupModel;

public class AddGroup extends AppCompatActivity {



    @InjectView(R.id.group_title)
    TextView groupTitle;

    @InjectView(R.id.group_save)
    Button groupSave;


    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        ButterKnife.inject(this);

        initialiseIDS();

        groupSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(groupTitle.getText().toString().length()>0) {
                    GroupModel groupModel = new GroupModel();
                    groupModel.setName(groupTitle.getText().toString());
                    groupModel.setDescription("des");
                    databaseReference.push().setValue(groupModel);
                    databaseReference.keepSynced(true);
                    Intent intent = new Intent(AddGroup.this, Group.class);
                    startActivity(intent);
                }

            }
        });
    }

    private void initialiseIDS()
    {
        firebaseDatabase    =   FirebaseDatabase.getInstance();
        databaseReference   =  firebaseDatabase.getReference().child("Group");
    }
}
