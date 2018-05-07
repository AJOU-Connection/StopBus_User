package com.connection.stopbus.stopbus_user;

import java.util.Map;

public interface NetworkInterface {
    /**
     * Search call
     * @param query search query
     * @return String
     */
    String postQuery(String api, Map query);
    String getQuery(String api, Map query);
}