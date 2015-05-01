//Filename: MHSEvent.java
/**
 * This is a class for each event
 * This object is for storing an event (1 line from the CSV file)
 * Created by Andrew on 1/19/2015.
 * @author Andrew Katz
 * @version 1.0
 */

package com.aakportfolio.www.fbla2015;

//Import section

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;
//End of imports

public class MHSEvent implements Serializable, Comparable {

    //Instance variables. They have some defaults here for purposes of examples

    boolean isAllDay = true;
    //Event name
    private String eventName = "Untitled Event";
    //Event Description
    private String eventDescription = "No Description";
    //Email Address
    private String contactEmail = "events-temp@mamkschools.org";
    //Date variables
    private int MM = 00;
    private int DD = 00;
    private int YYYY = 0000;
    private String eventStartDate = "00/00/0000";
    private String eventEndDate = "00/00/0000";
    //Millisecond variables
    private long startMillisec = 0;
    private long endMillisec = 0;

    /**
     * Constructor for event.
     *
     * @param name        Event name
     * @param Description Event Description
     * @param startDate   Event date (MM/DD/YYYY)
     * @param startTime   Start time for event (HH:MM AMPM)
     * @param endDate     Ending date of event (MM/DD/YYYY)
     * @param endTime     End time (HH:MM AMPM)
     * @param email       email address
     */
    public MHSEvent(String name, String Description, String startDate, String startTime,
                    String endDate, String endTime, String email, String type) {
        //Initialize variables
        eventName = name;
        startMillisec = 0;
        endMillisec = 0;
        isAllDay = true;
        eventDescription = eventName + "\n";
        if (!startTime.trim().equals("none")) {
            if (startTime.trim().equals("allday")) {
                //If there is an all day event for start time, note it
                eventDescription += "All day event" + "\n";
            } else {
                //If we have specific times...
                try {
                    {
                        //Try to calculate milliseconds
                        long hours = Long.parseLong(startTime.split(":")[0]);
                        long minutes = Long.parseLong(startTime.split(":")[1].split(" ")[0]);
                        String ampm = startTime.substring(startTime.indexOf(" ") + 1);
                        if (ampm.equals("PM")) hours += 12L;
                        minutes += hours * 60L;
                        long seconds = minutes * 60L;
                        //If we reach this point, we have a valid time, so write the variables
                        startMillisec = seconds * 1000L;
                        endMillisec = startMillisec;
                        isAllDay = false;
                        eventDescription += "Start time: " + startTime + "\n";
                    }
                    if (!endTime.trim().equals("none")) {
                        try {
                            //Try to calculate milliseconds
                            long hours = Long.parseLong(endTime.split(":")[0]);
                            long minutes = Long.parseLong(endTime.split(":")[1].split(" ")[0]);
                            String ampm = startTime.substring(endTime.indexOf(" ") + 1);
                            if (ampm.equals("PM")) hours += 12L;
                            minutes += hours * 60L;
                            long seconds = minutes * 60L;
                            //If we reach this point, we have a valid time, so write variables
                            endMillisec = seconds * 1000L;
                            eventDescription += "End Time: " + endTime + "\n";
                        } catch (Exception e) {
                            //Leave it blank, treat it as if there is no end time
                        }
                    }
                } catch (Exception e) {
                    //Just use the defaults, and treat it as no start time if invalid
                }
            }
        }
        //Set description, if we have one
        if (Description.trim().equals("none")) {
            eventDescription += "No Description";
        } else {
            eventDescription += Description;
        }

        //Try to set dates
        try {
            MM = Integer.parseInt(startDate.split(Pattern.quote("/"))[0]);
            DD = Integer.parseInt(startDate.split(Pattern.quote("/"))[1]);
            YYYY = Integer.parseInt(startDate.split(Pattern.quote("/"))[2]);
            eventEndDate = endDate;
            eventStartDate = startDate;
        } catch (Exception e) {
            //If invalid date, basically nullify the event
            MM = 00;
            DD = 00;
            YYYY = 00;
            eventStartDate = "00/00/0000";
            eventEndDate = "00/00/0000";
            e.printStackTrace();
        }
        //Set the last variable
        contactEmail = email.trim();
    }

    /**
     * This constructor takes an array (from String.split) and passes it to the main constructor.
     *
     * @param inArr Array of All the same paramaters of main constructor
     */
    public MHSEvent(String[] inArr) {
        this(inArr[0], inArr[1], inArr[2], inArr[3], inArr[4], inArr[5], inArr[6],""/*Change to arr 7 */);
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
            return nowYYYY >= YYYY1 && ((nowYYYY != YYYY1) || ((nowMM < MM1) || (nowMM <= MM1 && nowDD <= DD1)));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Compares MHSEvetns by start date. For sorting.
     *
     * @param another
     * @return
     */
    @Override
    public int compareTo(Object another) {
        if (!(another instanceof MHSEvent)) return -1;
        int[] anotherEventDateArray = ((MHSEvent) another).getDateFields();
        if (YYYY < anotherEventDateArray[2]) {
            return -1;
        } else if (YYYY > anotherEventDateArray[2]) {
            return 1;
        } else {
            if (MM < anotherEventDateArray[0]) {
                return -1;
            } else if (MM > anotherEventDateArray[0]) {
                return 1;
            } else {
                if (DD < anotherEventDateArray[1]) {
                    return -1;
                } else if (DD > anotherEventDateArray[1]) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    }

    /**
     * Tostring
     *
     * @return event name
     */
    public String toString() {
        return getEventName();
    }

    //Begin getter methods
    public GregorianCalendar getCalStart() {
        return new GregorianCalendar(YYYY, MM, DD);
    }

    public GregorianCalendar getCalEnd() {
        try {
            int MM1 = Integer.parseInt(eventEndDate.split(Pattern.quote("/"))[0]),
                    DD1 = Integer.parseInt(eventEndDate.split(Pattern.quote("/"))[1]),
                    YYYY1 = Integer.parseInt(eventEndDate.split(Pattern.quote("/"))[2]);
            return new GregorianCalendar(YYYY1, MM1, DD1);
        } catch (Exception e) {
            e.printStackTrace();
            return new GregorianCalendar(YYYY, MM, DD);
        }

    }

    public long startMS() {
        return startMillisec;
    }

    public long endMS() {
        return endMillisec;
    }

    public boolean getIsAllDay() {
        return isAllDay;
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

    public String getEventDates() {
        return eventStartDate.equals(eventEndDate)
                ? "Date: " + eventStartDate
                : "Starts: " + eventStartDate + "\n" + "Ends: " + eventEndDate;
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

    //End of getter methods

    /**
     * Says if email should be shown
     *
     * @return if event should be shown
     */
    public boolean showEmail() {
        return !contactEmail.equals("none");
    }
}
