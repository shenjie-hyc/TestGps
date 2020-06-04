package com.example.testgps.submit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.testgps.R;
import com.example.testgps.model.bean.FileBean;

import java.util.List;


public class ImageAdapter extends RecyclerView.Adapter {
    Context context;
    List<String> list;
    public ImageAdapter(Context context, List<String> list){
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       ViewHolder viewHolder =new ViewHolder(context,LayoutInflater.from(context).inflate(R.layout.item_image,null));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        String fileBean = list != null && list.size() > position ? list.get(position):null;
        viewHolder.setData(fileBean);
        if(list == null){
            viewHolder.image.setVisibility(position ==0 ? View.VISIBLE :View.INVISIBLE);
        }else if(position > list.size()){
            viewHolder.image.setVisibility(View.INVISIBLE);
        }else {
            viewHolder.image.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        public View rootView;
        public ImageView image;
        private String fileBean;

        public ViewHolder(Context context,View rootView){
            super(rootView);
            this.context = context;
            this.rootView = rootView;
            this.image = (ImageView) rootView.findViewById(R.id.image);
            this.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(fileBean == null){
                        ((SubmitActivity)ViewHolder.this.context).clickAddPic();
                    }
                }
            });
        }
        public void setData(String fileBean){
            this.fileBean = fileBean;
            if(fileBean != null){
                Glide.with(rootView.getContext()).load(fileBean).into(image);
            }else{
                image.setImageResource(R.drawable.load_img);
            }
        }
    }
}
