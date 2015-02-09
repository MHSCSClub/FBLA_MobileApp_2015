package com.aakportfolio.www.fbla2015;

import java.io.Serializable;

/**
 * Created by Andrew on 1/19/2015.
 */


public class MHSEvent implements Serializable {
    private String eventName = "Untitled Event";
    private String eventDescription = "No Description";
    private String contactEmail = "events-temp@mamkschools.org";
    private int MM;
    private int DD;
    private int YYYY;
    private String eventStartDate;
    private String eventEndDate;


    public MHSEvent(String name, String Description, String startDate, String endDate, String startTime, String endTime) {
        eventName = name;
        eventDescription = "Start time: " + startTime + "\nEnd Time: " + endTime + "\n" + Description;
        MM = Integer.parseInt(endDate.substring(0, 2));
        DD = Integer.parseInt(endDate.substring(3, 5));
        YYYY = Integer.parseInt(endDate.substring(6));
        eventEndDate = endDate;
        eventStartDate = startDate;
    }

    public MHSEvent(String[] inArr) {
        this(inArr[0], inArr[1], inArr[2], inArr[3], inArr[4], inArr[5]);
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    /**
     * Compares date and sees if should be displayed.
     *
     * @param nowMM   Current Month
     * @param nowDD   Current Day
     * @param nowYYYY Current Year
     * @return
     */
    public boolean showEvent(int nowMM, int nowDD, int nowYYYY) {
        return nowMM <= MM && nowDD <= DD && nowYYYY <= YYYY;
    }

    public String toString() {
        return getEventName();
    }

    public String getEventDates() {
        return "Starts: " + eventStartDate + "\n" + "Ends: " + eventEndDate;
    }

    public String getEventStartDate() {
        return eventStartDate;
    }
}
