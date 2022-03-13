package io.agora.openlive.media;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import io.agora.openlive.IMediaPlayer;
import io.agora.openlive.INotification;

public class MediaPlayerClient {

    private static final String TAG = MediaPlayerClient.class.getSimpleName();
    private static IMediaPlayer mMediaPlayerSvc;
    private IStateListener mStateListener;
    private static volatile MediaPlayerClient mInstance;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service){
            mMediaPlayerSvc = IMediaPlayer.Stub.asInterface(service);
            try {
                mMediaPlayerSvc.registerCallback(mNotification);
                mMediaPlayerSvc.startShare();
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }

        public void onServiceDisconnected(ComponentName className) {

        }
    };

    public static MediaPlayerClient getInstance(){
        if (mInstance == null) {
            synchronized (MediaPlayerClient.class) {
                if (mInstance == null) {
                    mInstance = new MediaPlayerClient();
                }
            }
        }
        return mInstance;
    }
    public void start(Context context){
        if (mMediaPlayerSvc == null) {
            Intent intent = new Intent(context, MediaPlayerService.class);
            context.bindService(intent,mConnection,Context.BIND_AUTO_CREATE);
        }
        else
            {
            try {
                mMediaPlayerSvc.startShare();
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
    }

    public void stop(Context context) {
        if (mMediaPlayerSvc != null) {
            try {
                mMediaPlayerSvc.stopShare();
                mMediaPlayerSvc.unregisterCallback(mNotification);
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.e(TAG, Log.getStackTraceString(e));
            } finally {
                mMediaPlayerSvc = null;
            }
        }
        context.unbindService(mConnection);
    }

    public void renewToken(String token) {
        if (mMediaPlayerSvc != null) {
            try {
                mMediaPlayerSvc.renewToken(token);
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.e(TAG, Log.getStackTraceString(e));
            }
        } else {
            Log.e(TAG, "screen sharing service not exist");
        }
    }

    private final INotification mNotification = new INotification.Stub() {
        /**
         * This is called by the remote service to tell us about error happened.
         * Note that IPC calls are dispatched through a thread
         * pool running in each process, so the code executing here will
         * NOT be running in our main thread like most other things -- so,
         * if to update the UI, we need to use a Handler to hop over there.
         */
        public void onError(int error) {
            Log.e(TAG, "screen sharing service error happened: " + error);
            mStateListener.onError(error);
        }

        public void onTokenWillExpire() {
            Log.d(TAG, "access token for screen sharing service will expire soon");
            mStateListener.onTokenWillExpire();
        }
    };

    public void setListener(IStateListener listener) {
        mStateListener = listener;
    }

    public interface IStateListener {
        void onError(int error);

        void onTokenWillExpire();
    }
}
