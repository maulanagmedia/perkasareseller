package gmedia.net.id.perkasareseller.HomePenjualanLain;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.bluetoothprinter.Model.Item;
import com.leonardus.irfan.bluetoothprinter.Model.Transaksi;
import com.leonardus.irfan.bluetoothprinter.PspPrinter;
import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.FormatItem;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.OptionItem;
import com.maulana.custommodul.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import gmedia.net.id.perkasareseller.HomeActivity;
import gmedia.net.id.perkasareseller.R;
import gmedia.net.id.perkasareseller.Utils.ServerURL;

public class DetailOrderLain extends AppCompatActivity {

    private Context context;
    private SessionManager session;
    private ItemValidation iv = new ItemValidation();
    private DialogBox dialogBox;
    private EditText edtJenis, edtNomor, edtMsisdn, edtNama, edtTotal, edtSN, edtGolongan, edtStandMeter;
    private Button btnProses;
    private String idKategori = "", nama = "", flag = "";
    private Spinner spnNamaProduk;
    private List<OptionItem> listProduk = new ArrayList<>();
    private ArrayAdapter adapterProduk;
    private PspPrinter printer;
    private LinearLayout llFooter, llHarga;
    private Button btnHarga;
    private boolean isInquery = false;
    private int state = 1;
    private String idProduk = "";
    private String currentCounter = "";
    private String harga = "", namaPIC = "", msisdn = "", sn = "", namaProduk = "", jml = "", denda = "", admin = "", periode = "", standMeter = "", golongan = "";
    private String isPPOB = "";
    private Timer timer = new Timer();
    private final long DELAY = 1500;
    private ProgressBar pbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order_lain);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Detail Penjualan");
        context = this;
        dialogBox = new DialogBox(context);
        printer = new PspPrinter(context);
        printer.startService();
        initUI();
        initEvent();
        if(!flag.equals("1")) initData();
    }

    private void initUI() {

        edtJenis = (EditText) findViewById(R.id.edt_jenis);
        edtNomor = (EditText) findViewById(R.id.edt_nomor);
        spnNamaProduk = (Spinner) findViewById(R.id.spn_nama_produk);
        btnProses = (Button) findViewById(R.id.btn_proses);
        llFooter = (LinearLayout) findViewById(R.id.ll_footer);
        llHarga = (LinearLayout) findViewById(R.id.ll_harga);
        btnHarga = (Button) findViewById(R.id.btn_harga);
        edtMsisdn = (EditText) findViewById(R.id.edt_msisdn);
        edtNama = (EditText) findViewById(R.id.edt_nama);
        edtSN = (EditText) findViewById(R.id.edt_sn);
        edtTotal = (EditText) findViewById(R.id.edt_total);
        edtGolongan = (EditText) findViewById(R.id.edt_golongan);
        edtStandMeter = (EditText) findViewById(R.id.edt_stand_meter);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);

        isInquery = false;
        state = 1;
        session = new SessionManager(context);

        setProdukAdapter();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            idKategori = bundle.getString("kategori", "");
            nama = bundle.getString("nama", "");
            flag = bundle.getString("flag", ""); // 1 : all operator
            if(flag.equals("1")) setInquiry(false);
            edtJenis.setText(nama);

        }
    }

    private void initEvent() {

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edtNomor.getText().toString().isEmpty()){

                    edtNomor.setError("Harap diisi");
                    edtNomor.requestFocus();
                    return;
                }else{

                    edtNomor.setError(null);
                }

                if(state == 1 && isInquery){

                    Toast.makeText(context, "Harap tekan Hitung Harga terlebih dahulu", Toast.LENGTH_LONG).show();
                    return;
                }

                currentCounter = "";

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin memproses transaksi?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                doTransaksi(false);
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();


                /*List<Item> items = new ArrayList<>();
                items.add(new Item("Item 1", 1, 20000));
                items.add(new Item("Item 2", 1, 21000));
                items.add(new Item("Item 3", 1, 19000));
                Calendar date = Calendar.getInstance();

                final Transaksi transaksi = new Transaksi("Salam cell", "Irvan", "PB/KS/1811/0030", date.getTime(), items);

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Transaksi Berhasil")
                        .setPositiveButton("Cetak Transaksi", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if(printer.isPrinterReady()){

                                    printer.print(transaksi);
                                }else {

                                    Toast.makeText(context, "Harap pilih device printer telebih dahulu", Toast.LENGTH_LONG).show();
                                    printer.showDevices();
                                }
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();*/
            }
        });

        btnHarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edtNomor.getText().toString().isEmpty()){

                    edtNomor.setError("Harap diisi");
                    edtNomor.requestFocus();
                    return;
                }else{

                    edtNomor.setError(null);
                }

                state = 2;
                currentCounter = "";
                doTransaksi(true);
            }
        });

        if(flag.equals("1")){

            edtNomor.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                }
                @Override
                public void onTextChanged(final CharSequence s, int start, int before,
                                          int count) {
                    if(timer != null)
                        timer.cancel();
                }
                @Override
                public void afterTextChanged(final Editable s) {
                    //avoid triggering event when text is too short
                    if (s.length() >= 3) {

                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        getProvider(s.toString());
                                    }
                                });
                            }

                        }, DELAY);
                    }
                }
            });
        }
    }

    private void doTransaksi(final boolean checkHarga) {

        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Login_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Memproses...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JSONObject jBody = new JSONObject();
        String proses = isInquery && checkHarga ? "INQ" : "PAY";

        try {
            jBody.put("id_produk", idProduk);
            jBody.put("nomor", edtNomor.getText().toString());
            jBody.put("proses", proses);
            jBody.put("konter", currentCounter);
            jBody.put("pay", isInquery ? harga : "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.payPPBOB, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                if(progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                iv.hideSoftKey(context);
                String message = "Terjadi kesalahan saat memuat data";
                harga = "0";
                jml = "";
                denda = "";
                admin = "";
                sn = "";
                namaPIC = "";
                msisdn = "";
                periode = "";
                standMeter = "";

                edtTotal.setText(iv.ChangeToCurrencyFormat(harga));
                edtNama.setText(namaPIC);
                edtSN.setText(sn);
                edtMsisdn.setText(msisdn);
                edtGolongan.setText(golongan);
                edtStandMeter.setText(standMeter);

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");
                    //currentCounter = response.getJSONObject("response").getString("counter");
                    currentCounter = "0";

                    if(iv.parseNullInteger(status) == 200){

                        final String finalMessage = message;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(context, finalMessage, Toast.LENGTH_LONG).show();
                                HomeActivity.stateFragment = 2;
                                //onBackPressed();
                                Intent intent = new Intent(context, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        });
                        /*harga = response.getJSONObject("response").getJSONObject("data").getString("harga");

                        jml =  response.getJSONObject("response").getJSONObject("data").getString("jml");
                        //admin = response.getJSONObject("response").getJSONObject("data").getString("admin");
                        admin = "0";
                        //denda = response.getJSONObject("response").getJSONObject("data").getString("denda");
                        denda = "0";
                        periode = response.getJSONObject("response").getJSONObject("data").getString("periode");
                        standMeter = response.getJSONObject("response").getJSONObject("data").getString("stand_meter");

                        sn = response.getJSONObject("response").getJSONObject("data").getString("sn");
                        msisdn = response.getJSONObject("response").getJSONObject("data").getString("msisdn");
                        namaPIC = response.getJSONObject("response").getJSONObject("data").getString("nama");
                        golongan = response.getJSONObject("response").getJSONObject("data").getString("daya") + "/" + response.getJSONObject("response").getJSONObject("transaksi").getString("kwh");

                        edtTotal.setText(iv.ChangeToCurrencyFormat(harga));
                        edtNama.setText(namaPIC);
                        edtSN.setText(sn);
                        edtMsisdn.setText(msisdn);
                        edtGolongan.setText(golongan);
                        edtStandMeter.setText(standMeter);

                        if(!checkHarga){

                            final String finalMessage = message;
                            new CountDownTimer(2000, 1000) {

                                public void onTick(long millisUntilFinished) {

                                }

                                public void onFinish() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            Toast.makeText(context, finalMessage, Toast.LENGTH_LONG).show();
                                            HomeActivity.stateFragment = 2;
                                            //onBackPressed();
                                            Intent intent = new Intent(context, HomeActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                }

                            }.start();

                            // semtara tidak ditampilkan
                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                            View viewDialog = inflater.inflate(R.layout.dialog_cetak, null);
                            builder.setView(viewDialog);
                            builder.setCancelable(false);

                            final Button btnTutup = (Button) viewDialog.findViewById(R.id.btn_tutup);
                            final Button btnShare = (Button) viewDialog.findViewById(R.id.btn_share);
                            final Button btnCetak = (Button) viewDialog.findViewById(R.id.btn_cetak);
                            final TextView tvTitle = (TextView) viewDialog.findViewById(R.id.tv_title);
                            final EditText edtBiaya = (EditText) viewDialog.findViewById(R.id.edt_biaya);
                            if(isPPOB.equals("0")){

                                tvTitle.setText("Total Harga");
                                edtBiaya.setHint("Total Harga");
                            }

                            final AlertDialog alert = builder.create();
                            alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                            List<Item> items = new ArrayList<>();

                            items.add(new Item(namaProduk, 1, iv.parseNullDouble(isPPOB.equals("0") ? edtBiaya.getText().toString() : jml)));

                            Calendar date = Calendar.getInstance();
                            final Transaksi transaksi = new Transaksi(namaPIC, session.getNama(), sn, date.getTime(), items, iv.getCurrentDate(FormatItem.formatDateTimeDisplay));

                            btnTutup.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view2) {

                                    if(alert != null){

                                        try {

                                            alert.dismiss();
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }

                                    Toast.makeText(context, finalMessage, Toast.LENGTH_LONG).show();
                                    HomeActivity.stateFragment = 2;
                                    //onBackPressed();
                                    Intent intent = new Intent(context, HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            });

                            btnShare.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if(edtBiaya.getText().toString().isEmpty()){

                                        String message = "Biaya Admin harap diisi";
                                        if(isPPOB.equals("0")) message = "Total Harga harap diisi";
                                        edtBiaya.setError(message);
                                        edtBiaya.requestFocus();
                                        return;
                                    }else{

                                        edtBiaya.setError(null);
                                    }

                                    saveCustomHarga(edtBiaya.getText().toString());

                                    String shareBody = "";
                                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                                    sharingIntent.setType("text/plain");
                                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Penjualan " +getResources().getString(R.string.app_name));
                                    if(isPPOB.equals("0")){

                                        shareBody += "nama   : " + namaPIC +"\n";
                                        shareBody += "Item   : " + namaProduk +"\n";
                                        shareBody += "Token  : " + sn +"\n";
                                        shareBody += "MSISDN : " + msisdn +"\n";
                                        shareBody += "Harga  : " + iv.ChangeToCurrencyFormat(edtBiaya.getText().toString()) +"\n";

                                    }else{

                                        shareBody += "Nama   : " + namaPIC +"\n";
                                        shareBody += "Item   : " + namaProduk +"\n";
                                        shareBody += "Token  : " + sn +"\n";
                                        shareBody += "MSISDN : " + msisdn +"\n";
                                        shareBody += "denda  : " + iv.ChangeToCurrencyFormat(denda) +"\n";
                                        shareBody += "admin  : " + iv.ChangeToCurrencyFormat(admin) +"\n";
                                        shareBody += "Harga  : " + iv.ChangeToCurrencyFormat(jml) +"\n";
                                    }

                                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                                    startActivity(Intent.createChooser(sharingIntent, "Bagikan"));

                                }
                            });

                            btnCetak.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if(edtBiaya.getText().toString().isEmpty()){

                                        String message = "Biaya Admin harap diisi";
                                        if(isPPOB.equals("0")) message = "Total Harga harap diisi";
                                        edtBiaya.setError(message);
                                        edtBiaya.requestFocus();
                                        return;
                                    }else{

                                        edtBiaya.setError(null);
                                    }

                                    if(!printer.bluetoothAdapter.isEnabled()){

                                        Toast.makeText(context, "Mohon hidupkan bluetooth anda, kemudian klik cetak kembali", Toast.LENGTH_LONG).show();
                                        try{
                                            printer.dialogBluetooth.show();
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }else{

                                        if(printer.isPrinterReady()){

                                            saveCustomHarga(edtBiaya.getText().toString());

                                            if(isPPOB.equals("0")){

                                                List<Item> items1 = new ArrayList<>();
                                                items1.add(new Item(namaProduk, 1, iv.parseNullDouble(edtBiaya.getText().toString())));
                                                Calendar date = Calendar.getInstance();
                                                final Transaksi transaksi1 = new Transaksi(namaPIC, session.getNama(), sn, date.getTime(), items1, iv.getCurrentDate(FormatItem.formatDateTimeDisplay));
                                                transaksi1.setMsisdn(msisdn);

                                                printer.print(transaksi1,"Nama");
                                            }else{

                                                double biayaAdmin = iv.parseNullDouble(edtBiaya.getText().toString());
                                                double dpp = biayaAdmin / 1.1;
                                                double ppn = biayaAdmin - dpp;
                                                double nonPPn = iv.parseNullDouble(harga) - biayaAdmin;
                                                if(nonPPn < 0) nonPPn = iv.parseNullDouble(harga);

                                                transaksi.setBiayaAdmin(iv.ChangeToCurrencyFormat(iv.doubleToString(biayaAdmin)));
                                                transaksi.setDpp(iv.ChangeToCurrencyFormat(iv.doubleToString(dpp)));
                                                transaksi.setPpn(iv.ChangeToCurrencyFormat(iv.doubleToString(ppn)));
                                                transaksi.setNonPPN(iv.ChangeToCurrencyFormat(iv.doubleToString(nonPPn)));
                                                transaksi.setJml(iv.parseNullDouble(jml));
                                                transaksi.setDenda(iv.parseNullDouble(denda));
                                                transaksi.setAdmin(iv.parseNullDouble(admin));
                                                transaksi.setMsisdn(msisdn);
                                                transaksi.setPeriode(periode);
                                                transaksi.setStandMeter(standMeter);
                                                transaksi.setGolongan(golongan);

                                                printer.print(transaksi);
                                            }

                                        }else{

                                            Toast.makeText(context, "Harap pilih device printer telebih dahulu", Toast.LENGTH_LONG).show();
                                            printer.showDevices();
                                        }
                                    }
                                }
                            });

                            try {
                                //alert.show();
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }*/

                    }else{

                        View.OnClickListener clickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                dialogBox.dismissDialog();
                                doTransaksi(checkHarga);
                            }
                        };

                        if(!currentCounter.isEmpty()) dialogBox.showDialog(clickListener, "Ulangi Proses", message);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onError(String result) {

                //String message = "Terjadi kesalahan saat memuat data";
                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                if(progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
            }
        });
    }

    private void saveCustomHarga(String hargaCustom) {

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("id", currentCounter);
            jBody.put("harga", hargaCustom);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.saveHargaPPOB, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                String message = "Terjadi kesalahan saat memuat data, mohon coba kembali";

                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(status.equals("200")){


                    }else{
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String result) {

                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setProdukAdapter() {

        adapterProduk = new ArrayAdapter(this,R.layout.layout_simple_list, listProduk);
        spnNamaProduk.setAdapter(adapterProduk);
        //spnNamaProduk.setSelection(0);

        spnNamaProduk.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                OptionItem item = (OptionItem) adapterView.getItemAtPosition(i);
                idProduk = item.getValue();
                namaProduk = item.getText();
                isPPOB = item.getAtt4();

                if(item.getAtt2().equals("1")){
                    isInquery = true;
                    state = 1;
                }else{
                    isInquery = false;
                }
                setInquiry(isInquery);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initData() {

        dialogBox.showDialog(true);
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("kategori", idKategori);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getProdukPPOB, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                String message = "Terjadi kesalahan saat memuat data, mohon coba kembali";
                listProduk.clear();

                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(status.equals("200")){

                        JSONArray jsonArray = response.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length(); i ++){
                            JSONObject jo = jsonArray.getJSONObject(i);
                            listProduk.add(
                                    new OptionItem(jo.getString("id")
                                            ,jo.getString("namabrg")
                                            ,jo.getString("kode")
                                            ,jo.getString("xml")
                                            ,jo.getString("get")
                                            ,jo.getString("ppob")
                                    ));
                        }

                    }else{
                        DialogBox.showDialog(context, 3, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            initData();
                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", message);
                }

                adapterProduk.notifyDataSetChanged();
            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        initData();
                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", result);
            }
        });
    }

    private void getProvider(final String nomor) {

        pbLoading.setVisibility(View.VISIBLE);
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("nomor", nomor);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        listProduk.clear();
        adapterProduk.notifyDataSetChanged();

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getProvider, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                pbLoading.setVisibility(View.GONE);
                dialogBox.dismissDialog();
                String message = "Terjadi kesalahan saat memuat data, mohon coba kembali";
                listProduk.clear();

                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(status.equals("200")){

                        JSONArray jsonArray = response.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length(); i ++){
                            JSONObject jo = jsonArray.getJSONObject(i);
                            listProduk.add(
                                    new OptionItem(jo.getString("id")
                                            ,jo.getString("namabrg")
                                            ,jo.getString("kode")
                                            ,jo.getString("xml")
                                            ,jo.getString("get")
                                            ,jo.getString("ppob")
                                    ));
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            getProvider(nomor);
                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", message);
                }

                adapterProduk.notifyDataSetChanged();
            }

            @Override
            public void onError(String result) {

                pbLoading.setVisibility(View.GONE);
                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        getProvider(nomor);
                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", result);
            }
        });
    }

    private void setInquiry(boolean flag){

        if(flag){

            llFooter.setVisibility(View.VISIBLE);
            llHarga.setVisibility(View.VISIBLE);
        }else{
            llFooter.setVisibility(View.GONE);
            llHarga.setVisibility(View.GONE);
        }
    }

    /*@Override
    protected void onDestroy() {
        printer.stopService();
        super.onDestroy();
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);
    }
}
