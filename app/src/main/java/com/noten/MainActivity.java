package com.noten;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.util.LogTime;
import com.google.android.material.navigation.NavigationView;
import com.noten.data.ArticleModel;
import com.noten.data.SqlHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private int side_status=0;//侧边栏状态，0为隐藏
    private int current_class = 0;//当前选中的分类
    private int noteNeedDeleteId = 0;
    private View side_btn;
    private ImageButton add_article_btn;
    private ImageButton add_class_btn;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private RecyclerView noteClassRecy;
    private RecyclerView articleRecy;
    private NoteClassRecyAdapter noteClassRecyAdapter;
    private ArticleRecyAdapter articleRecyAdapter;
    private List<String> noteClass = new ArrayList<String>();
    private List<ArticleModel> articleList = new ArrayList<ArticleModel>();
    private SqlHelper sqlHelper;
    private SQLiteDatabase sqLiteDatabase;
    private View noteClassCon;
    private AddClassDialogFrag addClassDialog;
    private DeleteClassDialogFrag deleteClassDialogFrag;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_main);

        side_btn = findViewById(R.id.side_btn_con);
        add_article_btn = findViewById(R.id.add_article_btn);
        add_class_btn = findViewById(R.id.add_class_btn);
        drawerLayout = findViewById(R.id.draw_lay);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.my_toolbar);
        noteClassRecy = findViewById(R.id.class_recy);
        articleRecy = findViewById(R.id.main_article_recy);
        noteClassCon = findViewById(R.id.note_class_con);
        addClassDialog = new AddClassDialogFrag();
        deleteClassDialogFrag = new DeleteClassDialogFrag();
        sqlHelper =new SqlHelper(MainActivity.this,"te",null,1);
        sqLiteDatabase= sqlHelper.getWritableDatabase();
//        setSupportActionBar(toolbar);
//        toolbar.setNavigationIcon(R.drawable.side);


        // 获取权限
        getPermission();

        //设置状态栏颜色
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.mainColor));

        //初始化点击事件
        initClick();

        //获取数据
        getNoteClass();
        getAllArticle();

        //初始化RecyclerView
        initRecy();



    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.w(TAG, "onResume: rererer" );
    }

    private void initRecy(){
        noteClassRecyAdapter = new NoteClassRecyAdapter(noteClass,MainActivity.this,R.layout.note_class_item);
        noteClassRecyAdapter.setOnItemClcikListen(new NoteClassRecyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, TextView name) {
                int tag = (int) view.getTag();
                if(current_class != tag){
                    current_class = tag;
                    //更新
                    noteClassRecyAdapter.setCurrentItem(tag);
                    noteClassRecyAdapter.updateAllItemStyle();
                    if(tag!=0){
                        queryNoteWithClass(noteClass.get(tag));
                    }else{
                        getAllArticle();
                    }
                    articleRecyAdapter.notifyDataSetChanged();
//                    Log.w(TAG, "onItemClick: "+view.getTag());
                }

            }
        });
        noteClassRecyAdapter.setLongClickListener(new NoteClassRecyAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view) {
                int tag = (int)view.getTag();
                if(tag!=0){
                    String na = noteClass.get(tag);
                    deleteClassDialogFrag.setName(na);
                    deleteClassDialogFrag.setDeleteWhat(0);
                    deleteClassDialogFrag.show(getSupportFragmentManager(),"ss");
                }

//                Log.w(TAG, "onItemLongClick: "+tag );
            }
        });
        noteClassRecy.setAdapter(noteClassRecyAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        noteClassRecy.setLayoutManager(linearLayoutManager);

        //
        articleRecyAdapter = new ArticleRecyAdapter(articleList,MainActivity.this,R.layout.main_article_item);
        articleRecyAdapter.setOnItemClcikListen(new ArticleRecyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                Log.w(TAG, "onItemClick: ___" );
                Intent intent = new Intent("note.update");
                int tag =(int) view.getTag();
//                ArticleModel item = articleList
                intent.putExtra("id",tag);
                startActivityForResult(intent,2);
            }
        });
        articleRecyAdapter.setOnItemLongClickListen(new ArticleRecyAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view) {
                int id = (int) view.getTag();
                noteNeedDeleteId = id;
                String ti;
                TextView title = view.findViewById(R.id.main_article_title);
                if(title.getText().toString().length()>5){
                     ti = title.getText().toString().substring(0,5)+"...";
                }else {
                     ti = title.getText().toString();
                }
                deleteClassDialogFrag.setName(ti);
                deleteClassDialogFrag.setDeleteWhat(1);
                deleteClassDialogFrag.show(getSupportFragmentManager(),"note");
            }
        });
        LinearLayoutManager noteRecyLayout = new LinearLayoutManager(MainActivity.this);
        articleRecy.setAdapter(articleRecyAdapter);
        articleRecy.setLayoutManager(noteRecyLayout);
    }

    private void getPermission(){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //没有授权则运行时发起授权申请
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }else if(ActivityCompat.checkSelfPermission(this,Manifest.permission.INTERNET)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.INTERNET},1);
            }else {
                Log.w("权限","已获取");
            }
    }

    private void initClick(){
        side_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(side_status==0){
                    drawerLayout.openDrawer(GravityCompat.START);
                    side_status = 1;
                }else {
                    drawerLayout.closeDrawers();
                    side_status = 0;
                }

            }
        });

        add_class_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addClassDialog.show(getSupportFragmentManager(),"gg");
//                addClassDialog.showDialog();
            }
        });

        addClassDialog.setOnItemClcikListen(new AddClassDialogFrag.OnItemClickListener() {
            @Override
            public void onItemClick(EditText view) {
                Log.w(TAG, "onItemClick: fuckl" );
                String name = view.getText().toString();
                if(!name.equals("")){
                    insetClass(name);//插入数据
                    getNoteClass();// 更新class列表
                    noteClassRecyAdapter.notifyDataSetChanged();
                    addClassDialog.dismiss();
                    Toast.makeText(MainActivity.this, "添加成功", Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(MainActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "onItemClick: 输入不能为空" );
                }

            }
        });

        deleteClassDialogFrag.setOnItemClcikListen(new DeleteClassDialogFrag.OnItemClickListener() {
            @Override
            public void onItemClick(String name,int what) {
                switch (what){
                    case 0:
                        deleteClass(name);
                        break;
                    case 1:
                        deleteNoteWithId(noteNeedDeleteId);
                }

            }
        });
        add_article_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("note.add");
                startActivityForResult(intent,1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if(resultCode==RESULT_OK){
                    updateNoteList();
                }
                break;
            case 2:
                if(resultCode==RESULT_OK){
                    updateNoteList();
                }
                break;
        }
    }

    private void updateNoteList(){
        getAllArticle();
        articleRecyAdapter.notifyDataSetChanged();
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

    private void getAllArticle(){
        Cursor cur = sqLiteDatabase.query("note",new String[]{"id","title","content","time","class"},null,null,null,null,null);
        List<ArticleModel> data = new ArrayList<ArticleModel>();
        while (cur.moveToNext()){
            ArticleModel item = new ArticleModel();
            item.setTitle(cur.getString(cur.getColumnIndex("title")));
            item.setContent(cur.getString(cur.getColumnIndex("content")));
            item.setTime("创建时间："+cur.getString(cur.getColumnIndex("time")));
            item.setClassN(cur.getString(cur.getColumnIndex("class")));
            item.setId(cur.getInt(cur.getColumnIndex("id")));
            data.add(item);
//            data.add(cur.(cur.getColumnIndex("name")));
        }
        articleList.clear();
        articleList.addAll(data);
    }

    private void insetClass(String name){
        if(!noteClass.contains(name)){
            ContentValues contentValues = new ContentValues();
            contentValues.put("name",name);
            sqLiteDatabase.insert("noteClass",null,contentValues);

        }else {
            Toast.makeText(MainActivity.this, "已存在分类", Toast.LENGTH_SHORT).show();
        }

    }

    private void deleteClass(String name){
            int id = queryClass(name);
            String ids = Integer.toString(id);
            sqLiteDatabase.delete("noteClass","id=?",new String[]{ids});
            getNoteClass();// 更新class列表
            noteClassRecyAdapter.notifyDataSetChanged();//刷新recylerview
            Toast.makeText(MainActivity.this, "已删除", Toast.LENGTH_SHORT).show();
            deleteClassDialogFrag.dismiss();
    }

    private int  queryClass(String n){
        Cursor c = sqLiteDatabase.rawQuery("select * from noteClass where name=?",new String[]{n});
        c.moveToNext();
        int id = c.getInt(c.getColumnIndex("id"));
        return id;
    }

//    private void selectClass(String className){
//        for(int i=0){
//
//        }
//    }

    private void queryNoteWithClass(String className){
       Cursor c = sqLiteDatabase.query("note",null,"class=?",new String[]{className},null,null,null);
       List<ArticleModel> noteOneClass = new ArrayList<ArticleModel>();
        while (c.moveToNext()){
            ArticleModel item = new ArticleModel();
            item.setTitle(c.getString(c.getColumnIndex("title")));
            item.setContent(c.getString(c.getColumnIndex("content")));
            item.setClassN(c.getString(c.getColumnIndex("class")));
            item.setTime(c.getString(c.getColumnIndex("time")));
            item.setId(c.getInt(c.getColumnIndex("id")));
            noteOneClass.add(item);
        }
        articleList.clear();
        articleList.addAll(noteOneClass);
    }

    private void deleteNoteWithId(int id){
        sqLiteDatabase.delete("note","id=?",new String[]{Integer.toString(id)});
        if(current_class!=0){
            String className = noteClass.get(current_class);
            queryNoteWithClass(className);//更新文章list
        }else{
            getAllArticle();//更新文章list
        }
        articleRecyAdapter.notifyDataSetChanged();//刷新recylerview
        Toast.makeText(MainActivity.this, "已删除", Toast.LENGTH_SHORT).show();
        deleteClassDialogFrag.dismiss();
    }

    private void addFrag(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.)
    }



}
