package com.example.contentprovidersandbox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.provider.UserDictionary;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText todoText;
    Button saveButton, doneButton, deleteButton;
    ListView listView;
    Cursor mCursor;
    SimpleCursorAdapter cursorAdapter;
    boolean filtered = false;
    long activeTodo = -1;
    public static final String TAG = "ContentProviderSandbox";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        todoText = (EditText) findViewById(R.id.todoText);
        listView = (ListView) findViewById(R.id.todoList);
        saveButton = (Button) findViewById(R.id.saveButton);
        doneButton = (Button) findViewById(R.id.doneButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        changeState();
        getTodos();

    }

    void changeState() {
        if (activeTodo > -1) {
            saveButton.setVisibility(View.GONE);
            doneButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            saveButton.setVisibility(View.VISIBLE);
            doneButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        }
    }

    //will update a record
    public void onClickDone(View view){
        String note = todoText.getText().toString();

        //checks if edit text is empty
        if (note == null || note.trim().equals("")) {
            Toast.makeText(getBaseContext(),
                    "You must enter a To Do item to save", Toast.LENGTH_SHORT).show();
            return;
        }

        //puts values to be placed
        ContentValues values = new ContentValues();
        values.put(TodoProvider.NOTE, note);

        //updates list
        Uri _uri = ContentUris.withAppendedId(TodoProvider.CONTENT_URI, activeTodo);
        int returnCount = getContentResolver().update(
                _uri, values, TodoProvider._ID, new String[]{Long.toString(activeTodo)});

        if (returnCount == 0){
            Toast.makeText(this, "Update failed!", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Update successful", Toast.LENGTH_SHORT).show();
        activeTodo = -1;
        changeState();
        getTodos();
        todoText.setText("");
    }

    public void onClickDelete(View view){
        //deletes item
        Uri _uri = ContentUris.withAppendedId(TodoProvider.CONTENT_URI, activeTodo);
        int returnCount = getContentResolver().delete(
                _uri, TodoProvider._ID, new String[]{Long.toString(activeTodo)});

        if (returnCount == 0){
            Toast.makeText(this, "Delete failed!", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Delete successful", Toast.LENGTH_SHORT).show();
        activeTodo = -1;
        changeState();
        getTodos();
        todoText.setText("");
    }

    public void onClickAddToDo(View view) {
        // Add a new record
        String note = todoText.getText().toString();
        if (note == null || note.trim().equals("")) {
            Toast.makeText(getBaseContext(),
                    "You must enter a To Do item to save", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(TodoProvider.NOTE, note);

        Uri uri = getContentResolver().insert(
                TodoProvider.CONTENT_URI, values);
        if (uri != null) {
            Toast.makeText(getBaseContext(),
                    "Saved " + uri.toString(), Toast.LENGTH_SHORT).show();
            getTodos();
            todoText.setText("");
        } else {
            Toast.makeText(getBaseContext(),
                    "There may have been an issue inserting the record", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateChecks(View view){
        View v;
        CheckBox doneToggle;
        boolean isDone;

        for (int i = 0; i < listView.getCount(); i++){
            v = listView.getChildAt(i);
            doneToggle = (CheckBox) v.findViewById(R.id.doneToggle);
            isDone = doneToggle.isChecked();

            ContentValues values = new ContentValues();
            values.put(TodoProvider.DONE, isDone ? "1":"0");

            Uri _uri = ContentUris.withAppendedId(TodoProvider.CONTENT_URI, i+1);
            int returnCount = getContentResolver().update(
                    _uri, values, TodoProvider._ID, new String[]{Long.toString(i+1)});
        }

        activeTodo = -1;
        getTodos();
    }

    public void toggleFilter(View view){
        //unfiltered to filtered
        if (filtered == false){
            filtered = true;
        }
        else{
            filtered = false;
        }
        getTodos();
    }


    void getTodos() {
        String[] projection = {
                TodoProvider._ID,    // Contract class constant for the _ID column name
                TodoProvider.NOTE,// ,   // Contract class constant for the word column name
                TodoProvider.CREATED,
                TodoProvider.COMPLETED,
                TodoProvider.DONE
        };

        // Defines a string to contain the selection clause
        String selectionClause = null;

        // Initializes an array to contain selection arguments
        String selArg = "";

        if (filtered == true){
            selectionClause = TodoProvider.DONE + " = ?";
            selArg = "0";
        }
        String[] selectionArgs = {selArg};

        mCursor = getContentResolver().query(
                TodoProvider.CONTENT_URI,                                                   // The content URI of the target table
                projection,                                                                 // The columns to return for each row
                selectionClause,                                                            // Either null, or the word the user entered
                selectionArgs,                                                              // Either empty, or the string the user entered
                null);                                                            // The sort order for the returned rows


        if (listView.getAdapter() == null) {
            cursorAdapter = new SimpleCursorAdapter(
                    getApplicationContext(),                                                    // The application's Context object
                    R.layout.list_item,                                                         // A layout in XML for one row in the ListView
                    mCursor,                                                                    // The result from the query
                    new String[]{TodoProvider.NOTE, TodoProvider.CREATED, TodoProvider.DONE},   // A string array of column names in the cursor
                    new int[]{R.id.noteText, R.id.createdText, R.id.doneToggle},                        // An integer array of view IDs in the row layout
                    0);

            int myBooleanColumnPosition = mCursor
                    .getColumnIndex("done");

            //assign our adapter and set our item click listener
            listView.setAdapter(cursorAdapter);

            //Note: for this to work either children must be unfocusable or parent needs android:descendantFocusability="blocksDescendants"
            listView.setOnItemClickListener((adapter, view, position, todoId) -> {
                Toast.makeText(getApplicationContext(), "selected Item Name is " + todoId, Toast.LENGTH_LONG).show();
                todoText.setText(((TextView) view.findViewById(R.id.noteText)).getText());
                activeTodo = todoId;
                changeState();
            });

        } else {
            //update the listview
            ((SimpleCursorAdapter) listView.getAdapter()).swapCursor(mCursor);
            ((SimpleCursorAdapter) listView.getAdapter()).notifyDataSetChanged();
        }
    }
}