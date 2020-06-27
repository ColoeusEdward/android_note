package com.noten;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.noten.data.ArticleModel;
import com.noten.data.SqlHelper;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class UpdateNoteAct extends AppCompatActivity {
    private static final String TAG = "UpdateNoteAct";
    private Button updateBtn;
    private EditText title;
    private EditText content;
    private SpinnerAdapter spinnerAdapter;
    private Spinner classSp;
    private String currentClass="";
    private View backBtn;
    private SqlHelper sqlHelper;
    private SQLiteDatabase sqLiteDatabase;
    private List<String> noteClass = new ArrayList<String>();
    private ArticleModel item = new ArticleModel();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_note_act);

        updateBtn = findViewById(R.id.update_note_act_btn);
        backBtn = findViewById(R.id.update_note_back_btn_con);
        title = findViewById(R.id.update_note_title);
        content = findViewById(R.id.update_note_act_content);
        classSp = findViewById(R.id.update_note_class_spinner);

        sqlHelper = new SqlHelper(this,"te",null,1);
        sqLiteDatabase = sqlHelper.getWritableDatabase();
        //设置状态栏颜色
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.mainColor));
        getNoteClass();
        getNoteDataItem();
        initSnipper();
        putValue();
        initClick();
    }
    private void getNoteDataItem(){
        int id = getIntent().getIntExtra("id",0);
        queryNoteWithId(id);
    }

    private void initSnipper(){
        spinnerAdapter = new ArrayAdapter<String>(UpdateNoteAct.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,noteClass);
        classSp.setAdapter(spinnerAdapter);
        classSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentClass = noteClass.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currentClass = item.getClassN();
            }
        });

    }

    private void putValue(){
        title.setText(item.getTitle());
        content.setText(item.getContent());

        int po = noteClass.indexOf(item.getClassN());
        Log.w(TAG, "putValue: "+po);
        classSp.setSelection(po,true);
    }

    private void initClick(){
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNote();
                setResult(RESULT_OK);
                finish();
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

    private void updateNote(){
        ContentValues values = new ContentValues();
        values.put("title",title.getText().toString());
        values.put("content",content.getText().toString());
        values.put("class",currentClass);
        sqLiteDatabase.update("note",values,"id=?",new String[]{Integer.toString(item.getId())});
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

    private void queryNoteWithId(int id){
        Cursor c = sqLiteDatabase.query("note",null,"id=?",new String[]{Integer.toString(id)},null,null,null);
//        List<ArticleModel> noteOneClass = new ArrayList<ArticleModel>();
        c.moveToNext();
//        ArticleModel i = new ArticleModel();
        item.setTitle(c.getString(c.getColumnIndex("title")));
        item.setContent(c.getString(c.getColumnIndex("content")));
        item.setClassN(c.getString(c.getColumnIndex("class")));
        item.setTime(c.getString(c.getColumnIndex("time")));
        item.setId(c.getInt(c.getColumnIndex("id")));
//        noteOneClass.add(item);
//        articleList.clear();
//        articleList.addAll(noteOneClass);
    }
}
