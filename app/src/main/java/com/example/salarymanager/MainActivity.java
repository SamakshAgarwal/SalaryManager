package com.example.salarymanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button maleButton = findViewById(R.id.maleButton);
        maleButton.setBackgroundResource(R.drawable.male);
        maleButton.setTag(1);
        Button femaleButton = findViewById(R.id.femaleButton);
        femaleButton.setBackgroundResource(R.drawable.female);
        femaleButton.setTag(2);
    }

    public void onClick(View view){
        Intent intent = new Intent(getApplicationContext(),Employees.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("Gender",(int)view.getTag());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}
