package com.hxd.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtil {
    private static Logger logger = LoggerFactory.getLogger(TimeUtil.class);
    public static SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Date[] getWeekDay() {
        Calendar calendar = Calendar.getInstance();

        while (calendar.get(7) != 2) {
            calendar.add(7, -1);
        }

        Date[] dates = new Date[7];

        for (int i = 0; i < 7; ++i) {
            dates[i] = calendar.getTime();
            calendar.add(5, 1);
        }

        return dates;
    }

    public static Date[] getMonthDay() {
        Calendar calendar = Calendar.getInstance();

        while (calendar.get(2) + 1 != 1) {
            calendar.add(2, -1);
        }

        Date[] dates = new Date[12];

        for (int i = 0; i < 12; ++i) {
            dates[i] = calendar.getTime();
            calendar.add(2, 1);
        }

        return dates;
    }

    public static String formatTime(String time) {

        try {

            Date parse = dateTimeFormatter.parse(time);
            return dateTimeFormatter.format(parse);
        } catch (Exception e) {
        }
        return time;
    }

    public static String dateTimeOfLongToStringTime(Long time) {
        return new Date(time).toLocaleString();
    }

    public static String formateStringTime(String time) {
        String realTime = "";
        if (time == null) {
            return realTime;
        }
        try {
            Pattern pattern = Pattern.compile("[0-9]*");
            boolean allNumber = pattern.matcher(time).matches();//111111
            if (allNumber) {
                realTime = dateTimeOfLongToStringTime(Long.valueOf(time));
            } else realTime = formatTime(time);
        } catch (NumberFormatException e) {
            realTime = time;
            logger.error(e.getMessage());
        } finally {
            return realTime;
        }

    }

    public static synchronized Long getTimeFromStringTime(String time) {
        try {
            return dateTimeFormatter.parse(time).getTime();
        } catch (ParseException e) {
            logger.info(e.getMessage());
            return null;
        }
    }

    //获取时间片段
    public static synchronized List<SliceOfTime> getSliceOfTime(String type) {
        List<SliceOfTime> list = new ArrayList<>();
        Calendar instance = Calendar.getInstance();
        if (type.equals("1Day")) {
            int currentHour = instance.get(Calendar.HOUR_OF_DAY);
            for (int i = 0; i <= currentHour; i++) {
                SliceOfTime sliceOfTime = new SliceOfTime();
                String hour = String.format("%02d", i);//小时补零
                String startTime = instance.get(Calendar.YEAR) + "-" + (instance.get(Calendar.MONTH) + 1) + "-" + instance.get(Calendar.DAY_OF_MONTH) + " " + hour + ":00:00";
                String endTime;
                if (i < currentHour) {
                    endTime = instance.get(Calendar.YEAR) + "-" + (instance.get(Calendar.MONTH) + 1) + "-" + instance.get(Calendar.DAY_OF_MONTH) + " " + hour + ":59:59";

                } else {
                    endTime = instance.getTime().toLocaleString();
                }
                sliceOfTime.setBeginTime(startTime);
                sliceOfTime.setEndTime(endTime);
                list.add(sliceOfTime);
            }
        } else if (type.equals("3Day") || type.equals("7Day")||type.equals("30Day")) {
            int count = Integer.valueOf(type.substring(0, 1));
            if(type.equals("30Day")){
                count=30;
            }
            for (int i = count; i > 0; i--) {
                Calendar calendar = Calendar.getInstance();
                SliceOfTime sliceOfTime = new SliceOfTime();
                String day = String.format("%02d", (calendar.get(Calendar.DAY_OF_MONTH) - i + 1));//day补零
                calendar.add(Calendar.DAY_OF_MONTH, -i + 1);
                calendar.set(Calendar.HOUR_OF_DAY, 00);
                calendar.set(Calendar.MINUTE, 00);
                calendar.set(Calendar.SECOND, 00);

                String startTime = dateTimeFormatter.format(calendar.getTime());
                String endTime;
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                if (i == 1) {
                    Calendar instance1 = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, instance1.get(Calendar.HOUR_OF_DAY));
                    calendar.set(Calendar.MINUTE, instance1.get(Calendar.MINUTE));
                    calendar.set(Calendar.SECOND, instance1.get(Calendar.SECOND));
                    endTime = dateTimeFormatter.format(new Date());
                } else {
                    endTime = dateTimeFormatter.format(calendar.getTime());
                }
                sliceOfTime.setBeginTime(startTime);
                sliceOfTime.setEndTime(endTime);
                list.add(sliceOfTime);
            }

        } else if (type.equals("1Month")) {
            int day = instance.get(Calendar.DAY_OF_MONTH);
            for (int i = 1; i <= day; i++) {
                SliceOfTime sliceOfTime = new SliceOfTime();
                String realDay = String.format("%02d", i);//day补零
                String startTime = instance.get(Calendar.YEAR) + "-" + (instance.get(Calendar.MONTH) + 1) + "-" + realDay + " 00:00:00";
                String endTime;
                if (i == day) {
                    endTime = instance.getTime().toLocaleString();
                } else {
                    endTime = instance.get(Calendar.YEAR) + "-" + (instance.get(Calendar.MONTH) + 1) + "-" + realDay + " 23:59:59";
                }
                sliceOfTime.setBeginTime(startTime);
                sliceOfTime.setEndTime(endTime);
                list.add(sliceOfTime);
            }
        } else if (type.contains("Month") && !type.equals("1Month")) {
            Pattern compile = Pattern.compile("[^0-9]");
            Matcher matcher = compile.matcher(type);
            int reduce = Integer.valueOf(matcher.replaceAll("").trim());
            for (int i = reduce; i > 0; i--) {//5-6  6-7  7-8
                SliceOfTime slice = new SliceOfTime();
                Calendar ins = Calendar.getInstance();
                ins.add(Calendar.MONTH, -i + 1);
                String time = dateTimeFormatter.format(ins.getTime());//eg:2019-06-20 00:00:00
                String[] time_number = time.split(" ")[0].split("-");
                slice.setBeginTime(time_number[0] + "-" + time_number[1]);
                slice.setEndTime(time_number[0] + "-" + (Integer.valueOf(time_number[1]) + 1));
                list.add(slice);

            }

        } else if (type.equals("1Year")) {
            int currentMonth = instance.get(Calendar.MONTH) + 1;
            //String[] time_number = type.split(" ")[0].split("-");
            int current_year = instance.get(Calendar.YEAR);
            for (int i = 1; i <= currentMonth; i++) {
                SliceOfTime slice = new SliceOfTime();

                slice.setBeginTime(current_year + "-" + String.format("%02d", i));
                slice.setEndTime(current_year + "-" + String.format("%02d", (i + 1)));
                list.add(slice);
            }
        }
        return list;
    }

    //获取本月月初时间
    public static String getBeginTimeOfMonth() {
        Calendar instance = Calendar.getInstance();
        instance.set(instance.get(Calendar.YEAR), instance.get(Calendar.MONTH), 01, 00, 00, 00);
        return dateTimeFormatter.format(instance.getTime());
    }

    //获取本月往前3个月月初时间
    /*public static String getBeginTimeOfMonth_3MonthBefore(){
        Calendar instance = Calendar.getInstance();
        instance.set(instance.get(Calendar.YEAR),instance.get(Calendar.MONTH),01,00,00,00);
        String format = dateTimeFormatter.format(instance.getTime());//2019-03-01 00:00:00
        return dateTimeFormatter.format(instance.getTime());
    }*/

    //根据时间点拼接查询时间
    public static SliceOfTime getQueryTime(String time) {
        Calendar instance = Calendar.getInstance();

        SliceOfTime sliceOfTime = null;
        if (time.contains("时")) {
            sliceOfTime = new SliceOfTime();
            String hour = time.substring(0, time.indexOf("时"));
            instance.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hour));
            instance.set(Calendar.MINUTE, 00);
            instance.set(Calendar.SECOND, 00);
            sliceOfTime.setBeginTime(dateTimeFormatter.format(instance.getTime()));
            instance.set(Calendar.MINUTE, 59);
            instance.set(Calendar.SECOND, 59);
            sliceOfTime.setEndTime(dateTimeFormatter.format(instance.getTime()));
        } else if (time.contains("-")) {       //7-03   2019-07
            sliceOfTime = new SliceOfTime();
            String[] time_number = time.split("-");

            if (Integer.valueOf(time_number[0]) > 12) {//2019-07
                sliceOfTime.setBeginTime(time);
                sliceOfTime.setEndTime(time_number[0] + "-" + (Integer.valueOf(time_number[1]) + 1));
            } else {//7-03
                String day = time_number[1];
                System.out.println(time_number[0]);
                instance.set(Calendar.MONTH, Integer.valueOf(time_number[0]) - 1);
                instance.set(Calendar.DAY_OF_MONTH, Integer.valueOf(day));
                instance.set(Calendar.HOUR_OF_DAY, 00);
                instance.set(Calendar.MINUTE, 00);
                instance.set(Calendar.SECOND, 00);
                sliceOfTime.setBeginTime(dateTimeFormatter.format(instance.getTime()));
                instance.set(Calendar.HOUR_OF_DAY, 23);
                instance.set(Calendar.MINUTE, 59);
                instance.set(Calendar.SECOND, 59);
                sliceOfTime.setEndTime(dateTimeFormatter.format(instance.getTime()));
            }

        } else if (time.equals("1Day")) {
            sliceOfTime = new SliceOfTime();
            instance.set(Calendar.HOUR_OF_DAY, 00);
            instance.set(Calendar.MINUTE, 00);
            instance.set(Calendar.SECOND, 00);
            sliceOfTime.setBeginTime(dateTimeFormatter.format(instance.getTime()));
            sliceOfTime.setEndTime(dateTimeFormatter.format(Calendar.getInstance().getTime()));
        } else if (time.contains("Day") && !time.equals("1Day")) {//3天，7天
            Pattern compile = Pattern.compile("[^0-9]");
            Matcher matcher = compile.matcher(time);
            int reduce = Integer.valueOf(matcher.replaceAll("").trim()) - 1;
            sliceOfTime = new SliceOfTime();
            instance.add(Calendar.DAY_OF_MONTH, -reduce);
            instance.set(Calendar.HOUR_OF_DAY, 00);
            instance.set(Calendar.MINUTE, 00);
            instance.set(Calendar.SECOND, 00);
            sliceOfTime.setBeginTime(dateTimeFormatter.format(instance.getTime()));
            sliceOfTime.setEndTime(dateTimeFormatter.format(Calendar.getInstance().getTime()));
        } else if (time.equals("1Month")) {
            sliceOfTime = new SliceOfTime();
            instance.set(Calendar.DAY_OF_MONTH, 01);
            instance.set(Calendar.HOUR_OF_DAY, 00);
            instance.set(Calendar.MINUTE, 00);
            instance.set(Calendar.SECOND, 00);
            sliceOfTime.setBeginTime(dateTimeFormatter.format(instance.getTime()));
            sliceOfTime.setEndTime(dateTimeFormatter.format(Calendar.getInstance().getTime()));

        }

        return sliceOfTime;

    }

    public static SliceOfTime getTodayTime() {
        SliceOfTime time = new SliceOfTime();
        Calendar instance = Calendar.getInstance();
        String endTime = dateTimeFormatter.format(instance.getTime());
        String beginTime = endTime.split(" ")[0] + " 00:00:00";
        time.setBeginTime(beginTime);
        time.setEndTime(endTime);
        return time;
    }

    public static SliceOfTime getYesterdayTime() {
        SliceOfTime time = new SliceOfTime();
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DAY_OF_MONTH, -1);
        instance.set(Calendar.HOUR_OF_DAY, 00);
        instance.set(Calendar.MINUTE, 00);
        instance.set(Calendar.SECOND, 00);
        time.setBeginTime(dateTimeFormatter.format(instance.getTime()));
        instance.set(Calendar.HOUR_OF_DAY, 23);
        instance.set(Calendar.MINUTE, 59);
        instance.set(Calendar.SECOND, 59);
        time.setEndTime(dateTimeFormatter.format(instance.getTime()));
        return time;
    }

    /**
     *  判断某时刻属于哪个时间区间
     * */
    public static String judgeTimeArea(String time){
        long dataTime;
        long dayBegin;
        long dayEnd;
        long weekBegin;
        long weekEnd;
        long monthBegin;
        long monthEnd;
        long yearBegin;
        long yearEnd;
        dayBegin=getDayStartTime(null).getTime();
        dayEnd = getDayEndTime(null).getTime();
        weekBegin=getBeginDayOfWeek().getTime();
        weekEnd=getEndDayOfWeek().getTime();
        monthBegin=getBeginDayOfMonth().getTime();
        monthEnd=getEndDayOfMonth().getTime();
        yearBegin=getBeginDayOfYear().getTime();
        yearEnd=getEndDayOfYear().getTime();
        try {
            dataTime=getTimeFromStringTime(time);
        } catch (Exception e) {
            return "1970";
        }

        if(dataTime>=dayBegin&&dataTime<=dayEnd){
            return "TADAY";
        }else if(dataTime>=weekBegin&&dataTime<=weekEnd){
            return "WEEK";
        }else if(dataTime>=monthBegin&&dataTime<=monthEnd){
            return "MONTH";
        }else if(dataTime>=yearBegin&&dataTime<=yearEnd){
            return "YEAR";
        }
        return "";

    }

    /**
     *  获取当天的开始时间
     * */
    public static Timestamp getDayStartTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d)
            calendar.setTime(d);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }

    /**
     *  获取本周的开始时间
     * */
    @SuppressWarnings("unused")
    public static Date getBeginDayOfWeek() {
        Date date = new Date();
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayofweek == 1) {
            dayofweek += 7;
        }
        cal.add(Calendar.DATE, 2 - dayofweek);
        return getDayStartTime(cal.getTime());
    }
    /**
     *  获取本周的结束时间
     * */
    public static Date getEndDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getBeginDayOfWeek());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        Date weekEndSta = cal.getTime();
        return getDayEndTime(weekEndSta);
    }

    /**
     *  获取本月的开始时间
     * */
    public static Date getBeginDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 1, 1);
        return getDayStartTime(calendar.getTime());
    }

    // 获取本月的结束时间
    public static Date getEndDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 1, 1);
        int day = calendar.getActualMaximum(5);
        calendar.set(getNowYear(), getNowMonth() - 1, day);
        return getDayEndTime(calendar.getTime());
    }

    // 获取本年的开始时间
    public static Date getBeginDayOfYear() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, getNowYear());
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DATE, 1);
        return getDayStartTime(cal.getTime());
    }

    // 获取本年的结束时间
    public static Date getEndDayOfYear() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, getNowYear());
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DATE, 31);
        return getDayEndTime(cal.getTime());
    }


    /**
     *  获取当前年份
     * */
    public static Integer getNowYear() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return Integer.valueOf(gc.get(1));
    }

    /**
     *  获取当前月份
     * */
    public static int getNowMonth() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return gc.get(2) + 1;
    }

    // 获取某个日期的结束时间
    public static Timestamp getDayEndTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d)
            calendar.setTime(d);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return new Timestamp(calendar.getTimeInMillis());
    }






    public static void main(String[] args) {
        List<SliceOfTime> sliceOfTime = getSliceOfTime("30Day");
        sliceOfTime.forEach(sliceOfTime1 -> System.out.println(sliceOfTime1));
    }

}
