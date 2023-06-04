package com.omo.projectomo;

public class StolenBike {
    public String bikeSerial, bikeId, ownerName, ownerId, date, description;

    public StolenBike(){}

    public StolenBike(String bikeSerial, String bikeId, String ownerName, String ownerId, String date, String description){
        this.bikeSerial = bikeSerial;
        this.bikeId = bikeId;
        this.ownerName = ownerName;
        this.ownerId = ownerId;
        this.date = date;
        this.description = description;
    }

    public String getBikeId() {
        return bikeId;
    }

    public String getBikeSerial() {
        return bikeSerial;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getDate() { return date; }

    public String getDescription() {
        return description;
    }

    public void setBikeId(String bikeId) {
        this.bikeId = bikeId;
    }

    public void setBikeSerial(String bikeSerial) {
        this.bikeSerial = bikeSerial;
    }

    public void setOwnerId(String userId) {
        this.ownerId = userId;
    }

    public void setOwnerName(String userName) {
        this.ownerName = userName;
    }

    public void setDate(String date) { this.date = date; }

    public void setDescription(String description) {
        this.description = description;
    }
}
