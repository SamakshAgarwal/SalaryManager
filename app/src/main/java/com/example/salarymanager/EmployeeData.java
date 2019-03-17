package com.example.salarymanager;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class EmployeeData extends AppCompatActivity {
    static String employee,date;
    static boolean isFirstTime=true;
    ConstraintLayout constraintLayout;
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"));
    MenuItem dateMenuButton;
    static ListView itemListView, rateListView, quantityListView, totalListView;
    SQLiteDatabase myDatabase;
    List<String> itemList,rateList,quantityList,totalList, idList;
    ArrayAdapter<String> itemArrayAdapter, rateArrayAdapter, quantityArrayAdapter, totalArrayAdapter;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_data);

        definitions();
        Objects.requireNonNull(getSupportActionBar()).setTitle(employee);
        swipeFunction();
        getDatabaseEntries();
    }

    public void addEntry(View view){
        final EditText itemEditText = new EditText(this);
        final EditText rateEditText = new EditText(this);
        final EditText quantityEditText = new EditText(this);
        final LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(itemEditText);
        linearLayout.addView(rateEditText);
        linearLayout.addView(quantityEditText);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        itemEditText.setLayoutParams(layoutParams);
        itemEditText.setHint("Enter item name");
        itemEditText.setSingleLine();
        rateEditText.setLayoutParams(layoutParams);
        rateEditText.setHint("Enter rate");
        rateEditText.setSingleLine();
        rateEditText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        quantityEditText.setLayoutParams(layoutParams);
        quantityEditText.setHint("Enter quantity");
        quantityEditText.setSingleLine();
        quantityEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        AlertDialog.Builder alertDialog= new AlertDialog.Builder(this);
        alertDialog.setTitle("Add Entry")
                .setView(linearLayout)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        itemList.add(itemEditText.getText().toString());
                        rateList.add(rateEditText.getText().toString());
                        quantityList.add(quantityEditText.getText().toString());
                        totalList.add("₹"+String.valueOf((int) (Double.parseDouble(rateEditText.getText().toString())*Integer.parseInt(quantityEditText.getText().toString()))));
                        itemArrayAdapter.notifyDataSetChanged();
                        rateArrayAdapter.notifyDataSetChanged();
                        quantityArrayAdapter.notifyDataSetChanged();
                        totalArrayAdapter.notifyDataSetChanged();
                        addDatabaseEntry(itemEditText.getText().toString(),Double.parseDouble(rateEditText.getText().toString()),Integer.parseInt(quantityEditText.getText().toString()));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = alertDialog.create();
                dialog.show();
        /*if(itemEditText.getText().toString().isEmpty()||rateEditText.getText().toString().isEmpty()||quantityEditText.getText().toString().isEmpty()){
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        }*/
    }

    public void definitions(){
        constraintLayout = findViewById(R.id.constraintLayout);

        itemListView = findViewById(R.id.itemListView);
        rateListView = findViewById(R.id.rateListView);
        quantityListView = findViewById(R.id.quantityListView);
        totalListView = findViewById(R.id.totalListView);

        itemList = new ArrayList<>();
        rateList = new ArrayList<>();
        quantityList = new ArrayList<>();
        totalList = new ArrayList<>();
        idList = new ArrayList<>();

        itemArrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,itemList);
        rateArrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,rateList);
        quantityArrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,quantityList);
        totalArrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,totalList);

        itemListView.setAdapter(itemArrayAdapter);
        rateListView.setAdapter(rateArrayAdapter);
        quantityListView.setAdapter(quantityArrayAdapter);
        totalListView.setAdapter(totalArrayAdapter);

        i = getIntent();
        employee = i.getStringExtra("Employee");
        if(isFirstTime) {
            date = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "/" + String.valueOf(calendar.get(Calendar.MONTH) + 1) + "/" + String.valueOf(calendar.get(Calendar.YEAR));
            isFirstTime=false;
        }
        myDatabase = this.openOrCreateDatabase("EmployeeData",MODE_PRIVATE,null);
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS employee_data ( employee_name TEXT, item TEXT, rate REAL, quantity INTEGER, date TEXT, id INTEGER PRIMARY KEY)");
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.employee_data_menu,menu);
        dateMenuButton = menu.findItem(R.id.dateMenubutton);
        dateMenuButton.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        dateMenuButton.setTitle(date);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        calendarDialog();
        return super.onOptionsItemSelected(item);
    }

    public void calendarDialog(){
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date = String.valueOf(dayOfMonth)+"/"+String.valueOf(month+1)+"/"+String.valueOf(year);
                System.out.println(date);
                dateMenuButton.setTitle(date);
                clearPage();
                getDatabaseEntries();
            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void swipeFunction(){
        constraintLayout.setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public void onSwipeRight() {
                calendar.add(Calendar.DATE,-1);
                date = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(calendar.get(Calendar.MONTH)+1)+"/"+String.valueOf(calendar.get(Calendar.YEAR));
                dateMenuButton.setTitle(date);
                clearPage();
                getDatabaseEntries();
                super.onSwipeRight();
            }

            @Override
            public void onSwipeLeft() {
                calendar.add(Calendar.DATE,1);
                date = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(calendar.get(Calendar.MONTH)+1)+"/"+String.valueOf(calendar.get(Calendar.YEAR));
                dateMenuButton.setTitle(date);
                clearPage();
                getDatabaseEntries();
                super.onSwipeLeft();
            }
        });
    }

    public void addDatabaseEntry(String item, double rate, int quantity){
        ContentValues contentValues = new ContentValues();
        contentValues.put("employee_name",employee);
        contentValues.put("item",item);
        contentValues.put("rate",rate);
        contentValues.put("quantity",quantity);
        contentValues.put("date",date);
        myDatabase.insert("employee_data",null,contentValues);
        System.out.println("databaseAddEntry() run");
    }

    public void getDatabaseEntries(){
         Cursor cursor =myDatabase.rawQuery("SELECT * FROM employee_data WHERE employee_name=\""+employee+"\" AND date=\""+date+"\"",null);
        System.out.println("Date : "+date);
        if(cursor.moveToFirst()) {
            int itemIndex = cursor.getColumnIndex("item");
            int rateIndex = cursor.getColumnIndex("rate");
            int quantityIndex = cursor.getColumnIndex("quantity");
            int idIndex = cursor.getColumnIndex("id");
            cursor.moveToFirst();
            do {
                itemList.add(cursor.getString(itemIndex));
                rateList.add(cursor.getString(rateIndex));
                quantityList.add(cursor.getString(quantityIndex));
                idList.add(cursor.getString(idIndex));
                totalList.add("₹"+String.valueOf((int)(Double.parseDouble(cursor.getString(rateIndex))*Integer.parseInt(cursor.getString(quantityIndex)))));
                itemArrayAdapter.notifyDataSetChanged();
                rateArrayAdapter.notifyDataSetChanged();
                quantityArrayAdapter.notifyDataSetChanged();
                totalArrayAdapter.notifyDataSetChanged();
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public void clearPage(){
        itemList.clear();
        rateList.clear();
        quantityList.clear();
        totalList.clear();
        idList.clear();

        itemArrayAdapter.notifyDataSetChanged();
        rateArrayAdapter.notifyDataSetChanged();
        quantityArrayAdapter.notifyDataSetChanged();
        totalArrayAdapter.notifyDataSetChanged();

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
