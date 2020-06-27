package com.noten;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.noten.data.SqlHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddNoteAct extends AppCompatActivity {
    private static final String TAG = "AddNoteAction";
    private String currentClass = "";
    private Spinner classSp;
    private EditText title;
    private Button addNoteBtn;
    private View backBtn;
    private EditText content;
    private SqlHelper sqlHelper;
    private SQLiteDatabase sqLiteDatabase;
    private List<String> noteClass = new ArrayList<String>();
    private SpinnerAdapter spinnerAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_note_act);
        sqlHelper =new SqlHelper(AddNoteAct.this,"te",null,1);
        sqLiteDatabase= sqlHelper.getWritableDatabase();
        classSp = (Spinner) findViewById(R.id.add_note_class_spinner);
        title  = findViewById(R.id.add_note_title);
        addNoteBtn = findViewById(R.id.add_note_act_btn);
        content = findViewById(R.id.add_note_act_content);
        backBtn = findViewById(R.id.add_note_back_btn_con);


        //设置状态栏颜色
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.mainColor));
        getNoteClass();
        initSnipper();
        initClick();

    }

    private void initSnipper(){
        spinnerAdapter = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,noteClass);
        classSp.setAdapter(spinnerAdapter);
        classSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentClass = noteClass.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    private void initClick(){
        addNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void getNoteClass(){

        //获取所有分类
        Cursor cur = sqLiteDatabase.query("noteClass",new String[]{"name"},null,null,null,null,null);
        List<String> data = new ArrayList<String>();
        while (cur.moveToNext()){
            data.add(cur.getString(cur.getColumnIndex("name")));
        }
        noteClass.clear();
        noteClass.addAll(data);
//        Log.w(TAG, "getNoteClass: "+data.get(0));
    }

    private void saveNote(){
        String time = getTime();
        String titieS = title.getText().toString();
        String contentS = content.getText().toString();
        if(!titieS.equals("")&&!contentS.equals("")&&!currentClass.equals("")){
            ContentValues contentValues = new ContentValues();
            contentValues.put("title",titieS);
            contentValues.put("content",contentS);
            contentValues.put("time",time);
            contentValues.put("class",currentClass);

            sqLiteDatabase.insert("note",null,contentValues);
            Toast.makeText(AddNoteAct.this, "保存成功", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent();
            setResult(RESULT_OK);
            finish();
        }else {
            Toast.makeText(AddNoteAct.this, "输入不能为空", Toast.LENGTH_SHORT).show();
        }



    }
    private String getTime(){
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String ti = format1.format(date);
        return ti;
    }
}
