package c.com.phoneauthfirebase;

import android.content.ClipData;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import c.com.phoneauthfirebase.models.GroupModel;
import c.com.phoneauthfirebase.models.ItemModel;
import c.com.phoneauthfirebase.models.OrderModel;
import c.com.phoneauthfirebase.models.StoreOrder;

public class Item extends AppCompatActivity {

    @InjectView(R.id.add_item)
    FloatingActionButton addItem;

    @InjectView(R.id.item_recyclerview)
    RecyclerView itemRecyclerView;


    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    int count=0;

     ArrayList<StoreOrder> cartList = new ArrayList<>();

     SearchView searchView;
     Toolbar itemToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        itemToolbar = (Toolbar) findViewById(R.id.item_toolbar);
        setSupportActionBar(itemToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ButterKnife.inject(this);
        initialiseIDS();

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Item.this,AddItem.class);
                intent.putExtra("id",getIntent().getStringExtra("id"));
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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        itemRecyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
searchRecyclerview("");

    }
    private void initialiseIDS()
    {

        String id = getIntent().getStringExtra("id");
        firebaseDatabase    =   FirebaseDatabase.getInstance();
        databaseReference   =  firebaseDatabase.getReference().child("Item").child(id);

    }



    @Override
    public void onStart() {
        super.onStart();
    }



    private void searchRecyclerview(String s)
    {
        Query databaseReference2;
        if(s.length()>0)
            databaseReference2=   databaseReference.orderByChild("name").startAt(s).endAt(s+"\uF8FF");
        else
            databaseReference2=   databaseReference;

        final FirebaseRecyclerAdapter<ItemModel,ItemViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ItemModel,ItemViewHolder>(
                ItemModel.class,
                R.layout.item_adapter_layout,
                ItemViewHolder.class,
                databaseReference2
        ) {
            @Override
            protected void populateViewHolder(final ItemViewHolder viewHolder, final ItemModel model, final int position) {

                count=0;
                viewHolder.setName(model.getName());
                viewHolder.setQuantity(""+model.getQuantity());
                viewHolder.setCost(""+model.getPrice());

            }
        };
        itemRecyclerView.setAdapter(firebaseRecyclerAdapter);

    }


    /*
    private void updateCart()
    {
        if(cartList!=null)
             cartCout.setText(""+cartList.size());
        Gson gson = new Gson();
        Log.i("tag","array is"+gson.toJson(cartList));

    }
*/
    private void addCartItemToList(StoreOrder storeOrder)
    {

        if(storeOrder!=null)
        {
            // check if oredermodel already exists or not, it its remove and insert
            // else directly insert to list

            if(isExistsOrNot(storeOrder)) {

            }
            else
            {
                if (cartList != null)
                    cartList.add(storeOrder);
            }
        }
    }

    private boolean  isExistsOrNot(StoreOrder storeOrder)
    {
        if(cartList!=null && cartList.size()>0)
        {
            for(int i=0;i<cartList.size();i++)
            {
                StoreOrder storeOrder1 = cartList.get(i);
                if(storeOrder.getPosition() == storeOrder1.getPosition())
                {

                    cartList.remove(storeOrder1);
                    cartList.add(storeOrder);
                    return true;
                }
            }
        }
        return false;
    }

    private void  removeCartItemFromList(StoreOrder storeOrder)
    {
        if(cartList!=null && cartList.size()>0)
        {
            for(int i=0;i<cartList.size();i++)
            {
                StoreOrder storeOrder1 = cartList.get(i);
                if(storeOrder.getPosition() == storeOrder1.getPosition())
                {
                    cartList.remove(storeOrder1);
                    return;
                }
            }
        }
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder
    {

        View mview;
        public ItemViewHolder(View itemView) {
            super(itemView);
            mview = itemView;
        }

        public void setName(String title)
        {
            TextView titleTextView =  (TextView) mview.findViewById(R.id.adapter_item_title);
            titleTextView.setText(""+title);
        }

        public void setCost(String cost)
        {
            TextView costTextView =  (TextView) mview.findViewById(R.id.adapter_item_cost);
            costTextView.setText("Rs : "+cost);
        }

        public void setQuantity(String quantity)
        {
            TextView quantityTextView =  (TextView) mview.findViewById(R.id.adapter_item_quantity);
            quantityTextView.setText("Qty : "+quantity);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate( R.menu.search_menu, menu);

       final MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Toast like print
                if( ! searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);

                   searchRecyclerview(s);
                return false;
            }
        });
        return true;

    }
}
