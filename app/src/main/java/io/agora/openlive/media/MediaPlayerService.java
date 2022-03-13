package io.agora.openlive.media;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import io.agora.openlive.AgoraApplication;
import io.agora.openlive.IMediaPlayer;
import io.agora.openlive.INotification;
import io.agora.openlive.R;
import io.agora.openlive.rtc.EngineConfig;
import io.agora.openlive.ui.VideoGridContainer;
import io.agora.openlive.utils.FileUtil;
import io.agora.openlive.utils.MediaPlayer;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class MediaPlayerService extends Service{

    private static final String LOG_TAG = MediaPlayerService.class.getSimpleName();
    private MediaPlayer mMediaPlayer;
    private RtcEngine mRtcEngine;
    private VideoGridContainer mVideoGridContainer;

    private RemoteCallbackList<INotification> mCallbacks
            = new RemoteCallbackList<INotification>();

    private final IMediaPlayer.Stub mBinder = new IMediaPlayer.Stub() {
        public void registerCallback(INotification cb) {
            if (cb != null) mCallbacks.register(cb);
        }

        public void unregisterCallback(INotification cb) {
            if (cb != null) mCallbacks.unregister(cb);
        }

        public void startShare() {
            startMediaPlayer();
        }

        public void stopShare() {
            stopMediaPlayer();
        }

        public void renewToken(String token) {
            refreshToken(token);
        }
    };

    private void startMediaPlayer(){

        mMediaPlayer.playKaraokeVideoTest();
        mMediaPlayer.remotePlayKaraokeTest(mRtcEngine);
        startForeground(55431,getForeNotification());
    }
    private Notification getForeNotification() {
        Notification notification;
        String eventTitle = getResources().getString(R.string.app_name);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationHelper.generateChannelId(getApplicationContext(), 55431))
                .setContentTitle(eventTitle)
                .setContentText(eventTitle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            builder.setColor(getResources().getColor(android.R.color.black));
        notification = builder.build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT;

        return notification;
    }
    private void stopMediaPlayer(){
        mMediaPlayer.stopKaraokeVideoTest();
        mMediaPlayer.cancelPlayKaraokeTest(mRtcEngine);
    }
    private void refreshToken(String token) {
        if (mRtcEngine != null) {
            mRtcEngine.renewToken(token);
        } else {
            Log.e(LOG_TAG, "rtc engine is null");
        }
    }
    private void joinChannel() {
        String token = getString(R.string.agora_access_token);
        if (TextUtils.isEmpty(token) || TextUtils.equals(token, "#YOUR ACCESS TOKEN#")) {
            token = null; // default, no token
        }
        mRtcEngine.joinChannel(token, config().getChannelName(),"",
                100);
    }

    private void setUpEngine() {
        String appId = getString(R.string.private_app_id);
        try {
            mRtcEngine = RtcEngine.create(getApplicationContext(), appId, new IRtcEngineEventHandler() {
                @Override
                public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
                    Log.d(LOG_TAG, "onJoinChannelSuccess " + channel + " " + elapsed);
                }

                @Override
                public void onWarning(int warn) {
                    Log.d(LOG_TAG, "onWarning " + warn);
                }

                @Override
                public void onError(int err) {
                    Log.d(LOG_TAG, "onError " + err);
                }

                @Override
                public void onRequestToken() {

                }

                @Override
                public void onTokenPrivilegeWillExpire(String token) {

                }

                @Override
                public void onConnectionStateChanged(int state, int reason) {

                }
            });
        } catch (Exception e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }

        mRtcEngine.setLogFile(FileUtil.initializeLogFile(this));
        mRtcEngine.setChannelProfile(io.agora.rtc.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
        mRtcEngine.enableVideo();

        mRtcEngine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER);

        mRtcEngine.muteAllRemoteAudioStreams(true);
        mRtcEngine.muteAllRemoteVideoStreams(true);
        mRtcEngine.disableAudio();

        SurfaceView surface = mMediaPlayer.prepareKaraokeVideo(getApplicationContext(),100,true,mRtcEngine);
        mVideoGridContainer.addUserVideoSurface(100,surface,true);
        Log.e("abc","?????");
    }
    private void setUpVideoConfig() {
        VideoEncoderConfiguration configuration = new VideoEncoderConfiguration(
                io.agora.openlive.Constants.VIDEO_DIMENSIONS[config().getVideoDimenIndex()],
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_LANDSCAPE
        );
        configuration.mirrorMode = io.agora.openlive.Constants.VIDEO_MIRROR_MODES[config().getMirrorEncodeIndex()];
        mRtcEngine.setVideoEncoderConfiguration(configuration);
    }
    protected void removeRtcVideo(int uid, boolean local) {
        if (local) {
            mRtcEngine.setupLocalVideo(null);
        } else {
            mRtcEngine.setupRemoteVideo(new VideoCanvas(null, VideoCanvas.RENDER_MODE_FIT, uid));
        }
    }
    private void initModule(){
        if(mMediaPlayer==null){
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setContext(getApplicationContext());
            mMediaPlayer.initMediaPlayerKit();
        }
        WindowManager windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        mVideoGridContainer = new VideoGridContainer(this);
        mVideoGridContainer.setId(R.id.live_video_grid_layout);
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        WindowManager.LayoutParams layout_params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        layout_params.width = 300;
        layout_params.height = 475;
        layout_params.gravity = Gravity.TOP | Gravity.START;
        layout_params.x = 0;
        layout_params.y = 0;
        windowManager.addView(mVideoGridContainer,layout_params);    }
    private void deInitModules() {
        mRtcEngine.leaveChannel();
        RtcEngine.destroy();
        mRtcEngine = null;

        if (mMediaPlayer != null) {
            mMediaPlayer.cancelPlayKaraokeTest(mRtcEngine);
            mMediaPlayer = null;
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        initModule();
        // Creates a RtcEngine object
        setUpEngine();
        // Set video encoding configurations
        setUpVideoConfig();
        // Join the channel
        joinChannel();
        return mBinder;
    }
    @Override
    public void onCreate(){

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        deInitModules();
    }

    protected AgoraApplication application() {
        return (AgoraApplication) getApplication();
    }
    protected EngineConfig config() {
        return application().engineConfig();
    }
}
