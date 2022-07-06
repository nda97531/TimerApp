package com.example.timerapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ClassLabelAdapter extends RecyclerView.Adapter<ClassLabelAdapter.ClassLabelViewHolder> {
    public static final String LOG_TAG = "ClassLabelListAdapter";
    private ArrayList<String> list_class_names;
    private Context context;
    private MainActivity.OnSequenceChangedListener item_toggle_listener;

    public ClassLabelAdapter(Context context, MainActivity.OnSequenceChangedListener item_toggle_listener) {
        this.list_class_names = new ArrayList<>();
        this.context = context;
        this.item_toggle_listener = item_toggle_listener;
    }

    public void addNewItem(String name) {
        list_class_names.add(name);
        notifyItemInserted(list_class_names.size() - 1);
    }

    public void deleteItem(int position) {
        list_class_names.remove(position);
        notifyItemRemoved(position);
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
        Log.i(LOG_TAG, "Bind new item: " + list_class_names.get(position) + " at index " + position);
        String class_name = list_class_names.get(position);
        holder.class_name.setText(class_name);
        holder.toggle_button.setChecked(false);

        // set up toggle button
        holder.toggle_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    Log.i(LOG_TAG, "toggle ON: " + class_name);
                    item_toggle_listener.onSequenceStart(class_name);
                } else {
                    Log.i(LOG_TAG, "toggle OFF: " + class_name);
                    item_toggle_listener.onSequenceEnd(class_name);
                }
            }
        });
        // set up delete button
        holder.delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "delete class: " + class_name);
                deleteItem(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list_class_names.size();
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
