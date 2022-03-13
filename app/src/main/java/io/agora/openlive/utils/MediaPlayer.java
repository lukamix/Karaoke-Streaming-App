package io.agora.openlive.utils;

import android.content.Context;
import android.view.SurfaceView;

import io.agora.RtcChannelPublishHelper;
import io.agora.mediaplayer.AgoraMediaPlayerKit;
import io.agora.mediaplayer.AudioFrameObserver;
import io.agora.mediaplayer.Constants.MediaPlayerError;
import io.agora.mediaplayer.Constants.MediaPlayerEvent;
import io.agora.mediaplayer.Constants.MediaPlayerMetadataType;
import io.agora.mediaplayer.Constants.MediaPlayerPreloadEvent;
import io.agora.mediaplayer.Constants.MediaPlayerState;
import io.agora.mediaplayer.MediaPlayerObserver;
import io.agora.mediaplayer.VideoFrameObserver;
import io.agora.mediaplayer.data.AudioFrame;
import io.agora.mediaplayer.data.VideoFrame;
import io.agora.openlive.Constants;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.utils.LogUtil;

/**
 * 1. Call the setView method in the AgoraMediaPlayerKit interface to set the render view of the player.
 *
 * 2. Call the setRenderMode method in the AgoraMediaPlayerKit interface to set the rendering mode of the player's view.
 *
 * 3. Call the open method in the AgoraMediaPlayerKit interface to open the media resource. The media resource path can be a network path or a local path. Both absolute and relative paths are supported.
 *
 * Warning: Do not proceed until you receive the onPlayerStateChanged callback reporting PLAYER_STATE_OPEN_COMPLETED(2).
 *
 * 4. Call the play method in the AgoraMediaPlayerKit interface to play the media resource locally.
 *
* */
public class MediaPlayer {

    private AgoraMediaPlayerKit mMediaPlayerKit;
    private RtcChannelPublishHelper mChannelPublishHelper;
    private Context mContext;

    public void setContext(Context contextActivity){
        mContext = contextActivity;
    }

    public MediaPlayer(){
        this.mChannelPublishHelper = RtcChannelPublishHelper.getInstance();
    }
    public MediaPlayer(Context contextActivity){
        this.mContext = contextActivity;
        this.mChannelPublishHelper = RtcChannelPublishHelper.getInstance();
        initMediaPlayerKit();
    }
    public void initMediaPlayerKit(){
        mMediaPlayerKit = new AgoraMediaPlayerKit(mContext);
        mMediaPlayerKit.registerPlayerObserver(new MediaPlayerObserver() {
            @Override
            public void onPlayerStateChanged(MediaPlayerState state, MediaPlayerError error) {
                if(state==MediaPlayerState.PLAYER_STATE_OPEN_COMPLETED ){
                    mMediaPlayerKit.play();
                }
                if(state==MediaPlayerState.PLAYER_STATE_PLAYING){

                }
            }

            @Override
            public void onPositionChanged(final long position) {
                LogUtil.i("onPositionChanged:"+position);
            }

            @Override
            public void onPlayerEvent(MediaPlayerEvent eventCode) {
                LogUtil.i("onEvent:"+eventCode);
            }

            @Override
            public void onMetaData(MediaPlayerMetadataType mediaPlayerMetadataType, byte[] bytes) {
                LogUtil.i("onMetaData "+ new String(bytes));
            }

            @Override
            public void onPlayBufferUpdated(long l) {

            }

            @Override
            public void onPreloadEvent(String s, MediaPlayerPreloadEvent mediaPlayerPreloadEvent) {

            }

        });

        mMediaPlayerKit.registerVideoFrameObserver(new VideoFrameObserver() {
            @Override
            public void onFrame(VideoFrame videoFrame) {
                LogUtil.i("video onFrame :"+videoFrame);
            }
        });

        mMediaPlayerKit.registerAudioFrameObserver(new AudioFrameObserver() {
            @Override
            public void onFrame(AudioFrame audioFrame) {
                LogUtil.i("audio onFrame :"+audioFrame);
            }
        });
    }
    public SurfaceView prepareKaraokeVideo(Context context,int uid,boolean local,RtcEngine mRtcEngine){
        SurfaceView surface = RtcEngine.CreateRendererView(context);
        if (local) {
            mRtcEngine.setupLocalVideo(
                    new VideoCanvas(
                            surface,
                            VideoCanvas.RENDER_MODE_FIT,
                            0,
                            Constants.VIDEO_MIRROR_MODES[1]
                    )
            );
        } else {
            mRtcEngine.setupRemoteVideo(
                    new VideoCanvas(
                            surface,
                            VideoCanvas.RENDER_MODE_FIT,
                            uid,
                            Constants.VIDEO_MIRROR_MODES[1]
                    )
            );
        }
        if(local){
            mMediaPlayerKit.setView(surface);
            mMediaPlayerKit.setRenderMode(VideoCanvas.RENDER_MODE_HIDDEN);
        }
        else{
            mMediaPlayerKit.setView(surface);
            mMediaPlayerKit.setRenderMode(VideoCanvas.RENDER_MODE_HIDDEN);
        }
        return surface;
    }
    public void playKaraokeVideoTest(){
        if(mMediaPlayerKit==null){
            initMediaPlayerKit();
        }
        mMediaPlayerKit.open(Constants.VIDEO_SOURCE_EXP,0);
    }
    /**
     * 1. Call the stop method in the AgoraMediaPlayerKit interface to stop playback.
     * 2. (Optional) Call the unregisterPlayerObserver method in the AgoraMediaPlayerKit interface to
     *     unregister the player observer object.
     * 3. Call the destroy method in the AgoraMediaPlayerKit interface to destroy the AgoraMediaPlayerKit instance.
    * */
    public void stopKaraokeVideoTest(){
        mMediaPlayerKit.stop();
        mMediaPlayerKit.destroy();
    }
    public void remotePlayKaraokeTest(RtcEngine rtcEngine){
        if(mChannelPublishHelper==null){
            mChannelPublishHelper = RtcChannelPublishHelper.getInstance();
        }
        mChannelPublishHelper.attachPlayerToRtc(mMediaPlayerKit,rtcEngine);
        mChannelPublishHelper.publishVideo();
        /*
        *  The volume ranges from 0 to 400, where 100 indicates the original volume and 400 represents
        *  that the maximum volume can be 4 times the original volume (with built-in overflow protection).
        * */
        mChannelPublishHelper.adjustPublishSignalVolume(100,100);
    }
    public void cancelPlayKaraokeTest(RtcEngine rtcEngine){
        mChannelPublishHelper.unpublishVideo();
        mChannelPublishHelper.detachPlayerFromRtc();
        mChannelPublishHelper.release();
    }
}
