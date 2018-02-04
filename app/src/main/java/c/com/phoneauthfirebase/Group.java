package c.com.phoneauthfirebase;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.ButterKnife;
import butterknife.InjectView;
import c.com.phoneauthfirebase.models.GroupModel;

public class Group extends AppCompatActivity {


    @InjectView(R.id.add_group)
    FloatingActionButton addGroup;

    @InjectView(R.id.group_recyclerview)
    RecyclerView groupRecyclerView;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_group);
        ButterKnife.inject(this);

        addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Group.this,AddGroup.class);
                startActivity(intent);
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        searchRecyclerview("");
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); // set Horizontal Orientation
        groupRecyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView

    }





    @Override
    public void onStart() {
        super.onStart();

    }


    private void searchRecyclerview(String s)
    {
        Query databaseReference2;
        if(s.length()>0)
            databaseReference2=   FirebaseDatabase.getInstance().getReference().child("Group").orderByChild("name").startAt(s).endAt(s+"\uF8FF");
        else
            databaseReference2=   FirebaseDatabase.getInstance().getReference().child("Group");
        final FirebaseRecyclerAdapter<GroupModel,GroupViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<GroupModel, GroupViewHolder>(
                GroupModel.class,
                R.layout.group_adapter_layout,
                GroupViewHolder.class,
                databaseReference2
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }
}
