package com.example.myapplication;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
//Класс описывающий дело
public class Event implements Parcelable {
    //Объявляем поля класса дел и связываем их с полями из JSON
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("date_start")
    @Expose
    private String dateStart;
    @SerializedName("date_end")
    @Expose
    private String dateEnd;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;

    protected Event(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
            dateStart = null;
            dateEnd = null;
            name = null;
            description = null;
        } else {
            id = in.readInt();
            dateStart = in.readString();
            dateEnd = in.readString();
            name = in.readString();
            description = in.readString();
        }

    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public Event(Integer id, String dateStart,String dateEnd, String name, String description) {
        this.id = id;
        this.dateStart = dateStart;
        this.dateStart = dateEnd;
        this.name = name;
        this.description = description;
    }

    public Event() {

    }

    //Гетеры и сетеры
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    /*Перевод из timestamp в в формат date, а из date получаем день события для использования
    в качестве ключа в hashMap далее
     */
    public String getDayofEvent(){
        Date date_start = new Date (new Timestamp(Long.parseLong(getDateStart())).getTime());
        SimpleDateFormat dateForm = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            dateForm = new SimpleDateFormat("d/M/YYYY");
        }
        return dateForm.format(date_start);
    }
    /*Перевод из timestamp в в формат date
     */
    public Date getDateEvent(){
        Date dateEvent = new Date (new Timestamp(Long.parseLong(getDateStart())).getTime());
        return dateEvent;
    }
    public String getTimeStartOfEvent(){
        Date date_start = new Date (new Timestamp(Long.parseLong(getDateStart())).getTime());
        SimpleDateFormat dateForm = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            dateForm = new SimpleDateFormat("HH:mm");
        }
        return dateForm.format(date_start);
    }
    public String getTimeEndOfEvent(){
        Date date_start = new Date (new Timestamp(Long.parseLong(getDateEnd())).getTime());
        SimpleDateFormat dateForm = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            dateForm = new SimpleDateFormat("HH:mm");
        }
        return dateForm.format(date_start);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    /*
    Метод сортировки списка дел по дням
     */
    public static ArrayList<Event> getSortedList(ArrayList<Event> arr){
        arr.sort((Event x1, Event x2) -> {
            if(x1.getDateEvent().before(x2.getDateEvent())){
                return 1;
            }else {
                return 0;
            }
        });
        return arr;
    }
    //Описываем объекты для упаковки
    @Override
    public int describeContents() {
        return 0;
    }
    //Производим упаковку и передачу данным методом
    @Override
    public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(dateStart);
            dest.writeString(dateEnd);
            dest.writeString(name);
            dest.writeString(description);
    }
}
