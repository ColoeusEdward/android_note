package com.noten.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class SqlHelper extends SQLiteOpenHelper {
    private static final String TAG = "SqlHelper";
    private String noteClassTBName = "noteClass";
    private String noteClassName = "name";
    private String firstClass ="全部";

    private String noteTBName = "note";
    private String noteTitle = "title";
    private String noteCreateTime = "time";
    private String noteContent = "content";
    private String noteClass = "class";


    public SqlHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建表
        StringBuilder sqlNoteClass = new StringBuilder();
        sqlNoteClass.append("CREATE TABLE ");
        sqlNoteClass.append(noteClassTBName);
        sqlNoteClass.append(" (");
        sqlNoteClass.append( " id"+ " integer PRIMARY KEY autoincrement NOT NULL,");
        sqlNoteClass.append(noteClassName + " TEXT NOT NULL ");
        sqlNoteClass.append(");");

        StringBuilder sqlNote = new StringBuilder();
        sqlNote.append("CREATE TABLE ");
        sqlNote.append(noteTBName);
        sqlNote.append(" (");
        sqlNote.append( "id"+ " integer PRIMARY KEY autoincrement NOT NULL, ");
        sqlNote.append(noteTitle + " TEXT NOT NULL, ");
        sqlNote.append(noteCreateTime + " TEXT NOT NULL, ");
        sqlNote.append(noteContent + " TEXT NOT NULL, ");
        sqlNote.append(noteClass + " TEXT NOT NULL, ");
        sqlNote.append("FOREIGN KEY (class) REFERENCES "+noteClassTBName+" ("+noteClassName+")");
        sqlNote.append(");");

        db.execSQL(sqlNoteClass.toString());
        db.execSQL(sqlNote.toString());

        //class初始化
        db.execSQL("INSERT INTO noteClass(name) VALUES('"+firstClass+"');");//插入一个"全部"类

        Log.w(TAG, "onCreate: 数据库已经创建");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
