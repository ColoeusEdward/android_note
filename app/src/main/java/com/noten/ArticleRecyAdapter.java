package com.noten;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.noten.data.ArticleModel;

import java.util.ArrayList;
import java.util.List;

public class ArticleRecyAdapter extends RecyclerView.Adapter<ArticleRecyAdapter.ViewHolder> {
    private View view;
    private List<ArticleModel> articleList = new ArrayList<ArticleModel>();
    private Context context;
    private int resId;
    private ViewHolder vh;

    public OnItemClickListener clickListener;
    public OnItemLongClickListener longClickListener;

    public interface  OnItemClickListener{
        void onItemClick(View view);
    }
    public interface OnItemLongClickListener{
        void onItemLongClick(View view);
    }

    public void setOnItemClcikListen(OnItemClickListener listener){
        this.clickListener = listener;
    }
    public void setOnItemLongClickListen(OnItemLongClickListener longClickListen){
        this.longClickListener = longClickListen;
    }

    public ArticleRecyAdapter(List<ArticleModel> am,Context context,int resId){
        articleList = am;
        this.context = context;
        this.resId = resId;
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView firstLine;
        TextView time;
        TextView classN;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.main_article_title);
            firstLine = itemView.findViewById(R.id.main_article_first_line);
            time = itemView.findViewById(R.id.main_article_time);
            classN = itemView.findViewById(R.id.main_article_class);
        }

        public void bind(final OnItemClickListener listener,int po){
            int id = articleList.get(po).getId();
            itemView.setTag(id);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(itemView);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    longClickListener.onItemLongClick(itemView);
                    return false;
                }
            });

//            allItem.add(itemView);
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

        ArticleModel item = articleList.get(position);
//        Log.w(TAG, "onBindViewHolder: "+clName);
        holder.title.setText(item.getTitle());
        if(item.getContent().length()>10){
            holder.firstLine.setText(item.getContent().substring(0,10)+"....");
        }else{
            holder.firstLine.setText(item.getContent());
        }

        holder.time.setText(item.getTime());
        holder.classN.setText(item.getClassN());

        if(clickListener!= null){
            holder.bind(clickListener,position);
        }

    }



}
