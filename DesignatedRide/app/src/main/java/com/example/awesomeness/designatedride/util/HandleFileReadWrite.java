package com.example.awesomeness.designatedride.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class HandleFileReadWrite {

    private final static String TAG = "BLEDiscovery";

    private boolean isRead;
    private boolean isExist;
    private File fileToOpen = null;
    private FileWriter fileWriter;
    private FileReader fileReader;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;

    public HandleFileReadWrite() {
        isRead = false;
        isExist = false;
    }

    public void open(Context context, String fileName) {
        open(context, fileName, fileOperator.OPEN_READ);
    }

    public void open(Context context, String fileName, fileOperator op) {
        fileToOpen = new File(context.getFilesDir(), fileName);
        Log.i(TAG, "FILE PATH " + fileToOpen.toString() );
        switch (op) {
            case OPEN_READ: {
                try {
                    fileReader = new FileReader(fileToOpen);
                    bufferedReader = new BufferedReader(fileReader);
                    isRead = true;
                    isExist = true;
                } catch (IOException e) {
                    //
                    isExist = false;
                }
                break;
            }
            case OPEN_WRITE: {
                try {
                    fileWriter = new FileWriter(fileToOpen, false);
                    bufferedWriter = new BufferedWriter(fileWriter);
                    isRead = false;
                } catch (IOException e) {
                    //
                }
                break;
            }
            case OPEN_APPEND: {
                try {
                    fileWriter = new FileWriter(fileToOpen, true);
                    bufferedWriter = new BufferedWriter(fileWriter);
                    isRead = false;
                } catch (IOException e) {
                    //
                }
                break;
            }
        }
    }


    public String readLine()
    {
        if (!isRead) {
            System.err.println("This object is not open to read");
            return null;
        }

        if (bufferedReader != null) {
            try {
                return bufferedReader.readLine();
            } catch (IOException e) {
                return null;
            }
        } else {
            return null;
        }
    }


    public void writeLine(String data)
    {
        if (isRead) {
            System.err.println("This object is not open to write");
            return;
        }

        if (bufferedWriter != null) {
            try {
                bufferedWriter.write(data);
                bufferedWriter.write("\r\n");
            } catch (IOException e) {
                //
            }
        }
    }

    public void close() {
        if (bufferedWriter != null) {
            try {
                bufferedWriter.close();
                fileWriter.close();
            } catch (IOException e) {
                //
            }
        }
        if (bufferedReader != null) {
            try {
                bufferedReader.close();
                fileReader.close();
            } catch (IOException e) {
                //
            }
        }
    }

    public boolean isExist() {
        return isExist;
    }


    public enum fileOperator {
        OPEN_READ, OPEN_WRITE, OPEN_APPEND;
    }
}
