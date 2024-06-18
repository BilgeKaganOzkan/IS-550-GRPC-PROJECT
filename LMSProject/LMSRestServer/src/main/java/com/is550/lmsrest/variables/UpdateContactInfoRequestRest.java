package com.is550.lmsrest.variables;

public class UpdateContactInfoRequestRest {

    private String newTelNumber;
    private String newLocation;


    public String getNewTelNumber() {
        return newTelNumber;
    }

    public void setNewTelNumber(String newTelNumber) {
        this.newTelNumber = newTelNumber;
    }

    public String getNewLocation() {
        return newLocation;
    }

    public void setNewLocation(String newLocation) {
        this.newLocation = newLocation;
    }
}
