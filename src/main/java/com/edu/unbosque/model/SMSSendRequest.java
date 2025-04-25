package com.edu.unbosque.model;

import lombok.Data;

@Data
public class SMSSendRequest {

    private String destinationSMSNumber;
    private String smsMessages;

    public String getDestinationSMSNumber() {
        return destinationSMSNumber;
    }

    public void setDestinationSMSNumber(String destinationSMSNumber) {
        this.destinationSMSNumber = destinationSMSNumber;
    }

    public String getSmsMessages() {
        return smsMessages;
    }

    public void setSmsMessages(String smsMessages) {
        this.smsMessages = smsMessages;
    }
}
