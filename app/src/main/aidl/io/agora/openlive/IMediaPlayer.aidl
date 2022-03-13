package io.agora.openlive;
import io.agora.openlive.INotification;
// Declare any non-default types here with import statements

interface IMediaPlayer {
     void registerCallback(INotification callback);
     void unregisterCallback(INotification callback);
     void startShare();
     void stopShare();
     void renewToken(String token);
}