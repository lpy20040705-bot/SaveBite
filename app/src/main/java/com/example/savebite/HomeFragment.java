package com.example.savebite;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private EditText etName, etCategory, etQuantity, etDate;
    private Button btnAdd;
    private TextView tvWelcome;
    private ImageView btnLogout;
    private DatabaseHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        etName = view.findViewById(R.id.etName);
        etCategory = view.findViewById(R.id.etCategory);
        etQuantity = view.findViewById(R.id.etQuantity);
        etDate = view.findViewById(R.id.etDate);
        btnAdd = view.findViewById(R.id.btnAdd);
        tvWelcome = view.findViewById(R.id.tvWelcome);
        btnLogout = view.findViewById(R.id.iconLogout);

        db = new DatabaseHelper(getContext());

        SharedPreferences prefs = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String username = prefs.getString("username", "User");
        tvWelcome.setText("Hi, " + username + "!");

        btnLogout.setOnClickListener(v -> showLogoutConfirmation());

        etDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(getContext(), (view1, year, month, dayOfMonth) -> {
                String date = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
                etDate.setText(date);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnAdd.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String cat = etCategory.getText().toString();
            String qty = etQuantity.getText().toString();
            String date = etDate.getText().toString();

            if(name.isEmpty() || date.isEmpty()) {
                Toast.makeText(getContext(), "Please fill details", Toast.LENGTH_SHORT).show();
            } else {
                db.addItem(name, cat, qty, date);
                Toast.makeText(getContext(), "Item Added!", Toast.LENGTH_SHORT).show();
                etName.setText(""); etCategory.setText(""); etQuantity.setText(""); etDate.setText("");
            }
        });

        return view;
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(getContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SharedPreferences prefs = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                    prefs.edit().clear().apply();

                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("No", null)
                .show();
    }
}