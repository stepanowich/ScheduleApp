package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

public class ActivityTwo extends AppCompatActivity {
    private TextInputLayout TxInLaName;
    private TextInputLayout TxInLaDescription;
    private TextInputLayout textInputDateStartEnd;
    private TextInputEditText TxEditName;
    private TextInputEditText TxEditDescription;
    private AutoCompleteTextView editableDateStartEnd;
    private Button saveButton;
    Event event ;
    String date;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initAdapterDropItem();
        /*Получаем объект Intent, в котором упакован список с делами, "ловим" с помощью имени name
        и начинаем работать со списком из основной активности
         */

        if(getIntent().getParcelableExtra("event")!=null) {
            event = getIntent().getParcelableExtra("event");
            Event eventOut = eventModerator(event);
            saveButton.setOnClickListener(new View.OnClickListener() {

                @SuppressLint("NotifyDataSetChanged")
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("newEvent", (Parcelable) eventOut);
                }
            });
        }else if(getIntent().getStringExtra("dateOfEvent")!=null){
            date = getIntent().getStringExtra("dateOfEvent");


                //обновить кнопку сохранить и чтобы он закрывал активити и возвращался
                saveButton.setOnClickListener(new View.OnClickListener() {

                    @SuppressLint("NotifyDataSetChanged")
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View view) {
                        event = eventCreator(date);
                        Intent intent = new Intent();
                        intent.putExtra("newEvent", event);
                        setResult(78, intent);
                        ActivityTwo.super.onBackPressed();
                    }
                });



        }







    }
    private void init(){
        setContentView(R.layout.activity_two);
        TxInLaDescription = findViewById(R.id.TxInLaDescription);
        TxEditDescription = findViewById(R.id.TxEditDescription);
        TxInLaName = findViewById(R.id.TxInLaName);
        TxEditName = findViewById(R.id.TxEditName);
        textInputDateStartEnd= findViewById(R.id.textInputDateStartEnd);
        editableDateStartEnd = findViewById(R.id.editableDateStartEnd);
        saveButton = findViewById(R.id.buttonSave);

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private Event eventCreator(String date){
        Event event = new Event(JSONConverter.getCurrentID(),
                timestampStart(date,editableDateStartEnd.getText().toString()),
                timestampEnd(date,editableDateStartEnd.getText().toString()),
                TxEditName.getText().toString(),TxEditDescription.getText().toString());
        return event;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String timestampStart(String date, String time) {
        String parsedTimeStart=time.substring(0,5);
            String timestampStart="";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            DateFormat formatter = new SimpleDateFormat("d/M/YYYY HH:mm");
            try {
                Date dateStart = formatter.parse(date+" "+parsedTimeStart);
//                Timestamp timestamp = new Timestamp(dateStart.());
                long timestamp = dateStart.getTime()/1000;
                timestampStart=timestamp+"";

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        return  timestampStart;
    }
    private String timestampEnd(String date, String time) {
        String parsedTimeEnd=time.substring(6,11);
        String timestampEnd="";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            DateFormat formatter = new SimpleDateFormat("d/M/YYYY HH:mm");
            try {
                Date dateStart = formatter.parse(date+" "+parsedTimeEnd);
//                Timestamp timestamp = new Timestamp(dateStart.getTime());
                long timestamp = dateStart.getTime()/1000;
                timestampEnd=timestamp+"";


            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        return  timestampEnd;
    }

    private Event eventModerator(Event event){
        if(event!=null){
            TxEditName.setText(event.getName());
            TxEditDescription.setText(event.getDescription());
            editableDateStartEnd.setText(event.getTimeStartOfEvent()+"-"+event.getTimeEndOfEvent());
        }else if(TxEditName.getText().toString()!=event.getName()&&TxEditName.getText().toString()!=""||
                TxEditDescription.getText().toString()!=event.getDescription()&&TxEditDescription.getText().toString()!=""||
                editableDateStartEnd.getText().toString()!=event.getTimeStartOfEvent()+"-"+event.getTimeEndOfEvent()){
            //Высветить кнопку Save
            //Листенер и в нем уже
//            event = new Event()
        }
        return event;
    }
    private void initAdapterDropItem(){

        //Получаем TextView по его id

        String[] items = new String[]{
                "00:00-01:00",
                "01:00-02:00",
                "02:00-03:00",
                "03:00-04:00",
                "05:00-06:00",
                "06:00-07:00",
                "07:00-08:00",
                "08:00-09:00",
                "09:00-10:00",
                "10:00-11:00",
                "11:00-12:00",
                "12:00-13:00",
                "13:00-14:00",
                "14:00-15:00",
                "15:00-16:00",
                "16:00-17:00",
                "17:00-18:00",
                "18:00-19:00",
                "19:00-20:00",
                "20:00-21:00",
                "21:00-22:00",
                "22:00-23:00",
                "23:00-00:00",
        };
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                R.layout.dropdown_item, items);
        editableDateStartEnd.setAdapter(arrayAdapter);
    }


}
