package com.albertukrida.a412020031_projectuas;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ClassTransactionAdapter extends RecyclerView.Adapter<ClassTransactionAdapter.MyViewHolder> {

    Context context;
    ArrayList<ClassTransaction> transactionArrayList = new ArrayList<>();
    InterfaceTransaction interfaceTransaction;

    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();

    String typeSelected;
    String categorySelected;
    String transactionAmt;
    String transactionDesc;
    String transactionDate;
    String transactionMonth;
    String transactionYear;

    Button btnAutomotive, btnBeautyCare, btnBooks, btnDebtCredit, btnDecoration, btnElectronic, btnFashion;
    Button btnFoodBeverages, btnGift, btnHealth, btnHomeLiving, btnSports, btnTransportation, btnOthers;
    TextView txtTransactionAmount, txtTransactionDesc, txtTransactionDate;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView category, date, desc, amount, edit;
        public ImageView icon;
        public Button delete;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            category = itemView.findViewById(R.id.category);
            date = itemView.findViewById(R.id.date);
            desc = itemView.findViewById(R.id.desc);
            amount = itemView.findViewById(R.id.amount);
            edit = itemView.findViewById(R.id.btnEdit);
            delete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public ClassTransactionAdapter(Context context, ArrayList<ClassTransaction> transactionArrayList, InterfaceTransaction interfaceTransaction) {
        this.context = context;
        this.transactionArrayList = transactionArrayList;
        this.interfaceTransaction = interfaceTransaction;
    }

    @NonNull
    @Override
    public ClassTransactionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View v = inflater.inflate(R.layout.cardview_item_transaction, parent, false);

        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ClassTransactionAdapter.MyViewHolder holder, int position) {

        ClassTransaction transaction = transactionArrayList.get(position);

        String convertedAmount = convert(transaction.getType(), transaction.getAmount());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.US);

        TextView cTextView = holder.category;
        cTextView.setText(transaction.getCategory());
        TextView daTextView = holder.date;
        daTextView.setText(dateFormat.format(transaction.getDate()));
        TextView deTextView = holder.desc;
        deTextView.setText(transaction.getDescription());
        TextView aTextView = holder.amount;
        aTextView.setText(convertedAmount);

        if(transaction.getType().equals("Income")){
            aTextView.setTextColor(Color.parseColor("#39B54A"));
        }else{
            aTextView.setTextColor(Color.parseColor("#ff4229"));
        }

        ImageView iconImage = holder.icon;
        if(transaction.getCategory().equals("Automotive")){
            iconImage.setBackgroundResource(R.mipmap.icon_automotive);
        }else if(transaction.getCategory().equals("Beauty & Care")){
            iconImage.setBackgroundResource(R.mipmap.icon_beauty_care);
        }else if(transaction.getCategory().equals("Books")){
            iconImage.setBackgroundResource(R.mipmap.icon_books);
        }else if(transaction.getCategory().equals("Debt/Credit")){
            iconImage.setBackgroundResource(R.mipmap.icon_debt_credit);
        }else if(transaction.getCategory().equals("Decoration")){
            iconImage.setBackgroundResource(R.mipmap.icon_decoration);
        }else if(transaction.getCategory().equals("Electronic")){
            iconImage.setBackgroundResource(R.mipmap.icon_electronic);
        }else if(transaction.getCategory().equals("Fashion")){
            iconImage.setBackgroundResource(R.mipmap.icon_fashion);
        }else if(transaction.getCategory().equals("Food & Beverages")){
            iconImage.setBackgroundResource(R.mipmap.icon_food_beverages);
        }else if(transaction.getCategory().equals("Gift")){
            iconImage.setBackgroundResource(R.mipmap.icon_gift);
        }else if(transaction.getCategory().equals("Health")){
            iconImage.setBackgroundResource(R.mipmap.icon_health);
        }else if(transaction.getCategory().equals("Home & Living")){
            iconImage.setBackgroundResource(R.mipmap.icon_home_living);
        }else if(transaction.getCategory().equals("Sports")){
            iconImage.setBackgroundResource(R.mipmap.icon_sports);
        }else if(transaction.getCategory().equals("Transportation")){
            iconImage.setBackgroundResource(R.mipmap.icon_transportation);
        }else if(transaction.getCategory().equals("Others")){
            iconImage.setBackgroundResource(R.mipmap.icon_others);
        }

        holder.delete.setOnClickListener(view -> remove(transaction.getTransactionID(), transaction));
        holder.edit.setOnClickListener(view -> edit(transaction.getTransactionID(),transaction.getType(),
                transaction.getCategory(),transaction.getAmount(),transaction.getDescription(),
                dateFormat.format(transaction.getDate()), transaction.getMonth(), transaction.getYear()));
    }

    @Override
    public int getItemCount() {
        return transactionArrayList.size();
    }

    public String convert(String type, String amt){
        // Reverse
        String reverse = "";
        for (int i = 0; i < amt.length(); i++) {
            reverse = amt.charAt(i) + reverse;
        }
        // Join
        StringBuilder join = new StringBuilder();
        // Split
        String[] split = reverse.split("");
        // Join and add . every 3 char
        for (int i = 0; i < split.length; i++) {
            if(i != 0 && i%3 == 0){
                join.append(".");
            }
            join.append(split[i]);
        }
        // Reverse again
        String editedAmt = "";
        for (int i = 0; i < join.length(); i++) {
            editedAmt = join.charAt(i) + editedAmt;
        }
        // Add +IDR or -IDR
        if(type.equals("Income")){
            amt = "+IDR " + editedAmt;
        }else{
            amt = "-IDR " + editedAmt;
        }
        // Return
        return amt;
    }

    public void remove(String id, ClassTransaction transaction){
        final AlertDialog.Builder confirmDeleteDialog = new AlertDialog.Builder(context)
                .setTitle("Delete This Transaction?")
                .setCancelable(false);

        confirmDeleteDialog.setPositiveButton("Yes", (dialogInterface, i) -> {
            DocumentReference reff = fStore.collection("transactions")
                    .document(id);
            reff.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(context,
                            "Delete Success! ", Toast.LENGTH_LONG).show();
                    transactionArrayList.remove(transaction);
                    interfaceTransaction.updateData();
                    notifyDataSetChanged();
                }
            }).addOnFailureListener(e -> Toast.makeText(context,
                    "Error! "+e.getMessage(), Toast.LENGTH_LONG).show());
        });

        confirmDeleteDialog.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel());

        confirmDeleteDialog.show();
    }

    public void update(ArrayList<ClassTransaction> classTransactions){
        this.transactionArrayList = classTransactions;
        this.notifyDataSetChanged();
    }

    public void edit(String id, String type, String category, String amount, String desc, String date, String tMonth, String tYear){
        final AlertDialog addTransactionDialog = new AlertDialog.Builder(context)
                .setView(R.layout.dialog_add_transaction)
                .setTitle("Edit this Transaction?")
                .create();
        addTransactionDialog.show();

        assert fAuth.getCurrentUser() != null;

        TextView btnNext = addTransactionDialog.findViewById(R.id.btnNext);
        TextView btnCancel = addTransactionDialog.findViewById(R.id.btnCancel);

        Button btnIncome = addTransactionDialog.findViewById(R.id.btnIncome);
        Button btnExpenses = addTransactionDialog.findViewById(R.id.btnExpenses);

        typeSelected = type;
        categorySelected = category;

        txtTransactionAmount = addTransactionDialog.findViewById(R.id.txtTransactionAmt);
        txtTransactionDesc = addTransactionDialog.findViewById(R.id.txtTransactionDesc);
        txtTransactionDate = addTransactionDialog.findViewById(R.id.txtTransactionDate);

        txtTransactionAmount.setText(amount);
        txtTransactionDesc.setText(desc);
        txtTransactionDate.setText(date);
        transactionMonth = tMonth;
        transactionYear = tYear;

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        btnAutomotive = addTransactionDialog.findViewById(R.id.btnAutomotive);
        btnBeautyCare = addTransactionDialog.findViewById(R.id.btnBeautyCare);
        btnBooks = addTransactionDialog.findViewById(R.id.btnBooks);
        btnDebtCredit = addTransactionDialog.findViewById(R.id.btnDebtCredit);
        btnDecoration = addTransactionDialog.findViewById(R.id.btnDecoration);
        btnElectronic = addTransactionDialog.findViewById(R.id.btnElectronic);
        btnFashion = addTransactionDialog.findViewById(R.id.btnFashion);
        btnFoodBeverages = addTransactionDialog.findViewById(R.id.btnFoodBeverages);
        btnGift = addTransactionDialog.findViewById(R.id.btnGift);
        btnHealth = addTransactionDialog.findViewById(R.id.btnHealth);
        btnHomeLiving = addTransactionDialog.findViewById(R.id.btnHomeLiving);
        btnSports = addTransactionDialog.findViewById(R.id.btnSports);
        btnTransportation = addTransactionDialog.findViewById(R.id.btnTransportation);
        btnOthers = addTransactionDialog.findViewById(R.id.btnOthers);

        btnAutomotive.setOnClickListener(view14 -> updateCategorySelected("Automotive"));
        btnBeautyCare.setOnClickListener(view14 -> updateCategorySelected("Beauty & Care"));
        btnBooks.setOnClickListener(view14 -> updateCategorySelected("Books"));
        btnDebtCredit.setOnClickListener(view14 -> updateCategorySelected("Debt/Credit"));
        btnDecoration.setOnClickListener(view14 -> updateCategorySelected("Decoration"));
        btnElectronic.setOnClickListener(view14 -> updateCategorySelected("Electronic"));
        btnFashion.setOnClickListener(view14 -> updateCategorySelected("Fashion"));
        btnFoodBeverages.setOnClickListener(view14 -> updateCategorySelected("Food & Beverages"));
        btnGift.setOnClickListener(view14 -> updateCategorySelected("Gift"));
        btnHealth.setOnClickListener(view14 -> updateCategorySelected("Health"));
        btnHomeLiving.setOnClickListener(view14 -> updateCategorySelected("Home & Living"));
        btnSports.setOnClickListener(view14 -> updateCategorySelected("Sports"));
        btnTransportation.setOnClickListener(view14 -> updateCategorySelected("Transportation"));
        btnOthers.setOnClickListener(view14 -> updateCategorySelected("Others"));


        if(typeSelected.equals("Income")){
            btnIncome.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_green));
            btnIncome.setTextColor(Color.parseColor("#ffffff"));
            btnExpenses.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button));
            btnExpenses.setTextColor(Color.parseColor("#000000"));
        }else{
            btnIncome.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button));
            btnIncome.setTextColor(Color.parseColor("#000000"));
            btnExpenses.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_red));
            btnExpenses.setTextColor(Color.parseColor("#ffffff"));
        }
        updateCategorySelected(categorySelected);

        btnIncome.setOnClickListener(view13 -> {
            typeSelected = "Income";
            btnIncome.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_green));
            btnIncome.setTextColor(Color.parseColor("#ffffff"));
            btnExpenses.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button));
            btnExpenses.setTextColor(Color.parseColor("#000000"));
            updateCategorySelected(categorySelected);
        });
        btnExpenses.setOnClickListener(view13 -> {
            typeSelected = "Expenses";
            btnIncome.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button));
            btnIncome.setTextColor(Color.parseColor("#000000"));
            btnExpenses.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_red));
            btnExpenses.setTextColor(Color.parseColor("#ffffff"));
            updateCategorySelected(categorySelected);
        });

        txtTransactionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                updateTransactionDate(dayOfMonth,monthOfYear,year);
                            }
                        },
                        year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        btnNext.setOnClickListener(view12 -> {
            if(txtTransactionAmount.getText().toString().length() == 0){
                txtTransactionAmount.setError("Transaction amount is required");
                txtTransactionAmount.requestFocus();
            }else if(txtTransactionDesc.getText().toString().length() >= 24){
                txtTransactionDesc.setError("Transaction description is too long");
                txtTransactionDesc.requestFocus();
            }else{
                transactionAmt = txtTransactionAmount.getText().toString();
                transactionDesc = txtTransactionDesc.getText().toString();
                transactionDate = txtTransactionDate.getText().toString();
                editTransactionToFirestore(id, typeSelected, categorySelected,transactionAmt,
                        transactionDesc,transactionDate,transactionMonth,transactionYear);
                addTransactionDialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(view1 -> addTransactionDialog.dismiss());
    }

    public void editTransactionToFirestore(String id, String type, String category, String amt,
                                          String desc, String date, String month, String year){
        // Get User ID
        String UserID = fAuth.getCurrentUser().getUid();
        // Date
        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
        Date dateFormat = null;
        try {
            dateFormat = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ClassTransaction transaction = new ClassTransaction(id, UserID, type, category, amt, desc, dateFormat, month, year);
        fStore.collection("transactions")
                .document(id)
                .set(transaction)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        interfaceTransaction.updateData();
                        Toast.makeText(context,
                                "Your transaction has been successfully updated!", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,
                                "Error! " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void updateTransactionDate(int day, int month, int year){
        switch(month){
            case 0:
                transactionMonth = "January";
                break;
            case 1:
                transactionMonth = "February";
                break;
            case 2:
                transactionMonth = "March";
                break;
            case 3:
                transactionMonth = "April";
                break;
            case 4:
                transactionMonth = "May";
                break;
            case 5:
                transactionMonth = "June";
                break;
            case 6:
                transactionMonth = "July";
                break;
            case 7:
                transactionMonth = "August";
                break;
            case 8:
                transactionMonth = "September";
                break;
            case 9:
                transactionMonth = "October";
                break;
            case 10:
                transactionMonth = "November";
                break;
            case 11:
                transactionMonth = "December";
                break;
        }
        transactionYear = String.valueOf(year);
        txtTransactionDate.setText(day + " " + transactionMonth + " " + year);
    }

    public void changeDrawableColor(Button button, String color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            button.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor(color)));
        }
    }

    public void updateCategorySelected(String category){
        Button[] categories = {btnAutomotive,btnBeautyCare,btnBooks,btnDebtCredit,btnDecoration,btnElectronic,
                btnFashion,btnFoodBeverages,btnGift,btnHealth,btnHomeLiving,btnSports,btnTransportation,btnOthers};
        for(Button button : categories){
            button.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button));
            button.setTextColor(Color.parseColor("#000000"));
            changeDrawableColor(button,"#000000");
        }
        switch (category){
            case "Automotive":
                if(typeSelected.equals("Income")){
                    btnAutomotive.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_green));
                }else{
                    btnAutomotive.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_red));
                }
                btnAutomotive.setTextColor(Color.parseColor("#ffffff"));
                changeDrawableColor(btnAutomotive,"#ffffff");
                categorySelected = "Automotive";
                break;
            case "Beauty & Care":
                if(typeSelected.equals("Income")){
                    btnBeautyCare.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_green));
                }else{
                    btnBeautyCare.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_red));
                }
                btnBeautyCare.setTextColor(Color.parseColor("#ffffff"));
                changeDrawableColor(btnBeautyCare,"#ffffff");
                categorySelected = "Beauty & Care";
                break;
            case "Books":
                if(typeSelected.equals("Income")){
                    btnBooks.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_green));
                }else{
                    btnBooks.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_red));
                }
                btnBooks.setTextColor(Color.parseColor("#ffffff"));
                changeDrawableColor(btnBooks,"#ffffff");
                categorySelected = "Books";
                break;
            case "Debt/Credit":
                if(typeSelected.equals("Income")){
                    btnDebtCredit.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_green));
                }else{
                    btnDebtCredit.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_red));
                }
                btnDebtCredit.setTextColor(Color.parseColor("#ffffff"));
                changeDrawableColor(btnDebtCredit,"#ffffff");
                categorySelected = "Debt/Credit";
                break;
            case "Decoration":
                if(typeSelected.equals("Income")){
                    btnDecoration.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_green));
                }else{
                    btnDecoration.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_red));
                }
                btnDecoration.setTextColor(Color.parseColor("#ffffff"));
                changeDrawableColor(btnDecoration,"#ffffff");
                categorySelected = "Decoration";
                break;
            case "Electronic":
                if(typeSelected.equals("Income")){
                    btnElectronic.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_green));
                }else{
                    btnElectronic.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_red));
                }
                btnElectronic.setTextColor(Color.parseColor("#ffffff"));
                changeDrawableColor(btnElectronic,"#ffffff");
                categorySelected = "Electronic";
                break;
            case "Fashion":
                if(typeSelected.equals("Income")){
                    btnFashion.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_green));
                }else{
                    btnFashion.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_red));
                }
                btnFashion.setTextColor(Color.parseColor("#ffffff"));
                changeDrawableColor(btnFashion,"#ffffff");
                categorySelected = "Fashion";
                break;
            case "Food & Beverages":
                if(typeSelected.equals("Income")){
                    btnFoodBeverages.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_green));
                }else{
                    btnFoodBeverages.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_red));
                }
                btnFoodBeverages.setTextColor(Color.parseColor("#ffffff"));
                changeDrawableColor(btnFoodBeverages,"#ffffff");
                categorySelected = "Food & Beverages";
                break;
            case "Gift":
                if(typeSelected.equals("Income")){
                    btnGift.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_green));
                }else{
                    btnGift.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_red));
                }
                btnGift.setTextColor(Color.parseColor("#ffffff"));
                changeDrawableColor(btnGift,"#ffffff");
                categorySelected = "Gift";
                break;
            case "Health":
                if(typeSelected.equals("Income")){
                    btnHealth.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_green));
                }else{
                    btnHealth.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_red));
                }
                btnHealth.setTextColor(Color.parseColor("#ffffff"));
                changeDrawableColor(btnHealth,"#ffffff");
                categorySelected = "Health";
                break;
            case "Home & Living":
                if(typeSelected.equals("Income")){
                    btnHomeLiving.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_green));
                }else{
                    btnHomeLiving.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_red));
                }
                btnHomeLiving.setTextColor(Color.parseColor("#ffffff"));
                changeDrawableColor(btnHomeLiving,"#ffffff");
                categorySelected = "Home & Living";
                break;
            case "Sports":
                if(typeSelected.equals("Income")){
                    btnSports.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_green));
                }else{
                    btnSports.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_red));
                }
                btnSports.setTextColor(Color.parseColor("#ffffff"));
                changeDrawableColor(btnSports,"#ffffff");
                categorySelected = "Sports";
                break;
            case "Transportation":
                if(typeSelected.equals("Income")){
                    btnTransportation.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_green));
                }else{
                    btnTransportation.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_red));
                }
                btnTransportation.setTextColor(Color.parseColor("#ffffff"));
                changeDrawableColor(btnTransportation,"#ffffff");
                categorySelected = "Transportation";
                break;
            case "Others":
                if(typeSelected.equals("Income")){
                    btnOthers.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_green));
                }else{
                    btnOthers.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_red));
                }
                btnOthers.setTextColor(Color.parseColor("#ffffff"));
                changeDrawableColor(btnOthers,"#ffffff");
                categorySelected = "Others";
                break;
        }
    }
}