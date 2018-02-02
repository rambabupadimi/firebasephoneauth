package c.com.phoneauthfirebase.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.google.gson.Gson;

import java.util.List;

import c.com.phoneauthfirebase.R;
import c.com.phoneauthfirebase.adapter.OrderListAdapter;
import c.com.phoneauthfirebase.models.OrderModel;

/**
 * Created by Ramu on 27-01-2018.
 */

public class OrderShowCompletedFragment  extends Fragment{

    private RecyclerView orderShowCompletedRecyclerView;

    RelativeLayout notFoundLayout;
    Gson gson = new Gson();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_order_completed,container, false);
        orderShowCompletedRecyclerView  = (RecyclerView) view.findViewById(R.id.view_order_completed_recyclerview);
        notFoundLayout          = (RelativeLayout) view.findViewById(R.id.not_found_layout);
        try {

           if (getArguments().getSerializable("completed") != null) {
               List<OrderModel> orderModelList = (List<OrderModel>) getArguments().getSerializable("completed");
               Log.i("tag", "orders model completed list" + gson.toJson(orderModelList));
                setAdapter(orderModelList,"completed");
           }
       }catch (Exception e)
       {
           e.printStackTrace();
       }
        return view;
    }



    private void setAdapter(List<OrderModel> orderModelList,String from)
    {
        if(orderModelList!=null && orderModelList.size()>0) {
            OrderListAdapter orderListAdapter = new OrderListAdapter(getActivity(), orderModelList,from);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            orderShowCompletedRecyclerView.setAdapter(orderListAdapter);
            orderShowCompletedRecyclerView.setLayoutManager(linearLayoutManager);
            notFoundLayout.setVisibility(View.GONE);
            orderShowCompletedRecyclerView.setVisibility(View.VISIBLE);
        }
        else {
            notFoundLayout.setVisibility(View.VISIBLE);
            orderShowCompletedRecyclerView.setVisibility(View.GONE);
        }
    }

}
