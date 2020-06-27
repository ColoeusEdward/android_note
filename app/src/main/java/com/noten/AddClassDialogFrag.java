package com.noten;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.noten.R;

public class AddClassDialogFrag extends DialogFragment {
    private static final String TAG = "AddClassDialogFrag";
    private  Button addBtn;
    private EditText className;
    private  View v;
    public OnItemClickListener clickListener;

    public interface  OnItemClickListener{
        void onItemClick(EditText view);

    }
    public void setOnItemClcikListen(OnItemClickListener listener){
        this.clickListener = listener;
    }

//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        v = inflater.inflate(R.layout.add_class_dialog,container,false);
//        Log.w(TAG, "onCreateView: 执行" );
//        initView(v);
//        return v;
//    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        Log.w(TAG, "onCreateDialog: active" );
//        if (null==v){
//            v = LayoutInflater.from(getActivity()).inflate(R.layout.add_class_dialog,null);
//        }
//
//    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.w(TAG, "onCreateDialog: nonon" );
//        if (null==v){
            v = LayoutInflater.from(getActivity()).inflate(R.layout.add_class_dialog,null);
//        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        initView(v);
        return builder.create();
    }

    public void initView(View vv){
        Log.w(TAG, "fuck: dfd");
        addBtn = vv.findViewById(R.id.add_class_dia_btn);
        className =(EditText) vv.findViewById(R.id.note_class_input);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(className);
            }
        });
    }



}
