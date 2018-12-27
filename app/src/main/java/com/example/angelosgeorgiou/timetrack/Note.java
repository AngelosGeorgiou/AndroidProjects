package com.example.angelosgeorgiou.timetrack;


import android.widget.DatePicker;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_table")
public class Note {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
//  @ColumnInfo(name = "column_info")
    private int time;
    private int date;

    public Note(String title, String description, int time, int date) {
        this.title = title;
        this.description = description;
        this.time = time;
        this.date = date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getTime() { return time; }

    public int getDate(){ return  date;}
}
