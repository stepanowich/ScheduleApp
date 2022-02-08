package com.example.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
//Класс для обработки списока дел из JSON и для передачи из одной активности в другую список дел
class EventManager implements Parcelable {
    @SerializedName("event")
    @Expose
    private ArrayList<Event> event = null;
    public EventManager() {
    }
    //Описываем как распаковать список дел
    protected EventManager(Parcel in) {
        event = in.createTypedArrayList(Event.CREATOR);
    }
    //Переопределеяем методы класса CREATOR
    public static final Creator<EventManager> CREATOR = new Creator<EventManager>() {
        @Override
        public EventManager createFromParcel(Parcel in) {
            return new EventManager(in);
        }

        @Override
        public EventManager[] newArray(int size) {
            return new EventManager[size];
        }
    };
    //обычный гетер
    public ArrayList<Event> getEvent() {
        return event;
    }
    //обычный сетер
    public void setEvent(ArrayList<Event> event) {
        this.event = event;
    }
    public int eventCounterId(){
         int counter=0;
        if(event==null){
            counter =0;
        }else {
            counter = event.size();
        }
        return counter;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    //Описываем как упаковать список дел
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(getEvent());
    }
}
