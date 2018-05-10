package com.connection.stopbus.stopbus_user;

public  class ApiData {


    static class Resp {
        public int result;
        public String msg;
        public String data;
    }

    static class Route {
        public int districtCd;
        public int routeNumber;
        public String routeTypeName;
    }
    static class Station {
        public int districtCd;
        public int stationNumber;
        public String stationName;
        public String stationDirect;
    }

}