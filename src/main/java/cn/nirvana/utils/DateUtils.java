package cn.nirvana.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * @author
 * @Description ${TODO}
 * @Date 2019/6/24 18:52
 **/
public class DateUtils {

    public static final Date getIntervalTimePreDay(int intervalTime) {
        //得到一个Calendar的实例
        Calendar ca = Calendar.getInstance();
        //设置时间为当前时间
        ca.setTime(new Date());
        ca.add(Calendar.DATE, -intervalTime);
        Date date = ca.getTime();
        return date;
    }
}
