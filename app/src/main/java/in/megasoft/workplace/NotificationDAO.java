package in.megasoft.workplace;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    private final DatabaseHelper dbHelper;

    public NotificationDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void insertNotification(String title, String body) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_TITLE, title);
        values.put(DatabaseHelper.COL_BODY, body);
        db.insert(DatabaseHelper.TABLE_NAME, null, values);
        db.close();
    }

    public List<String> getAllNotifications() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
            DatabaseHelper.TABLE_NAME,
            null, null, null, null, null,
            DatabaseHelper.COL_TIMESTAMP + " DESC"
        );
        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TITLE));
                String body = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BODY));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TIMESTAMP));
                list.add(timestamp + " - " + title + ": " + body);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }
}
