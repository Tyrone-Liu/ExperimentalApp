package com.tyroneil.longshootalpha;

class Support_Utility {
    // region: {@function arrayIndexOf}
    static int arrayIndexOf(int[] array, int value) {
        for (int i = 0; i < array.length; i ++) {
            if (array[i] == value) {return i;}
        }
        return -1;
    }

    static int arrayIndexOf(float[] array, float value) {
        for (int i = 0; i < array.length; i ++) {
            if (array[i] == value) {return i;}
        }
        return -1;
    }

    static int arrayIndexOf(String[] array, String value) {
        for (int i = 0; i < array.length; i ++) {
            if (array[i].equals(value)) {return i;}
        }
        return -1;
    }
    // endregion: {@function arrayIndexOf}
}
