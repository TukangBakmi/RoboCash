package com.albertukrida.a412020031_projectuas;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentTransactions#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentTransactions extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public FragmentTransactions() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentTransactions.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentTransactions newInstance(String param1, String param2) {
        FragmentTransactions fragment = new FragmentTransactions();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // TODO: Rename and change types of parameters
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    TextView currentBalance, income, expenses, leftover;
    TextView allTime, jan, feb, mar, apr, may, jun, jul, aug, sep, oct, nov, dec, thisYear;
    Button btnAddTransaction;
    Button btnAutomotive, btnBeautyCare, btnBooks, btnDebtCredit, btnDecoration, btnElectronic, btnFashion;
    Button btnFoodBeverages, btnGift, btnHealth, btnHomeLiving, btnSports, btnTransportation, btnOthers;
    TextView txtTransactionAmount, txtTransactionDesc, txtTransactionDate;

    StorageReference storageReference;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    ProgressDialog pdLoading;

    String monthSelected = "allTime";
    String typeSelected;
    String categorySelected;
    String transactionAmt;
    String transactionDesc;
    String transactionDate;
    String transactionMonth;
    String transactionYear;

    // Recycler view
    public static RecyclerView recyclerView;
    ArrayList<ClassTransaction> transactionArrayList;
    ClassTransactionAdapter myAdapter;
    InterfaceTransaction interfaceTransaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_transactions, null);

        pdLoading = new ProgressDialog(getActivity());
        pdLoading.setMessage("\tLoading...");
        pdLoading.setCancelable(false);
        pdLoading.show();

        storageReference = FirebaseStorage.getInstance().getReference();
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        currentBalance = root.findViewById(R.id.txtCurrentBalance);
        income = root.findViewById(R.id.txtIncome);
        expenses = root.findViewById(R.id.txtExpenses);
        leftover = root.findViewById(R.id.txtLeftOver);

        if(currentBalance.getText().toString().equals("")){
            currentBalance.setText("IDR 0");
        }

        interfaceTransaction = () -> EventChangeListener(monthSelected);

        // Recycler View
        recyclerView = root.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        transactionArrayList = new ArrayList<>();
        myAdapter = new ClassTransactionAdapter(getContext(),transactionArrayList, interfaceTransaction);
        EventChangeListener(monthSelected);

        allTime = root.findViewById(R.id.allTime);
        jan = root.findViewById(R.id.jan);
        feb = root.findViewById(R.id.feb);
        mar = root.findViewById(R.id.mar);
        apr = root.findViewById(R.id.apr);
        may = root.findViewById(R.id.may);
        jun = root.findViewById(R.id.jun);
        jul = root.findViewById(R.id.jul);
        aug = root.findViewById(R.id.aug);
        sep = root.findViewById(R.id.sep);
        oct = root.findViewById(R.id.oct);
        nov = root.findViewById(R.id.nov);
        dec = root.findViewById(R.id.dec);
        thisYear = root.findViewById(R.id.thisYear);
        btnAddTransaction = root.findViewById(R.id.btnAddTransaction);

        allTime.setOnClickListener(view -> updateMonthSelected("allTime"));
        jan.setOnClickListener(view -> updateMonthSelected("jan"));
        feb.setOnClickListener(view -> updateMonthSelected("feb"));
        mar.setOnClickListener(view -> updateMonthSelected("mar"));
        apr.setOnClickListener(view -> updateMonthSelected("apr"));
        may.setOnClickListener(view -> updateMonthSelected("may"));
        jun.setOnClickListener(view -> updateMonthSelected("jun"));
        jul.setOnClickListener(view -> updateMonthSelected("jul"));
        aug.setOnClickListener(view -> updateMonthSelected("aug"));
        sep.setOnClickListener(view -> updateMonthSelected("sep"));
        oct.setOnClickListener(view -> updateMonthSelected("oct"));
        nov.setOnClickListener(view -> updateMonthSelected("nov"));
        dec.setOnClickListener(view -> updateMonthSelected("dec"));
        thisYear.setOnClickListener(view -> updateMonthSelected("thisYear"));
        btnAddTransaction.setOnClickListener(this::AddTransaction);

        // Inflate the layout for this fragment
        return root;
    }

    public void EventChangeListener(String month){
        transactionArrayList.clear();
        String userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        CollectionReference questionRef = fStore.collection("transactions");
        Query query;
        if(month.equals("allTime")){
            query = questionRef.whereEqualTo("userID", userID)
                    .orderBy("date", Query.Direction.DESCENDING);
        }else if(month.equals("thisYear")){
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            query = questionRef.whereEqualTo("userID", userID)
                    .whereEqualTo("year", String.valueOf(year))
                    .orderBy("date", Query.Direction.DESCENDING);
        }else{
            query = questionRef.whereEqualTo("userID", userID)
                    .whereEqualTo("month", month)
                    .orderBy("date", Query.Direction.DESCENDING);
        }
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if( error == null){
                    assert value != null;
                    for (DocumentChange dc : value.getDocumentChanges()){
                        if(dc.getType() == DocumentChange.Type.ADDED){
                            transactionArrayList.add(dc.getDocument().toObject(ClassTransaction.class));
                        }
                    }
                    income.setText(convert(String.valueOf(getIncome())));
                    expenses.setText(convert(String.valueOf(getExpenses())));
                    leftover.setText(getLeftOver(getIncome(),getExpenses()));

                    myAdapter.update(transactionArrayList);
                    recyclerView.setAdapter(myAdapter);
                    myAdapter.notifyDataSetChanged();
                    getCurrentBalance();
                }else{
                    income.setText(convert(String.valueOf(getIncome())));
                    expenses.setText(convert(String.valueOf(getExpenses())));
                    leftover.setText(getLeftOver(getIncome(),getExpenses()));

                    myAdapter.update(transactionArrayList);
                    recyclerView.setAdapter(myAdapter);
                    myAdapter.notifyDataSetChanged();
                }
                pdLoading.dismiss();
            }
        });
    }

    public long getIncome(){
        long intIncome = 0;
        String userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        for (int counter = 0; counter < transactionArrayList.size(); counter++) {
            ClassTransaction transaction = transactionArrayList.get(counter);
            if(transaction.getUserID().equals(userID) && transaction.getType().equals("Income")){
                intIncome += Long.parseLong(transaction.getAmount());
            }
        }
        return intIncome;
    }

    public long getExpenses(){
        long intExpenses = 0;
        String userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        for (int counter = 0; counter < transactionArrayList.size(); counter++) {
            ClassTransaction transaction = transactionArrayList.get(counter);
            if(transaction.getUserID().equals(userID) && transaction.getType().equals("Expenses")){
                intExpenses += Long.parseLong(transaction.getAmount());
            }
        }
        return intExpenses;
    }

    public String getLeftOver(long income, long expenses){
        String left;
        if(income < expenses){
            left = convert(String.valueOf(expenses-income));
            left = "-"+left;
        }else{
            left = convert(String.valueOf(income-expenses));
            left = "+"+left;
        }
        return left;
    }

    public void getCurrentBalance(){
        ArrayList<ClassTransaction> temp = new ArrayList<ClassTransaction>();;
        String userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        CollectionReference questionRef = fStore.collection("transactions");
        questionRef.whereEqualTo("userID", userID).orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        return;
                    }
                    assert value != null;
                    for (DocumentChange dc : value.getDocumentChanges()){
                        if(dc.getType() == DocumentChange.Type.ADDED){
                            temp.add(dc.getDocument().toObject(ClassTransaction.class));
                        }
                    }
                    long intIncome = 0;
                    long intExpenses = 0;
                    String userID1 = fAuth.getCurrentUser().getUid();
                    for (int counter = 0; counter < temp.size(); counter++) {
                        ClassTransaction transaction = temp.get(counter);
                        if(transaction.getUserID().equals(userID1) && transaction.getType().equals("Income")){
                            intIncome += Long.parseLong(transaction.getAmount());
                        }else if(transaction.getUserID().equals(userID1) && transaction.getType().equals("Expenses")){
                            intExpenses += Long.parseLong(transaction.getAmount());
                        }
                    }
                    String left;
                    if(intIncome < intExpenses){
                        currentBalance.setTextColor(Color.parseColor("#ff4229"));
                        left = convert(String.valueOf(intExpenses-intIncome));
                        left = "-"+left;
                    }else{
                        currentBalance.setTextColor(Color.parseColor("#39B54A"));
                        left = convert(String.valueOf(intIncome-intExpenses));
                        left = "+"+left;
                    }
                    currentBalance.setText(left);
                });
    }

    public String convert(String amt){
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
        amt = "IDR " + editedAmt;
        // Return
        return amt;
    }

    public void AddTransaction(View view){
        assert getContext() != null;
        final AlertDialog addTransactionDialog = new AlertDialog.Builder(view.getContext())
                .setView(R.layout.dialog_add_transaction)
                .setTitle("Add Transaction?")
                .create();
        addTransactionDialog.show();

        assert fAuth.getCurrentUser() != null;

        TextView btnNext = addTransactionDialog.findViewById(R.id.btnNext);
        TextView btnCancel = addTransactionDialog.findViewById(R.id.btnCancel);

        Button btnIncome = addTransactionDialog.findViewById(R.id.btnIncome);
        Button btnExpenses = addTransactionDialog.findViewById(R.id.btnExpenses);

        typeSelected = "Income";
        categorySelected = "Automotive";

        txtTransactionAmount = addTransactionDialog.findViewById(R.id.txtTransactionAmt);
        txtTransactionDesc = addTransactionDialog.findViewById(R.id.txtTransactionDesc);
        txtTransactionDate = addTransactionDialog.findViewById(R.id.txtTransactionDate);

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        updateTransactionDate(day, month, year);

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

        btnIncome.setOnClickListener(view13 -> {
            typeSelected = "Income";
            btnIncome.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button_green));
            btnIncome.setTextColor(Color.parseColor("#ffffff"));
            btnExpenses.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button));
            btnExpenses.setTextColor(Color.parseColor("#000000"));
            updateCategorySelected(categorySelected);
        });
        btnExpenses.setOnClickListener(view13 -> {
            typeSelected = "Expenses";
            btnIncome.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button));
            btnIncome.setTextColor(Color.parseColor("#000000"));
            btnExpenses.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button_red));
            btnExpenses.setTextColor(Color.parseColor("#ffffff"));
            updateCategorySelected(categorySelected);
        });

        txtTransactionDate.setOnClickListener(view15 -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view15, int year1, int monthOfYear, int dayOfMonth) {
                            updateTransactionDate(dayOfMonth,monthOfYear, year1);
                        }
                    },
                    year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
            datePickerDialog.show();
        });

        btnNext.setOnClickListener(view12 -> {
            if(txtTransactionAmount.getText().toString().length() == 0){
                txtTransactionAmount.setError("Transaction amount is required");
                txtTransactionAmount.requestFocus();
            }else if(txtTransactionAmount.getText().toString().length() >= 14){
                txtTransactionAmount.setError("Transaction amount is too large");
                txtTransactionAmount.requestFocus();
            }else if(txtTransactionDesc.getText().toString().length() >= 24){
                txtTransactionDesc.setError("Transaction description is too long");
                txtTransactionDesc.requestFocus();
            }else{
                pdLoading.setMessage("\tLoading...");
                pdLoading.setCancelable(false);
                pdLoading.show();
                transactionAmt = txtTransactionAmount.getText().toString();
                transactionDesc = txtTransactionDesc.getText().toString();
                transactionDate = txtTransactionDate.getText().toString();
                addTransactionToFirestore(typeSelected, categorySelected,transactionAmt,
                        transactionDesc,transactionDate,transactionMonth,transactionYear);
                addTransactionDialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(view1 -> addTransactionDialog.dismiss());
    }

    public void addTransactionToFirestore(String type, String category, String amt,
                                          String desc, String date, String month, String year){
        // Generate UID
        DocumentReference key;
        key = fStore.collection("transactions").document();
        String TransactionID = key.getId();
        // Get User ID
        String UserID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        // Date
        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
        Date dateFormat = null;
        try {
            dateFormat = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ClassTransaction transaction = new ClassTransaction(TransactionID, UserID, type, category, amt, desc, dateFormat, month, year);
        fStore.collection("transactions")
                .document(TransactionID)
                .set(transaction)
                .addOnSuccessListener(unused -> {
                    EventChangeListener(monthSelected);
                    pdLoading.dismiss();
                    Toast.makeText(getContext(),
                            "Your transaction has been successfully added!", Toast.LENGTH_LONG).show();
                }).addOnFailureListener(e -> {
                    pdLoading.dismiss();
                    Toast.makeText(getContext(),
                            "Error! " + e.getMessage(), Toast.LENGTH_LONG).show();
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
        txtTransactionDate.setText(day + " " + transactionMonth + " " + transactionYear);
    }

    @SuppressLint("UseCompatTextViewDrawableApis")
    public void changeDrawableColor(Button button, String color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            button.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor(color)));
        }
    }

    public void updateCategorySelected(String category){
        assert getContext() != null;
        Button[] categories = {btnAutomotive,btnBeautyCare,btnBooks,btnDebtCredit,btnDecoration,btnElectronic,
            btnFashion,btnFoodBeverages,btnGift,btnHealth,btnHomeLiving,btnSports,btnTransportation,btnOthers};
        for(Button button : categories){
            button.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button));
            button.setTextColor(Color.parseColor("#000000"));
            changeDrawableColor(button,"#000000");
        }
        switch (category){
            case "Automotive":
                if(typeSelected.equals("Income")){
                    btnAutomotive.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button_green));
                }else{
                    btnAutomotive.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button_red));
                }
                btnAutomotive.setTextColor(Color.parseColor("#ffffff"));
                changeDrawableColor(btnAutomotive,"#ffffff");
                categorySelected = "Automotive";
                break;
            case "Beauty & Care":
                if(typeSelected.equals("Income")){
                    btnBeautyCare.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button_green));
                }else{
                    btnBeautyCare.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button_red));
                }
                btnBeautyCare.setTextColor(Color.parseColor("#ffffff"));
                changeDrawableColor(btnBeautyCare,"#ffffff");
                categorySelected = "Beauty & Care";
                break;
            case "Books":
                if(typeSelected.equals("Income")){
                    btnBooks.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button_green));
                }else{
                    btnBooks.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button_red));
                }
                btnBooks.setTextColor(Color.parseColor("#ffffff"));
                changeDrawableColor(btnBooks,"#ffffff");
                categorySelected = "Books";
                break;
            case "Debt/Credit":
                if(typeSelected.equals("Income")){
                    btnDebtCredit.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button_green));
                }else{
                    btnDebtCredit.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button_red));
                }
                btnDebtCredit.setTextColor(Color.parseColor("#ffffff"));
                changeDrawableColor(btnDebtCredit,"#ffffff");
                categorySelected = "Debt/Credit";
                break;
            case "Decoration":
                if(typeSelected.equals("Income")){
                    btnDecoration.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button_green));
                }else{
                    btnDecoration.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button_red));
                }
                btnDecoration.setTextColor(Color.parseColor("#ffffff"));
                changeDrawableColor(btnDecoration,"#ffffff");
                categorySelected = "Decoration";
                break;
            case "Electronic":
                if(typeSelected.equals("Income")){
                    btnElectronic.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button_green));
                }else{
                    btnElectronic.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button_red));
                }
                btnElectronic.setTextColor(Color.parseColor("#ffffff"));
                changeDrawableColor(btnElectronic,"#ffffff");
                categorySelected = "Electronic";
                break;
            case "Fashion":
                if(typeSelected.equals("Income")){
                    btnFashion.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button_green));
                }else{
                    btnFashion.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button_red));
                }
                btnFashion.setTextColor(Color.parseColor("#ffffff"));
                changeDrawableColor(btnFashion,"#ffffff");
                categorySelected = "Fashion";
                break;
            case "Food & Beverages":
                if(typeSelected.equals("Income")){
                    btnFoodBeverages.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button_green));
                }else{
                    btnFoodBeverages.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button_red));
                }
                btnFoodBeverages.setTextColor(Color.parseColor("#ffffff"));
                changeDrawableColor(btnFoodBeverages,"#ffffff");
                categorySelected = "Food & Beverages";
                break;
            case "Gift":
                if(typeSelected.equals("Income")){
                    btnGift.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button_green));
                }else{
                    btnGift.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button_red));
                }
                btnGift.setTextColor(Color.parseColor("#ffffff"));
                changeDrawableColor(btnGift,"#ffffff");
                categorySelected = "Gift";
                break;
            case "Health":
                if(typeSelected.equals("Income")){
                    btnHealth.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button_green));
                }else{
                    btnHealth.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button_red));
                }
                btnHealth.setTextColor(Color.parseColor("#ffffff"));
                changeDrawableColor(btnHealth,"#ffffff");
                categorySelected = "Health";
                break;
            case "Home & Living":
                if(typeSelected.equals("Income")){
                    btnHomeLiving.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button_green));
                }else{
                    btnHomeLiving.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button_red));
                }
                btnHomeLiving.setTextColor(Color.parseColor("#ffffff"));
                changeDrawableColor(btnHomeLiving,"#ffffff");
                categorySelected = "Home & Living";
                break;
            case "Sports":
                if(typeSelected.equals("Income")){
                    btnSports.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button_green));
                }else{
                    btnSports.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button_red));
                }
                btnSports.setTextColor(Color.parseColor("#ffffff"));
                changeDrawableColor(btnSports,"#ffffff");
                categorySelected = "Sports";
                break;
            case "Transportation":
                if(typeSelected.equals("Income")){
                    btnTransportation.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button_green));
                }else{
                    btnTransportation.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button_red));
                }
                btnTransportation.setTextColor(Color.parseColor("#ffffff"));
                changeDrawableColor(btnTransportation,"#ffffff");
                categorySelected = "Transportation";
                break;
            case "Others":
                if(typeSelected.equals("Income")){
                    btnOthers.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button_green));
                }else{
                    btnOthers.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_button_red));
                }
                btnOthers.setTextColor(Color.parseColor("#ffffff"));
                changeDrawableColor(btnOthers,"#ffffff");
                categorySelected = "Others";
                break;
        }
    }

    public void updateMonthSelected(String month){
        TextView[] months = {allTime,jan,feb,mar,apr,may,jun,jul,aug,sep,oct,nov,dec,thisYear};
        for (TextView textView : months) {
            textView.setTypeface(null, Typeface.NORMAL);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
            textView.setTextColor(Color.parseColor("#606060"));
        }
        switch (month){
            case "allTime":
                monthSelected = "allTime";
                allTime.setTypeface(null, Typeface.BOLD);
                allTime.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                allTime.setTextColor(Color.parseColor("#000000"));
                EventChangeListener(monthSelected);
                break;
            case "jan":
                monthSelected = "January";
                jan.setTypeface(null, Typeface.BOLD);
                jan.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                jan.setTextColor(Color.parseColor("#000000"));
                EventChangeListener(monthSelected);
                break;
            case "feb":
                monthSelected = "February";
                feb.setTypeface(null, Typeface.BOLD);
                feb.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                feb.setTextColor(Color.parseColor("#000000"));
                EventChangeListener(monthSelected);
                break;
            case "mar":
                monthSelected = "March";
                mar.setTypeface(null, Typeface.BOLD);
                mar.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                mar.setTextColor(Color.parseColor("#000000"));
                EventChangeListener(monthSelected);
                break;
            case "apr":
                monthSelected = "April";
                apr.setTypeface(null, Typeface.BOLD);
                apr.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                apr.setTextColor(Color.parseColor("#000000"));
                EventChangeListener(monthSelected);
                break;
            case "may":
                monthSelected = "May";
                may.setTypeface(null, Typeface.BOLD);
                may.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                may.setTextColor(Color.parseColor("#000000"));
                EventChangeListener(monthSelected);
                break;
            case "jun":
                monthSelected = "June";
                jun.setTypeface(null, Typeface.BOLD);
                jun.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                jun.setTextColor(Color.parseColor("#000000"));
                EventChangeListener(monthSelected);
                break;
            case "jul":
                monthSelected = "July";
                jul.setTypeface(null, Typeface.BOLD);
                jul.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                jul.setTextColor(Color.parseColor("#000000"));
                EventChangeListener(monthSelected);
                break;
            case "aug":
                monthSelected = "August";
                aug.setTypeface(null, Typeface.BOLD);
                aug.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                aug.setTextColor(Color.parseColor("#000000"));
                EventChangeListener(monthSelected);
                break;
            case "sep":
                monthSelected = "September";
                sep.setTypeface(null, Typeface.BOLD);
                sep.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                sep.setTextColor(Color.parseColor("#000000"));
                EventChangeListener(monthSelected);
                break;
            case "oct":
                monthSelected = "October";
                oct.setTypeface(null, Typeface.BOLD);
                oct.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                oct.setTextColor(Color.parseColor("#000000"));
                EventChangeListener(monthSelected);
                break;
            case "nov":
                monthSelected = "November";
                nov.setTypeface(null, Typeface.BOLD);
                nov.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                nov.setTextColor(Color.parseColor("#000000"));
                EventChangeListener(monthSelected);
                break;
            case "dec":
                monthSelected = "December";
                dec.setTypeface(null, Typeface.BOLD);
                dec.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                dec.setTextColor(Color.parseColor("#000000"));
                EventChangeListener(monthSelected);
                break;
            case "thisYear":
                monthSelected = "thisYear";
                thisYear.setTypeface(null, Typeface.BOLD);
                thisYear.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                thisYear.setTextColor(Color.parseColor("#000000"));
                EventChangeListener(monthSelected);
                break;
        }
    }
}