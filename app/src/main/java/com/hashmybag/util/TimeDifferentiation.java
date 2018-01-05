package com.hashmybag.util;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by cp-android on 4/7/16.
 */
public class TimeDifferentiation {
    public String getTimeDifference(String fromTime) {
        String difference = "";
        String fromDate;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = sdf.format(c.getTime());
        String lastString = fromTime.substring(fromTime.length() - 1);
        String l1 = fromTime.substring(fromTime.length() - 5);
        String l5 = String.valueOf(l1.charAt(0));

        if (lastString.equals("M")) {
            DateFormat df = new SimpleDateFormat("dd MMM yy HH:mm a");
            Date startDate = null;
            try {
                startDate = df.parse(fromTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            fromDate = sdf.format(startDate);
        } else if (lastString.equals("C")) {
            DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm Z");
            Date startDate = null;
            try {
                startDate = df.parse(fromTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            fromDate = sdf.format(startDate);
        } else if (l5.equals("+")) {
            DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm Z");
            Date startDate = null;
            try {
                startDate = df.parse(fromTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            fromDate = sdf.format(startDate);
        } else {
            fromDate = fromTime;
        }

        try {
            String currentArray[] = new String[6];
            currentArray[0] = (currentDate.split(" "))[0].split("-")[0];
            currentArray[1] = (currentDate.split(" "))[0].split("-")[1];
            currentArray[2] = (currentDate.split(" "))[0].split("-")[2];

            currentArray[3] = (currentDate.split(" "))[1].split(":")[0];
            currentArray[4] = (currentDate.split(" "))[1].split(":")[1];
            currentArray[5] = (currentDate.split(" "))[1].split(":")[2];

            // currentArray[5] = "00";aa


            String fromDateArray[] = new String[6];
            fromDateArray[0] = (fromDate.split(" "))[0].split("-")[0];
            fromDateArray[1] = (fromDate.split(" "))[0].split("-")[1];
            fromDateArray[2] = (fromDate.split(" "))[0].split("-")[2];

            fromDateArray[3] = (fromDate.split(" "))[1].split(":")[0];
            fromDateArray[4] = (fromDate.split(" "))[1].split(":")[1];
            fromDateArray[5] = (fromDate.split(" "))[1].split(":")[2];

            // fromDateArray[5] = "00";
            if (Integer.parseInt(currentArray[0]) == Integer.parseInt(fromDateArray[0])) {
                if (Integer.parseInt(currentArray[1]) == Integer.parseInt(fromDateArray[1])) {
                    if (Integer.parseInt(currentArray[2]) == Integer.parseInt(fromDateArray[2])) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        Date date1 = format.parse(fromDate);
                        Date date2 = format.parse(currentDate);
                        long d1 = date1.getTime();
                        long d2 = date2.getTime();
                        long differenced = date2.getTime() - date1.getTime();
                        long diffSeconds = differenced / 1000 % 60;
                        long diffMinutes = differenced / (60 * 1000) % 60;
                        long diffHours = differenced / (60 * 60 * 1000) % 24;
                        long diffDays = differenced / (24 * 60 * 60 * 1000);

                        if (diffHours == 0) {
                            if (diffMinutes == 0) {
                                difference = "just now ";
                            } else {
                                difference = Math.abs(diffMinutes) + " minutes ago";
                            }
                        } else if (diffHours == 1) {
                            difference = Math.abs(diffHours) + " hour ago";
                        } else {
                            difference = Math.abs(diffHours) + " hour ago";
                        }
                    } else {
                        if ((Integer.parseInt(currentArray[2]) - Integer.parseInt(fromDateArray[2])) == 1) {
                            difference = "yesterday";
                        } else {
                            difference = fromDateArray[2] + " " + getMonthFromNumber(Integer.parseInt(fromDateArray[1]));
                        }
                    }
                } else {
                    difference = fromDateArray[2] + " " + getMonthFromNumber(Integer.parseInt(fromDateArray[1]));
                }
            } else {
                difference = fromDateArray[2] + " " + getMonthFromNumber(Integer.parseInt(fromDateArray[1])) + " " + fromDateArray[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.print("Date text" + difference);
        return difference;
    }

    String getMonthFromNumber(int monthNumber) {
        String month = "";
        switch (monthNumber) {
            case 1:
                month = "Jan";
                break;
            case 2:
                month = "Feb";
                break;
            case 3:
                month = "Mar";
                break;
            case 4:
                month = "Apr";
                break;
            case 5:
                month = "May";
                break;
            case 6:
                month = "Jun";
                break;
            case 7:
                month = "Jul";
                break;
            case 8:
                month = "Aug";
                break;
            case 9:
                month = "Sep";
                break;
            case 10:
                month = "Oct";
                break;
            case 11:
                month = "Nov";
                break;
            case 12:
                month = "Dec";
                break;
        }
        return month;
    }

    public String getCurrentDate() {
        int Hh, mm;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.US);

        return sdf.format(cal.getTime());
    }

    public String getCurrentDateForInbox() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

        return sdf.format(cal.getTime());
    }

    public String getCurrentDateMsg() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.US);


        String fromDate = sdf.format(cal.getTime());
        return getMsgDate(fromDate);
    }

    public String getDateFormat(String created_at) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.US);
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm Z", Locale.US);

        Date startDate = null;

        try {
            startDate = df.parse(created_at);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String dateFormat = sdf.format(startDate);
        return sdf.format(startDate);
    }

    public String getMsgDate(String created_at) {
        String str = null;
        long stringDate;
        try {
            String msgDate[] = created_at.split(" ");
            String msgDay[] = msgDate[0].split("/");
            String day = msgDay[0];
            String month = msgDay[1];
            String year = msgDay[2];
            stringDate = Long.parseLong(day + month + year);

            str = Long.toString(stringDate);
            if (str.length() == 7) {
                str = new StringBuffer(str).insert(1, "/").toString();
                str = "0" + (new StringBuffer(str).insert(4, "/").toString());
            } else {
                str = new StringBuffer(str).insert(2, "/").toString();
                str = new StringBuffer(str).insert(5, "/").toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("Message Date", str);
        return str;
    }

}