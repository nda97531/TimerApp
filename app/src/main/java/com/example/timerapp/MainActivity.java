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

import com.example.timerapp.model.OneSequence;
import com.example.timerapp.model.SequenceDataset;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = "MainActivity";
    private final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private final int REQUEST_EXTERNAL_STORAGE = 1;

    private OneSequence current_sequence;
    private SequenceDataset dataset = new SequenceDataset();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up RecyclerView
        RecyclerView recyclerview_classes = findViewById(R.id.recyclerview_classes);
        ClassLabelAdapter recyclerview_adapter = new ClassLabelAdapter(this, new OnSequenceChangedListener());
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
                recyclerview_adapter.addNewItem(edittext_cls_name.getText().toString());
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
        findViewById(R.id.button_discard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataset.clear_dataset();
            }
        });

        // set SAVE button
        findViewById(R.id.button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "saving to file");
                for (OneSequence seq : dataset.getAll()) {
                    System.out.println(seq.toString(","));
                }
                dataset.save_to_file();
            }
        });

        // ask permission
        verifyStoragePermissions(this);
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
        }
    }
}