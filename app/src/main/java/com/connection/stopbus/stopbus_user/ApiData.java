package com.connection.stopbus.stopbus_user;

import java.sql.Array;
import java.util.Arrays;

/**
 * Created by Danbk on 2018-05-06.
 */

public  class ApiData {


    static class Resp {
        public boolean result;
        public int errorCode;
        public String errorContent;
        public Array body;
    }

    static class Route {
        public int districtCd;
        public String routeNumber;
        public String routeTypeName;
    }
    static class Station {
        public int districtCd;
        public String stationNumber;
        public String stationName;
        public String stationDirect;
    }

}