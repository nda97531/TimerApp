package com.example.timerapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timerapp.model.OneSequence;
import com.example.timerapp.model.SequenceDataset;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = "MainActivity";
    private final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private final int REQUEST_EXTERNAL_STORAGE = 1;
    private final String CLASS_NAME_FILE = "class_name.txt";

    private TextView num_recorded;
    private OneSequence current_sequence;
    private SequenceDataset dataset = new SequenceDataset();
    ClassLabelAdapter recyclerview_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        num_recorded = findViewById(R.id.textview_num_recorded);

        // set up RecyclerView
        RecyclerView recyclerview_classes = findViewById(R.id.recyclerview_classes);
        recyclerview_adapter = new ClassLabelAdapter(this, new OnSequenceChangedListener(),
                loadSavedClassNames());
        recyclerview_classes.setAdapter(recyclerview_adapter);
        recyclerview_classes.setLayoutManager(new LinearLayoutManager(this));

        // set up ADD dialog
        EditText edittext_cls_name = new EditText(this);
        AlertDialog.Builder dialog_builder = new AlertDialog.Builder(this);
        edittext_cls_name.setHint("New class name");
        dialog_builder.setTitle("Add new class");
        dialog_builder.setView(edittext_cls_name);
        dialog_builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String[] names = edittext_cls_name.getText().toString().split("\n");
                for (String name : names)
                    recyclerview_adapter.addNewItem(name);
                edittext_cls_name.setText("");
            }
        });
        dialog_builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog add_class_dialog = dialog_builder.create();

        // set ADD button listener
        findViewById(R.id.button_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_class_dialog.show();
            }
        });

        // set DISCARD button
        dialog_builder = new AlertDialog.Builder(this);
        dialog_builder.setMessage("Discard all unsaved labels?");
        dialog_builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dataset.clear_dataset();
                num_recorded.setText("");
                Toast.makeText(MainActivity.this, "Discarded all labels", Toast.LENGTH_SHORT).show();
            }
        });
        dialog_builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog discard_dialog = dialog_builder.create();
        findViewById(R.id.button_discard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discard_dialog.show();
            }
        });

        // set SAVE button
        findViewById(R.id.button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // skip if there's nothing to save
                if (dataset.size() == 0) {
                    Log.i(LOG_TAG, "nothing to save");
                    Toast.makeText(MainActivity.this, "Nothing to save", Toast.LENGTH_SHORT).show();
                    return;
                }
                // save to file
                Log.i(LOG_TAG, "saving to file");
                for (OneSequence seq : dataset.getAll()) {
                    System.out.println(seq.toString(","));
                }
                boolean save_successful = dataset.save_to_file();
                // show message
                if (save_successful)
                    Toast.makeText(MainActivity.this, "Saved all labels", Toast.LENGTH_SHORT).show();
                else
                    new AlertDialog.Builder(MainActivity.this).setMessage("Cannot save :(").create().show();
            }
        });

        // ask permission
        verifyStoragePermissions(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveClassNamesToFile(this.recyclerview_adapter.getAllClassNames());
    }

    private String[] loadSavedClassNames() {
        File file = new File(getFilesDir(), CLASS_NAME_FILE);
        //Read text from file
        ArrayList<String> class_names = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null)
                class_names.add(line);
        } catch (FileNotFoundException e) {
            Log.w(LOG_TAG, "Class name file not found");
            return new String[0];
        } catch (IOException e) {
            e.printStackTrace();
            return new String[0];
        }
        Log.i(LOG_TAG, "loaded " + class_names.size() + " classes");
        return class_names.toArray(new String[0]);
    }

    private void saveClassNamesToFile(String[] class_names) {
        if (class_names.length == 0)
            return;

        File file = new File(getFilesDir(), CLASS_NAME_FILE);
        try (BufferedWriter wr = new BufferedWriter(new FileWriter(file))) {
            for (int i = 0; i < class_names.length; i++) {
                wr.write(class_names[i]);
                if (i < class_names.length - 1)
                    wr.newLine();
            }
            Log.i(LOG_TAG, "saved " + class_names.length + " classes");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public class OnSequenceChangedListener {
        public void onSequenceStart(String name) {
            current_sequence = new OneSequence(name, System.currentTimeMillis());
        }

        public void onSequenceEnd(String name) {
            if (!Objects.equals(current_sequence.name, name)) {
                Log.e(LOG_TAG, String.format("%s vs %s", current_sequence.name, name));
            }
            current_sequence.end_timestamp = System.currentTimeMillis();
            dataset.add(current_sequence);
            num_recorded.setText("Num recorded sequences: " + dataset.size());
        }
    }
}