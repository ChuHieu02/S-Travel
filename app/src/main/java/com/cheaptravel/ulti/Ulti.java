package com.cheaptravel.ulti;

import android.content.Context;
import android.widget.Toast;

public class Ulti {
    public static class TimeAgo {
        private static final int SECOND_MILLIS = 1000;
        private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

        public static final String getTimeAgo(long time) {
            if (time < 1000000000000L) {
                time *= 1000;
            }
            long now = System.currentTimeMillis();
            if (time > now || time <= 0) {
                return null;
            }
            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "vừa xong";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "1 phút trước";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " phút trước";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "1 giờ trước";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " giờ trước";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "hôm qua";
            } else {
                return diff / DAY_MILLIS + " ngày trước";
            }
        }

    }


}
