package server;

import java.util.Arrays;

public class ArrayDB {
    public final int SIZE = 100;
    private final String[] db;

    ArrayDB() {
        Arrays.fill(db = new String[SIZE], "");
    }

    String get(String recordID) {
        int _index = parseIndexString(recordID);
        if (isEmpty(_index)) {
            return "ERROR";
        } else {
            return db[_index];
        }
    }

    String set(String recordID, String str) {
        int _index = parseIndexString(recordID);
        if (indexIsValid(_index)) {
            db[_index] = str;
            return "OK";
        } else {
            return "ERROR";
        }
    }

    String delete(String recordID) {
        int _index = parseIndexString(recordID);
        if (indexIsValid(_index)) {
            db[_index] = "";
            return "OK";
        } else {
            return "ERROR";
        }
    }

    int parseIndexString(String index) {
        return Integer.parseInt(index) - 1;
    }

    boolean indexIsValid(int index) {
        return 0 <= index && index <= SIZE - 1;
    }

    boolean isEmpty(int _index) {
        return db[_index].equals("");
    }
}
