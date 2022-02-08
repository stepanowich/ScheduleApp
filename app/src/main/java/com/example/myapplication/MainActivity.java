package com.example.myapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
/*
made by Ivanov Pavel
Это ежедневник для Android, ежедневник работает с JSON файлами
 */
public class MainActivity extends AppCompatActivity {
    private DatePicker mDatePicker;
    private Button changingDateButton;
    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private Event eventFromOtherActivity;
    private String chosen_day = "";
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d("MainActiv","onActivityResult: ");
                        if(result.getResultCode()==78){
                            Intent intent = result.getData();
                            if(intent!=null){
                                eventFromOtherActivity = (Event) intent.getParcelableExtra("newEvent");
                            }
                        }
                }
            }
    );


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        Calendar today = Calendar.getInstance();
        JSONConverter jsonConverter = new JSONConverter( this);
        LinkedHashMap<String, ArrayList<Event>> hashMap = jsonConverter.getHashMapOut();
        //Пишем в строку текущий день
        chosen_day += (today.get(Calendar.DAY_OF_MONTH))+"/";
        // Месяц отсчитывается с 0, поэтому добавляем 1 и заполняем строку
        chosen_day += (today.get(Calendar.MONTH) + 1)+"/";
        chosen_day += (today.get(Calendar.YEAR));
        if (hashMap.containsKey(chosen_day) && !hashMap.isEmpty()) {
            adapter = new EventAdapter(hashMap.get(chosen_day));
        } else {
            hashMap.put(chosen_day, new ArrayList<Event>());
            adapter = new EventAdapter(hashMap.get(chosen_day));
        }
        //инициализируем интерфейс


        mDatePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDateChanged(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                        //обновляем значения строки(ключа)
                        chosen_day = "" + mDatePicker.getDayOfMonth()+"/";
                        chosen_day += (mDatePicker.getMonth() + 1)+"/";
                        chosen_day += (mDatePicker.getYear());
                        Toast.makeText(getApplicationContext(),
                                chosen_day, Toast.LENGTH_SHORT).show();
                        if (hashMap.containsKey(chosen_day) && !hashMap.isEmpty()) {
                            adapter.update(hashMap.get(chosen_day));
                        } else {
                            hashMap.put(chosen_day, new ArrayList<Event>());
                            adapter.update(hashMap.get(chosen_day));
                        }
                    }
                });

        changingDateButton.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("NotifyDataSetChanged")
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                ArrayList<Event> businesses1 = new ArrayList<Event>();
                businesses1.addAll(hashMap.get(chosen_day));
                businesses1 =Event.getSortedList(businesses1);
                Intent intent = new Intent(MainActivity.this, ActivityTwo.class);
                intent.putExtra("dateOfEvent", chosen_day);
                activityResultLauncher.launch(intent);
//              !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!  ПОМЕСТИТЬ В КЛИК ЛИСТЕНЕР РЕКУКЛЕВЬЮ!!!!!!!!!!!!!!!!!!!!!!!!!
//                intent.putExtra("event", eventToTransfer);
//                startActivity(intent);
                    

                    businesses1.add(eventFromOtherActivity);
                    businesses1 =Event.getSortedList(businesses1);
                    hashMap.put(chosen_day, businesses1);
                    adapter.update(hashMap.get(chosen_day));

            }
        });
        // создаем адаптер
        recyclerView.setAdapter(adapter);

    }

    private void init() {
        setContentView(R.layout.activity_main);
        changingDateButton = findViewById(R.id.button);
        mDatePicker = findViewById(R.id.datePicker);
        recyclerView = findViewById(R.id.recycleView);
    }

}