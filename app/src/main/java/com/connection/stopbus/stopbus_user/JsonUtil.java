package com.connection.stopbus.stopbus_user;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


/**
 * Created by Danbk on 2018-05-06.
 */

public class JsonUtil {

    /**
     * Map을 json으로 변환한다.
     */
    public static JSONObject getJsonStringFromMap(Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject();

        try{
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                jsonObject.put(key, value);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }


        return jsonObject;
    }

}
