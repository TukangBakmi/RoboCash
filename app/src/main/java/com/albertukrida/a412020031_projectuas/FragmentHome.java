package com.albertukrida.a412020031_projectuas;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHome extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public FragmentHome() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentHome.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentHome newInstance(String param1, String param2) {
        FragmentHome fragment = new FragmentHome();
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

    GraphView graph;
    LineGraphSeries<DataPoint> incomeGraph;
    LineGraphSeries<DataPoint> expensesGraph;
    LineGraphSeries<DataPoint> leftoverGraph;

    ImageView circleProfileImage;
    TextView profileName, currentBalance, income, expenses, leftover, latestTransaction;
    TextView allTime, jan, feb, mar, apr, may, jun, jul, aug, sep, oct, nov, dec, thisYear;

    StorageReference storageReference;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    ProgressDialog pdLoading;

    String monthSelected = "allTime";

    // Recycler View
    public static RecyclerView recyclerView;
    ArrayList<ClassTransaction> transactionArrayList;
    ClassHomeAdapter myAdapter;
    InterfaceTransaction interfaceTransaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, null);

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

        // Graph
        graph = root.findViewById(R.id.graph);
        createGraph(monthSelected);

        interfaceTransaction = () -> EventChangeListener(monthSelected);

        // Recycler View
        recyclerView = root.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        transactionArrayList = new ArrayList<>();
        myAdapter = new ClassHomeAdapter(getContext(),transactionArrayList, interfaceTransaction);

        circleProfileImage = root.findViewById(R.id.circleProfileImage);
        profileName = root.findViewById(R.id.txtProfileName);
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
        latestTransaction = root.findViewById(R.id.latestTransaction);

        circleProfileImage.setOnClickListener(view -> ((MainActivity)getActivity()).goToProfile());
        profileName.setOnClickListener(view -> ((MainActivity)getActivity()).goToProfile());
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
        latestTransaction.setOnClickListener(view -> ((MainActivity)getActivity()).ShowAllTransactions());

        getProfilePict();
        getProfileName();
        updateMonthSelected(monthSelected);

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
                    createGraph(monthSelected);
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

    public void createGraph(String month){
        graph.removeAllSeries();
        incomeGraph = new LineGraphSeries<>();
        expensesGraph = new LineGraphSeries<>();
        leftoverGraph = new LineGraphSeries<>();

        ArrayList<ClassTransaction> temp = new ArrayList<ClassTransaction>();
        String userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        CollectionReference questionRef = fStore.collection("transactions");
        if(month.equals("allTime")){
            questionRef.whereEqualTo("userID", userID)
                    .orderBy("date", Query.Direction.ASCENDING)
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
                        // enable scaling
                        graph.getViewport().setScalable(true);
                        long intIncome = 0;
                        long intExpenses = 0;
                        String userID1 = fAuth.getCurrentUser().getUid();
                        for (int counter = 0; counter < temp.size(); counter++) {
                            ClassTransaction transaction = temp.get(counter);
                            Date date = transaction.getDate();
                            if(transaction.getUserID().equals(userID1) && transaction.getType().equals("Income")){
                                intIncome += Long.parseLong(transaction.getAmount());
                            }else if(transaction.getUserID().equals(userID1) && transaction.getType().equals("Expenses")){
                                intExpenses += Long.parseLong(transaction.getAmount());
                            }
                            incomeGraph.appendData(new DataPoint(date,intIncome),true,1000);
                            expensesGraph.appendData(new DataPoint(date,intExpenses),true,1000);
                            leftoverGraph.appendData(new DataPoint(date,(intIncome-intExpenses)),true,1000);
                        }
                    });
        }else if(month.equals("thisYear")){
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            questionRef.whereEqualTo("userID", userID)
                    .whereEqualTo("year", String.valueOf(year))
                    .orderBy("date", Query.Direction.ASCENDING)
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
                        // enable scaling
                        graph.getViewport().setScalable(true);
                        double intIncome = 0;
                        double intExpenses = 0;
                        String userID1 = fAuth.getCurrentUser().getUid();
                        for (int counter = 0; counter < temp.size(); counter++) {
                            ClassTransaction transaction = temp.get(counter);
                            Date date = transaction.getDate();
                            if(transaction.getUserID().equals(userID1) && transaction.getType().equals("Income")){
                                intIncome += Double.parseDouble(transaction.getAmount());
                            }else if(transaction.getUserID().equals(userID1) && transaction.getType().equals("Expenses")){
                                intExpenses += Double.parseDouble(transaction.getAmount());
                            }
                            incomeGraph.appendData(new DataPoint(date,intIncome),true,1000);
                            expensesGraph.appendData(new DataPoint(date,intExpenses),true,1000);
                            leftoverGraph.appendData(new DataPoint(date,(intIncome-intExpenses)),true,1000);
                        }
                    });
        }else{
            questionRef.whereEqualTo("userID", userID)
                    .whereEqualTo("month", month)
                    .orderBy("date", Query.Direction.ASCENDING)
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
                        // enable scaling
                        graph.getViewport().setScalable(false);
                        double intIncome = 0;
                        double intExpenses = 0;
                        String userID1 = fAuth.getCurrentUser().getUid();
                        for (int counter = 0; counter < temp.size(); counter++) {
                            ClassTransaction transaction = temp.get(counter);
                            Date date = transaction.getDate();
                            if(transaction.getUserID().equals(userID1) && transaction.getType().equals("Income")){
                                intIncome += Double.parseDouble(transaction.getAmount());
                            }else if(transaction.getUserID().equals(userID1) && transaction.getType().equals("Expenses")){
                                intExpenses += Double.parseDouble(transaction.getAmount());
                            }
                            incomeGraph.appendData(new DataPoint(date,intIncome),true,1000);
                            expensesGraph.appendData(new DataPoint(date,intExpenses),true,1000);
                            leftoverGraph.appendData(new DataPoint(date,(intIncome-intExpenses)),true,1000);
                        }
                    });
        }

        // income series
        incomeGraph.setTitle("Income");
        incomeGraph.setColor(Color.argb(255, 150, 255, 0));
        incomeGraph.setBackgroundColor(Color.argb(100, 150, 255, 0));
        incomeGraph.setDrawDataPoints(true);
        graph.addSeries(incomeGraph);

        // expenses series
        expensesGraph.setTitle("Expenses");
        expensesGraph.setColor(Color.argb(255, 255, 66, 41));
        expensesGraph.setBackgroundColor(Color.argb(100, 255, 66, 41));
        expensesGraph.setDrawDataPoints(true);
        graph.addSeries(expensesGraph);

        // leftover series
        leftoverGraph.setTitle("Leftover");
        leftoverGraph.setColor(Color.argb(255, 60, 60, 255));
        leftoverGraph.setBackgroundColor(Color.argb(60, 60, 60, 255));
        leftoverGraph.setDrawBackground(true);
        leftoverGraph.setDrawDataPoints(true);
        graph.addSeries(leftoverGraph);

        graph.setTitle(month);
        graph.setTitleColor(Color.WHITE);
        graph.setTitleTextSize(50);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        graph.getGridLabelRenderer().reloadStyles();

        // set manual X and Y bounds
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(-1300000000);
        graph.getViewport().setMaxX(1300000000);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-10000000);
        graph.getViewport().setMaxY(30000000);

        // enable scrolling
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScrollableY(true);

        // set label axis color
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);

        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(graph.getContext()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3);
        graph.getGridLabelRenderer().setHumanRounding(false);

        // legend
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);
        graph.getLegendRenderer().setBackgroundColor(Color.argb(50, 0, 0, 0));
        graph.getLegendRenderer().setTextColor(Color.WHITE);
        graph.getLegendRenderer().setTextSize(32);
    }

    public void getProfilePict(){
        assert fAuth.getCurrentUser() != null;
        DocumentReference documentReference = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    if (Objects.equals(documentSnapshot.getString("Profile Picture"), "profile_icon1.png")) {
                        circleProfileImage.setImageResource(R.mipmap.profile_icon1);
                    } else if (Objects.equals(documentSnapshot.getString("Profile Picture"), "profile_icon2.png")) {
                        circleProfileImage.setImageResource(R.mipmap.profile_icon2);
                    } else if (Objects.equals(documentSnapshot.getString("Profile Picture"), "profile_icon3.png")) {
                        circleProfileImage.setImageResource(R.mipmap.profile_icon3);
                    } else if (Objects.equals(documentSnapshot.getString("Profile Picture"), "profile_icon4.png")) {
                        circleProfileImage.setImageResource(R.mipmap.profile_icon4);
                    } else if (Objects.equals(documentSnapshot.getString("Profile Picture"), "profile_icon5.png")) {
                        circleProfileImage.setImageResource(R.mipmap.profile_icon5);
                    } else if (Objects.equals(documentSnapshot.getString("Profile Picture"), "profile_icon6.png")) {
                        circleProfileImage.setImageResource(R.mipmap.profile_icon6);
                    } else if (Objects.equals(documentSnapshot.getString("Profile Picture"), "profile_icon7.png")) {
                        circleProfileImage.setImageResource(R.mipmap.profile_icon7);
                    } else if (Objects.equals(documentSnapshot.getString("Profile Picture"), "profile_icon8.png")) {
                        circleProfileImage.setImageResource(R.mipmap.profile_icon8);
                    } else if (Objects.equals(documentSnapshot.getString("Profile Picture"), "profile_icon9.png")) {
                        circleProfileImage.setImageResource(R.mipmap.profile_icon9);
                    } else {
                        StorageReference profilePictureRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
                        profilePictureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri).into(circleProfileImage);
                            }
                        });
                    }
                }
            }
        });
    }

    public void getProfileName(){
        FirebaseUser user = fAuth.getCurrentUser();
        assert user != null;
        DocumentReference documentReference = fStore.collection("users").document(user.getUid());
        documentReference.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot documentSnapshot = task.getResult();
                if(documentSnapshot != null &&documentSnapshot.exists()){
                    String name = documentSnapshot.getString("Name");
                    String[] arrName = {};
                    if(name != null){
                        arrName = name.split(" ");
                    }
                    profileName.setText("Hello, " + arrName[0]);
                }
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

    public void updateMonthSelected(String month){
        TextView[] months = {allTime,jan,feb,mar,apr,may,jun,jul,aug,sep,oct,nov,dec,thisYear};
        for (TextView textView : months) {
            textView.setTypeface(null, Typeface.NORMAL);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
            textView.setTextColor(Color.parseColor("#99FFFFFF"));
        }
        switch (month){
            case "allTime":
                monthSelected = "allTime";
                allTime.setTypeface(null, Typeface.BOLD);
                allTime.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                allTime.setTextColor(Color.parseColor("#FFFFFFFF"));
                EventChangeListener(monthSelected);
                break;
            case "jan":
                monthSelected = "January";
                jan.setTypeface(null, Typeface.BOLD);
                jan.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                jan.setTextColor(Color.parseColor("#FFFFFFFF"));
                EventChangeListener(monthSelected);
                break;
            case "feb":
                monthSelected = "February";
                feb.setTypeface(null, Typeface.BOLD);
                feb.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                feb.setTextColor(Color.parseColor("#FFFFFFFF"));
                EventChangeListener(monthSelected);
                break;
            case "mar":
                monthSelected = "March";
                mar.setTypeface(null, Typeface.BOLD);
                mar.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                mar.setTextColor(Color.parseColor("#FFFFFFFF"));
                EventChangeListener(monthSelected);
                break;
            case "apr":
                monthSelected = "April";
                apr.setTypeface(null, Typeface.BOLD);
                apr.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                apr.setTextColor(Color.parseColor("#FFFFFFFF"));
                EventChangeListener(monthSelected);
                break;
            case "may":
                monthSelected = "May";
                may.setTypeface(null, Typeface.BOLD);
                may.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                may.setTextColor(Color.parseColor("#FFFFFFFF"));
                EventChangeListener(monthSelected);
                break;
            case "jun":
                monthSelected = "June";
                jun.setTypeface(null, Typeface.BOLD);
                jun.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                jun.setTextColor(Color.parseColor("#FFFFFFFF"));
                EventChangeListener(monthSelected);
                break;
            case "jul":
                monthSelected = "July";
                jul.setTypeface(null, Typeface.BOLD);
                jul.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                jul.setTextColor(Color.parseColor("#FFFFFFFF"));
                EventChangeListener(monthSelected);
                break;
            case "aug":
                monthSelected = "August";
                aug.setTypeface(null, Typeface.BOLD);
                aug.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                aug.setTextColor(Color.parseColor("#FFFFFFFF"));
                EventChangeListener(monthSelected);
                break;
            case "sep":
                monthSelected = "September";
                sep.setTypeface(null, Typeface.BOLD);
                sep.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                sep.setTextColor(Color.parseColor("#FFFFFFFF"));
                EventChangeListener(monthSelected);
                break;
            case "oct":
                monthSelected = "October";
                oct.setTypeface(null, Typeface.BOLD);
                oct.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                oct.setTextColor(Color.parseColor("#FFFFFFFF"));
                EventChangeListener(monthSelected);
                break;
            case "nov":
                monthSelected = "November";
                nov.setTypeface(null, Typeface.BOLD);
                nov.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                nov.setTextColor(Color.parseColor("#FFFFFFFF"));
                EventChangeListener(monthSelected);
                break;
            case "dec":
                monthSelected = "December";
                dec.setTypeface(null, Typeface.BOLD);
                dec.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                dec.setTextColor(Color.parseColor("#FFFFFFFF"));
                EventChangeListener(monthSelected);
                break;
            case "thisYear":
                monthSelected = "thisYear";
                thisYear.setTypeface(null, Typeface.BOLD);
                thisYear.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                thisYear.setTextColor(Color.parseColor("#FFFFFFFF"));
                EventChangeListener(monthSelected);
                break;
        }
    }
}