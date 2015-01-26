package com.aakportfolio.www.fbla2015;

import java.io.Serializable;

/**
 * TODO Make the class serializable
 * Created by Andrew on 1/19/2015.
 */


public class MHSEvent implements Serializable{
    private String eventName = "Untitled Event";
    private String eventDescription = "No Description";
    private String contactEmail = "events-temp@mamkschools.org";
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

    /**
     * Compares date and sees if should be displayed.
     * @param nowMM Current Month
     * @param nowDD Current Day
     * @param nowYYYY Current Year
     * @return
     */
    public boolean showEvent(int nowMM, int nowDD, int nowYYYY){
        return nowMM <= MM && nowDD <= DD && nowYYYY <= YYYY;
    }
    public String toString(){
        return getEventName();
    }
}
