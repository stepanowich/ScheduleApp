package com.example.myapplication;
//Одиночка для первичного создания списка дел
public final class DataJSON {
    private static DataJSON baseDataJSON;
     private DataJSON(){
         try {
             Thread.sleep(1000);
         } catch (InterruptedException ex) {
             ex.printStackTrace();
         }
     }
    public static DataJSON getInstance(String value) {
        if (baseDataJSON == null) {
            baseDataJSON = new DataJSON();
        }
        return baseDataJSON;
    }
    //JSON из ТЗ
    private static String JSONString ="{\n  \"event\": [\n    {\n      \"id\": 1,\n      \"date_start\": \"147600000\",\n      \"date_end\": \"147610000\",\n      \"name\": \"My task1\",\n      \"description\":\"Description1\"\n    },\n    {\n      \"id\": 2,\n      \"date_start\": \"147610000\",\n      \"date_end\": \"147620000\",\n      \"name\": \"My task2\",\n      \"description\":\"Description2\"\n    }\n  ]\n}";

    public static String getJSONString() {
        return JSONString;
    }


}

