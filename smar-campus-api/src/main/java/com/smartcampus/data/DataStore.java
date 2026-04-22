package com.smartcampus.data;
import com.smartcampus.models.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore {
    private static final DataStore instance = new DataStore();
    public Map<String, Room> rooms = new ConcurrentHashMap<>();
    public Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    public Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    private DataStore() {}
    public static DataStore getInstance() { return instance; }
}