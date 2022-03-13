package io.agora.openlive.audio;

import io.agora.rtc.IAudioEffectManager;
import io.agora.rtc.RtcEngine;

public class AudioController {
    private IAudioEffectManager audioEffectManager;
    private RtcEngine mRtcEngine;
    private boolean isPlaying;
    private boolean isPausing;
    public AudioController(RtcEngine mRtcEngine){
        this.mRtcEngine = mRtcEngine;
        audioEffectManager = mRtcEngine.getAudioEffectManager();
    }
    //Audio Effect
    public void playEffect(int soundid,String filePath){
        audioEffectManager.playEffect(
                 soundid,  // Sets the audio effect ID.
                 filePath,  // Sets the path of the audio effect file.
                1,  // Sets the number of times the audio effect loops. -1 represents an infinite loop.
                1,  // Sets the pitch of the audio effect. The value range is 0.5 to 2.0, where 1.0 is the original pitch.
                0.0,  // Sets the spatial position of the audio effect. The value range is -1.0 to 1.0.  -1.0 represents the audio effect occurs on the left; 0 represents the audio effect occurs in the front; 1.0 represents the audio effect occurs on the right.
                100,  // Sets the volume of the audio effect. The value range is 0 to 100. 100 represents the original volume.
                true,  // Sets whether to publish the audio effect to the remote users. true represents that both the local user and remote users can hear the audio effect; false represents that only the local user can hear the audio effect.
                0  // Sets the playback position (ms) of the audio effect file. 0 represents that the playback starts at the 0 ms mark of the audio effect file.
        );
    }
    public void stopEffect(int soundId){
        // Stops playing a specified audio effect file.
        audioEffectManager.stopEffect(soundId);

    }
    public void stopAllEffects(){
        // Stops playing all audio effect files.
        audioEffectManager.stopAllEffects();
    }
    public void pauseEffect(int soundId){
        // Pauses playing a specified audio effect file.
        audioEffectManager.pauseEffect(soundId);
    }
    public void pauseAllEffects(){
        // Pauses playing all audio effect files.
        audioEffectManager.pauseAllEffects();
    }
    public void resumeEffect(int soundId){
        // Resumes playing a specified audio effect file.
        audioEffectManager.resumeEffect(soundId);
    }
    public void resumeAllEffects(){
        // Resumes playing all audio effect files.
        audioEffectManager.resumeAllEffects();
    }
    public int getEffectDuration(String path){
        // Gets the total duration of a specified local audio effect file.
        return audioEffectManager.getEffectDuration("your file path");
    }
    public void setEffectPosition(int id,int milisecposition){
        // Sets the playback position (ms) of a specified local audio effect file. milisecposition represents that the playback starts at the milisecposition ms mark of the audio effect file.
        audioEffectManager.setEffectPosition(id, milisecposition);
    }
    public void getEffectCurrentPosition(int id){
        // Gets the current playback position of a specified local audio effect file.
        audioEffectManager.getEffectCurrentPosition(id);
    }
    //Audio Mixing
    public void startAudioMixing(String filePath){
        mRtcEngine.startAudioMixing(
                filePath,  // Specifies the absolute path of the local or online music file that you want to play.
                false,  // Sets whether to only play a music file on the local client. true represents that only the local user can hear the music; false represents that both the local user and remote users can hear the music.
                false,  // Sets whether to replace the audio captured by the microphone with a music file. true represents that the user can only hear music; false represents that the user can hear both the music and the audio captured by the microphone.
                1,  // Sets the number of times to play the music file. 1 represents play the file once.
                0  // Sets the playback position (ms) of the music file. 0 represents that the playback starts at the 0 ms mark of the music file.
        );
        isPlaying = true;
    }
    public int getAudioFileInfo(String filePath){
        return mRtcEngine.getAudioFileInfo(filePath);
    }
    public void stopAudioMixing(){
        mRtcEngine.stopAudioMixing();
        isPlaying = false;
    }
    public void pauseAudioMixing(){
        mRtcEngine.pauseAudioMixing();
        isPausing=true;
    }
    public void resumeAudioMixing(){
        mRtcEngine.resumeAudioMixing();
        isPausing=false;
    }
    public int getAudioMixingDuration(){
        return mRtcEngine.getAudioMixingCurrentPosition();
    }
    public void setAudioMixingPlaybackPosition(int position){
        mRtcEngine.setAudioMixingPosition(position);
    }
    public int getAudioMixingCurrentPosition(){
        return mRtcEngine.getAudioMixingCurrentPosition();
    }
    public void changeAudioMixingVol(int Vol){
        mRtcEngine.adjustAudioMixingVolume(Vol); //Range 0-100
    }
    public void changeAudioMixingPublishVol(int Vol){
        mRtcEngine.adjustAudioMixingPublishVolume(Vol); //Range 0-100
    }
    public void changeAudioMixingPlayoutVol(int Vol){
        mRtcEngine.adjustAudioMixingPlayoutVolume(Vol); //Range 0-100
    }
    public int getAudioMixingPlayoutVol(){
        return  mRtcEngine.getAudioMixingPlayoutVolume();
    }
    public int getAudioMixingPublishVol(){
        return  mRtcEngine.getAudioMixingPublishVolume();
    }
    public void setAudioMixingPitch(int pitch){
        mRtcEngine.setAudioMixingPitch(pitch);
    }
    /*
    * 50- 400
    * 100 is original
    * */
    public void setAudioMixingPlaybackSpeed(int speed){
        mRtcEngine.setAudioMixingPlaybackSpeed(speed);
    }
    public int getAudioTrackCount(){
        return mRtcEngine.getAudioTrackCount();
    }
    // Specifies the playback track of the current music file.
    // The index parameter must be less than or equal to the return value of getAudioTrackCount.
    public void selectAudioTrack(int trackindex){
        mRtcEngine.selectAudioTrack(trackindex);
    }

    //deprecated
    public void setAudioMixingDualMonoMode(int mode){
        mRtcEngine.setAudioMixingDualMonoMode(mode);
    }
    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isPausing() {
        return isPausing;
    }
}
