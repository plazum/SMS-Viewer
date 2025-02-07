package org.zrtech.sms_viewer;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.Telephony;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SmsGetter {
    public static String text(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            new String[]{
                Telephony.Sms.ADDRESS,
                Telephony.Sms.DATE,
                Telephony.Sms.BODY
            },
            null,
            null,
            Telephony.Sms.DATE + " DESC"
        );

        StringBuilder smsBuilder = new StringBuilder();

        if (cursor != null && cursor.moveToFirst()) {
            int i = 0;
            do {
                i++;
                @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS));
                @SuppressLint("Range") long date_long = cursor.getLong(cursor.getColumnIndex(Telephony.Sms.DATE));
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                Date date = new Date(date_long);
                String date_string = sdf.format(date);
                @SuppressLint("Range") String body = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY));
                smsBuilder
                    .append("发自：")
                    .append(address)
                    .append("\n时间：")
                    .append(date_string)
                    .append("\n内容：")
                    .append(body)
                    .append("\n\n");
            } while (i < MainActivity.count && cursor.moveToNext());
            cursor.close();
        }

        if (smsBuilder.length() > 2) {
            smsBuilder.setLength(smsBuilder.length() - 2);  // 去除最后两个多余的回车
        }
        return smsBuilder.toString();
    }
}
