package cn.com.telecomphone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 17-8-18.
 */

public class DBOperate {

    public static final String DB_NAME = "BlackName";
    public static final int VERSION = 1;
    private static DBOperate dbOperate;
    private SQLiteDatabase db;

    private DBOperate(Context context) {
        MyHelpDB myHelpDB = new MyHelpDB(context, DB_NAME, null, VERSION);
        db = myHelpDB.getWritableDatabase();
    }


    public static synchronized DBOperate getDbOperate(Context context) {
        if (dbOperate == null) {
            dbOperate = new DBOperate(context);
        }
        return dbOperate;
    }

    public boolean saveBlackName(String phoneNumber) {

        List<String> num = new ArrayList<>();
        Cursor cursor = db.query("BlackName", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Person person = new Person();
                person.setPhoneNumber(cursor.getString(cursor.getColumnIndex("phone")));
                num.add(person.getPhoneNumber());
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();
        for (int i = 0; i < num.size(); i++) {
            if (num.get(i).equals(phoneNumber)) {
                return false;
            }

        }

        ContentValues values = new ContentValues();
        values.put("phone", phoneNumber);
        db.insert("BlackName", null, values);

        return true;


    }

    public List<Person> getBlackName(List list) {
        if (list.size() > 0) {
            list.clear();
        }
        Cursor cursor = db.query("BlackName", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Person person = new Person();
                person.setId(cursor.getInt(cursor.getColumnIndex("id")));
                person.setPhoneNumber(cursor.getString(cursor.getColumnIndex("phone")));
                list.add(person);
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();
        return list;

    }


    public void deleteBlackName(String number) {
        db.delete("BlackName", "phone=?", new String[]{number});


    }

}
