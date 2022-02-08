package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;

@RequiresApi(api = Build.VERSION_CODES.N)
public class JSONConverter {
    //Объявляем поля
    private JSONObject jsonObject;
    private Activity activity;
    private static EventManager data = new EventManager();
    private LinkedHashMap<String, ArrayList<Event>> hashMapOut = new LinkedHashMap<String, ArrayList<Event>>();


    //Конструктор для передачи mainActivity
    JSONConverter(Activity activity) {
        this.activity = activity;
        startConverter(activity.getApplicationContext());
    }

    //Метод начала наработы класса
    void startConverter(Context context) {
        try {
            load(context);
            hashMapOut = getLinkedHashMap();
            saveToJson(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Гетер для hashMap
    public LinkedHashMap<String, ArrayList<Event>> getHashMapOut() {
        return hashMapOut;
    }
    //Метод в котором получаем из обычного List HashMap`у
    @RequiresApi(api = Build.VERSION_CODES.N)
    public LinkedHashMap<String, ArrayList<Event>> getLinkedHashMap() {
        //Берем текущий список, который ранее получили из файла
        ArrayList<Event> bufferEvents = data.getEvent();
        //проверяем, что он не пустой
        if (!bufferEvents.isEmpty()) {
            //сортируем, чтобы даты шли друг за другом
            bufferEvents = Event.getSortedList(bufferEvents);
            //создаем LinkedHashMap, Linked потому как важен порядок дат
            LinkedHashMap<String, ArrayList<Event>> hashMapFromList
                    = new LinkedHashMap<String, ArrayList<Event>>();
            //создаем буферный List в который будем записывать отсортированные Дела
            ArrayList<Event> arrayListBuffer = new ArrayList<>();
            /*метод сортировки пузырьком, неэффективно, но просто. Но перед ним важно чтобы уже
            список был отсортирован, и Дела были в своей ествественной последовательности
             */
            for (int i = 0; i < bufferEvents.size(); i++) {
                for (int j = 1; j < bufferEvents.size(); j++) {
                    //Если текущий день равен прошлому, то
                    if (bufferEvents.get(j).getDayofEvent().equals(bufferEvents.get(i).getDayofEvent())) {
                        //добавляем его в список
                        arrayListBuffer.add(bufferEvents.get(i));
                        //обновляем HashMap с текущим днем(ключем) и делом (значением)
                        hashMapFromList.put(bufferEvents.get(i).getDayofEvent(), arrayListBuffer);
                    }else{
                        //если не равен, значит это новый день и необходимо обновить список
                        arrayListBuffer.clear();
                        arrayListBuffer.add(bufferEvents.get(j));
                        hashMapFromList.put(bufferEvents.get(j).getDayofEvent(), arrayListBuffer);
                    }

                }
            }
            //Отдаем полученную HashMap
            return hashMapFromList;
        }
        //но если список был пуст, то и отдавать нечего
        return null;
    }
    /*Метод в котором сохраняем наш список дел в файл
    на всякий случай каждое исключание поместил в отдельный catch, возможно сделаю позде Toast с
    сообщениями
     */
    public void saveToJson(Context context) {

        Gson gson = new Gson();
        //формируем наш список дел в JSON формат
        String json = gson.toJson(data);
        //открываем потом записи информации с try-catch
        FileOutputStream fos = null;
        try {
            //название файла
            fos = context.openFileOutput("jsonfile.json", Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            //запись файла
            fos.write(json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            //закрываем поток
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Метод на чтение файла JSON
    public void load(Context context) {
        //открываем потом на чтение с try-catch
        FileInputStream fis = null;
        try {
            //находим сам файл
            fis = context.openFileInput("jsonfile.json");
        } catch (FileNotFoundException e) {
            //если файл не найден, то создаем его и пробуем найти заново
            e.printStackTrace();
            //вызываем метод для первичной инициализации ежедневника и берем дела из ТЗ
            firstInitEvents();
            //в поле data находятся дела из ТЗ, вызываем метод сохранения файла
            saveToJson(context);
            //вызываем заново загрузку
            load(context);
            //надо добавь всплывающее окно Тоаст с тем, что ещё не создано дел
        }
        //открываем потом на чтение и оборачиваем его
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        String bufferString = "";
        String line = "";
        //ловим исключения без ресурсов
        try {
            while ((line = bufferedReader.readLine()) != null) {
                line = line.replaceAll("\\\\", "");
                bufferString += line;
            }
            //закрываем поток
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bufferString.equals("null")||bufferString.equals("")) {
            //если каким то образом в файле null или он просто пустой, то повторяем действия выше
            firstInitEvents();
            saveToJson(context);
            load(context);
            return;
        }
        //получаем из JSON с помощью GSON список дел
        String json = bufferString;
        Gson gson = new Gson();
        data = gson.fromJson(json, EventManager.class);

    }
    public static int getCurrentID(){
        return data.eventCounterId();
    }
    //метод для первичной инициализации списка дел, дела берутся из ТЗ как базовые
    private void firstInitEvents() {
        Gson gson = new Gson();
        data = gson.fromJson(DataJSON.getJSONString(), EventManager.class);
    }
}

