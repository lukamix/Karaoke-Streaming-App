package io.agora.openlive.rtm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.agora.openlive.R;
import io.agora.openlive.common.IItemClickListener;
import io.agora.openlive.rtm.model.MessageItem;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    final Context mContext;
    final ArrayList<MessageItem> mMessageItems;

    public MessageAdapter(Context context, ArrayList<MessageItem> audioItems) {
        this.mContext = context;
        this.mMessageItems = audioItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from((mContext));
        View messageView = inflater.inflate(R.layout.live_message_item,parent,false);
        MessageAdapter.ViewHolder viewHolder = new MessageAdapter.ViewHolder(messageView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MessageItem messageItem = mMessageItems.get(position);
        holder.mUsername.setText(messageItem.getUsername());
        holder.mUserMessage.setText(messageItem.getMessage());
        holder.setItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if(isLongClick){
                    //Do nothing
                }
                else{
                    //Do nothing again :vv
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMessageItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        final TextView mUsername;
        final TextView mUserMessage;

        private IItemClickListener itemClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mUsername = itemView.findViewById(R.id.live_audience_username);
            mUserMessage = itemView.findViewById(R.id.live_user_message);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }
        public void setItemClickListener(IItemClickListener itemClickListener)
        {
            this.itemClickListener = itemClickListener;
        }
        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition(),false);
        }

        @Override
        public boolean onLongClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition(),true);
            return true;
        }
    }
}
