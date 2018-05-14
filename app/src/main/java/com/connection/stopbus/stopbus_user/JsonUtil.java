package com.connection.stopbus.stopbus_user;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


/**
 * 일반문자열 유틸.
 *
 * @author someone
 * @version 1.0.0
 */
public class JsonUtil {

    /**
     * Map을 json으로 변환한다.
     *
     * @param map Map<String, Object>.
     * @return JSONObject.
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
