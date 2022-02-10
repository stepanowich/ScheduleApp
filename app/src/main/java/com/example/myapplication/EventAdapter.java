package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> implements Serializable {

    private List<Event> events;
    private OnListListener mOnListListener;


    EventAdapter(ArrayList<Event> businesses,OnListListener mOnListListener) {
        this.events = businesses;
        this.mOnListListener =mOnListListener;

    }

    //тут мы создаем новые viewHolder`ы
    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //получаем контекст recycleView
        Context context = parent.getContext();
        //загрузили id xml файла в java код
        int layoutIdForListItem = R.layout.item_list;
        LayoutInflater inflater = LayoutInflater.from(context);
        //создаём новый View элемент списка из полученного контекста и xml
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        //обарачиваем в ранее созданый BusinessViewHolder
        EventViewHolder viewHolder = new EventViewHolder(view, mOnListListener);


        return viewHolder;
    }

    //В ранее созданом viewHolder`е обновляем значения
    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        //обновляем значения в viewHolder
        holder.name.setText(String.valueOf(events.get(position).getName()));
        holder.elementIndex.setText(String.valueOf(events.get(position).getTimeStartOfEvent()) + "-"
                + String.valueOf(events.get(position).getTimeEndOfEvent()));

    }

    //Общее колличество элементов, которые необходимо реализовать
    @Override
    public int getItemCount() {
        return events.size();
    }

    //Создаем вложенный класс(обёртка для элемента списка)
    class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView elementIndex;
        TextView name;
        ImageView deleteButton;
        OnListListener onListListener;

        //itemView это объект который соответствует элементу списка, который генерируется из
        //соответствуещего элемента xml файла
        public EventViewHolder(View itemView, OnListListener onListListener) {
            super(itemView);
            //находим TextView и генерурем java object
            elementIndex = itemView.findViewById(R.id.element_index);
            name = itemView.findViewById(R.id.business_name);
            deleteButton = itemView.findViewById(R.id.delete_button);
            this.onListListener=onListListener;
            itemView.setOnClickListener(this);
            deleteButton.setOnClickListener(this);
        }



        @Override
        public void onClick(View v) {

            if(v.getId()==(R.id.delete_button)){
                onListListener.onDeleteClick(getAdapterPosition());
            }else{
                onListListener.onListClick(getAdapterPosition());
            }


        }
    }
    //Метод обновления списка дел на экране
    @SuppressLint("NotifyDataSetChanged")
    public void update(ArrayList<Event> updateEvents) {
        if (updateEvents == null) {
            updateEvents = new ArrayList<Event>();
        } else {
            events=new ArrayList<Event>();
        }
        events.addAll(updateEvents);
        notifyDataSetChanged();
    }
    public interface OnListListener{
        void onListClick(int pos);
        void onDeleteClick(int pos);
    }

}
