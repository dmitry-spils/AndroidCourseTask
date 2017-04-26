package com.example.oiltanker.test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.widget.ArrayAdapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

import com.example.oiltanker.test.ToDoContentsActivity;


class TodoAdapter extends ArrayAdapter<ToDoItem> {

    private Activity activity;
    //private ArrayList<ToDoItem> objects;

    private class ToDoViewHolder {
        TextView name;
        TextView date;
        CheckBox check;
        Button button;
        ImageView picture;
    }

    TodoAdapter(Activity activity/*, ArrayList<ToDoItem> objects*/) {
        super(activity, R.layout.todo_list_item, ToDoDataManager.values());
        this.activity = activity;

        ToDoDataManager.giveAdapter(this);
        //this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull final ViewGroup parent) {
        ToDoViewHolder vh;

        if (convertView == null) {
            vh = new ToDoViewHolder();
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.todo_list_item, null);

            vh.name = (TextView) convertView.findViewById(R.id.text_name);
            vh.date = (TextView) convertView.findViewById(R.id.text_date);
            vh.check = (CheckBox) convertView.findViewById(R.id.checkbox);
            vh.button = (Button) convertView.findViewById(R.id.button);
            vh.picture = (ImageView) convertView.findViewById(R.id.picture);
            convertView.setTag(vh);
        } else {
            vh = (ToDoViewHolder) convertView.getTag();
        }

        vh.name.setText(ToDoDataManager.get(position).name);
        vh.date.setText(DateFormat.format("dd-MM-yyyy", ToDoDataManager.get(position).date));
        vh.check.setChecked(ToDoDataManager.get(position).check);
        vh.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToDoDataManager.get(position).check ^= true;
                //ToDoDataManager.notify(position);
                ToDoDataManager.notify_check(position);
            }
        });
        vh.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //objects.remove(position);
                //((TodoAdapter) ((ListView) view.getParent().getParent()).getAdapter()).notifyDataSetChanged();

                final View v = view;

                new AlertDialog.Builder(parent.getContext())
                        .setTitle("Please confirm")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ToDoDataManager.remove(position);
                                ((TodoAdapter) ((ListView) v.getParent().getParent()).getAdapter()).notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        //.setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        vh.picture.setImageBitmap(ToDoDataManager.get(position).picture);

        final Context context = getContext();

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create todo_contents activity
                Intent intent = new Intent(context, ToDoContentsActivity.class);
                intent.putExtra("position", ToDoDataManager.get(position).id);
                //intent.putExtra("ToDo", ToDoDataManager.get(position));
                context.startActivity(intent);
            }
        });

        return convertView;
    }

}
