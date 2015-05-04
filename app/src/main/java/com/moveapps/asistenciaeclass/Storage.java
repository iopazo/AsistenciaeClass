package com.moveapps.asistenciaeclass;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * Created by iopazog on 06-03-15.
 */
public class Storage {

    private static final String ERROR = "ErrorAccess";
    public static final int MinimunPercent = 10;

    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    public static String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return formatSize(availableBlocks * blockSize);
    }

    public static String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return formatSize(totalBlocks * blockSize);
    }

    public static String getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return formatSize(availableBlocks * blockSize);
        } else {
            return ERROR;
        }
    }

    public static String getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return formatSize(totalBlocks * blockSize);
        } else {
            return ERROR;
        }
    }

    public static long getTotalMemorySize() {
        long percent = 0;
        //Log.d("Internal Memory", getTotalInternalMemorySize());
        long TotalExternalMemorySize = 0;
        long AvailableExternalMemorySize = 0;
        Boolean isExternalSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if(isExternalSDPresent) {
            TotalExternalMemorySize = Long.parseLong(getTotalExternalMemorySize().replaceAll("\\D+", ""));
            AvailableExternalMemorySize = Long.parseLong(getAvailableExternalMemorySize().replaceAll("\\D+", ""));
        }
        long TotalInternalMemorySize = Long.parseLong(getTotalInternalMemorySize().replaceAll("\\D+", ""));
        long AvailableInternalMemorySize = Long.parseLong(getAvailableInternalMemorySize().replaceAll("\\D+", ""));

        //Log.d("Espacios", String.format("%d %d %d %d", TotalExternalMemorySize, AvailableExternalMemorySize, TotalInternalMemorySize, AvailableInternalMemorySize));
        percent = (((AvailableExternalMemorySize + AvailableInternalMemorySize) * 100) / (TotalExternalMemorySize + TotalInternalMemorySize));

        return percent;
    }

    private static String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }
}
