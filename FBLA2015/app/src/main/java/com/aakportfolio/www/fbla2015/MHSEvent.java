package com.aakportfolio.www.fbla2015;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * This object is for storing an event (1 line from the CSV file)
 * Created by Andrew on 1/19/2015.
 */

//TODO: implement the functionality to disable the email button, include email in constructor and file and eventDummy
public class MHSEvent implements Serializable, Comparable {
    private String eventName = "Untitled Event";
    private String eventDescription = "No Description";
    private String contactEmail = "events-temp@mamkschools.org";
    private int MM;
    private int DD;
    private int YYYY;
    private String eventStartDate;
    private String eventEndDate;


    public MHSEvent(String name, String Description, String startDate, String startTime, String endDate, String endTime, String email) {
        eventName = name;
        eventDescription = eventName + "\n";
        if (!startTime.trim().equals("none")) {
            if (startTime.trim().equals("allday")) {
                eventDescription += "All day event" + "\n";
            } else {
                eventDescription += "Start time: " + startTime + "\n";
                if (!endTime.trim().equals("none")) {
                    eventDescription += "End Time: " + endTime + "\n";
                }
            }
        }
        if (Description.trim().equals("none")) {
            eventDescription += "No Description";
        } else {
            eventDescription += Description;
        }
        try {
            MM = Integer.parseInt(startDate.split(Pattern.quote("/"))[0]);
            DD = Integer.parseInt(startDate.split(Pattern.quote("/"))[1]);
            YYYY = Integer.parseInt(startDate.split(Pattern.quote("/"))[2]);
        } catch (Exception e) {
            MM = 00;
            DD = 00;
            YYYY = 00;
            eventStartDate = "00/00/0000";
            eventEndDate = "00/00/0000";
            e.printStackTrace();
        }
        eventEndDate = endDate;
        eventStartDate = startDate;
        contactEmail = email.trim();
    }


    public MHSEvent(String[] inArr) {
        this(inArr[0], inArr[1], inArr[2], inArr[3], inArr[4], inArr[5], inArr[6]);
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

    public boolean showEmail() {
        return !contactEmail.equals("none");
    }
    /**
     * Compares date and sees if should be displayed.
     *
     * @param nowMM   Current Month
     * @param nowDD   Current Day
     * @param nowYYYY Current Year
     * @return Compares date and sees if should be displayed.
     */
    public boolean showEvent(int nowMM, int nowDD, int nowYYYY) {
        try {
            int MM1 = Integer.parseInt(eventEndDate.split(Pattern.quote("/"))[0]),
                    DD1 = Integer.parseInt(eventEndDate.split(Pattern.quote("/"))[1]),
                    YYYY1 = Integer.parseInt(eventEndDate.split(Pattern.quote("/"))[2]);
            if (nowYYYY > YYYY1) return false;
            return nowYYYY >= YYYY1 && (nowYYYY != YYYY1 || nowMM <= MM1 && nowDD <= DD1);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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

    public String getEventEndDate() {
        return eventEndDate;
    }

    public int[] getDateFields() {
        return new int[]{MM, DD, YYYY};
    }

    @Override
    public int compareTo(@NonNull Object another) {
        if (!(another instanceof MHSEvent)) return -1;
        int[] aa = ((MHSEvent) another).getDateFields();
        if (YYYY < aa[2]) {
            return -1;
        } else if (YYYY > aa[2]) {
            return 1;
        } else {
            if (MM < aa[0]) {
                return -1;
            } else if (MM > aa[0]) {
                return 1;
            } else {
                if (DD < aa[1]) {
                    return -1;
                } else if (DD > aa[1]) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    }
}
