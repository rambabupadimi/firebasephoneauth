package c.com.phoneauthfirebase;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenu;
import android.support.design.internal.NavigationMenuItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

import c.com.phoneauthfirebase.fragments.OrderShowCompletedFragment;
import c.com.phoneauthfirebase.fragments.OrderShowPendingFragment;
import c.com.phoneauthfirebase.fragments.OrderShowProgressFragment;
import c.com.phoneauthfirebase.models.OrderModel;

public class ShowOrdersList extends AppCompatActivity {

    private TextView mTextMessage;

    private OrderShowProgressFragment inprogressFragment;
    private OrderShowCompletedFragment completedFragment;
    private OrderShowPendingFragment pendingFragment;

    FrameLayout fragment_nav;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;

    Bundle bundle;
    Toolbar toolbar;
Gson gson;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText("PENDING");
                    getDataFromDB();
                    setFragment(pendingFragment);
                    toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText("INPROGRESS");
                    getDataFromDB();
                    setFragment(inprogressFragment);
                    toolbar.setBackgroundColor(getResources().getColor(R.color.inprogress));
                    return true;
                case R.id.navigation_notifications:
                    getDataFromDB();
                    setFragment(completedFragment);
                    mTextMessage.setText("COMPLETED");
                    toolbar.setBackgroundColor(getResources().getColor(R.color.completed));
                    return true;
            }
            return false;
        }
    };

    private void setFragment(Fragment fragment)
    {if(bundle!=null) {
        fragment.setArguments(bundle);
        FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
        fragmentManager.replace(R.id.main_frag, fragment);
        fragmentManager.commit();
    }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_orders_list);

        inprogressFragment  =   new OrderShowProgressFragment();
        completedFragment   =   new OrderShowCompletedFragment();
        pendingFragment     =   new OrderShowPendingFragment();
        toolbar             = (Toolbar) findViewById(R.id.toolbar);

        fragment_nav            = (FrameLayout) findViewById(R.id.main_frag);
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        mTextMessage.setText("PENDING");
        getDataFromDB();
        setFragment(pendingFragment);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        gson = new Gson();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);

    }

    private void getDataFromDB()
    {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference   =  FirebaseDatabase.getInstance().getReference().child("Orders");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null && dataSnapshot.getChildrenCount()>0)
                {
                    int count=0;
                    for(DataSnapshot ds:dataSnapshot.getChildren())
                    {
                        count++;
                        ArrayList<OrderModel> orderModelPendingList = new ArrayList<>();
                        ArrayList<OrderModel> orderModelInprogressList = new ArrayList<>();
                        ArrayList<OrderModel> orderModelCompletedList = new ArrayList<>();
                        int insideCount=0;
                        for(DataSnapshot ds1: ds.getChildren())
                        {
                            insideCount++;
                            OrderModel orderModel = ds1.getValue(OrderModel.class);
                            orderModel.setKeyIs(ds1.getKey());
                            Log.i("tag","order key is"+ds1.getKey());

                            Log.i("tag","ds value is"+orderModel.getName());
                            Log.i("tag","ds value status is"+orderModel.getOrderStatus());

                            if(orderModel.getOrderStatus()==0)
                            {
                                orderModelPendingList.add(orderModel);
                            }
                            else if(orderModel.getOrderStatus()==1)
                            {
                                orderModelInprogressList.add(orderModel);
                            }
                            else if(orderModel.getOrderStatus()==2)
                            {
                                orderModelCompletedList.add(orderModel);
                            }

                            if(count ==dataSnapshot.getChildrenCount()
                                    && insideCount == ds.getChildrenCount())
                            {

                                Log.i("tag","ds value pending is"+gson.toJson(orderModelPendingList));
                                Log.i("tag","ds value inprogress is"+gson.toJson(orderModelInprogressList));
                                Log.i("tag","ds value  completed is"+gson.toJson(orderModelCompletedList));

                                bundle=new Bundle();
                                bundle.putSerializable("pending",orderModelPendingList);
                                bundle.putSerializable("inprogress",orderModelInprogressList);
                                bundle.putSerializable("completed",orderModelCompletedList);
                            }

                        }

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
