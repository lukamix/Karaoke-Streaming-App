package io.agora.openlive.utils;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import io.agora.openlive.R;

public class AnimationUtil {
    Context mContext;
    TextView mNowPlayingSong;
    public AnimationUtil(Context context,TextView textView){
        this.mContext = context;
        mNowPlayingSong = textView;
    }
    public void startFloatingTextAnimation(){
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.anim);
        animation.setRepeatCount(Animation.INFINITE);
        mNowPlayingSong.startAnimation(animation);
    }
}
