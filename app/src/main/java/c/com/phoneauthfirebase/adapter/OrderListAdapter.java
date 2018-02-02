package c.com.phoneauthfirebase.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import c.com.phoneauthfirebase.MainActivity;
import c.com.phoneauthfirebase.R;
import c.com.phoneauthfirebase.ShowOrdersList;
import c.com.phoneauthfirebase.models.OrderModel;

/**
 * Created by Ramu on 28-01-2018.
 */

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.CustomViewHolder>{
    private Context mContext;
    private String TAG = "OrderListAdapter.java";
    List<OrderModel> messagesModelList;
    String from;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    public OrderListAdapter(Context context,List<OrderModel> messagesModelList,String from) {
        this.mContext       =   context;
        this.messagesModelList = messagesModelList;
        this.from = from;

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference   =  FirebaseDatabase.getInstance().getReference().child("Orders");


    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_adapter_layout,parent,false);
        CustomViewHolder holder=new CustomViewHolder(view);
        return  holder;

    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int position) {
        final OrderModel orderModel = messagesModelList.get(position);

        holder.titleTextView.setText(orderModel.getName());
        holder.phoneTextView.setText(orderModel.getPhonenumber());
        holder.addressTextView.setText(orderModel.getAddress());
        holder.costTextView.setText(""+orderModel.getPrice());
        holder.doorNumberTextView.setText(orderModel.getDoornumber());
        holder.quantityTextView.setText(""+orderModel.getQuantity());
        if(orderModel.getOrderStatus()==0) {
            holder.orderStatusTextView.setBackgroundColor(Color.parseColor("#64B5F6"));
        }
        else if(orderModel.getOrderStatus()==1)
        {
            holder.orderStatusTextView.setBackgroundColor(Color.parseColor("#FF8A65"));

        }
        else if(orderModel.getOrderStatus()==2)
        {
            holder.orderStatusTextView.setBackgroundColor(Color.parseColor("#81C784"));

        }

        if(from.equalsIgnoreCase("progress"))
        {
            holder.inprogress_button.setVisibility(View.GONE);
            holder.pending_button.setVisibility(View.VISIBLE);
            holder.completed_button.setVisibility(View.VISIBLE);
        }
        else if(from.equalsIgnoreCase("pending"))
        {
            holder.inprogress_button.setVisibility(View.VISIBLE);
            holder.pending_button.setVisibility(View.GONE);
            holder.completed_button.setVisibility(View.VISIBLE);


        }
        else if(from.equalsIgnoreCase("completed"))
        {
            holder.inprogress_button.setVisibility(View.VISIBLE);
            holder.pending_button.setVisibility(View.VISIBLE);
            holder.completed_button.setVisibility(View.GONE);

        }
        holder.pending_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(orderModel,0);
            }
        });
        holder.inprogress_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(orderModel,1);

            }
        });
        holder.completed_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(orderModel,2);

            }
        });
    }

    private void showDialog(final OrderModel orderModel, final int value)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //Setting message manually and performing action on button click
        builder.setMessage("Are you sure want to move?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        updateStatus(value,orderModel);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });

        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("L-Mart");
        alert.show();

    }

    private void updateStatus(final int val, final OrderModel orderModel)
    {
        Log.i("tag","data value is of"+orderModel.getKeyIs());
        Log.i("tag","data value is of"+orderModel.getName());

        Query query = databaseReference.orderByChild(firebaseUser.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    Log.i("tag","data v key is"+dataSnapshot1.getKey());
                    Log.i("tag","data v value is"+dataSnapshot1.getValue());


/*
                    for(DataSnapshot dataSnapshot2:dataSnapshot1.getChildren()) {
                        if(dataSnapshot2.getKey().toString().equalsIgnoreCase(orderModel.getKeyIs()))
                        {

                            Log.i("tag","our value one is"+dataSnapshot2.getKey());
                            Log.i("tag","our value two is"+orderModel.getKeyIs());

                            OrderModel orderModel1 = dataSnapshot2.getValue(OrderModel.class);
                            dataSnapshot2.getRef().child("orderStatus").setValue(val);
                            Log.i("tag","coming is ");

                            return;
                        }
                    }
*/

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    //    databaseReference1.child("orderStatus").setValue(val);
    //    Intent intent = new Intent(mContext, ShowOrdersList.class);
    //    mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return (null != messagesModelList ? messagesModelList.size() : 0);
        // return 7;

    }
    public class CustomViewHolder extends RecyclerView.ViewHolder
    {



        TextView doorNumberTextView,addressTextView,titleTextView,
                phoneTextView,costTextView,quantityTextView;
        View orderStatusTextView;
        Button inprogress_button,pending_button,completed_button;

        public CustomViewHolder(View itemView) {
            super(itemView);
            doorNumberTextView =  (TextView) itemView.findViewById(R.id.adapter_order_door_number);
            addressTextView =  (TextView) itemView.findViewById(R.id.adapter_order_address);
            titleTextView =  (TextView) itemView.findViewById(R.id.adapter_order_title);
            phoneTextView =  (TextView) itemView.findViewById(R.id.adapter_order_phone);
            costTextView =  (TextView) itemView.findViewById(R.id.adapter_order_price);
            quantityTextView =  (TextView) itemView.findViewById(R.id.adapter_order_quantity);
            orderStatusTextView = itemView.findViewById(R.id.adapter_order_status);

            inprogress_button  = (Button) itemView.findViewById(R.id.button_inprogress);
            pending_button     = (Button) itemView.findViewById(R.id.button_pending);
            completed_button    = (Button) itemView.findViewById(R.id.button_completed);
        }
    }



}