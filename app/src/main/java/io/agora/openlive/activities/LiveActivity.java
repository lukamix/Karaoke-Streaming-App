package io.agora.openlive.activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.agora.openlive.R;
import io.agora.openlive.audio.AudioController;
import io.agora.openlive.audio.adapter.AudioAdapter;
import io.agora.openlive.audio.data.AudioData;
import io.agora.openlive.media.MediaPlayerClient;
import io.agora.openlive.rtm.adapter.MessageAdapter;
import io.agora.openlive.rtm.model.MessageItem;
import io.agora.openlive.stats.LocalStatsData;
import io.agora.openlive.stats.RemoteStatsData;
import io.agora.openlive.stats.StatsData;
import io.agora.openlive.ui.VideoGridContainer;
import io.agora.openlive.utils.AnimationUtil;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.video.VideoEncoderConfiguration;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmChannelAttribute;
import io.agora.rtm.RtmChannelListener;
import io.agora.rtm.RtmChannelMember;
import io.agora.rtm.RtmFileMessage;
import io.agora.rtm.RtmImageMessage;
import io.agora.rtm.RtmMessage;

public class LiveActivity extends RtcBaseActivity {
    private static final String TAG = LiveActivity.class.getSimpleName();

    private VideoGridContainer mVideoGridContainer;
     //NBĐ add
//    private VideoGridContainer mKaraokeGridContainer;
//    private MediaPlayer mKaraokePlayer;
    private AudioController mAudioController;
//    private MediaPlayerClient mMediaPlayerClient;
    private ImageView mMuteAudioBtn;
    private ImageView mMuteVideoBtn;
    private VideoEncoderConfiguration.VideoDimensions mVideoDimension;
    private AnimationUtil mAnimationUtil ;

    private MessageAdapter mMessageAdapter;
    private RecyclerView mMessageRecyclerView;
    private ArrayList<MessageItem> mMessageItems = new ArrayList<>();
    private EditText mSendMessageInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_room);
        initUI();
        initData();
        joinRTMChannel();
    }
    private void initUI() {
        TextView roomName = findViewById(R.id.live_room_name);
        roomName.setText(config().getChannelName());
        roomName.setSelected(true);

        initUserIcon();
        int role = getIntent().getIntExtra(
                io.agora.openlive.Constants.KEY_CLIENT_ROLE,
                Constants.CLIENT_ROLE_AUDIENCE);
        boolean isBroadcaster =  (role == Constants.CLIENT_ROLE_BROADCASTER);

        mMuteVideoBtn = findViewById(R.id.live_btn_mute_video);
        mMuteVideoBtn.setActivated(isBroadcaster);

        mMuteAudioBtn = findViewById(R.id.live_btn_mute_audio);
        mMuteAudioBtn.setActivated(isBroadcaster);

        ImageView beautyBtn = findViewById(R.id.live_btn_beautification);
        beautyBtn.setActivated(true);
        rtcEngine().setBeautyEffectOptions(beautyBtn.isActivated(),
                io.agora.openlive.Constants.DEFAULT_BEAUTY_OPTIONS);

        mVideoGridContainer = findViewById(R.id.live_video_grid_layout);
        mVideoGridContainer.setStatsManager(statsManager());

//        mKaraokeGridContainer = findViewById(R.id.karaoke_video);
//        mKaraokeGridContainer.setStatsManager(statsManager());
//        mKaraokePlayer =new MediaPlayer(this);
        mAudioController = new AudioController(rtcEngine());

        TextView nowPlayingSong = findViewById(R.id.live_now_playing_song);
        mAnimationUtil = new AnimationUtil(this,nowPlayingSong);

        mMessageRecyclerView = findViewById(R.id.live_chat_container);
        mMessageAdapter = new MessageAdapter(getBaseContext(),mMessageItems);
        mMessageRecyclerView.setAdapter(mMessageAdapter);
        mMessageRecyclerView.setLayoutManager(new LinearLayoutManager(this.getBaseContext()));
        mSendMessageInput = findViewById(R.id.live_send_message_input);

        rtcEngine().setClientRole(role);
        if (isBroadcaster) {
            startBroadcast();
        }
    }

    private void initUserIcon() {
        Bitmap origin = BitmapFactory.decodeResource(getResources(), R.drawable.fake_user_icon);
        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), origin);
        drawable.setCircular(true);
        ImageView iconView = findViewById(R.id.live_name_board_icon);
        iconView.setImageDrawable(drawable);
    }

    private void initData() {
        mVideoDimension = io.agora.openlive.Constants.VIDEO_DIMENSIONS[
                config().getVideoDimenIndex()];
        Context context = this.getApplicationContext();
        if (context == null)
        {
            return;
        }
//        try
//        {
//        mMediaPlayerClient = MediaPlayerClient.getInstance();
//        mMediaPlayerClient.setListener(mListener);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
    }
    private void joinRTMChannel(){
        RtmChannelListener mRtmChannelListener = new RtmChannelListener() {
            @Override
            public void onMemberCountUpdated(int i) {

            }

            @Override
            public void onAttributesUpdated(List<RtmChannelAttribute> list) {

            }

            @Override
            public void onMessageReceived(RtmMessage message, RtmChannelMember fromMember) {
                String text = message.getText();
                String fromUser = fromMember.getUserId();
                writeToMessageHistory(fromUser,text);
            }

            @Override
            public void onImageMessageReceived(RtmImageMessage rtmImageMessage, RtmChannelMember rtmChannelMember) {

            }

            @Override
            public void onFileMessageReceived(RtmFileMessage rtmFileMessage, RtmChannelMember rtmChannelMember) {

            }

            @Override
            public void onMemberJoined(RtmChannelMember member) {

            }

            @Override
            public void onMemberLeft(RtmChannelMember member) {

            }
        };

        try {
            // Create an RTM channel
            rtmChannel= rtmClient().createChannel(config().getChannelName(), mRtmChannelListener);
        } catch (RuntimeException e) {
        }
        // Join the RTM channel
        rtmChannel.join(new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                CharSequence text = "User: " + config().getUserName() + " completed to join the channel!" + responseInfo.toString();
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                toast.show();
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                CharSequence text = "User: " + config().getUserName() + " failed to join the channel!" + errorInfo.toString();
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                toast.show();
            }
        });
    }

    @Override
    protected void onGlobalLayoutCompleted() {
        RelativeLayout topLayout = findViewById(R.id.live_room_top_layout);
        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams) topLayout.getLayoutParams();
        params.height = mStatusBarHeight + topLayout.getMeasuredHeight();
        topLayout.setLayoutParams(params);
        topLayout.setPadding(0, mStatusBarHeight, 0, 0);
    }

    private void startBroadcast() {
        rtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        SurfaceView surface = prepareRtcVideo(0, true);
        mVideoGridContainer.addUserVideoSurface(0, surface, true);
        mMuteAudioBtn.setActivated(true);
    }

    private void stopBroadcast() {
        rtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
        removeRtcVideo(0, true);
        mVideoGridContainer.removeUserVideo(0, true);
        mMuteAudioBtn.setActivated(false);
    }

    private final MediaPlayerClient.IStateListener mListener = new MediaPlayerClient.IStateListener() {
        @Override
        public void onError(int error) {
            Log.e(TAG, "Screen share service error happened: " + error);
        }

        @Override
        public void onTokenWillExpire() {
            Log.d(TAG, "Screen share service token will expire");
//            mMediaPlayerClient.renewToken(null); // Replace the token with your valid token
        }
    };
    private void startKaraoke(){
//        SurfaceView surface = mKaraokePlayer.prepareKaraokeVideo(this.getApplicationContext(),true);
//        mKaraokeGridContainer.addUserVideoSurface(0,surface,true);
//        mKaraokePlayer.playKaraokeVideoTest();
////      mKaraokePlayer.remotePlayKaraokeTest(rtcEngine());
//        mMediaPlayerClient.start(getApplicationContext());

//        mKaraokePlayer = MediaPlayer.getInstance();
//        mKaraokePlayer.setContext(getApplicationContext());
//        mKaraokePlayer.initMediaPlayerKit();
//
//        SurfaceView surface = mKaraokePlayer.prepareKaraokeVideo(getApplicationContext(),100,true);
//        mKaraokeGridContainer.addUserVideoSurface(100,surface,true);
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        // Do nothing at the moment
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        // Do nothing at the moment
    }

    @Override
    public void onUserOffline(final int uid, int reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                removeRemoteUser(uid);
            }
        });
    }

    @Override
    public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                renderRemoteUser(uid);
            }
        });
    }

    private void renderRemoteUser(int uid) {
        SurfaceView surface = prepareRtcVideo(uid, false);
        mVideoGridContainer.addUserVideoSurface(uid, surface, false);
    }

    private void removeRemoteUser(int uid) {
        removeRtcVideo(uid, false);
        mVideoGridContainer.removeUserVideo(uid, false);
    }

    @Override
    public void onLocalVideoStats(IRtcEngineEventHandler.LocalVideoStats stats) {
        if (!statsManager().isEnabled()) return;

        LocalStatsData data = (LocalStatsData) statsManager().getStatsData(0);
        if (data == null) return;

        data.setWidth(mVideoDimension.width);
        data.setHeight(mVideoDimension.height);
        data.setFramerate(stats.sentFrameRate);
    }

    @Override
    public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {
        if (!statsManager().isEnabled()) return;

        LocalStatsData data = (LocalStatsData) statsManager().getStatsData(0);
        if (data == null) return;

        data.setLastMileDelay(stats.lastmileDelay);
        data.setVideoSendBitrate(stats.txVideoKBitRate);
        data.setVideoRecvBitrate(stats.rxVideoKBitRate);
        data.setAudioSendBitrate(stats.txAudioKBitRate);
        data.setAudioRecvBitrate(stats.rxAudioKBitRate);
        data.setCpuApp(stats.cpuAppUsage);
        data.setCpuTotal(stats.cpuAppUsage);
        data.setSendLoss(stats.txPacketLossRate);
        data.setRecvLoss(stats.rxPacketLossRate);
    }

    @Override
    public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
        if (!statsManager().isEnabled()) return;

        StatsData data = statsManager().getStatsData(uid);
        if (data == null) return;

        data.setSendQuality(statsManager().qualityToString(txQuality));
        data.setRecvQuality(statsManager().qualityToString(rxQuality));
    }

    @Override
    public void onRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats) {
        if (!statsManager().isEnabled()) return;

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null) return;

        data.setWidth(stats.width);
        data.setHeight(stats.height);
        data.setFramerate(stats.rendererOutputFrameRate);
        data.setVideoDelay(stats.delay);
    }

    @Override
    public void onRemoteAudioStats(IRtcEngineEventHandler.RemoteAudioStats stats) {
        if (!statsManager().isEnabled()) return;

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null) return;

        data.setAudioNetDelay(stats.networkTransportDelay);
        data.setAudioNetJitter(stats.jitterBufferDelay);
        data.setAudioLoss(stats.audioLossRate);
        data.setAudioQuality(statsManager().qualityToString(stats.quality));
    }

    @Override
    public void finish() {
        super.finish();
        statsManager().clearAllData();
//        //NBĐ add
//        mKaraokePlayer.stopKaraokeVideoTest();
//        mKaraokePlayer.cancelPlayKaraokeTest(rtcEngine());
//        mMediaPlayerClient.stop(this.getApplicationContext());

    }

    public void onLeaveClicked(View view) {
        finish();
    }

    public void onSwitchCameraClicked(View view) {
        rtcEngine().switchCamera();
    }

    public void onBeautyClicked(View view) {
        view.setActivated(!view.isActivated());
        rtcEngine().setBeautyEffectOptions(view.isActivated(),
                io.agora.openlive.Constants.DEFAULT_BEAUTY_OPTIONS);
    }

    public void onKaraokeClicked(View view) {
        // Temporarily used as a karaoke feature.
        // startKaraoke();
        showKaraokeDialog();
//        if(!mAudioController.isPlaying()){
//            mAudioController.startAudioMixing(io.agora.openlive.Constants.AUDIO_SOURCE_EXP);
//        }
//        else{
//            mAudioController.stopAudioMixing();
//        }
    }
    public void showKaraokeDialog(){
        final Dialog karaokeDialog = new Dialog(LiveActivity.this);
        karaokeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        karaokeDialog.setCancelable(true);
        karaokeDialog.setContentView(R.layout.audio_choosing_modal);

        RecyclerView mRecyclerViewAudio = karaokeDialog.findViewById(R.id.audio_choosing_recycler_view);
        if(AudioData.mAudioItems.size()<=0){
            AudioData.createGmailList();
        }
        TextView mTextNowPlayingSong = findViewById(R.id.live_now_playing_song);
        AudioAdapter mAudioAdapter = new AudioAdapter(getBaseContext(),AudioData.mAudioItems,
                mAudioController,karaokeDialog,mAnimationUtil,mTextNowPlayingSong);
        mRecyclerViewAudio.setAdapter(mAudioAdapter);
        mRecyclerViewAudio.setLayoutManager(new LinearLayoutManager(this.getBaseContext()));

        karaokeDialog.show();
    }
    public void onPushStreamClicked(View view) {
        // Do nothing at the moment
    }

    public void onMuteAudioClicked(View view) {
        if (!mMuteVideoBtn.isActivated()) return;

        rtcEngine().muteLocalAudioStream(view.isActivated());
        view.setActivated(!view.isActivated());
    }

    public void onMuteVideoClicked(View view) {
        if (view.isActivated()) {
            stopBroadcast();
        } else {
            startBroadcast();
        }
        view.setActivated(!view.isActivated());
    }

    public void onSendMessageClicked(View view) {
        String message_content = mSendMessageInput.getText().toString();

        // Create RTM message instance
        final RtmMessage message = rtmClient().createMessage();
        message.setText(message_content);

        // Send message to channel
        rtmChannel.sendMessage(message, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                String text = message.getText();
                writeToMessageHistory(rtmChannel.getId(),text);
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                String text = "Message fails to send to channel " + rtmChannel.getId() + " Error: " + errorInfo + "\n"
                        +"Only you can see this message !";
                writeToMessageHistory(rtmChannel.getId(),text);
            }
        });
    }
    private void writeToMessageHistory(String username,String text){
        MessageItem tmp = new MessageItem(username,text);
        mMessageItems.add(tmp);
        mMessageAdapter.notifyItemInserted(mMessageItems.size()-1);
    }
}