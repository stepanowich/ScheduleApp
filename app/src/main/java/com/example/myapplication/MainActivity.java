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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

/*
made by Ivanov Pavel
Это ежедневник для Android, ежедневник работает с JSON файлами
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity implements EventAdapter.OnListListener {
    private DatePicker mDatePicker;
    private Button changingDateButton;
    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private View.OnClickListener listenerButton;
    private ArrayList<Event> events;
    private Calendar today;
    private ActivityResultCallback<ActivityResult> resultActivityResultCallback;
    private DatePicker.OnDateChangedListener datePickerListener;
    private LinkedHashMap<String, ArrayList<Event>> hashMapBuffer;
    private String chosen_day = "";
    private JSONConverter jsonConverter;
    private LinkedHashMap<String, ArrayList<Event>> hashMap;
    protected ActivityResultLauncher<Intent> activityResultLauncher;
    private  Event eventToEdit;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initToday();
        jsonConverter = new JSONConverter(this);
        hashMap = jsonConverter.getHashMapOut();
        setResultActivityResultCallback();
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), resultActivityResultCallback);

        if (hashMap.containsKey(chosen_day) && !hashMap.isEmpty()) {
            adapter = new EventAdapter(hashMap.get(chosen_day),this);
        } else {
            hashMap.put(chosen_day, new ArrayList<Event>());
            adapter = new EventAdapter(hashMap.get(chosen_day),this);
        }
        setChangingDateButtonListener();
        mDatePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH), datePickerListener);
        onButtonSaveListener();
        changingDateButton.setOnClickListener(listenerButton);
        recyclerView.setAdapter(adapter);


    }

    private void setChangingDateButtonListener() {
        datePickerListener = new DatePicker.OnDateChangedListener() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDateChanged(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                //обновляем значения строки(ключа)
                chosen_day = "" + mDatePicker.getDayOfMonth() + "/";
                chosen_day += (mDatePicker.getMonth() + 1) + "/";
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
        };
    }

    private void onButtonSaveListener() {
        listenerButton = new View.OnClickListener() {

            @SuppressLint("NotifyDataSetChanged")
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                ArrayList<Event> businesses1 = new ArrayList<Event>();
                businesses1.addAll(hashMap.get(chosen_day));
                businesses1 = Event.getSortedList(businesses1);
                Intent intent = new Intent(MainActivity.this, ActivityTwo.class);
                intent.putExtra("dateOfEvent", chosen_day);
                activityResultLauncher.launch(intent);
                events = businesses1;
                hashMapBuffer = hashMap;

            }
        };
    }

    private void setResultActivityResultCallback() {
        resultActivityResultCallback = new ActivityResultCallback<ActivityResult>() {

            @Override
            public void onActivityResult(ActivityResult result) {
                Log.d("MainActiv", "onActivityResult: ");
                if (result.getResultCode() == 01) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        int id = Integer.parseInt(intent.getStringExtra("newID"));
                        String dateStart = intent.getStringExtra("newStart");
                        String dateEnd = intent.getStringExtra("newEnd");
                        String name = intent.getStringExtra("newName");
                        String discription = intent.getStringExtra("newDescr");
                        events.add(new Event(id, dateStart, dateEnd, name, discription));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            events = Event.getSortedList(events);
                            hashMapBuffer.put(chosen_day, events);
                            adapter.update(hashMapBuffer.get(chosen_day));
                            EventManager eventsManagerToSafe = new EventManager();
                            ArrayList<Event> events = new ArrayList<>();
                            Object[] ar = hashMapBuffer.values().toArray();
                            for (int i = 0; i < ar.length; i++) {
                                events.addAll((ArrayList<Event>) ar[i]);
                            }
                            Set<Event> eventSet = new HashSet<Event>(events);
                            events.clear();
                            events.addAll(eventSet);
                            eventsManagerToSafe.setEvent(events);
                            jsonConverter.saveToJson(getApplicationContext(), eventsManagerToSafe);
                        }

                    }
                }else if(result.getResultCode()==02){
                    Intent intent = result.getData();
                    if (intent != null) {
                        int id = Integer.parseInt(intent.getStringExtra("editedID"));
                        String dateStart = intent.getStringExtra("editedStart");
                        String dateEnd = intent.getStringExtra("editedEnd");
                        String name = intent.getStringExtra("editedName");
                        String discription = intent.getStringExtra("editedDescr");
                        events.set(events.indexOf(eventToEdit),new Event(id, dateStart, dateEnd, name, discription));

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            events = Event.getSortedList(events);
                            hashMapBuffer.put(chosen_day, events);
                            adapter.update(hashMapBuffer.get(chosen_day));
                            EventManager eventsManagerToSafe = new EventManager();
                            ArrayList<Event> events = new ArrayList<>();
                            Object[] ar = hashMapBuffer.values().toArray();
                            for (int i = 0; i < ar.length; i++) {
                                events.addAll((ArrayList<Event>) ar[i]);
                            }
                            Set<Event> eventSet = new HashSet<Event>(events);
                            events.clear();
                            events.addAll(eventSet);
                            eventsManagerToSafe.setEvent(events);
                            jsonConverter.saveToJson(getApplicationContext(), eventsManagerToSafe);
                        }

                    }
                }
            }

        };
    }

    private void init() {
        setContentView(R.layout.activity_main);
        changingDateButton = findViewById(R.id.button);
        mDatePicker = findViewById(R.id.datePicker);
        recyclerView = findViewById(R.id.recycleView);
    }

    private void initToday() {
        today = Calendar.getInstance();
        //Пишем в строку текущий день
        chosen_day += (today.get(Calendar.DAY_OF_MONTH)) + "/";
        // Месяц отсчитывается с 0, поэтому добавляем 1 и заполняем строку
        chosen_day += (today.get(Calendar.MONTH) + 1) + "/";
        chosen_day += (today.get(Calendar.YEAR)) + "";
    }


    @Override
    public void onListClick(int pos) {

        events = hashMap.get(chosen_day);
        hashMapBuffer = hashMap;
        eventToEdit = hashMap.get(chosen_day).get(pos);
        Intent intent=new Intent(this, ActivityTwo.class);
        intent.putExtra("eventID", eventToEdit.getId().toString());
        intent.putExtra("eventStart", eventToEdit.getDateStart());
        intent.putExtra("eventEnd", eventToEdit.getDateEnd());
        intent.putExtra("eventName", eventToEdit.getName());
        intent.putExtra("eventDescr", eventToEdit.getDescription());
        activityResultLauncher.launch(intent);

    }
    @Override
    public void onDeleteClick(int pos) {
        hashMapBuffer = hashMap;
        events = hashMap.get(chosen_day);
        Event eventToDelete = hashMap.get(chosen_day).get(pos);
        events.remove(eventToDelete);
        hashMapBuffer.put(chosen_day, events);
        adapter.update(hashMapBuffer.get(chosen_day));
        EventManager eventsManagerToSafe = new EventManager();
        Object[] ar = hashMapBuffer.values().toArray();
        ArrayList<Event> eventsToSafeAfterDelete = new ArrayList<>();

        for (int i = 0; i < ar.length; i++) {
            eventsToSafeAfterDelete.addAll((ArrayList<Event>) ar[i]);
        }
        Set<Event> eventSet = new HashSet<Event>(events);
        eventsToSafeAfterDelete.clear();
        eventsToSafeAfterDelete.addAll(eventSet);
        eventsManagerToSafe.setEvent(eventsToSafeAfterDelete);
        jsonConverter.saveToJson(getApplicationContext(), eventsManagerToSafe);


    }
}