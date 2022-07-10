package com.example.timerapp.model;

import android.os.Environment;
import android.util.Log;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class SequenceDataset {
    private final String LOG_TAG = "TimerLog_SequenceDataset";
    private ArrayList<OneSequence> dataset = new ArrayList<>();
    private String start_datetime = "0", end_datetime = "0";
    private final DateFormat date_format = new SimpleDateFormat("yyyyMMddHHmmss");

    public void clear_dataset() {
        dataset = new ArrayList<>();
    }

    public void add(OneSequence seq) {
        // if this is the first record, mark dataset's starting time
        if (dataset.size() == 0) {
            start_datetime = date_format.format(new Date(seq.start_timestamp));
        }
        dataset.add(seq);
    }

    public void remove(int position) {
        dataset.remove(position);
    }

    public int size() {
        return dataset.size();
    }

    public OneSequence getItem(int position) {
        return dataset.get(position);
    }

    public ArrayList<OneSequence> getAll() {
        return dataset;
    }

    public boolean save_to_file(String suffix) {
        if (dataset.size() == 0)
            return false;
        // mark ending time of dataset
        end_datetime = date_format.format(new Date(dataset.get(dataset.size() - 1).end_timestamp));
        // find file path
        File folder = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile(),
                "TimerApp"
        );
        // create folder to write file
        boolean create_success = true;
        if (!folder.exists())
            create_success = folder.mkdirs();
        if (!create_success)
            return false;

        File file = new File(folder, String.format("%s_%s_%s.csv", start_datetime, end_datetime, suffix));
        // write file
        CSVWriter csv_writer;
        try {
            if (file.exists() && !file.isDirectory())
                csv_writer = new CSVWriter(new FileWriter(file, true));
            else
                csv_writer = new CSVWriter(new FileWriter(file));

            csv_writer.writeNext(new String[]{"label", "start", "end"});
            for (OneSequence seq : dataset)
                csv_writer.writeNext(seq.toStringArray(), false);
            csv_writer.close();
            return true;
        } catch (IOException e) {
            Log.e(LOG_TAG, "cannot write file");
            e.printStackTrace();
        }
        return false;
    }
}
