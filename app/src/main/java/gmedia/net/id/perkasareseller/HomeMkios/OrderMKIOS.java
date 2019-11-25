package gmedia.net.id.perkasareseller.HomeMkios;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.FormatItem;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.OptionItem;
import com.maulana.custommodul.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.perkasareseller.HomeActivity;
import gmedia.net.id.perkasareseller.HomeMkios.Adapter.CustomSpinnerAdapter;
import gmedia.net.id.perkasareseller.HomeMkios.Adapter.ListDenomMkiosAdapter;
import gmedia.net.id.perkasareseller.R;
import gmedia.net.id.perkasareseller.Utils.ServerURL;

public class OrderMKIOS extends AppCompatActivity {

    private static List<CustomItem> items;
    private static double totalHarga = 0;
    private Spinner spNoRS;
    private Button btnProses;
    private static ItemValidation iv = new ItemValidation();
    private List<OptionItem> listNoRS;
    private List<CustomItem> listDenom;
    private Context context;
    private ListView lvDenom;
    private DialogBox dialogBox;
    private String selectedNomor = "";
    private static ListDenomMkiosAdapter adapterDenom;
    private static TextView tvJumlahHarga;
    private SessionManager session;
    private TextView tvNomor;
    private String pin = "", flagPin = "";
    private TextView tvDenom1, tvDenom5, tvDenom10, tvDenom20, tvDenom25, tvDenom50, tvDenom100
            , tvHarga1, tvHarga5, tvHarga10, tvHarga20, tvHarga25, tvHarga50, tvHarga100;
    private EditText edtJumlah1, edtJumlah5, edtJumlah10, edtJumlah20, edtJumlah25, edtJumlah50, edtJumlah100;
    private RadioGroup rgCrBayar;
    private String crBayar = "1";
    private LinearLayout llBank;
    private Spinner spBank;
    private List<OptionItem> listBank = new ArrayList<>();
    private CustomSpinnerAdapter adapterBank;
    private String selectedBank = "";
    private ScrollView svCountainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_mkios);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_down));

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        context = this;
        session = new SessionManager(context);

        setTitle("Beli Stok MKIOS");

        initUI();
    }

    private void initUI() {

        spNoRS = (Spinner) findViewById(R.id.sp_no_rs);
        lvDenom = (ListView) findViewById(R.id.lv_denom);
        lvDenom.setItemsCanFocus(true);
        btnProses = (Button) findViewById(R.id.btn_proses);
        tvJumlahHarga = (TextView) findViewById(R.id.tv_jumlah_harga);
        tvNomor = (TextView) findViewById(R.id.tv_nomor);
        tvNomor.setText(session.getUsername());

        tvDenom1 = (TextView) findViewById(R.id.tv_denom1);
        tvDenom5 = (TextView) findViewById(R.id.tv_denom5);
        tvDenom10 = (TextView) findViewById(R.id.tv_denom10);
        tvDenom20 = (TextView) findViewById(R.id.tv_denom20);
        tvDenom25 = (TextView) findViewById(R.id.tv_denom25);
        tvDenom50 = (TextView) findViewById(R.id.tv_denom50);
        tvDenom100 = (TextView) findViewById(R.id.tv_denom100);

        tvHarga1 = (TextView) findViewById(R.id.tv_harga1);
        tvHarga5 = (TextView) findViewById(R.id.tv_harga5);
        tvHarga10 = (TextView) findViewById(R.id.tv_harga10);
        tvHarga20 = (TextView) findViewById(R.id.tv_harga20);
        tvHarga25 = (TextView) findViewById(R.id.tv_harga25);
        tvHarga50 = (TextView) findViewById(R.id.tv_harga50);
        tvHarga100 = (TextView) findViewById(R.id.tv_harga100);

        edtJumlah1 = (EditText) findViewById(R.id.edt_jumlah1);
        edtJumlah5 = (EditText) findViewById(R.id.edt_jumlah5);
        edtJumlah10 = (EditText) findViewById(R.id.edt_jumlah10);
        edtJumlah20 = (EditText) findViewById(R.id.edt_jumlah20);
        edtJumlah25 = (EditText) findViewById(R.id.edt_jumlah25);
        edtJumlah50 = (EditText) findViewById(R.id.edt_jumlah50);
        edtJumlah100 = (EditText) findViewById(R.id.edt_jumlah100);

        svCountainer = (ScrollView) findViewById(R.id.sv_container);

        llBank = (LinearLayout) findViewById(R.id.ll_bank);
        spBank = (Spinner) findViewById(R.id.sp_bank);

//        rgCrBayar = (RadioGroup) findViewById(R.id.rg_crbayar);
        crBayar = "1";
        selectedBank = "";

        items = new ArrayList<>();
        listDenom = new ArrayList<>();

        listBank = new ArrayList<>();
        adapterBank = new CustomSpinnerAdapter((Activity) context, R.layout.adapter_bank, listBank);
        spBank.setAdapter(adapterBank);

        dialogBox = new DialogBox(context);
        //getNoRs();

        getDataDenom();

        initEvent();
    }

    private void initEvent() {

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // validasi
                if(totalHarga <= 0){
                    DialogBox.showDialog(context, 3, "Harap pilih denom terlebih dahulu");
                    return;
                }

                if(listDenom == null || listDenom.size() <= 0){
                    DialogBox.showDialog(context, 3, "Harap pilih denom terlebih dahulu");
                    return;
                }

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle("Konfirmasi")
                        .setMessage("Anda yakin ingin menyimpan data?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                showPinDialog();
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();

            }
        });

        edtJumlah1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                updateHarga();
            }
        });

        edtJumlah5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                updateHarga();
            }
        });

        edtJumlah10.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                updateHarga();
            }
        });

        edtJumlah20.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                updateHarga();
            }
        });

        edtJumlah25.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                updateHarga();
            }
        });

        edtJumlah50.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                updateHarga();
            }
        });

        edtJumlah100.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                updateHarga();
            }
        });

//        rgCrBayar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//
//                if(i == R.id.rb_tunai) {
//
//                    crBayar = "1";
//                    llBank.setVisibility(View.GONE);
//                    selectedBank = "";
//                }else {
//
//                    crBayar = "2";
//                    llBank.setVisibility(View.VISIBLE);
//
//                    svCountainer.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            svCountainer.fullScroll(ScrollView.FOCUS_DOWN);
//                        }
//                    });
//
//                    if(spBank.getSelectedItem() != null) {
//
//                        OptionItem item = (OptionItem) spBank.getSelectedItem();
//                        selectedBank = item.getValue();
//                    }
//                }
//            }
//        });

        spBank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                OptionItem item = (OptionItem) adapterView.getItemAtPosition(i);

                if(crBayar.equals("1")){

                    selectedBank = "";
                }else{

                    selectedBank = item.getValue();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void showPinDialog(){

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
        View viewDialog = inflater.inflate(R.layout.dialog_pin, null);
        builder.setView(viewDialog);
        builder.setCancelable(false);

        final EditText edtPin = (EditText) viewDialog.findViewById(R.id.edt_pin);
        final Button btnTutup = (Button) viewDialog.findViewById(R.id.btn_tutup);
        final Button btnProses = (Button) viewDialog.findViewById(R.id.btn_proses);
        final CheckBox cbSimpan = (CheckBox) viewDialog.findViewById(R.id.cb_simpan);

        if(flagPin.equals("1")){

            cbSimpan.setChecked(true);
            edtPin.setText(pin);
            if(pin.length() > 0) edtPin.setSelection(pin.length());
        }else{
            cbSimpan.setChecked(false);
            edtPin.setText("");
        }

        final android.app.AlertDialog alert = builder.create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        btnTutup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                if(alert != null) alert.dismiss();
            }
        });

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                if(edtPin.getText().toString().isEmpty()){

                    edtPin.setError("Sandi harap diisi");
                    edtPin.requestFocus();
                    return;
                }else{

                    edtPin.setError(null);
                }

                if(edtPin.getText().toString().length() < 4){

                    edtPin.setError("Sandi harap 4 digit");
                    edtPin.requestFocus();
                    return;
                }else{

                    edtPin.setError(null);
                }

                if(alert != null) alert.dismiss();

                //saveDeposit(edtPin.getText().toString());
                if(cbSimpan.isChecked()) {

                    flagPin = "1";
                }else{
                    flagPin = "0";
                }

                pin = edtPin.getText().toString();
                savePin();
            }
        });

        alert.show();
    }

    private void savePin() {

        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Login_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Menyimpan...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JSONObject jBody = new JSONObject();

        try {
            jBody.put("pin", pin);
            jBody.put("tipe", "2");
            jBody.put("flag", flagPin);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.savePinFlag, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                if(progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                String message = "Terjadi kesalahan saat memuat data";

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(iv.parseNullInteger(status) == 200){

                        saveMkios(pin);
                    }else{

                        View.OnClickListener clickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                dialogBox.dismissDialog();
                                savePin();
                            }
                        };

                        dialogBox.showDialog(clickListener, "Ulangi Proses", message);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onError(String result) {

                String message = "Terjadi kesalahan saat memuat data";
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                if(progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
            }
        });
    }

    private void saveMkios(String pin) {

        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Login_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Menyimpan...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JSONArray jArrayBarang = new JSONArray();

        //MKIOS
        if(listDenom != null && listDenom.size() > 0){


            for(CustomItem item : listDenom){

                if(iv.parseNullDouble(item.getItem4()) > 0){

                    JSONObject jDenom = new JSONObject();
                    try {
                        jDenom.put("kode", item.getItem1());
                        jDenom.put("jumlah", item.getItem4());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    jArrayBarang.put(jDenom);
                }
            }
        }

        JSONObject jBody = new JSONObject();

        try {
            jBody.put("barang", jArrayBarang);
            jBody.put("pin", pin);
            jBody.put("id_bank", selectedBank);
            jBody.put("nomor", session.getUsername());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.beliMkios, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                if(progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                String message = "Terjadi kesalahan saat menyimpan, atau anda pernah order dengan nominal yang sama pada hari ini";

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");
                    if(iv.parseNullInteger(status) == 200){

                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        if(crBayar.equals("1")) { // Tunai

                            HomeActivity.stateFragment = 1;
                            onBackPressed();
                        }else{ // Transfer

                            String total = response.getJSONObject("response").getJSONObject("bill").getString("total");
                            String angkaUnik = response.getJSONObject("response").getJSONObject("bill").getString("angka_unik");

                            String totalTransfer = iv.doubleToStringRound(iv.parseNullDouble(total) + iv.parseNullDouble(angkaUnik));
                            String nobukti = response.getJSONObject("response").getJSONObject("bill").getString("nobukti");

                            String bank = response.getJSONObject("response").getJSONObject("bank").getString("bank");
                            String noRekening = response.getJSONObject("response").getJSONObject("bank").getString("norekening");
                            String namaBank = response.getJSONObject("response").getJSONObject("bank").getString("nama");

                            String expiredAt = response.getJSONObject("response").getString("expired_at");

                            showResultDialog(totalTransfer, noRekening, bank, namaBank, expiredAt);
                        }

                        /*HomeActivity.stateFragment = 1;
                        onBackPressed();*/


                    }else{

                        if(!message.toLowerCase().contains("pin")){

                            DialogBox.showDialog(context, 3,message);
                        }else{
                            View.OnClickListener clickListener = new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    dialogBox.dismissDialog();
                                    showPinDialog();

                                }
                            };

                            dialogBox.showDialog(clickListener, "Ulangi Proses", message);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onError(String result) {

                String message = "Terjadi kesalahan saat menyimpan, atau anda pernah order dengan nominal yang sama pada hari ini";
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                if(progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
            }
        });
    }

    private void showResultDialog(final String nominal, final String rekening, final String bank, final String an, final String expiration){

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
        View viewDialog = inflater.inflate(R.layout.dialog_hasil_topup, null);
        builder.setView(viewDialog);
        builder.setCancelable(false);

        final RadioGroup rgCaraBayar = (RadioGroup) viewDialog.findViewById(R.id.rg_cara_bayar);
        final TextView tvNominal = (TextView) viewDialog.findViewById(R.id.tv_nominal);
        final TextView tvRekening = (TextView) viewDialog.findViewById(R.id.tv_rekening);
        final TextView tvBank = (TextView) viewDialog.findViewById(R.id.tv_bank);
        final TextView tvAn = (TextView) viewDialog.findViewById(R.id.tv_nama);
        final TextView tvExpiration = (TextView) viewDialog.findViewById(R.id.tv_expiration);
        final ImageView ivNominal = (ImageView) viewDialog.findViewById(R.id.iv_nominal);
        final ImageView ivRekening = (ImageView) viewDialog.findViewById(R.id.iv_rekening);
        final Button btnOk = (Button) viewDialog.findViewById(R.id.btn_ok);

        final android.app.AlertDialog alert = builder.create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        tvNominal.setText(iv.ChangeToCurrencyFormat(nominal));
        tvRekening.setText(rekening);
        tvBank.setText(bank);
        tvAn.setText(an);
        tvExpiration.setText(Html.fromHtml("Harap lakukan transfer sebelum <b>"+ iv.ChangeFormatDateString(expiration, FormatItem.formatTimestamp, FormatItem.formatDateTimeDisplay) + "</b>"));

        ivNominal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("nominal", tvNominal.getText().toString().replaceAll("[,.]", ""));
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Nominal disimpan di clipboard", Toast.LENGTH_LONG).show();
            }
        });

        ivRekening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Rekening", tvRekening.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Rekening disimpan di clipboard", Toast.LENGTH_LONG).show();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                if(alert != null) alert.dismiss();
                HomeActivity.stateFragment = 1;
                onBackPressed();
            }
        });

        alert.show();
    }

    private void getNoRs() {

        dialogBox.showDialog(false);
        final ApiVolley request = new ApiVolley(context, new JSONObject(), "GET", ServerURL.getNomor, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");
                    listNoRS = new ArrayList<>();
                    if(status.equals("200")){

                        JSONArray jsonArray = response.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length(); i ++){
                            JSONObject jo = jsonArray.getJSONObject(i);
                            listNoRS.add(new OptionItem(String.valueOf(i+1), jo.getString("nomor")));
                        }

                        setNoRSAdapter(listNoRS);

                    }else{
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        setNoRSAdapter(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    setNoRSAdapter(null);

                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            getNoRs();

                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                }
            }

            @Override
            public void onError(String result) {
                setNoRSAdapter(null);
                dialogBox.dismissDialog();

                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        getNoRs();

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        });
    }

    private void setNoRSAdapter(List<OptionItem> listItem) {

        if(listItem != null && listItem.size() > 0){

            ArrayAdapter adapter = new ArrayAdapter(OrderMKIOS.this, R.layout.layout_simple_list, listItem);
            spNoRS.setAdapter(adapter);

            spNoRS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                    OptionItem selectedItem = (OptionItem) parent.getItemAtPosition(position);
                    selectedNomor = selectedItem.getText();

                    getDataDenom();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            spNoRS.setSelection(0);
        }
    }

    private void getDataDenom() {

        dialogBox.showDialog(false);
        JSONObject jBody = new JSONObject();

        try {
            jBody.put("nomor", session.getUsername());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getDenom, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    listDenom = new ArrayList<>();
                    if(status.equals("200")){

                        JSONArray jsonArray = response.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length(); i ++){
                            JSONObject jo = jsonArray.getJSONObject(i);
                            listDenom.add(new CustomItem(
                                    jo.getString("kodebrg"),
                                    jo.getString("hargabeli"),
                                    jo.getString("hargajual"),
                                    "",
                                    jo.getString("active")));

                            if(i == 0){

                                tvDenom1.setText(jo.getString("namabrg"));
                                tvHarga1.setText("Harga " + iv.ChangeToRupiahFormat(jo.getString("hargajual")));
                                if(jo.getString("active").equals("1")){
                                    edtJumlah1.setEnabled(true);
                                }else{
                                    edtJumlah1.setEnabled(false);
                                }

                            }else if(i == 1){

                                tvDenom5.setText(jo.getString("namabrg"));
                                tvHarga5.setText("Harga " + iv.ChangeToRupiahFormat(jo.getString("hargajual")));
                                if(jo.getString("active").equals("1")){
                                    edtJumlah5.setEnabled(true);
                                }else{
                                    edtJumlah5.setEnabled(false);
                                }

                            }else if(i == 2){

                                tvDenom10.setText(jo.getString("namabrg"));
                                tvHarga10.setText("Harga " + iv.ChangeToRupiahFormat(jo.getString("hargajual")));
                                if(jo.getString("active").equals("1")){
                                    edtJumlah10.setEnabled(true);
                                }else{
                                    edtJumlah10.setEnabled(false);
                                }
                            }else if(i == 3){

                                tvDenom20.setText(jo.getString("namabrg"));
                                tvHarga20.setText("Harga " + iv.ChangeToRupiahFormat(jo.getString("hargajual")));
                                if(jo.getString("active").equals("1")){
                                    edtJumlah20.setEnabled(true);
                                }else{
                                    edtJumlah20.setEnabled(false);
                                }
                            }else if(i == 4){

                                tvDenom25.setText(jo.getString("namabrg"));
                                tvHarga25.setText("Harga " + iv.ChangeToRupiahFormat(jo.getString("hargajual")));
                                if(jo.getString("active").equals("1")){
                                    edtJumlah25.setEnabled(true);
                                }else{
                                    edtJumlah25.setEnabled(false);
                                }
                            }else if(i == 5){

                                tvDenom50.setText(jo.getString("namabrg"));
                                tvHarga50.setText("Harga " + iv.ChangeToRupiahFormat(jo.getString("hargajual")));
                                if(jo.getString("active").equals("1")){
                                    edtJumlah50.setEnabled(true);
                                }else{
                                    edtJumlah50.setEnabled(false);
                                }
                            }else if(i == 6){

                                tvDenom100.setText(jo.getString("namabrg"));
                                tvHarga100.setText("Harga " + iv.ChangeToRupiahFormat(jo.getString("hargajual")));
                                if(jo.getString("active").equals("1")){
                                    edtJumlah100.setEnabled(true);
                                }else{
                                    edtJumlah100.setEnabled(false);
                                }
                            }
                        }
                    }

                    //setDenomAdapter(listDenom);
                } catch (JSONException e) {
                    e.printStackTrace();

                    //setDenomAdapter(null);
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            getDataDenom();

                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                }

                getDataPin();
            }

            @Override
            public void onError(String result) {

                //setDenomAdapter(null);
                dialogBox.dismissDialog();

                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        getDataDenom();

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                getDataPin();
            }
        });
    }

    private void getDataPin() {

        dialogBox.showDialog(false);
        JSONObject jBody = new JSONObject();

        try {
            jBody.put("tipe", "2");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getSavedPin, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");

                    if(status.equals("200")){

                        pin = response.getJSONObject("response").getString("pin");
                        flagPin = response.getJSONObject("response").getString("flag");

                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            getDataPin();

                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                }

                initDataBank();
            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();

                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        getDataPin();

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        });
    }

    private void initDataBank() {

        dialogBox.showDialog(false);
        JSONObject jBody = new JSONObject();

        try {
            jBody.put("tipe", "");
            jBody.put("start", "");
            jBody.put("count", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getBankBayar, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                listBank.clear();
                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");

                    if(status.equals("200")){

                        JSONArray jsonArray = response.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length(); i++){

                            JSONObject jo = jsonArray.getJSONObject(i);
                            listBank.add(new OptionItem(
                                    jo.getString("id")
                                    , jo.getString("bank") + " (" + jo.getString("nama") + ")"
                                    , jo.getString("norekening")
                            ));
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            initDataBank();

                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                }

                adapterBank.notifyDataSetChanged();
            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();

                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        initDataBank();

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        });
    }

    private void updateHarga(){

        String jml1 = edtJumlah1.getText().toString();
        String jml5 = edtJumlah5.getText().toString();
        String jml10 = edtJumlah10.getText().toString();
        String jml20 = edtJumlah20.getText().toString();
        String jml25 = edtJumlah25.getText().toString();
        String jml50 = edtJumlah50.getText().toString();
        String jml100 = edtJumlah100.getText().toString();

        double qty5 = iv.parseNullDouble(jml5);
        double qty10 = iv.parseNullDouble(jml10);
        double qty20 = iv.parseNullDouble(jml20);
        double qty25 = iv.parseNullDouble(jml25);
        double qty50 = iv.parseNullDouble(jml50);
        double qty100 = iv.parseNullDouble(jml100);

        totalHarga = 0;

        if(listDenom != null && listDenom.size() > 0){

            int x = 0;
            for(CustomItem item : listDenom){

                if(x == 0){
                    item.setItem4(jml1);
                }else if(x == 1){

                    item.setItem4(jml5);
                }else if(x == 2){

                    item.setItem4(jml10);
                }else if(x == 3){

                    item.setItem4(jml20);
                }else if(x == 4){

                    item.setItem4(jml25);
                }else if(x == 5){

                    item.setItem4(jml50);
                }else if(x == 6){

                    item.setItem4(jml100);
                }

                totalHarga += (iv.parseNullDouble(item.getItem3()) * iv.parseNullDouble(item.getItem4()));
                x++;
            }
        }

        tvJumlahHarga.setText(iv.ChangeToRupiahFormat(totalHarga));
    }

    private void setDenomAdapter(List<CustomItem> listItem) {

        lvDenom.setAdapter(null);
        if(listItem != null && listItem.size() > 0){

            adapterDenom = new ListDenomMkiosAdapter(OrderMKIOS.this, listItem);
            lvDenom.setAdapter(adapterDenom);
        }
    }

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
        overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_down);
    }
}
