package com.example.stanley.cgpacalculator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class GPAActivity extends AppCompatActivity {

    ArrayList<String> list1 = new ArrayList<String>();
    ArrayList<String> list2 = new ArrayList<String>();
    ArrayList<String> list3 = new ArrayList<String>();
    List<String> course;
    List<String> grade;
    List<Integer> unit;
    List<Integer> point;
    Spinner spinner1, spinner3, spinner2;
    TextView textViewName, textViewNum, d, e, f;
    int a;
    double totalUnit, totatPoint;
    Button next, back, finish;
    EditText cour, uni;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpa);
        spinner1 = findViewById(R.id.level_spinner);
        spinner2 = findViewById(R.id.semester_spinner);
        spinner3 = findViewById(R.id.grade);
        textViewName = findViewById(R.id.nam);
        textViewNum = findViewById(R.id.count);
        cour = findViewById(R.id.cousrse_code);
        uni = findViewById(R.id.unit);
        next = findViewById(R.id.nextP);
        back = findViewById(R.id.previous);
        finish = findViewById(R.id.check);
        d = findViewById(R.id.see);
        e = findViewById(R.id.see2);
        f = findViewById(R.id.see3);
        a = 0;
        unit = new ArrayList<>();
        point = new ArrayList<>();
        course = new ArrayList<>();
        grade = new ArrayList<>();
        adds();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        back.setEnabled(false);
        String eamval = getIntent().getStringExtra("aha");
        textViewName.setText(eamval);

        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, R.layout.spinner_layout, list1);
        // Apply the adapter to the spinner
        spinner1.setAdapter(adapter1);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, R.layout.spinner_layout, list2);
        // Apply the adapter to the spinner
        spinner2.setAdapter(adapter2);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, R.layout.spinner_layout, list3);
        // Apply the adapter to the spinner
        spinner3.setAdapter(adapter3);


//        listener for the next and finish button

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forward();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backward();
            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finish.getText().equals("Finished")) {
                    see();
                } else {
                    Toast.makeText(GPAActivity.this, "Saving", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void backward() {
        int size = course.size();
        a -= 1;
        if (course.size() < 0 | a < 0) {
            Toast.makeText(this, "You have reach the end", Toast.LENGTH_SHORT).show();
            return;
        }

        cour.setText(""+course.get(size-a));
        uni.setText(""+unit.get(size-a));


    }

    private void forward() {
        try {

            if (TextUtils.isEmpty(cour.getText().toString())) {
                //email is empty
                Toast.makeText(this, "Please Enter the course code", Toast.LENGTH_SHORT).show();
                cour.setError("*Required");
                cour.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(uni.getText().toString())) {
                //email is empty
                Toast.makeText(this, "Please Enter the course unit(CL)", Toast.LENGTH_SHORT).show();
                uni.setError("*Required");
                uni.requestFocus();
                return;
            }
            if (spinner3.getSelectedItem().toString().equals("Grade")) {
                //email is empty
                Toast.makeText(this, "Please Select the course Grade", Toast.LENGTH_SHORT).show();
                spinner3.requestFocus();
                return;
            }

            back.setEnabled(true);

            a = a + 1;
            textViewNum.setText(Integer.toString(a));

            course.add(cour.getText().toString());
            grade.add(spinner3.getSelectedItem().toString());
            unit.add(Integer.parseInt(uni.getText().toString()));
            cour.setText(null);
            uni.setText(null);
            spinner3.setSelection(0);
            System.out.println(course + " and " + grade + " plus " + unit);

        } catch (Exception e) {
            androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(GPAActivity.this).create();
            alertDialog.setTitle("Error Alert");
            alertDialog.setIcon(R.drawable.ic_error_black_24dp);
            alertDialog.setMessage("The following error occurred\n" + e.getMessage());
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        }

    }

    void see() {
        if (spinner1.getSelectedItem().toString().equals("Select Year")) {
            //email is empty
            Toast.makeText(this, "Please Select the Year", Toast.LENGTH_SHORT).show();
            spinner3.requestFocus();
            return;
        }
        if (spinner2.getSelectedItem().toString().equals("Select Semester")) {
            //email is empty
            Toast.makeText(this, "Please Select the Semester", Toast.LENGTH_SHORT).show();
            spinner3.requestFocus();
            return;
        }

        for (int i = 0; i < grade.size(); i++) {
            int cl = unit.get(i);
            String car = grade.get(i);
            point.add(getPoint(cl, car.charAt(0)));
        }
        System.out.println("points " + point);

        for (int t = 0; t < point.size(); t++) {
            totalUnit += unit.get(t);
            totatPoint += point.get(t);
        }
        double gpa = totatPoint / totalUnit;

        for (int i = 0; i < course.size(); i++) {
            d.append("\n\n" + course.get(i));
            e.append("\n\n" + unit.get(i));
            f.append("\n\n" + grade.get(i));
        }
        finish.setText("Save");
        enab(false);
        if (!TextUtils.isEmpty(textViewNum.getText().toString())) {
            cour.setError("");
            cour.requestFocus();
        }
        AlertDialog alertDialog = new AlertDialog.Builder(GPAActivity.this).create();
        alertDialog.setTitle("Calculated");
        alertDialog.setMessage("Your GPA is \n" +
                gpa + "\nPress save to store for later view");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }

    int getPoint(int cl, char grade) {
        int tcp = 0;
        switch (grade) {
            case 'A':
                tcp = cl * 5;
                break;
            case 'B':
                tcp = cl * 4;
                break;
            case 'C':
                tcp = cl * 3;
                break;
            case 'D':
                tcp = cl * 2;
                break;
            case 'E':
                tcp = cl * 1;
                break;
            case 'F':
                tcp = cl * 0;
                break;
            default:
                break;
        }

        return tcp;
    }

    void enab(boolean val) {
//        finish.setEnabled(val);
        next.setEnabled(val);
        back.setEnabled(val);
        next.setBackgroundColor(getResources().getColor(R.color.gray));
        next.setTextColor(getResources().getColor(R.color.colorPrimary));
        back.setBackgroundColor(getResources().getColor(R.color.gray));
        back.setTextColor(getResources().getColor(R.color.colorPrimary));
        cour.setEnabled(val);
        uni.setEnabled(val);
        spinner1.setEnabled(val);
        spinner2.setEnabled(val);
        spinner3.setEnabled(val);

    }


    private void adds() {
        list1.add("Select Year");
        list1.add("1");
        list1.add("2");
        list1.add("3");
        list1.add("4");
        list1.add("5");
        list1.add("6");
        list1.add("7");
        list1.add("8");
        list1.add("9");
        list1.add("10");

        list2.add("Select Semester");
        list2.add("First Semester");
        list2.add("Second Semester");

        list3.add("Grade");
        list3.add("A");
        list3.add("B");
        list3.add("C");
        list3.add("D");
        list3.add("E");
        list3.add("F");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Menu inflater
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_reset) {
            recreate();
            cour.setText(null);
            uni.setText(null);
            spinner3.setSelection(0);
            spinner2.setSelection(0);
            spinner1.setSelection(0);
        }
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
