package com.example.shoppingapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class CreateShoppingDialog  extends AppCompatDialogFragment {

    private EditText dialogShoppingName;
    private EditText dialogShoppingPlace;
    private EditText dialogShoppingDate;
    private CreateShoppingDialogListener listener;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable  Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.create_shopping_layout, null);

        builder.setView(view)
                .setTitle("Create Shopping")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String shoppingName = dialogShoppingName.getText().toString();
                        String shoppingPlace = dialogShoppingPlace.getText().toString();
                        Date shoppingDate = null;
                        try {
                            shoppingDate = new SimpleDateFormat("dd/MM/yyyy").parse(dialogShoppingDate.getText().toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        listener.applyTexts(shoppingName,shoppingPlace,shoppingDate);

                    }
                });

        dialogShoppingName = view.findViewById(R.id.dialogShoppingName);
        dialogShoppingPlace = view.findViewById(R.id.dialogShoppingPlace);
        dialogShoppingDate = view.findViewById(R.id.dialogShoppingDate);


        dialogShoppingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog dialog = new DatePickerDialog(
                        getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener, year, month, day);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = dayOfMonth + "/" + month + "/" + year;
                dialogShoppingDate.setText(date);
            }
        };



        return builder.create();
    }

    @Override
    public void onAttach(@NonNull  Context context) {
        super.onAttach(context);

        listener = (CreateShoppingDialogListener) context;
    }

    public interface CreateShoppingDialogListener{
        void applyTexts(String shoppingName, String shoppingPlace, Date date);
    }
}


