package gmedia.net.id.perkasareseller.HomeTcash;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import gmedia.net.id.perkasareseller.HomeActivity;
import gmedia.net.id.perkasareseller.HomeMkios.Adapter.CustomSpinnerAdapter;
import gmedia.net.id.perkasareseller.R;
import gmedia.net.id.perkasareseller.Utils.ServerURL;

public class OrderTcash extends AppCompatActivity {

    private ItemValidation iv = new ItemValidation();
    private Spinner spNoRS;
    private List<OptionItem> listNoRS;
    private DialogBox dialogBox;
    private String selectedNomor = "";
    private Context context;
    private Button btnProses;
    private EditText edtNominal;
    private Timer timer;
    private String harga = "";
    private TextView tvJumlahHarga;
    private SessionManager session;
    private TextView tvNomor;
    private String pin = "", flagPin = "";
    private String currentString = "";
    private boolean isProcess = false;

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
        setContentView(R.layout.activity_order_tcash);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_down));

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        context = this;
        session = new SessionManager(context);

        setTitle("Beli Stok Link Aja");

        initUI();
    }

    private void initUI() {

        spNoRS = (Spinner) findViewById(R.id.sp_no_rs);
        dialogBox = new DialogBox(context);
        edtNominal = (EditText) findViewById(R.id.edt_nominal);
        tvJumlahHarga = (TextView) findViewById(R.id.tv_jumlah_harga);
        btnProses = (Button) findViewById(R.id.btn_proses);
        tvNomor = (TextView) findViewById(R.id.tv_nomor);
        tvNomor.setText(session.getUsername());

        isProcess = false;

        svCountainer = (ScrollView) findViewById(R.id.sv_container);

        llBank = (LinearLayout) findViewById(R.id.ll_bank);
        spBank = (Spinner) findViewById(R.id.sp_bank);

//        rgCrBayar = (RadioGroup) findViewById(R.id.rg_crbayar);
//        crBayar = "1";
//        selectedBank = "";

        listBank = new ArrayList<>();
        adapterBank = new CustomSpinnerAdapter((Activity) context, R.layout.adapter_bank, listBank);
        spBank.setAdapter(adapterBank);

        //getNoRs();
        initEvent();

        getDataPin();
    }

    private void initEvent() {

        edtNominal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (timer != null) {
                    timer.cancel();
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(!editable.toString().equals(currentString)){

                    String cleanString = editable.toString().replaceAll("[,.]", "");
                    edtNominal.removeTextChangedListener(this);

                    String formatted = iv.ChangeToCurrencyFormat(cleanString);

                    currentString = formatted;
                    edtNominal.setText(formatted);
                    edtNominal.setSelection(formatted.length());
                    isProcess = true;

                    if(editable.length() > 0){
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {

                                try {
                                    Thread.sleep(200);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        getHarga();
                                    }
                                });
                            }
                        }, 600);
                    }
                    edtNominal.addTextChangedListener(this);
                }
            }
        });

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // validasi
                /*if(listNoRS.size() <= 0){
                    DialogBox.showDialog(context, 3, "Tidak ada nomor RS terdaftar");
                    return;
                }*/

                if(isProcess){

                    Toast.makeText(context, "Tunggu proses pengambilan harga selesai atau ketik ulang nominal anda karena harga tidak terproses", Toast.LENGTH_LONG).show();
                    return;
                }

                if(edtNominal.getText().toString().isEmpty()){

                    edtNominal.setError("Nominal harap diisi");
                    edtNominal.requestFocus();
                    return;
                }else{

                    edtNominal.setError(null);
                }

                if(edtNominal.getText().toString().equals("0")){
                    edtNominal.setError("Nominal harap lebih besar dari 0");
                    edtNominal.requestFocus();
                    return;
                }else{

                    edtNominal.setError(null);
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

//        spBank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//
//                OptionItem item = (OptionItem) adapterView.getItemAtPosition(i);
//
//                if(crBayar.equals("1")){
//
//                    selectedBank = "";
//                }else{
//
//                    selectedBank = item.getValue();
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
    }

    private void getHarga() {

        isProcess = true;
        JSONObject jBody = new JSONObject();
        final String hargaTcash = edtNominal.getText().toString().replaceAll("[,.]", "");

        try {
            jBody.put("flag", "TC");
            jBody.put("nominal", hargaTcash);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getHarga, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");
                    if(status.equals("200")){

                        isProcess = false;
                        harga = response.getJSONObject("response").getString("harga");
                        tvJumlahHarga.setText(iv.ChangeToRupiahFormat(harga));

                    }else{

                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String result) {
                Toast.makeText(context, "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();
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
                String message = "Terjadi kesalahan saat memuat data, harap ulangi kembali";

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(iv.parseNullInteger(status) == 200){

                        saveData(pin);
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

    private void saveData(String pin) {

        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Login_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Menyimpan...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final String hargaTcash = edtNominal.getText().toString().replaceAll("[,.]", "");
        JSONArray jArrayData = new JSONArray();

        // Tcash
        JSONObject jBody = new JSONObject();

        try {
            jBody.put("harga", hargaTcash);
            jBody.put("nominal", hargaTcash);
            jBody.put("pin", pin);
            jBody.put("nomor", session.getUsername());
            jBody.put("crbayar", crBayar);
            jBody.put("id_bank", selectedBank);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.beliTcash, new ApiVolley.VolleyCallback() {
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

            ArrayAdapter adapter = new ArrayAdapter(OrderTcash.this, R.layout.layout_simple_list, listItem);
            spNoRS.setAdapter(adapter);

            spNoRS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    OptionItem selectedItem = (OptionItem) parent.getItemAtPosition(position);
                    selectedNomor = selectedItem.getText();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            spNoRS.setSelection(0);
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
