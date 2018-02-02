package com.example.ramu.chatfirebase;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by user on 07-05-2017.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private List<ChatMessage> chatMessageList;
    String senderId;
    private SparseBooleanArray selectedItems;
    private static int currentSelectedIndex = -1;

    private ChatListAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView messageLeft,messageRight,messageLeftTime,messageRightTime;
        LinearLayout leftLayout,rightLayout;

        public MyViewHolder(View view) {
            super(view);
            leftLayout = (LinearLayout) view.findViewById(R.id.message_layout_left);
            rightLayout = (LinearLayout) view.findViewById(R.id.message_layout_right);
            messageLeft =  (TextView) view.findViewById(R.id.message_left);
            messageLeftTime =  (TextView) view.findViewById(R.id.send_time_left);
            messageRight =  (TextView) view.findViewById(R.id.message_right);
            messageRightTime =  (TextView) view.findViewById(R.id.send_time_right);
        }
    }


    public ChatAdapter(List<ChatMessage> chatMessageList,String senderId,ChatListAdapterListener listener) {
        this.chatMessageList = chatMessageList;
        this.senderId = senderId;
        this.listener = listener;
        selectedItems       =   new SparseBooleanArray();

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        ChatMessage chatMessage =chatMessageList.get(position);
        if(chatMessage.getSenderId().toString().equalsIgnoreCase(senderId))
        {
            // visible right
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.messageRight.setText(chatMessage.getMessage());
            holder.messageRightTime.setText(chatMessage.getSent_time());
        }
         else
        {
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.messageLeft.setText(chatMessage.getMessage());
            holder.messageLeftTime.setText(chatMessage.getSent_time());
        }

        holder.itemView.setActivated(selectedItems.get(position, false));

        holder.rightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onMessageRowClicked(position);
            }
        });

        holder.rightLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onRowLongClicked(position);

                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;

            }
        });

    }

    @Override
    public int getItemCount() {
        return (chatMessageList!=null)?chatMessageList.size():0;
    }

    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);

        } else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }


    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }


    public SparseBooleanArray getSelectedItemsListData()
    {
        return selectedItems;
    }

    public void clearSelections() {
        //  reverseAllAnimations = true;
        selectedItems.clear();

        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }


    public interface ChatListAdapterListener {
        void onIconClicked(int position);

        void onIconImportantClicked(int position);

        void onMessageRowClicked(int position);

        void onRowLongClicked(int position);
    }


}
