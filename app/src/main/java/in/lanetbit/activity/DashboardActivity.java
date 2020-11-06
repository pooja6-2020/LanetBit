package in.lanetbit.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import in.lanetbit.R;
import in.lanetbit.adapter.OrderBookBidListAdapter;
import in.lanetbit.adapter.OrderBookListAdapter;
import in.lanetbit.apis.ApiClient;
import in.lanetbit.apis.AuthInterface;
import in.lanetbit.apis.ExchangeInterfaces;
import in.lanetbit.model.AskOrderBookHistory;
import in.lanetbit.model.BidOrderBookHistory;
import in.lanetbit.model.CountryData;
import in.lanetbit.model.CountryList;
import in.lanetbit.model.ExchangeInstrumentsData;
import in.lanetbit.model.ExchangeMarketHistoryDataList;
import in.lanetbit.model.OrderBookHistory;
import in.lanetbit.utils.AppConstants;
import in.lanetbit.utils.DateFormatter;
import in.lanetbit.utils.PrefStorage;
import in.lanetbit.utils.TLHelper;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    Context mContext;
    PrefStorage prefStorage;
    TLHelper hlp;
    ExchangeInterfaces apiServiceExchange;
    AuthInterface apiServiceAuth;

    String TAG = DashboardActivity.class.getName();

    private ImageView imageViewMenu;
    private String stringTypeSupportedExchange,stringTypeExchangeMaxPrice, stringStartDateExchange, stringEndDateExchange, stringSupportedExchangePairs, stringAuthorization, stringBaseAssets, stringQuoteAssets, keyPairs;
    private Spinner spinnerSupportedSpinner;
    private ArrayList<String> arrayListSupportedExchangePairs;
    private CardView cardViewOneHour, cardViewOneDay, cardViewOneMonths,cardViewBuy,cardViewSell;
    private EditText editTextStartDate, editTextEndDate;
    private TextView textViewSubmit;
    private Calendar calender;
    private CandleStickChart candleStickChart;
    private ArrayList<CandleEntry> values;

    private CombinedChart chart;
    XAxis xAxis;
    private ArrayList<BarEntry> entries1;
    private ArrayList<BarEntry> entries2;


    ArrayList<ExchangeMarketHistoryDataList> exchangeMarketHistoryDataList;
    ArrayList<AskOrderBookHistory> askOrderBookHistoryArrayList = new ArrayList<>();
    ArrayList<BidOrderBookHistory> bidOrderBookHistoryArrayList = new ArrayList<>();
    OrderBookListAdapter orderBookListAdapter;
    OrderBookBidListAdapter orderBookBidListAdapter;
    ExchangeMarketHistoryDataList marketHistoryDataList;

    int marketHistoryCount = 50;
    private RecyclerView recyclerViewOrderBook,recyclerViewOrderBookBid;
    private Disposable disposableOrderBook;
    Date d;
    SimpleDateFormat sdf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mContext = DashboardActivity.this;
        prefStorage = new PrefStorage(mContext);
        hlp = new TLHelper(mContext);
        apiServiceExchange = ApiClient.getClient().create(ExchangeInterfaces.class);
        apiServiceAuth = ApiClient.getClient().create(AuthInterface.class);
        arrayListSupportedExchangePairs = new ArrayList<>();
        exchangeMarketHistoryDataList = new ArrayList<>();
        bidOrderBookHistoryArrayList = new ArrayList<>();
        askOrderBookHistoryArrayList = new ArrayList<>();
        calender = Calendar.getInstance();
        stringTypeSupportedExchange = AppConstants.ONE_HOUR;


        initElement();
        Log.e("Token ?", prefStorage.getJwtToken());



        getSupportedInstruments();

        getProfileDetails();
        disposableOrderBook = Observable.interval(1000, 5000,
                TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::callOrderBookHistory, this::onError);


    }

    private void initElement() {
        imageViewMenu = findViewById(R.id.iv_menu);
        imageViewMenu.setOnClickListener(this);

        spinnerSupportedSpinner = findViewById(R.id.spn_supported_instrument);
        spinnerSupportedSpinner.setOnItemSelectedListener(this);

        cardViewBuy = findViewById(R.id.cd_buy);
        cardViewSell = findViewById(R.id.cd_sell);

        cardViewOneDay = findViewById(R.id.cd_one_day);
        cardViewOneHour = findViewById(R.id.cd_one_hour);
        cardViewOneMonths = findViewById(R.id.cd_one_month);
        recyclerViewOrderBook = findViewById(R.id.rv_order_book_ask);
        recyclerViewOrderBookBid = findViewById(R.id.rv_order_book_bid);
        recyclerViewOrderBook.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerViewOrderBook.setHasFixedSize(true);
        recyclerViewOrderBookBid.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerViewOrderBookBid.setHasFixedSize(true);
        recyclerViewOrderBookBid.setNestedScrollingEnabled(false);
        recyclerViewOrderBook.setNestedScrollingEnabled(false);

        editTextEndDate = findViewById(R.id.ed_end_date);
        editTextStartDate = findViewById(R.id.ed_start_date);

        textViewSubmit = findViewById(R.id.tv_submit);

        cardViewBuy.setOnClickListener(this);
        cardViewSell.setOnClickListener(this);
        /**-----------------Combined Chart---------------------**/
        chart = findViewById(R.id.chart1);
        chart.getDescription().setEnabled(false);

        chart.setBackgroundColor(Color.TRANSPARENT);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setHighlightFullBarEnabled(false);
        chart.setDrawGridBackground(false);

        xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(getResources().getColor(R.color.colorTextGrey));

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setLabelCount(9, false);
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setTextColor(getResources().getColor(R.color.colorTextGrey));
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        chart.getLegend().setEnabled(false);

        chart.zoom(6.9f, 1f, 1.9f, 1f);


        /**-----------Candle Stick Chart---------**/
/*

        candleStickChart = findViewById(R.id.candle_stick_chart);
        candleStickChart.setBackgroundColor(Color.TRANSPARENT);

        candleStickChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
      //  candleStickChart.setMaxVisibleValueCount(100);

        // scaling can now only be done on x- and y-axis separately
            candleStickChart.setPinchZoom(false);
        candleStickChart.setDrawGridBackground(false);

        XAxis xAxis = candleStickChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(getResources().getColor(R.color.colorTextGrey));

        YAxis leftAxis = candleStickChart.getAxisLeft();
//        leftAxis.setEnabled(false);
        leftAxis.setLabelCount(9, false);
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setTextColor(getResources().getColor(R.color.colorTextGrey));
        YAxis rightAxis = candleStickChart.getAxisRight();
        rightAxis.setEnabled(false);


//        rightAxis.setStartAtZero(false);

*/

        values = new ArrayList<>();
        textViewSubmit.setOnClickListener(this);
        cardViewOneDay.setOnClickListener(this);
        cardViewOneHour.setOnClickListener(this);
        cardViewOneMonths.setOnClickListener(this);

        editTextStartDate.setInputType(InputType.TYPE_NULL);
        editTextEndDate.setInputType(InputType.TYPE_NULL);

        editTextStartDate.setOnClickListener(this);
        editTextEndDate.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        disposableOrderBook.dispose();
    }
    @Override
    protected void onResume() {
        super.onResume();

        if (disposableOrderBook.isDisposed()) {
            disposableOrderBook = Observable.interval(1000, 5000,
                    TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::callOrderBookHistory, this::onError);
        }
    }

    private void getSupportedInstruments() {
        hlp.showLoader("Please wait ...");
        retrofit2.Call<ExchangeInstrumentsData> call = apiServiceExchange.getSupportedInstruments();
        Log.wtf("URL Called", call.request().url() + "");
        call.enqueue(new Callback<ExchangeInstrumentsData>() {
            @Override
            public void onResponse(Call<ExchangeInstrumentsData> call, Response<ExchangeInstrumentsData> response) {
                hlp.hideLoader();
                if (response.body().getCode().equalsIgnoreCase("200") || response.body().getCode() == "200") {
                    // generateCountyList(response.body().getCountryListArrayList());
                    Log.e("List ?", response.body().getData().toString());

                    try {
                        String jsonString = String.valueOf(response.body().getData());
                        JSONObject jObject = new JSONObject(jsonString).getJSONObject("pairs");
                        Iterator<String> keys = jObject.keys();
                        while (keys.hasNext()) {
                            keyPairs = keys.next();
                            Log.v("**********", "**********");
                            Log.v("pairs key", keyPairs);
                            JSONObject innerJObject = jObject.getJSONObject(keyPairs);

                            stringBaseAssets = innerJObject.getString("baseAsset");
                            stringQuoteAssets = innerJObject.getString("quoteAsset");
                            stringTypeExchangeMaxPrice = innerJObject.getString("maxPrice");
                            arrayListSupportedExchangePairs.add(keyPairs);
                            generateSupprtedExchangePairs(arrayListSupportedExchangePairs);


                            Log.e("stringBaseAssets = " + stringBaseAssets, "stringQuoteAssets = " + stringQuoteAssets);

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                    Toast.makeText(mContext, response.body().getMessage(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(Call<ExchangeInstrumentsData> call, Throwable t) {
                hlp.hideLoader();
                hlp.noConnection();
            }
        });
    }

    private void generateSupprtedExchangePairs(ArrayList<String> arrayListSupportedExchangePairs) {
        String[] stringSupportedExchangePairsList = new String[arrayListSupportedExchangePairs.size() + 1];
        stringSupportedExchangePairsList[0] = "Select Exchange Instruments";
        //int i = 1;
        for (int i = 1; i <= arrayListSupportedExchangePairs.size(); i++) {
            stringSupportedExchangePairsList[i] = arrayListSupportedExchangePairs.get(i - 1).toString();

        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext,
                R.layout.spinner_item, stringSupportedExchangePairsList);

        spinnerSupportedSpinner.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();
        if (arrayListSupportedExchangePairs.contains(AppConstants.BTC_USD)) {
            int spinnerPosition = dataAdapter.getPosition(AppConstants.BTC_USD);
            spinnerSupportedSpinner.setSelection(spinnerPosition);
        }


    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        if (position != 0) {
            stringSupportedExchangePairs = arrayListSupportedExchangePairs.get(position - 1);
            hlp.showToast(stringSupportedExchangePairs);

            Log.e(TAG, "Exchange pairs : " + stringSupportedExchangePairs);
            Date d = new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            stringStartDateExchange =  sdf.format(d);
            stringEndDateExchange =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(new Date());

            getRequestMarketHistoryData(stringSupportedExchangePairs, stringStartDateExchange, stringEndDateExchange, stringTypeSupportedExchange, AppConstants.COUNT_MARKET_HISTORY_DATA);

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_submit:
                actionMarketHistoryData();
                break;
            case R.id.iv_menu:
                Intent intent = new Intent(mContext, MenuBarActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.cd_one_day:
                cardViewOneDay.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
                cardViewOneHour.setCardBackgroundColor(Color.TRANSPARENT);
                cardViewOneMonths.setCardBackgroundColor(Color.TRANSPARENT);
                stringTypeSupportedExchange = AppConstants.ONE_DAY;
                 d = new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24));
                 sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

                stringStartDateExchange =  sdf.format(d);
                stringEndDateExchange =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(new Date());

                getRequestMarketHistoryData(stringSupportedExchangePairs, stringStartDateExchange, stringEndDateExchange, stringTypeSupportedExchange, AppConstants.COUNT_MARKET_HISTORY_DATA);

                break;
            case R.id.cd_one_hour:
                cardViewOneHour.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
                cardViewOneDay.setCardBackgroundColor(Color.TRANSPARENT);
                cardViewOneMonths.setCardBackgroundColor(Color.TRANSPARENT);
                stringTypeSupportedExchange = AppConstants.ONE_HOUR;
                 d = new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24));
                 sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

                stringStartDateExchange =  sdf.format(d);
                stringEndDateExchange =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(new Date());

                getRequestMarketHistoryData(stringSupportedExchangePairs, stringStartDateExchange, stringEndDateExchange, stringTypeSupportedExchange, AppConstants.COUNT_MARKET_HISTORY_DATA);

                break;
            case R.id.cd_one_month:
                cardViewOneMonths.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
                cardViewOneHour.setCardBackgroundColor(Color.TRANSPARENT);
                cardViewOneDay.setCardBackgroundColor(Color.TRANSPARENT);
                stringTypeSupportedExchange = AppConstants.ONE_MONTHS;
                d = new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24));
                sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

                stringStartDateExchange =  sdf.format(d);
                stringEndDateExchange =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(new Date());

                getRequestMarketHistoryData(stringSupportedExchangePairs, stringStartDateExchange, stringEndDateExchange, stringTypeSupportedExchange, AppConstants.COUNT_MARKET_HISTORY_DATA);

                break;
            case R.id.ed_start_date:
                callStartDatePicker();

                break;
            case R.id.ed_end_date:
                callEndDatePicker();
                break;
            case R.id.cd_buy:
                    Intent intent1= new Intent(mContext,BuySellActivity.class);
                    intent1.putExtra("CATEGORY",AppConstants.BUY);
                    intent1.putExtra("PRICE",bidOrderBookHistoryArrayList.get(0).getPrice());
                    intent1.putExtra("INSTRUMENT",stringSupportedExchangePairs);
                    startActivity(intent1);
                break;

            case R.id.cd_sell:
                Intent intent2= new Intent(mContext,BuySellActivity.class);
                intent2.putExtra("CATEGORY",AppConstants.SELL);
                intent2.putExtra("PRICE",askOrderBookHistoryArrayList.get(0).getPrice());
                intent2.putExtra("INSTRUMENT",stringSupportedExchangePairs);

                startActivity(intent2);

                break;

        }
    }

    private void callEndDatePicker() {
        calender.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        DatePickerDialog datePicker = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calender.set(year, month, dayOfMonth);
                editTextEndDate.setText(DateFormatter.getDesireFormatFromCalender("yyyy-MM-dd'T'HH:mm:ss", calender));
                if (editTextEndDate.getText().toString().isEmpty()){
                    hlp.showToast("Select end date ?");

                }else if (stringStartDateExchange!=null){
                    actionMarketHistoryData();
                }else {
                    hlp.showToast("Select start date ?");
                }

            }
        }, calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DATE));
//        datePicker.getDatePicker().setMinDate(calender.getTimeInMillis());
        datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePicker.show();
    }

    private void callStartDatePicker() {
        calender.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        DatePickerDialog datePicker = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calender.set(year, month, dayOfMonth);
                editTextStartDate.setText(DateFormatter.getDesireFormatFromCalender("yyyy-MM-dd'T'HH:mm:ss", calender));
                Log.e("Date?", DateFormatter.getDesireFormatFromCalender("yyyy-MM-dd'T'HH:mm:ss", calender));
            }
        }, calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DATE));
//        datePicker.getDatePicker().setMinDate(calender.getTimeInMillis());
        datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePicker.show();

    }

    private void actionMarketHistoryData() {

        if (editTextStartDate.getText().toString().isEmpty()) {
            hlp.showToast("Select Start Date!");

        } else if (editTextEndDate.getText().toString().isEmpty()) {
            hlp.showToast("Select End Date!");
        } else {
            stringStartDateExchange = editTextStartDate.getText().toString();
            stringEndDateExchange= editTextEndDate.getText().toString();
            getRequestMarketHistoryData(stringSupportedExchangePairs, stringStartDateExchange, stringEndDateExchange, stringTypeSupportedExchange, AppConstants.COUNT_MARKET_HISTORY_DATA);

        }
//        stringStartDateExchange = "2020-02-29T03:22:59";
//        stringEndDateExchange = "2020-05-05T09:23:59";


    }

    private void getRequestMarketHistoryData(String stringSupportedExchangePairs, String stringStartDateExchange, String stringEndDateExchange,
                                             String stringTypeSupportedExchange, String countMarketHistoryData) {
        try {

            Call<JsonElement> call =
                    apiServiceExchange.getMarketHistoryData(stringSupportedExchangePairs, stringStartDateExchange, stringEndDateExchange, stringTypeSupportedExchange, countMarketHistoryData);
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    hlp.hideLoader();
                    JsonElement jsonElement = response.body();
                    assert jsonElement != null;
                    String body = jsonElement.toString();
                    Log.e(TAG, "Api Response : " + body);

                    try {
                        JSONObject res = new JSONObject(body);

                        if (res.getString("code").equalsIgnoreCase("200")) {
                            hlp.showToast(res.getString("type"));
                            JSONObject data = res.getJSONObject("data");
                            JSONArray jsonArrayData = data.getJSONArray("data");
                            values = new ArrayList<>();
                            exchangeMarketHistoryDataList = new ArrayList<>();
                            entries1 = new ArrayList<>();
                            entries2 = new ArrayList<>();

                            for (int i = 0; i < jsonArrayData.length(); i++) {
                                JSONObject jsonObjectStrapType = jsonArrayData.getJSONObject(i);

                                marketHistoryDataList = new ExchangeMarketHistoryDataList();
                                marketHistoryDataList.setClose(jsonObjectStrapType.getString("close"));
                                marketHistoryDataList.setOpen(jsonObjectStrapType.getString("open"));
                                marketHistoryDataList.setHigh(jsonObjectStrapType.getString("high"));
                                marketHistoryDataList.setLow(jsonObjectStrapType.getString("low"));
                                marketHistoryDataList.setQuoteVolume(jsonObjectStrapType.getString("quoteVolume"));
                                marketHistoryDataList.setVolume(jsonObjectStrapType.getString("volume"));

                                exchangeMarketHistoryDataList.add(marketHistoryDataList);

                                values.add(new CandleEntry(
                                        i, Float.parseFloat(marketHistoryDataList.getHigh()),
                                        Float.parseFloat(marketHistoryDataList.getLow()),
                                        Float.parseFloat(marketHistoryDataList.getOpen()), Float.parseFloat(marketHistoryDataList.getClose()),
                                        getResources().getDrawable(R.drawable.ic_btc)
                                ));
                                //entries1.add(new BarEntry(0,Float.parseFloat(marketHistoryDataList.getVolume())));
                                entries2.add(new BarEntry(0, Float.parseFloat(marketHistoryDataList.getQuoteVolume())));

                                CandleData d = new CandleData();

                                CandleDataSet set1 = new CandleDataSet(values, "Data Set");
                                chart.animateX(500);

                                set1.setDrawIcons(false);
                                set1.setAxisDependency(YAxis.AxisDependency.RIGHT);
                                set1.setShadowColorSameAsCandle(true);
                                set1.setBarSpace(0.2f);
                                set1.setShadowWidth(0.5f);
                                set1.setDecreasingColor(Color.RED);
                                set1.setDecreasingPaintStyle(Paint.Style.FILL);
                                set1.setIncreasingColor(Color.rgb(122, 242, 84));
                                set1.setIncreasingPaintStyle(Paint.Style.STROKE);
                                set1.setNeutralColor(Color.BLUE);
                                set1.setValueTextColor(getResources().getColor(R.color.colorWhite));
                                d.addDataSet(set1);


                                BarDataSet barDataSet = new BarDataSet(entries1, "Bar 1");
                                chart.animateX(500);
                                barDataSet.setColor(getResources().getColor(R.color.colorFadeAccent));
                                barDataSet.setValueTextColor(getResources().getColor(R.color.colorWhite));
                                barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

                                BarDataSet barDataSet1 = new BarDataSet(entries2, "");
                                barDataSet1.setColors(getResources().getColor(R.color.colorFadeAccent));
                                barDataSet1.setValueTextColor(getResources().getColor(R.color.colorWhite));
                                barDataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);


                                float groupSpace = 0.06f;
                                float barSpace = 0.02f; // x2 dataset
                                float barWidth = 0.45f; // x2 dataset
                                // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"

                                BarData barData = new BarData(barDataSet, barDataSet1);
                                barData.setBarWidth(barWidth);

                                // make this BarData object grouped
                                barData.groupBars(0, groupSpace, barSpace); // start at x = 0

                                CombinedData dataCombined = new CombinedData();

                                dataCombined.setData(barData);
                                dataCombined.setData(d);

                                xAxis.setAxisMaximum(dataCombined.getXMax() + 0.25f);

                                chart.setData(dataCombined);
                                chart.invalidate();


                            }


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    hlp.hideLoader();
                    hlp.noConnection();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void onError(Throwable throwable) {
        Toast.makeText(this, "OnError in Observable Timer",
                Toast.LENGTH_LONG).show();
    }

    @SuppressLint("CheckResult")
    private void callOrderBookHistory(Long aLong) {
        Observable<OrderBookHistory> observable = apiServiceExchange.getOrderBookHistory(stringSupportedExchangePairs);
        observable.subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread())
                .map(result -> result)
                .subscribe(this::handleResults, this::handleError);


    }

    private void handleError(Throwable throwable) {
    }

    private void handleResults(OrderBookHistory orderBookHistory) {
        try {

            JsonObject jsonObject = orderBookHistory.getData();
            Log.e("Order Book?", String.valueOf(jsonObject));

            JsonArray jsonArrayDataAsks = jsonObject.getAsJsonArray("asks");
            JsonArray jsonArrayDataBids = jsonObject.getAsJsonArray("bids");

            bidOrderBookHistoryArrayList = new ArrayList<>();
            askOrderBookHistoryArrayList = new ArrayList<>();
            for (int i = 0; i < jsonArrayDataAsks.size(); i++) {
                JsonObject jsonObjectAskOrderHistory = (JsonObject) jsonArrayDataAsks.get(i);
                AskOrderBookHistory askOrderBookHistory = new AskOrderBookHistory();
                askOrderBookHistory.setAmount(String.valueOf(jsonObjectAskOrderHistory.get("amount")));
                askOrderBookHistory.setPrice(String.valueOf(jsonObjectAskOrderHistory.get("price")));
                askOrderBookHistoryArrayList.add(askOrderBookHistory);
            }
            for (int i1 = 0; i1 < jsonArrayDataBids.size(); i1++) {
                JsonObject jsonObjectBidOrderHistory = (JsonObject) jsonArrayDataBids.get(i1);
                BidOrderBookHistory bidOrderBookHistory = new BidOrderBookHistory();
                bidOrderBookHistory.setAmount(String.valueOf(jsonObjectBidOrderHistory.get("amount")));
                bidOrderBookHistory.setPrice(String.valueOf(jsonObjectBidOrderHistory.get("price")));
                bidOrderBookHistoryArrayList.add(bidOrderBookHistory);
            }

            renderOrderBookHistory();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void renderOrderBookHistory() {

        if (askOrderBookHistoryArrayList.size() > 0 ) {
            orderBookListAdapter = new OrderBookListAdapter(mContext,recyclerViewOrderBook,askOrderBookHistoryArrayList);
            recyclerViewOrderBook.setAdapter(orderBookListAdapter);
            orderBookListAdapter.notifyDataSetChanged();
            recyclerViewOrderBook.setVisibility(View.VISIBLE);

        }else {
            recyclerViewOrderBookBid.setVisibility(View.GONE);
        }


        if (bidOrderBookHistoryArrayList.size()>0){
            orderBookBidListAdapter = new OrderBookBidListAdapter(mContext,recyclerViewOrderBook,bidOrderBookHistoryArrayList);
            recyclerViewOrderBookBid.setAdapter(orderBookBidListAdapter);
            orderBookBidListAdapter.notifyDataSetChanged();
            recyclerViewOrderBookBid.setVisibility(View.VISIBLE);
        }
    }

    private void getProfileDetails() {
        Log.e(TAG, "Calling API");
        try {
            stringAuthorization = "Bearer" + " " + prefStorage.getJwtToken();

            hlp.showLoader("Please wait ...");
            Call<JsonElement> call = apiServiceAuth.getProfileDetails(stringAuthorization);
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    hlp.hideLoader();

                    JsonElement jsonElement = response.body();
                    assert jsonElement != null;
                    String body = jsonElement.toString();
                    Log.e(TAG, "Api Response : " + body);

                    try {
                        JSONObject res = new JSONObject(body);

                        if (res.getString("code").equalsIgnoreCase("200")) {
                            hlp.showToast(res.getString("message"));
                            JSONObject data = res.getJSONObject("data");
                            JSONObject jsonObjectContact = data.getJSONObject("contactDetails");
                            prefStorage.setValueString(AppConstants.UserPrefKey.USER_ID, data.getString("id"));
                            prefStorage.setValueString(AppConstants.UserPrefKey.USER_FULL_NAME, data.getString("fullName"));
                            prefStorage.setValueString(AppConstants.UserPrefKey.USER_PROFILE_IMAGE, data.getString("pic"));
                            prefStorage.setValueString(AppConstants.UserPrefKey.USER_LAST_LOGIN, data.getString("lastLogin"));
                            prefStorage.setValueString(AppConstants.UserPrefKey.USER_EMAIL, data.getString("email"));
                            prefStorage.setValueString(AppConstants.UserPrefKey.USER_MOBILE, jsonObjectContact.getString("phone"));

                        } else if (res.getString("code").equalsIgnoreCase("401")) {
                            hlp.showToast(res.getString("message"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    hlp.hideLoader();
                    hlp.noConnection();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
