package in.megasoft.workplace;

import static in.megasoft.workplace.DatabaseHelper.COL_BODY;
import static in.megasoft.workplace.DatabaseHelper.COL_TIMESTAMP;
import static in.megasoft.workplace.DatabaseHelper.COL_TITLE;
import static in.megasoft.workplace.DatabaseHelper.TABLE_NAME;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    private final DatabaseHelper dbHelper;

    public NotificationDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void insertNotification(String title, String body) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_TITLE, title);
            values.put(COL_BODY, body);
            long result = db.insert(TABLE_NAME, null, values);

            if (result == -1) {
                Log.e("DB_SAVE", "❌ Failed to insert: " + title + " | " + body);
            } else {
                Log.d("DB_SAVE", "✅ Saved notification: " + title + " | " + body);
            }
        } catch (Exception e) {
            Log.e("DB_SAVE", "Error inserting notification", e);
        } finally {
            if (db != null) db.close();
        }
    }


    public List<String> getAllNotifications() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
            TABLE_NAME,
            null, null, null, null, null,
            COL_TIMESTAMP + " DESC"
        );
        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE));
                String body = cursor.getString(cursor.getColumnIndexOrThrow(COL_BODY));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COL_TIMESTAMP));
                list.add(timestamp + " - " + title + ": " + body);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<String> getAllNotificationsAsText() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, COL_TIMESTAMP + " DESC");
        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE));
                String body = cursor.getString(cursor.getColumnIndexOrThrow(COL_BODY));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COL_TIMESTAMP));
                list.add(title + " - " + body + "\n(" + timestamp + ")");
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

}
