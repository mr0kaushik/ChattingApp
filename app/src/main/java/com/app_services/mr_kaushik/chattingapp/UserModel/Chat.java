package com.app_services.mr_kaushik.chattingapp.UserModel;

public class Chat {
    private String sender;
    private String receiver;
    private String message;
    private String sentTimeStamp;
    private String deliveredTimeStamp;
    private String seenTimeStamp;
    private boolean isSeen;



    public Chat(String sender, String receiver, String message, String sentTimeStamp, String deliveredTimeStamp, String seenTimeStamp, boolean isSeen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.sentTimeStamp = sentTimeStamp;
        this.deliveredTimeStamp = deliveredTimeStamp;
        this.seenTimeStamp = seenTimeStamp;
        this.isSeen = isSeen;
    }


    public Chat(){

    }

    public boolean getIsSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentTimeStamp() {
        return sentTimeStamp;
    }

    public void setSentTimeStamp(String sentTimeStamp) {
        this.sentTimeStamp = sentTimeStamp;
    }

    public String getDeliveredTimeStamp() {
        return deliveredTimeStamp;
    }

    public void setDeliveredTimeStamp(String deliveredTimeStamp) {
        this.deliveredTimeStamp = deliveredTimeStamp;
    }

    public String getSeenTimeStamp() {
        return seenTimeStamp;
    }

    public void setSeenTimeStamp(String seenTimeStamp) {
        this.seenTimeStamp = seenTimeStamp;
    }

}
