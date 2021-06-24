package com.example.shoppingapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

public class ShoppingsRecViewAdapter extends RecyclerView.Adapter<ShoppingsRecViewAdapter.ViewHolder> {

    private ArrayList<Shopping> shoppings = new ArrayList<>();
    private Context context;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();


    public ShoppingsRecViewAdapter(Context context) {
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shoppings_list_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull  ShoppingsRecViewAdapter.ViewHolder holder, int position) {
        holder.txtName.setText(shoppings.get(position).getName());
        holder.txtPlace.setText(shoppings.get(position).getPlace());
        holder.txtDate.setText(shoppings.get(position).getDate().toString());
        holder.checkBox.setChecked(shoppings.get(position).isStatus());
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                String id = shoppings.get(position).getId();
                intent = new Intent(context, ShopDetails.class);
                intent.putExtra("ShoppingID", id);
                context.startActivity(intent);

            }
        });

        holder.deleteListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firestore.collection("Shoppings").document(shoppings.get(position).getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                System.out.println("DocumentSnapshot successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println("Error deleting document" +e);
                            }
                        });
                shoppings.remove(position);
                notifyItemRemoved(position);
            }
        });
        holder.updateListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateDialogForRec createDialogForRec = new UpdateDialogForRec(shoppings.get(position).getId());

                createDialogForRec.show(((AppCompatActivity)context).getSupportFragmentManager(), "example dialog");

            }
        });


    }

    @Override
    public int getItemCount() {
        return shoppings.size();
    }

    public void setShoppings(ArrayList<Shopping> shoppings) {
        this.shoppings = shoppings;
        notifyDataSetChanged();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView txtName, txtDate,txtPlace;
        private CardView parent;
        private ImageButton updateListItem, deleteListItem;
        private CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            parent = itemView.findViewById(R.id.parent);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtPlace = itemView.findViewById(R.id.txtPlace);
            checkBox = itemView.findViewById(R.id.checkBox);
            updateListItem = itemView.findViewById(R.id.updateListItem);
            deleteListItem = itemView.findViewById(R.id.deleteListItem);
        }


    }
}
