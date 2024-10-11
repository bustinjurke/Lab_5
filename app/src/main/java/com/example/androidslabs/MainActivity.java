package com.example.androidslabs;

import android.app.AlertDialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<ToDoItem> elements = new ArrayList<>();
    private MyListAdapter myAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listview = findViewById(R.id.todo_list);
        EditText newItem = findViewById(R.id.new_item);
        Switch urgentSwitch = findViewById(R.id.urgent_switch);
        Button addButton = findViewById(R.id.add_button);
        listview.setAdapter( myAdapter = new MyListAdapter());

        addButton.setOnClickListener(view -> {
            String itemText = newItem.getText().toString();
            boolean isUrgent = urgentSwitch.isChecked();

            if(!itemText.isEmpty()) {
                elements.add(new ToDoItem(itemText, isUrgent));
                myAdapter.notifyDataSetChanged();
                newItem.setText("");
                urgentSwitch.setChecked(false);
            }
        });

        listView.setOnItemLongClickListener( (p, d, pos, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Delete Item")
                    .setMessage("Do you want to delete this item?")

                    .setPositiveButton("Yes", (click, arg) -> {
                        elements.remove(pos);
                        myAdapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("No", null)
                    .create().show();

                return true;
        });
    }

    private class MyListAdapter extends BaseAdapter {
       @Override
        public int getCount() {
            return elements.size();
                }

        @Override
        public Object getItem(int position) {
            return elements.get(position);
        }

        @Override
        public long getItemId(int position) {
            return (long) position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View newView = convertView;
            LayoutInflater inflater = getLayoutInflater();

            if(newView == null) {
                newView = inflater.inflate(R.layout.row_layout, parent, false);
            }
            TextView tView = newView.findViewById(R.id.textGoesHere);
            ToDoItem currentItem = elements.get(position);
            tView.setText(currentItem.getText());
            if (currentItem.isUrgent()) {
                newView.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                tView.setTextColor(getResources().getColor(android.R.color.white));
            } else {
                newView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                tView.setTextColor(getResources().getColor(android.R.color.black));
            }
            return newView;
            }
        }
        private class ToDoItem {
            private String text;
            private boolean urgent;

            public ToDoItem(String text, boolean urgent) {
                this.text = text;
                this.urgent = urgent;
            }
            public String getText() {
                return text;
            }
            public boolean isUrgent() {
                return urgent;
            }

    }


    }

