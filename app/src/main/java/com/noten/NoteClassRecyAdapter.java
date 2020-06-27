package com.noten;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NoteClassRecyAdapter extends RecyclerView.Adapter<NoteClassRecyAdapter.ViewHolder> {
    private static final String TAG = "NoteClassRecyAdapter";
    private int current_item = 0;
    private List<String> classList;
    private List<View> allItem = new ArrayList<View>();
    private Context context;
    private int resId;
    private ViewHolder vh;
    public OnItemClickListener clickListener;
    public OnItemLongClickListener longClickListener;

    public interface  OnItemClickListener{
        void onItemClick(View view, TextView name);
    }
    public interface  OnItemLongClickListener{
        void onItemLongClick(View view);
    }

    public void setOnItemClcikListen(OnItemClickListener listener){
        this.clickListener = listener;
    }

    public void setLongClickListener(OnItemLongClickListener listener){
        this.longClickListener = listener;
    }

    public NoteClassRecyAdapter(List<String> cl, Context context, int resId){
        Log.w(TAG, "NoteClassRecyAdapter: ????"+cl.get(0));
        classList = cl;
        this.context = context;
        this.resId = resId;
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        View con;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.note_class_name);
            con = itemView.findViewById(R.id.note_class_con);
        }

        public void bind(final OnItemClickListener listener,int po){
            itemView.setTag(po);
            if(po == 0){
                itemView.setBackgroundColor(Color.parseColor("#44dd88"));
                name.setTextColor(Color.parseColor("#ffffff"));
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(itemView,name);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    longClickListener.onItemLongClick(v);
                    return false;
                }
            });

            allItem.add(itemView);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(resId,parent,false);
        vh = new ViewHolder(view);
//        view.setOnClickListener();//注册点击事件
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String clName = classList.get(position);
//        Log.w(TAG, "onBindViewHolder: "+clName);
        holder.name.setText(clName);

        if(clickListener!= null){
            holder.bind(clickListener,position);

        }

    }




    public void updateAllItemStyle(){
        for(int i =0;i<allItem.size();i++){
           View ii =  allItem.get(i);
           ii.setBackgroundColor(Color.parseColor("#ffffff"));
            TextView s = ii.findViewById(R.id.note_class_name);
            s.setTextColor(Color.parseColor("#8A000000"));
        }
       View item =  allItem.get(current_item);
        item.setBackgroundColor(Color.parseColor("#44dd88"));
        TextView t = item.findViewById(R.id.note_class_name);
        t.setTextColor(Color.parseColor("#ffffff"));
    }

    public void setCurrentItem(int i){
        current_item = i;
    }
}
