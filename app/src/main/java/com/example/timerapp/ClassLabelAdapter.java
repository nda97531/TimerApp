package com.example.timerapp;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timerapp.model.ClassItem;

import java.util.ArrayList;

public class ClassLabelAdapter extends RecyclerView.Adapter<ClassLabelAdapter.ClassLabelViewHolder> {
    public static final String LOG_TAG = "TimerLog_ClassLabelListAdapter";
    private ArrayList<ClassItem> list_class_items;
    private Context context;
    private MainActivity.OnSequenceChangedListener item_toggle_listener;
    private MainActivity.OnItemChangedListener item_changed_listener;

    public ClassLabelAdapter(Context context,
                             MainActivity.OnSequenceChangedListener item_toggle_listener,
                             MainActivity.OnItemChangedListener item_changed_listener,
                             String[] list_class_names) {
        this.list_class_items = new ArrayList<>();
        this.context = context;
        this.item_toggle_listener = item_toggle_listener;
        this.item_changed_listener = item_changed_listener;
        for (String name : list_class_names)
            this.list_class_items.add(new ClassItem(name));
    }

    public String[] getAllClassNames() {
        String[] names = new String[list_class_items.size()];
        for (int i = 0; i < list_class_items.size(); i++)
            names[i] = list_class_items.get(i).name;
        return names;
    }

    public void addNewItem(String name) {
        list_class_items.add(new ClassItem(name));
        notifyItemInserted(list_class_items.size() - 1);
        this.item_changed_listener.onItemChanged();
    }

    public void deleteItem(int position) {
        list_class_items.remove(position);
        notifyItemRemoved(position);
        this.item_changed_listener.onItemChanged();
    }

    @NonNull
    @Override
    public ClassLabelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View heroView = inflater.inflate(R.layout.class_item, parent, false);
        return new ClassLabelViewHolder(heroView);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassLabelViewHolder holder, int position) {
        ClassItem item = list_class_items.get(position);
        Log.i(LOG_TAG, "Bind new item: " + item.name + " at index " + position);
        String class_name = item.name;
        holder.class_name.setText(class_name);
        item.view_holder = holder;

        // set up toggle button
        holder.toggle_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                // when button is checked
                if (checked) {
                    int checked_pos = holder.getAdapterPosition();
                    // uncheck all other buttons
                    for (int i = 0; i < list_class_items.size(); i++)
                        if (i != checked_pos)
                            list_class_items.get(i).view_holder.toggle_button.setChecked(false);

                    Log.i(LOG_TAG, "toggle ON: " + class_name);
                    item_toggle_listener.onSequenceStart(class_name);
                }
                // when button is unchecked
                else {
                    Log.i(LOG_TAG, "toggle OFF: " + class_name);
                    item_toggle_listener.onSequenceEnd(class_name);
                }
            }
        });
        // set up delete button
        AlertDialog.Builder dialog_builder = new AlertDialog.Builder(context);
        dialog_builder.setMessage(String.format("Delete \"%s\"?", class_name));
        dialog_builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i(LOG_TAG, "delete class: " + class_name);
                deleteItem(holder.getAdapterPosition());
            }
        });
        dialog_builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog del_class_dialog = dialog_builder.create();
        holder.delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                del_class_dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list_class_items.size();
    }

    // Holder class for one item
    public class ClassLabelViewHolder extends RecyclerView.ViewHolder {
        private TextView class_name;
        private ToggleButton toggle_button;
        private Button delete_button;

        public ClassLabelViewHolder(@NonNull View itemView) {
            super(itemView);
            // find views
            this.class_name = itemView.findViewById(R.id.textview_class_name);
            this.toggle_button = itemView.findViewById(R.id.button_toggle_class);
            this.delete_button = itemView.findViewById(R.id.button_delete_class);
        }
    }
}
