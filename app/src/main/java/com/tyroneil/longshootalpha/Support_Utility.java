package com.tyroneil.longshootalpha;

import android.hardware.camera2.CaptureRequest;

import java.util.Locale;

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

    static String formatCaptureRequestKeyValue(CaptureRequest.Key requestKey, Object value, boolean shortVersion) {
        String formatResult = null;

        if (requestKey.equals(CaptureRequest.FLASH_MODE)) {
            switch ((Integer) value) {
                case CaptureRequest.FLASH_MODE_OFF:
                    formatResult = "OFF"; break;
                case CaptureRequest.FLASH_MODE_TORCH:
                    formatResult = "ON"; break;
            }
        }

        else if (requestKey.equals(CaptureRequest.CONTROL_AWB_MODE)) {
            switch ((Integer) value) {
                case CaptureRequest.CONTROL_AWB_MODE_OFF:
                    formatResult = (shortVersion? "OFF" : "OFF"); break;
                case CaptureRequest.CONTROL_AWB_MODE_AUTO:
                    formatResult = (shortVersion? "AUTO" : "AUTO"); break;
                case CaptureRequest.CONTROL_AWB_MODE_INCANDESCENT:
                    formatResult = (shortVersion? "INC." : "Incandescent"); break;
                case CaptureRequest.CONTROL_AWB_MODE_FLUORESCENT:
                    formatResult = (shortVersion? "FLU." : "Fluorescent"); break;
                case CaptureRequest.CONTROL_AWB_MODE_WARM_FLUORESCENT:
                    formatResult = (shortVersion? "FLU.W." : "Fluorescent (Warm)"); break;
                case CaptureRequest.CONTROL_AWB_MODE_DAYLIGHT:
                    formatResult = (shortVersion? "DAY." : "Daylight"); break;
                case CaptureRequest.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT:
                    formatResult = (shortVersion? "DAY.C." : "Daylight (Cloudy)"); break;
                case CaptureRequest.CONTROL_AWB_MODE_TWILIGHT:
                    formatResult = (shortVersion? "TWI." : "Twilight"); break;
                case CaptureRequest.CONTROL_AWB_MODE_SHADE:
                    formatResult = (shortVersion? "SHA." : "Shade"); break;
            }
        }

        else if (requestKey.equals(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE)) {
            switch ((Integer) value) {
                case CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE_OFF: formatResult = "OFF"; break;
                case CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE_ON: formatResult = "ON"; break;
            }
        }

        else if (requestKey.equals(CaptureRequest.SENSOR_EXPOSURE_TIME)) {
            Long exposureTime = (Long) value;
            String integerFormat;
            String decimalFormat;
            if (shortVersion) {
                integerFormat = "%d%s";
                decimalFormat = "%.1f%s";
            } else {
                integerFormat = "%d %s";
                decimalFormat = "%.2f %s";
            }

            if (exposureTime < 1E3) {
                formatResult = String.format(Locale.getDefault(),
                        integerFormat, exposureTime, "ns"
                );
            } else if (exposureTime < 1E6) {
                formatResult = String.format(Locale.getDefault(),
                        decimalFormat, ((double) exposureTime) / 1E3, "µs"
                );
            } else if (exposureTime < 1E9) {
                formatResult = String.format(Locale.getDefault(),
                        decimalFormat, ((double) exposureTime) / 1E6, "ms"
                );
            } else {
                formatResult = String.format(Locale.getDefault(),
                        decimalFormat, ((double) exposureTime) / 1E9, "s"
                );
            }
        }

        else if (requestKey.equals(CaptureRequest.LENS_FOCUS_DISTANCE)) {
            Float focusDistance = (Float) value;
            String format;
            String infinity;
            if (shortVersion) {
                format = "%.2f%s";
                infinity = "Inf.";
            } else {
                format = "%.4f %s";
                infinity = "Infinity";
            }

            if (focusDistance == 0.0f) {
                formatResult = infinity;
            } else {
                formatResult = String.format(Locale.getDefault(),
                        format, 1f / focusDistance, "m"
                );
            }
        }

        return formatResult;
    }

}
