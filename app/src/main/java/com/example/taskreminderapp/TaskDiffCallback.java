package com.example.taskreminderapp;

import androidx.recyclerview.widget.DiffUtil;

import com.example.taskreminderapp.models.Task;

import java.util.List;

public class TaskDiffCallback extends DiffUtil.Callback {
    private final List<Task> oldList;
    private final List<Task> newList;

    public TaskDiffCallback(List<Task> oldList, List<Task> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        // تحقق من تطابق معرّف المهمة (أو أي خاصية فريدة)
        return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        // تحقق من تطابق المحتوى الكامل للمهمة
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}
