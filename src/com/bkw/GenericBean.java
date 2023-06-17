package com.bkw;

import java.util.HashMap;

public class GenericBean<K, V> {
    public enum DataType implements IGenericDataType{
        STRING("String"),
        DATE("Date"),
        INTEGER("Integer"),
        FLOAT("Float"),
        BOOLEAN("Boolean");

        private String dataType;

        DataType(String dataType) {
            this.dataType = dataType;
        }

        public String getDataType() {
            return dataType;
        }
    }


    protected HashMap<K, V> map = new HashMap<>();

    public void put(K key, V value) {
        map.put(key, value);
    }

    public V get(K key) {
        return map.get(key);
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    public int size() {
        return map.size();
    }

    @Override
    public String toString() {
        return map.toString();
    }

    public String getTableName() {
        return "<unknown>";
    }

    public String getName() {
        return "<unknown>";
    }

}