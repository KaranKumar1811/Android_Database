package com.w20.databasedemo;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.util.List;

public class EmployeeAdapter extends ArrayAdapter {

    Context mContext;
    int layoutRes;
    List<Employee> employees;
    SQLiteDatabase mDatabase;


    public EmployeeAdapter(@NonNull Context mContext, int layoutRes, List<Employee> employees, SQLiteDatabase mDatabase) {
        super(mContext,layoutRes, employees);
        this.mContext = mContext;
        this.layoutRes = layoutRes;
        this.employees = employees;
        this.mDatabase = mDatabase;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(layoutRes,null);
        TextView tvName = v.findViewById(R.id.tv_name);
        TextView tvSalary = v.findViewById(R.id.tv_salary);
        TextView tvDept = v.findViewById(R.id.tv_department);
        TextView tvJoiningDate = v.findViewById(R.id.tv_joiningdate);

        final Employee employee = employees.get(position);
        tvName.setText(employee.getName());
        tvSalary.setText(String.valueOf(employee.getSalary()));
        tvDept.setText(employee.getDept());
        tvJoiningDate.setText(employee.getJoiningdate());

        v.findViewById(R.id.btn_edit_employee).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEmployee(employee);
            }
        });


        v.findViewById(R.id.btn_delete_employee).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEmployee(employee);
            }
        });

        return v;


    }

    private void deleteEmployee(final Employee employee) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Are you sure ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sql = "Delete from employees Where id = ?";
                mDatabase.execSQL(sql,new Integer[]{employee.getId()});
                loadEmployees();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog =builder.create();
        alertDialog.show();


    }


    private void updateEmployee(final Employee employee) {

        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        LayoutInflater inflater =  LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.dialog_update_employee,null);
        alert.setView(v);
        final AlertDialog alertDialog = alert.create();
        alert.show();

        final EditText etName = v.findViewById(R.id.editTextName);
        final EditText etSalary = v.findViewById(R.id.editTextSalary);
        final Spinner spinner = v.findViewById(R.id.spinnerDepartment);

        etName.setText(employee.getName());
        etSalary.setText(String.valueOf(employee.getSalary()));

        v.findViewById(R.id.btn_Update_Employee).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String salary = etSalary.getText().toString().trim();
                String dept = spinner.getSelectedItem().toString();

                if(name.isEmpty()){
                    etName.setError("Please Enter Name");
                    etName.requestFocus();
                    return;
                }

                if(salary.isEmpty()){
                    etSalary.setError("Please Enter Salary");
                    etSalary.requestFocus();
                    return;
                }
                String udpateSQL = "Update employees SET name = ? , department = ? , salary = ? where id = ?";
                mDatabase.execSQL(udpateSQL,new String[]{name,dept,salary,String.valueOf(employee.getId())});
                Toast.makeText(mContext,"Employee Updated",Toast.LENGTH_LONG).show();
                loadEmployees();
                alertDialog.dismiss();
            }
        });





    }


    private void loadEmployees() {
    String sql = "Select * from employees";
        Cursor cursor = mDatabase.rawQuery(sql,null);
        employees.clear();
        if(cursor.moveToFirst()){

            do{
                employees.add(new Employee(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getDouble(4)

                ));
            } while (cursor.moveToNext());
            cursor.close();
        }
        notifyDataSetChanged();
    }


}
