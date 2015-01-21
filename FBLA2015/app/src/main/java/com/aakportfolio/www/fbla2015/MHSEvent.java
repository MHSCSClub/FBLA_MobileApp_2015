package com.aakportfolio.www.fbla2015;

import android.widget.TextView;

/**
 * Created by Andrew on 1/19/2015.
 */
public class MHSEvent {
    private String eventName = "Untitled Event";
    private String eventDescription = "No Description";
    private String contactEmail = "events-temp@student.mamkschools.org";    //This will change
    private int MM;
    private int DD;
    private int YYYY;
    private String eventDate;

    MHSEvent(String name, String Description, String Date){
        eventName = name;
        eventDescription = Description;
        MM = Integer.parseInt(Date.substring(0,2));
        DD = Integer.parseInt(Date.substring(3,5));
        YYYY = Integer.parseInt(Date.substring(6));
        eventDate = Date;
    }
    public String getEventName(){
        return eventName;
    }
    public String getEventDescription(){
        return eventDescription;
    }
    public String getEventDate(){
        return eventDate;
    }
    public String getContactEmail(){
        return contactEmail;
    }
    public boolean showEvent(int nowMM, int nowDD, int nowYYYY){
        //TODO: write this method properly
        return true;
    }
}
