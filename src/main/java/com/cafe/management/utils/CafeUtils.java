package com.cafe.management.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.jsonwebtoken.lang.Strings;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CafeUtils {

    private CafeUtils() {

    }

    public static ResponseEntity<String> getResponseEntity(String responseMessage, HttpStatus httpStatus) {
        return new ResponseEntity<String>("{\"message\":\"" + responseMessage + "\"}", httpStatus);
    }

    public static String getUUID(){
        Date date = new Date();
        long time = date.getTime();

        return "DATE-" +time;
    }

    public static JSONArray getJsonArrayFromString(String data) throws JSONException{
        System.out.println("Inside getJSONArrayFromString");
        JSONArray jsonArray = new JSONArray(data);
        return jsonArray;
    }

    public static Map<String, Object> getMapFromJson(String data){
        if (!StringUtils.isEmpty(data))
            return new Gson().fromJson(data, new TypeToken<Map<String, Object>>(){

            }.getType());
        return new HashMap<>();
    }

    public static Boolean isFileExist(String path){
        log.info("Inside isFileExist{}",path);
        try {
            File file= new File(path);
            return (file != null && file.exists()) ? Boolean.TRUE :Boolean.FALSE;

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
}
