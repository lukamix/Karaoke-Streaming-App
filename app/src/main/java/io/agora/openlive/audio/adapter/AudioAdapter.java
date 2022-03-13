package io.agora.openlive.audio.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.agora.openlive.R;
import io.agora.openlive.audio.AudioController;
import io.agora.openlive.audio.model.AudioItem;
import io.agora.openlive.common.IItemClickListener;
import io.agora.openlive.utils.AnimationUtil;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder>{

    final Context mContext;
    final ArrayList<AudioItem> mAudioItems;
    private AudioController mAudioController;
    private Dialog mParentDialog;
    private AnimationUtil mAnimationUtil;
    private TextView mTextNowPlaingSong;

    public AudioAdapter(Context mContext, ArrayList<AudioItem> mAudioItems, AudioController audioController,
                        Dialog parent, AnimationUtil animationUtil,TextView textNowPlayingSong) {
        this.mContext = mContext;
        this.mAudioItems = mAudioItems;
        this.mAudioController = audioController;
        this.mParentDialog = parent;
        this.mAnimationUtil = animationUtil;
        this.mTextNowPlaingSong = textNowPlayingSong;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from((mContext));
        View gmailView = inflater.inflate(R.layout.audio_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(gmailView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final AudioItem audioItem = mAudioItems.get(position);
        holder.mThumbnail.setImageResource(audioItem.getmImage());
        holder.mTextSong.setText(audioItem.getmSongName());
        holder.mTextAuthor.setText(audioItem.getmAuthor());
        holder.setItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if(isLongClick){
                    //Do nothing
                }
                else{
                    if(mAudioController.isPlaying()) {
                        mAudioController.stopAudioMixing();
                    }
                    mAudioController.startAudioMixing(audioItem.getmDataURL());
                    //
                    // &*************
                    //
                    mTextNowPlaingSong.setText("Now Playing : "+audioItem.getmSongName() +" - "+audioItem.getmAuthor());
                    mAnimationUtil.startFloatingTextAnimation();
                    mParentDialog.cancel();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAudioItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        final ImageView mThumbnail;
        final TextView mTextSong;
        final TextView mTextAuthor;

        private IItemClickListener itemClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mThumbnail = itemView.findViewById(R.id.thumbnail);
            mTextSong = itemView.findViewById(R.id.text_song);
            mTextAuthor = itemView.findViewById(R.id.text_author);
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
