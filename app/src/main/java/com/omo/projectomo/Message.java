package com.omo.projectomo;

public class Message {
    public String msgId, author, group, authorId, text, date;
    public boolean visible;
    public Message(){}

    public Message(String msgId, String author, String group, String authorId, String text, String date, boolean visible){
        this.msgId = msgId; this.author = author; this.group = group; this.authorId = authorId;
        this.text = text; this.date = date; this.visible = visible;
    }

    public String getAuthor() {
        return author;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getDate() {
        return date;
    }

    public String getMsgId() {
        return msgId;
    }

    public String getText() {
        return text;
    }

    public String getGroup() {
        return group;
    }

    public boolean isVisible() { return visible; }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setGroup(String group) { this.group = group; }

    public void setVisible(boolean visible) { this.visible = visible; }
}
