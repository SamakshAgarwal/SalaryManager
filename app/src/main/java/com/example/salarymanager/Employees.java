package com.example.salarymanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Employees extends AppCompatActivity {
    List<String> maleEmployeeList=new ArrayList<>();
    List<String> femaleEmployeeList=new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    ListView listView;
    SharedPreferences sharedPreferences;
    Gson gson;
    static int gender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employees);
        listView = findViewById(R.id.listView);
        sharedPreferences = getSharedPreferences("com.example.salarymanager",MODE_PRIVATE);
        Intent i = getIntent();
        gender= i.getIntExtra("Gender",0);
        if(gender==1) {
            if (sharedPreferences.getString("MaleList", null) != null) {
                maleEmployeeList = getList("MaleList");
            }
        }
        else {
            if (sharedPreferences.getString("FemaleList", null) != null) {
                femaleEmployeeList = getList("FemaleList");
            }
        }

        if(gender==1) {
            arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, maleEmployeeList);
        }
        else{
            arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, femaleEmployeeList);
        }

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),EmployeeData.class);
                intent.putExtra("Employee",arrayAdapter.getItem(position));
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(Employees.this)
                        .setTitle("Delete Employee")
                        .setMessage("Are you sure you want to delete?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(gender==1){
                                    maleEmployeeList.remove(position);
                                }
                                else {
                                    femaleEmployeeList.remove(position);
                                }
                                arrayAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.employees_menu,menu);
        MenuItem addMenu1Button = menu.findItem(R.id.addMenu1Button);
        addMenu1Button.setIconTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white,null)));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final EditText editText = new EditText(this);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        editText.setLayoutParams(layoutParams);
        new AlertDialog.Builder(this)
                .setTitle("Add Employee")
                .setView(editText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(gender==1) {
                            maleEmployeeList.add(editText.getText().toString());
                        }
                        else {
                            femaleEmployeeList.add(editText.getText().toString());
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(gender==1)
            sharedPreferences.edit().putString("MaleList",setList(maleEmployeeList)).apply();
        else
            sharedPreferences.edit().putString("FemaleList",setList(femaleEmployeeList)).apply();
    }


    public <T> String setList(List<T> list){
        gson = new Gson();
        return gson.toJson(list) ;
    }

    public <T> ArrayList<T> getList(String key){
        List<T> arrayItems = new ArrayList<>();
        String serializedObject = sharedPreferences.getString(key, null);
        if (serializedObject != null){
            Gson gson = new Gson();
            Type type = new TypeToken<List<T>>(){}.getType();
            arrayItems = gson.fromJson(serializedObject, type);
        }
        return (ArrayList<T>) arrayItems;
    }
}
