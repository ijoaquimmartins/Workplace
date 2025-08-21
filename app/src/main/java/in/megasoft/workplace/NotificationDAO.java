package in.megasoft.workplace;

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

    //  Insert a new notification
    public void insertNotification(String title, String body) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("body", body);
        try {
            db.insert(DatabaseHelper.TABLE_NAME, null, values);
            Log.d("DAO", "Inserted: " + title + " | " + body);
        } catch (Exception e) {
            Log.e("DAO", "Insert failed", e);
        }
        db.close();
    }

    //  Get all notifications (formatted as "title: body")
    public List<String> getAllNotificationsAsText() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME,
                null, null, null, null, null,
                DatabaseHelper.COL_TIMESTAMP + " DESC");

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TITLE));
                String body = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BODY));
                list.add(title + ": " + body);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    //  Clear all (optional helper)
    public void clearAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_NAME, null, null);
        db.close();
    }
}
