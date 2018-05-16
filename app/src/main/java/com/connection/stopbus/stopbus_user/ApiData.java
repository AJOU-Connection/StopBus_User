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
        public String districtCd;
        public String routeNumber;
        public String routeTypeName;
    }
    static class Station {
        public int districtCd;
        public String stationNumber;
        public String stationName;
        public String stationDirect;
    }

    static class StationBus {
        public int routeId;
        public String routeNumber;
        public int remainSeatCnt1;
        public int remainSeatCnt2;
        public String predictTime1;
        public String predictTime2;
        public String plateNo1;
        public String plateNo2;
        public int lowPlate1;
        public int lowPlate2;
        public String locationNo1;
        public String locationNo2;
        public String routeTypeName;
    }

}