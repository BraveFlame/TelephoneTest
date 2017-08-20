package cn.com.telecomphone;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 17-8-18.
 */

public class MyHelpDB extends SQLiteOpenHelper {
    public static final String CREATE_BLACK_NAME = "create table BlackName(" +
            "id integer primary key autoincrement,phone text)";
    public static final String CREATE_BLACK_RECORD = "create table BlackRecord(" +
            "id integer primary key autoincrement,phone text,)";
    private Context mContext;

    public MyHelpDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BLACK_NAME);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
