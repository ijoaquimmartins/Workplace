package in.megasoft.workplace;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class EmployeeDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_details);

        //SELECT * FROM `employees` LEFT JOIN designations ON employees.designation=designations.id LEFT JOIN locations ON employees.depot=locations.id WHERE employees.id='1'



    }
}