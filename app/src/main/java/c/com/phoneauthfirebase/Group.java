package c.com.phoneauthfirebase;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.ButterKnife;
import butterknife.InjectView;
import c.com.phoneauthfirebase.models.GroupModel;

public class Group extends AppCompatActivity {


    @InjectView(R.id.add_group)
    FloatingActionButton addGroup;

    @InjectView(R.id.group_recyclerview)
    RecyclerView groupRecyclerView;


    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        ButterKnife.inject(this);
        initialiseIDS();

        addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Group.this,AddGroup.class);
                startActivity(intent);
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); // set Horizontal Orientation
        groupRecyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView

    }

    private void initialiseIDS()
    {
        firebaseDatabase    =   FirebaseDatabase.getInstance();
        databaseReference   =  firebaseDatabase.getReference().child("Group");
    }

    @Override
    public void onStart() {
        super.onStart();

        final FirebaseRecyclerAdapter<GroupModel,GroupViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<GroupModel, GroupViewHolder>(
                GroupModel.class,
                R.layout.group_adapter_layout,
                GroupViewHolder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(GroupViewHolder viewHolder, final GroupModel model, final int position) {
                viewHolder.setName(model.getName());
                viewHolder.setDescription(model.getDescription());
                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String keyis = getRef(position).getKey();
                        Log.i("tag","key is"+keyis);
                        Intent intent = new Intent(Group.this,Item.class);
                        intent.putExtra("id",keyis);
                        startActivity(intent);

                    }
                });
            }
        };
        groupRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }



public static class GroupViewHolder extends RecyclerView.ViewHolder
{

    View mview;
    public GroupViewHolder(View itemView) {
        super(itemView);
        mview = itemView;
    }

    public void setName(String title)
    {
        TextView titleTextView =  (TextView) mview.findViewById(R.id.adapter_group_title);
        titleTextView.setText(""+title);
    }

    public void setDescription(String description)
    {
        TextView descriptionTextView  = (TextView) mview.findViewById(R.id.adapter_group_description);
        descriptionTextView.setText(description);
    }


}
}
