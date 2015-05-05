//Filename: MHSEvent.java
/**
 * This is a class for each event
 * This object is for storing an event (1 line from the CSV file)
 *
 * Created by Andrew on 1/19/2015.
 * Version 2.0 released 5/4/2015.
 *
 * @author Andrew Katz
 * @version 2.0
 */

package com.aakportfolio.www.fbla2015;

//Import section

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;
//End of imports

public class MHSEvent implements Serializable, Comparable {

    //Instance variables. They have some defaults here for purposes of examples

    //If event is all day
    boolean isAllDay = true;
    //Event name
    private String eventName = "Untitled Event";
    //Event Description
    private String eventDescription = "No Description";
    //Email Address
    private String contactEmail = "events-temp@mamkschools.org";
    //Date variables
    private int MM = 0;
    private int DD = 0;
    private int YYYY = 0;
    private String eventStartDate = "00/00/0000";
    private String eventEndDate = "00/00/0000";
    //Millisecond variables
    private long startMillisec = 0;
    private long endMillisec = 0;
    //Type of event (for graphic)
    private String eventType = "none";


    /**
     * Constructor for event.
     *
     * @param name        Event name
     * @param Description Event Description
     * @param startDate   Event date (MM/DD/YYYY)
     * @param startTime   Start time for event (HH:MM AM/PM)
     * @param endDate     Ending date of event (MM/DD/YYYY)
     * @param endTime     End time (HH:MM AM/PM)
     * @param email       email address
     * @param type        event type. Should be in the hashmap.
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
                            if(MHSConstants.debug){
                                e.printStackTrace();
                            }
                            //Leave it blank, treat it as if there is no end time
                        }
                    }
                } catch (Exception e) {
                    //Just use the defaults, and treat it as no start time if invalid
                }
            }
        }
        //Set description, if we have one
        if (!Description.trim().equals("none")) {
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
            MM = 0;
            DD = 0;
            YYYY = 0;
            eventStartDate = "00/00/0000";
            eventEndDate = "00/00/0000";
            if(MHSConstants.debug) {
                e.printStackTrace();
            }
        }
        //Set the last variables
        contactEmail = email.trim();
        eventType = type.trim();
    }

    /**
     * This constructor takes an array (from String.split) and passes it to the main constructor.
     * Precondition: inArr has length of 8
     * @param inArr Array of all the same paramaters of main constructor
     */
    public MHSEvent(String[] inArr) {
        this(inArr[0], inArr[1], inArr[2], inArr[3], inArr[4], inArr[5], inArr[6], inArr[7]);
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
            if (MHSConstants.debug){
                e.printStackTrace();
            }
            return false;
        }
    }

    /**
     * Compares MHSEvetns by start date. For sorting.
     *
     * @param another Another object to compare to
     * @return value of object comparison (- is less, 0 i equal, 1 is greater)
     */
    @Override
    public int compareTo(Object another) {
        if (another == null || !(another instanceof MHSEvent)) return -1;
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
     * toString: returns event name
     *
     * @return event name
     */
    @Override
    public String toString() {
        return getEventName();
    }

    //Begin getter methods

    /**
     * Getter for start calendar
     * @return returns GregorianCalendar object of start date
     */
    public GregorianCalendar getCalStart() {
        return new GregorianCalendar(YYYY, MM, DD);
    }

    /**
     * Getter for end calendar
     * @return returns GregorianCalendar for event end date
     */
    public GregorianCalendar getCalEnd() {
        try {
            int MM1 = Integer.parseInt(eventEndDate.split(Pattern.quote("/"))[0]),
                    DD1 = Integer.parseInt(eventEndDate.split(Pattern.quote("/"))[1]),
                    YYYY1 = Integer.parseInt(eventEndDate.split(Pattern.quote("/"))[2]);
            return new GregorianCalendar(YYYY1, MM1, DD1);
        } catch (Exception e) {
            if(MHSConstants.debug){
                e.printStackTrace();
            }
            return new GregorianCalendar(YYYY, MM, DD);
        }

    }

    /**
     * getter for eventType
     * @return eventType
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * Getter for start milliseconds
     * @return startMillisec
     */
    public long startMS() {
        return startMillisec;
    }

    /**
     * Getter for end milliseconds
     * @return endMillisec
     */
    public long endMS() {
        return endMillisec;
    }

    /**
     * Getter for all day boolean
     * @return isAllDay
     */
    public boolean getIsAllDay() {
        return isAllDay;
    }

    /**
     * Getter for event name
     * @return eventName
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Getter for event description
     * @return eventDescription
     */
    public String getEventDescription() {
        return eventDescription;
    }

    /**
     * Getter for contact email
     * @return contactEmail
     */
    public String getContactEmail() {
        return contactEmail;
    }

    /**
     * getter for event dates
     * @return If event has only start date, just return that, else return start and end date.
     */
    public String getEventDates() {
        return eventStartDate.equals(eventEndDate)
                ? "Date: " + eventStartDate
                : "Starts: " + eventStartDate
                + "\n" + "Ends: " + eventEndDate;
    }

    /**
     * Getter for start date
     * @return eventStartDate
     */
    public String getEventStartDate() {
        return eventStartDate;
    }

    /**
     * Getter for the end date
     * @return eventEndDate
     */
    public String getEventEndDate() {
        return eventEndDate;
    }

    /**
     * Getter for teh date fields in array
     * @return An array with month, date, year of event start
     */
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
