/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author DeliveryExpress
 */
public  class DateUtils {
        
         // Formato deseado
        static String DateTimeFormatPatternformat = "dd/MMM/yy h:mm a";


        public static String getTodayDate() {

            long unixToday = Instant.now().getEpochSecond();
            Instant instant = Instant.ofEpochSecond(unixToday);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateTimeFormatPatternformat);
            Date date = Date.from(instant);

            return simpleDateFormat.format(date);

        }

        public static String getNowDate() {
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateTimeFormatPatternformat);
            return simpleDateFormat.format(date);
        }

        public static long getUnixTimeStamp() {

            long unixTime = System.currentTimeMillis() / 1000L;
            return unixTime;
        }

        public static int getWeekNumer() {

            Calendar calendar = Calendar.getInstance(Locale.getDefault());

            int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);

            return weekOfYear;

        }

        public static long getLastSundayOfThisWeekAt7amUNIX() {

            LocalDateTime loc = LocalDate
                    .now()
                    .with(
                            TemporalAdjusters.previous(DayOfWeek.SUNDAY)
                    ).atTime(7, 0);
            ZoneId zoneId = ZoneId.systemDefault();
            long epoch = loc.atZone(zoneId).toEpochSecond();

            System.out.println("getLastSundayOfThisWeek " + loc.toString());
            System.out.println("getLastSundayOfThisWeek UNIX " + epoch);

            return epoch;

        }

        public static long getTodayAtStartTimeStamp() {

            long todayAtStart = atStartOfDay(new Date());

            return todayAtStart;
        }

        public static long getThisWeekSundayAtStartTimeStamp() {

            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            long thisWeekAtStart = atStartOfDay(c.getTime()); //this week's sunday at 00:00 am
            return thisWeekAtStart;

        }

        public static long getThisMonthAtStartTimeStamp() {
            Calendar md1 = Calendar.getInstance();
            md1.set(Calendar.DAY_OF_MONTH, 1);
            long thisMonthFirstDayAtStart = atStartOfDay(md1.getTime()); //First day of this month at 00:00 am
            return thisMonthFirstDayAtStart;
        }

        public static String secondsToHHMMSS(int seconds) {

            int p1 = seconds % 60;
            int p2 = seconds / 60;
            int p3 = p2 % 60;
            p2 = p2 / 60;

            return p2 + ":" + p3 + ":" + p1;

        }

        public static String unixToDate(long unix) {
            Instant instant = Instant.ofEpochSecond(unix);
          
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateTimeFormatPatternformat);

            Date date = Date.from(instant);
            return simpleDateFormat.format(date);
        }

        public static long getTimeElapsedSeconds(long a, long b) {

            return a - b;

        }

        public static long dateToUnix(String creationDate) {

            try {
                DateFormat dateFormat = new SimpleDateFormat(DateTimeFormatPatternformat);
                Date date = dateFormat.parse(creationDate);
                long unixTime = (long) date.getTime() / 1000;
                return unixTime;
            } catch (ParseException ex) {
                Logger.getLogger(DateUtils.class.getName()).log(Level.SEVERE, null, ex);
            }

            return 0;

        }

        public static Long atStartOfDay(Date date) {
            LocalDateTime localDateTime = dateToLocalDateTime(date);
            LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);

            return startOfDay.toEpochSecond(ZoneOffset.UTC);

        }

        public static Long atEndOfDay(Date date) {
            LocalDateTime localDateTime = dateToLocalDateTime(date);
            LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
            return endOfDay.toEpochSecond(ZoneOffset.UTC);
        }

        private static LocalDateTime dateToLocalDateTime(Date date) {
            return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        }

    }

