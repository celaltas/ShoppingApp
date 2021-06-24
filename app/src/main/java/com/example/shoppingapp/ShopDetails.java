package com.example.shoppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.location.Address;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ShopDetails extends AppCompatActivity {

    private TextView txtNameShopDetail,txtDateShopDetail,txtPlaceShopDetail,txtLocationShopDetail;
    private EditText edtNoteShopDetail;
    private TextView toolbarText;
    private CheckBox checkBoxDetail;
    private Button mapBtn,saveBtn,remBtn;
    private ImageView billPhoto, leftIcon;
    private static final int IMAGE_PICK_CODE=101;
    String id;

    private FirebaseFirestore firestore;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);


        firestore = FirebaseFirestore.getInstance();
        txtNameShopDetail=findViewById(R.id.txtNameShopDetail);
        txtDateShopDetail=findViewById(R.id.txtDateShopDetail);
        txtPlaceShopDetail=findViewById(R.id.txtPlaceShopDetail);
        txtLocationShopDetail=findViewById(R.id.txtLocationShopDetail);
        checkBoxDetail=findViewById(R.id.checkBoxDetail);
        edtNoteShopDetail=findViewById(R.id.edtNoteShopDetail);
        toolbarText = findViewById(R.id.toolbarText);
        toolbarText.setText("Shop Details");
        leftIcon = findViewById(R.id.leftIcon);

        billPhoto=findViewById(R.id.billPhoto);
        mapBtn=findViewById(R.id.mapBtn);
        saveBtn=findViewById(R.id.saveBtn);
        remBtn=findViewById(R.id.remBtn);

        if (getIntent().getExtras() != null) {
            id = getIntent().getStringExtra("ShoppingID");
            getShopping(id);
        }






        billPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromGallery();
            }
        });
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopDetails.this, MapActivity.class);
                intent.putExtra("ShoppingID", id);
                startActivity(intent);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String note = edtNoteShopDetail.getText().toString();
                boolean status = checkBoxDetail.isChecked();
                DocumentReference docRef = firestore.collection("Shoppings").document(id);
                docRef.update("note", note,
                        "status", status);

                Toast.makeText(ShopDetails.this, "Saved", Toast.LENGTH_SHORT).show();
            }
        });

        remBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int  mHour, mMinute;
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(ShopDetails.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                    c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    c.set(Calendar.MINUTE, minute);
                                    c.set(Calendar.SECOND, 0);

                                    setAlarm(c);

                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopDetails.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }

    private void setAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ShopDetails.this, AlertReceiver.class);
        String title = edtNoteShopDetail.getText().toString();
        String message = txtNameShopDetail.getText().toString();
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ShopDetails.this,1,intent,0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),pendingIntent);
        Toast.makeText(ShopDetails.this, "Alarm created.m", Toast.LENGTH_SHORT).show();
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK && requestCode==IMAGE_PICK_CODE){
            String selectedImageUri = data.getData().toString();
            billPhoto.setImageURI(Uri.parse(selectedImageUri));
            updateImageFirestore(selectedImageUri,id);
            final int takeFlags = data.getFlags()& (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(Uri.parse(selectedImageUri), takeFlags);




        }

    }




    private void updateImageFirestore(String selectedImageUri,String id) {

        DocumentReference docRef = firestore.collection("Shoppings").document(id);
        docRef.update("imageUri", selectedImageUri);

    }

    private void getShopping(String id)  {

        DocumentReference docRef = firestore.collection("Shoppings").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull  Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map map = document.getData();
                        String name = map.get("name").toString();
                        boolean status = (boolean) map.get("status");
                        Timestamp ts = (Timestamp) map.get("date");
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
                        String dateString = simpleDateFormat.format(ts.toDate());

                        String place = map.get("place").toString();
                        String imageUri;
                        if(map.containsKey("imageUri")){
                            imageUri = map.get("imageUri").toString();
                        }else{
                            imageUri = "";
                        }
                        String location;
                        if(map.containsKey("location")){
                            location = map.get("location").toString();
                        }else{
                            location = "No location";
                        }

                        String note;
                        if(map.containsKey("note")){
                            note = map.get("note").toString();
                        }else{
                            note = "No note";
                        }

                        txtNameShopDetail.setText(name);
                        txtDateShopDetail.setText(dateString);
                        txtPlaceShopDetail.setText(place);
                        checkBoxDetail.setChecked(status);
                        txtLocationShopDetail.setText(location);
                        edtNoteShopDetail.setText(note);
                        if(!imageUri.equals("")){
                            billPhoto.setImageURI(Uri.parse(imageUri));
                        }else {
                            System.out.println("imageUri yok");
                        }



                        System.out.println( "DocumentSnapshot data: " + document.getData());
                    } else {
                        System.out.println( "No such document");
                    }
                } else {
                    System.out.println("get failed with "+ task.getException());
                }

            }
        });
    }

}