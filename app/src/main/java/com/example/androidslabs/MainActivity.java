package com.example.androidslabs;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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
    private todoDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.todo_list);
        EditText newItem = findViewById(R.id.new_item);
        Switch urgentSwitch = findViewById(R.id.urgent_switch);
        Button addButton = findViewById(R.id.add_button);
        listView.setAdapter(myAdapter = new MyListAdapter());
        dbHelper = new todoDBHelper(this);

        loadTodoFromDB();

        addButton.setOnClickListener(view -> {
            String itemText = newItem.getText().toString().trim();
            boolean isUrgent = urgentSwitch.isChecked();

            if(!itemText.isEmpty()) {
                addtodoToDB(itemText, isUrgent);
                loadTodoFromDB();
                newItem.setText("");
                urgentSwitch.setChecked(false);
            }
        });

        listView.setOnItemLongClickListener( (p, d, pos, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(R.string.delete_item)
                    .setMessage(R.string.confirmation_delete)

                    .setPositiveButton(R.string.yes, (click, arg) -> {
                        ToDoItem toDoItem = elements.get(pos);
                        deletetodoFromDB(toDoItem.getId());
                        loadTodoFromDB();
                    })
                    .setNegativeButton(R.string.no, null)
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
        private void loadTodoFromDB() {
            elements.clear();
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            Cursor cursor = db.query(
                    todoDBHelper.TABLE_TODO,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(todoDBHelper.COLUMN_ID));
                    String task = cursor.getString(cursor.getColumnIndexOrThrow(todoDBHelper.COLUMN_TASK));
                    boolean isUrgent = cursor.getInt(cursor.getColumnIndexOrThrow(todoDBHelper.COLUMN_URGENCY))==1;

                    elements.add(new ToDoItem(id, task, isUrgent));
                } while (cursor.moveToNext());
                printCursor(cursor);
                cursor.close();
            }
            db.close();
            myAdapter.notifyDataSetChanged();
        }
        private void addtodoToDB(String task, boolean isUrgent) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(todoDBHelper.COLUMN_TASK, task);
            values.put(todoDBHelper.COLUMN_URGENCY, isUrgent ? 1: 0);
            long newRowId = db.insert(todoDBHelper.TABLE_TODO, null, values);
            db.close();
        }
        private void deletetodoFromDB(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = todoDBHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        db.delete(todoDBHelper.TABLE_TODO, selection, selectionArgs);
        db.close();
        }

        private class ToDoItem {
            private String text;
            private boolean urgent;
            private int id;

            public ToDoItem(int id, String text, boolean urgent) {
                this.id = id;
                this.text = text;
                this.urgent = urgent;
            }
            public int getId() {
                return id;
            }
            public String getText() {
                return text;
            }
            public boolean isUrgent() {
                return urgent;
            }


    }
        private void printCursor(Cursor c) {

            if (c == null) {
                Log.d("CursorDebug", "Cursor is null");
                return;
            }

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Log.d("CursorDebug", "Database Version: " + db.getVersion());

            int columnCount = c.getColumnCount();
            Log.d("CursorDebug", "Number of Columns: " + columnCount);

            StringBuilder columnNames = new StringBuilder("Column Names: ");
            for (int i = 0; i < columnCount; i++) {
                columnNames.append(c.getColumnName(i)).append(" ");
            }
            Log.d("CursorDebug", columnNames.toString());

            Log.d("CursorDebug", "Number of Results: " + c.getCount());

            if (c.moveToFirst()) {
                do {
                    StringBuilder row = new StringBuilder("Row: ");
                    for (int i = 0; i < columnCount; i++) {
                        row.append(c.getString(i)).append(" ");
                    }
                    Log.d("CursorDebug", row.toString());
                } while (c.moveToNext());
            }

            c.moveToFirst(); // Reset cursor position if needed
            db.close();
        }
        }



