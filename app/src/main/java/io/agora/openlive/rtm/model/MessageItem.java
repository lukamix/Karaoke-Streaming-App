package io.agora.openlive.rtm.model;

public class MessageItem {
    private String mUsername;// aka uid :))
    private String mMessage;
    public MessageItem(String username,String message){
        this.mUsername = username;
        this.mMessage = message;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String mMessage) {
        this.mMessage = mMessage;
    }
}
