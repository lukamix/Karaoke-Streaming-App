package io.agora.openlive.audio.model;

public class AudioItem {
    private String mSongName;
    private int mImage;
    private String mAuthor;
    private String mDataURL;
    public AudioItem(int image,String songname,String author,String dataURL){
        this.mImage = image;
        this.mSongName = songname;
        this.mAuthor = author;
        this.mDataURL = dataURL;
    }

    public String getmSongName() {
        return mSongName;
    }

    public void setmSongName(String mSongName) {
        this.mSongName = mSongName;
    }

    public int getmImage() {
        return mImage;
    }

    public void setmImage(int mImage) {
        this.mImage = mImage;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public void setmAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public String getmDataURL() {
        return mDataURL;
    }

    public void setmDataURL(String mDataURL) {
        this.mDataURL = mDataURL;
    }
}
