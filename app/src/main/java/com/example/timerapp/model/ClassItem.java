package com.example.timerapp.model;

import com.example.timerapp.ClassLabelAdapter;

public class ClassItem {
    public String name;
    public ClassLabelAdapter.ClassLabelViewHolder view_holder;

    public ClassItem(String name) {
        this.name = name;
    }

    public ClassItem(String name, ClassLabelAdapter.ClassLabelViewHolder view_holder) {
        this.name = name;
        this.view_holder = view_holder;
    }
}
