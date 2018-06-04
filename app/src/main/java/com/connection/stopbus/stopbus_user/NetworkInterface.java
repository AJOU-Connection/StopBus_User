package com.connection.stopbus.stopbus_user;

import java.util.ArrayList;
import java.util.Map;

public interface NetworkInterface {
    /**
     * Search call
     * @param query search query
     * @return String
     */
    String postQuery(String api, Map query);
    String postQuery2(String api, ArrayList list, String key);
    String getQuery(String api, Map query);
}