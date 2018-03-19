package com.gpstransfer.ant.statemachine;

import java.util.LinkedList;
import java.util.List;

public class Data {

    private List<Byte> data = new LinkedList<>();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Data data1 = (Data) o;

        return data != null ? data.equals(data1.data) : data1.data == null;
    }

    @Override
    public int hashCode() {
        return data != null ? data.hashCode() : 0;
    }

    public List<Byte> getData() {
        return data;
    }
}
