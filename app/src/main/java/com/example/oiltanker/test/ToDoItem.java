package com.example.oiltanker.test;


import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;

import java.io.Serializable;
import java.util.Date;

class ToDoItem implements Serializable {
    String id;
    public String name;
    public Date date;
    public  boolean check;
    public String contents;
    public Bitmap picture = null;
    public String pic_name = null;

    ToDoItem(String id, String name, Date date, boolean check, String contents, Bitmap picture, String pic_name) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.check = check;
        this.contents = contents;
        this.picture = picture;
        this.pic_name = pic_name;
    }

    ToDoItem(ToDoItem td) {
        this.id = td.id;
        this.name = td.name;
        this.date = td.date;
        this.check = td.check;
        this.contents = td.contents;
        this.picture = td.picture;
        this.pic_name = td.pic_name;
    }
}
