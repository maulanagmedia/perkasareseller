package gmedia.net.id.perkasareseller.HomeBeliPerdana;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.ItemValidation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gmedia.net.id.perkasareseller.R;
import gmedia.net.id.perkasareseller.Utils.ServerURL;

public class DetailBarangPerdana extends AppCompatActivity {

    private Context context;
    private DialogBox dialogBox;
    private ItemValidation iv = new ItemValidation();
    private TextView tvNamaBarang, tvHarga, tvTotalHarga;
    private EditText edtJumlah;
    private Button btnProses;
    private String currentString = "", kdbrg = "", namabrg = "", harga = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_barang_perdana);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_down));

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        context = this;
        dialogBox = new DialogBox(context);

        setTitle("Detail Barang");

        initUI();
        initEvent();
    }

    private void initUI() {

        tvNamaBarang = (TextView) findViewById(R.id.tv_nama_barang);
        tvHarga = (TextView) findViewById(R.id.tv_harga);
        tvTotalHarga = (TextView) findViewById(R.id.tv_total_harga);
        edtJumlah = (EditText) findViewById(R.id.edt_jumlah);
        btnProses = (Button) findViewById(R.id.btn_proses);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            kdbrg = bundle.getString("kdbrg", "");
            namabrg = bundle.getString("namabrg", "");
            harga = bundle.getString("harga", "");

            tvNamaBarang.setText(namabrg);
            tvHarga.setText(iv.ChangeToRupiahFormat(harga));
        }
    }

    private void initEvent() {

        edtJumlah.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(!editable.toString().equals(currentString)){

                    String cleanString = editable.toString().replaceAll("[,.]", "");
                    edtJumlah.removeTextChangedListener(this);

                    String formatted = iv.ChangeToCurrencyFormat(cleanString);

                    currentString = formatted;
                    edtJumlah.setText(formatted);
                    edtJumlah.setSelection(formatted.length());
                    getHarga(cleanString);
                    edtJumlah.addTextChangedListener(this);
                }
            }
        });

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String jumlah = edtJumlah.getText().toString().toString().replaceAll("[,.]", "");
                if(iv.parseNullDouble(jumlah) <= 0){

                    edtJumlah.setError("Jumlah harap lebih dari 0");
                    edtJumlah.requestFocus();
                    return;
                }else{

                    edtJumlah.setError(null);
                }

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin menambahkan "+namabrg+ " sebanyak " + edtJumlah.getText().toString().toString() +" dengan total "+ tvTotalHarga.getText().toString()+" ?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                                DetailKeranjangPerdana.listKeranjang.add(new CustomItem(
                                        kdbrg,
                                        namabrg,
                                        harga,
                                        edtJumlah.getText().toString().toString().replaceAll("[,.]", "")
                                ));

                                Intent intent = new Intent(context, DetailKeranjangPerdana.class);
                                startActivity(intent);
                                finish();
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
    }

    private void getHarga(final String jumlah) {
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("kdgroup", kdbrg);
            jBody.put("jumlah", jumlah.equals("") ? "0" : jumlah);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.priceListPerkode, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                    String message = "Terjadi kesalahan saat memuat data, mohon coba kembali";

                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(status.equals("200")){
                        harga = response.getJSONObject("response").getString("harga");
                        tvHarga.setText(iv.ChangeToRupiahFormat(harga));
                    }
                    updateTotalHarga();
                } catch (JSONException e) {
                    e.printStackTrace();

                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            getHarga(jumlah);
                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", message);
                }

            }

            @Override
            public void onError(String result) {


                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        getHarga(jumlah);
                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat memuat data, mohon coba kembali");
            }
        });
    }

    private void updateTotalHarga() {

        double total = 0;
        String jumlah = edtJumlah.getText().toString().toString().replaceAll("[,.]", "");
        total = iv.parseNullDouble(jumlah) * iv.parseNullDouble(harga);
        tvTotalHarga.setText(iv.ChangeToRupiahFormat(total));
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
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);
    }
}
