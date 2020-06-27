package com.noten;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class DeleteClassDialogFrag extends DialogFragment {
    private static final String TAG = "DeleteClassDialogFrag";
    private View v;
    private Button deleteBtn;
    private TextView classN;
    private String name;// 分类名称
    private int deleteWhat=0;//0为删除分类，1为删除文章
    public OnItemClickListener clickListener;

    public interface  OnItemClickListener{
        void onItemClick(String name,int what);

    }
    public void setOnItemClcikListen(OnItemClickListener listener){
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
       v = LayoutInflater.from(getActivity()).inflate(R.layout.delete_class_dialog,null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        initView(v);
        return builder.create();
    }

    public void initView(View v){
        deleteBtn = v.findViewById(R.id.delete_class_btn);
        classN = v.findViewById(R.id.delete_class_name);
        classN.setText(name);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(name,deleteWhat);
            }
        });
    }

    public void setName(String n){
        this.name = n;
    }

    public void setDeleteWhat(int i){
        deleteWhat = i;
    }

}
