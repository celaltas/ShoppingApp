package com.example.shoppingapp;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class MainActivity extends AppCompatActivity  implements CreateShoppingDialog.CreateShoppingDialogListener,UpdateDialogForRec.UpdateDialogRecListener{

    private RecyclerView shoppingRecView;
    private FirebaseFirestore firestore;
    private CollectionReference collectionReference;
    FloatingActionButton fab;
    Shopping shoppingToWrite;
    ArrayList<Shopping> shoppings = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        firestore = FirebaseFirestore.getInstance();


        shoppingRecView=findViewById(R.id.listShoppingRecView);
        fab=findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();

            }
        });



        fetchShoppingFromStore(shoppings);





    }

    private void openDialog() {
        CreateShoppingDialog createShoppingDialog = new CreateShoppingDialog();
        createShoppingDialog.show(getSupportFragmentManager(), "example dialog");
    }

    private void initializeRecView(ArrayList<Shopping> shoppings) {
        ShoppingsRecViewAdapter adapter = new ShoppingsRecViewAdapter(this);
        adapter.setShoppings(shoppings);
        shoppingRecView.setAdapter(adapter);
        shoppingRecView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
    }

    private void fetchShoppingFromStore(ArrayList<Shopping> shoppings) {
        shoppings.clear();
        collectionReference = firestore.collection("Shoppings");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map map = document.getData();
                        String name = map.get("name").toString();
                        boolean status = (boolean) map.get("status");

                        String id = document.getId();
                        Timestamp ts = (Timestamp) map.get("date");
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
                        String dateString = simpleDateFormat.format(ts.toDate());
                        Date date = null;
                        try {
                            date = simpleDateFormat.parse(dateString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String place = map.get("place").toString();
                        String imageUri;
                        if(map.containsKey("imageUri")){
                            imageUri = map.get("imageUri").toString();
                        }else{
                            imageUri = "";
                        }

                        Shopping shopping = new Shopping(id,name,date,place,imageUri,status);

                        shoppings.add(shopping);
                    }
                    initializeRecView(shoppings);

                }else{
                    System.out.println("Error getting documents: " + task.getException());
                }
            }
        });




    }

    @Override
    public void applyTexts(String shoppingName, String shoppingPlace, Date date) {
        boolean status = false;
        shoppingToWrite = new Shopping(shoppingName,date,shoppingPlace,status);
        writeShoppingFirebase(shoppingToWrite);
        refresh(1000);

    }

    private void writeShoppingFirebase(Shopping shoppingToWrite) {
        Map map = shoppingToWrite.toMap();
        firestore.collection("Shoppings").document()
                .set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                       System.out.println("DocumentSnapshot successfully written!");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Error getting documents: " + e);
                    }
                });

    }

    private void refresh(int milliseconds){
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                fetchShoppingFromStore(shoppings);
            }
        };
        handler.postDelayed(runnable,milliseconds);
    }


    @Override
    public void fetchText(String shoppingName, String shoppingPlace, Date date,String Id) {
        DocumentReference docRef = firestore.collection("Shoppings").document(Id);
        docRef.update("name", shoppingName,
                "date", date,
                                   "place", shoppingPlace
                    ).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println( "DocumentSnapshot successfully updated!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println( "Error updating document"+ e);
                    }
                });

        refresh(1000);
    }
}