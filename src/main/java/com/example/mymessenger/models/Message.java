package com.example.mymessenger.models;

import com.example.mymessenger.tools.MessageType;

import java.io.Serializable;

public class Message implements Serializable {

    private final MessageType type;
    private final String data;
    private static final long serialVersionUID = 6529685098267757690L;


    //Constructors
    public Message(MessageType type, String data) {
        this.type = type;
        this.data = data;
    }

    public Message(MessageType type) {
        this.type = type;
        this.data = null;
    }


    //getters
    public MessageType getType() {
        return type;
    }
    public String getData() {
        return data;
    }
}
