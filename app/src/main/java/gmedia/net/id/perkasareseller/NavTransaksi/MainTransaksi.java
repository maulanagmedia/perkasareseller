package gmedia.net.id.perkasareseller.NavTransaksi;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.FormatItem;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import gmedia.net.id.perkasareseller.HomeActivity;
import gmedia.net.id.perkasareseller.NavTransaksi.Adapter.ListTransaksiAdapter;
import gmedia.net.id.perkasareseller.R;
import gmedia.net.id.perkasareseller.Utils.ServerURL;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class MainTransaksi extends Fragment {

    private Context context;
    private View layout;
    private SessionManager session;
    private ItemValidation iv = new ItemValidation();
    private EditText edtTglDari;
    private ImageView ivTglDari;
    private EditText edtTglSampai;
    private ImageView ivTglSampai;
    private ImageView ivNext;
    private ListView lvTransaksi;
    private ProgressBar pbLoading;
    private Button btnRefresh;
    private String dateFrom = "";
    private String dateTo = "";
    private List<CustomItem> listTransaksi;
    private DialogBox dialogBox;

    public MainTransaksi() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.fragment_main_transaksi, container, false);
        context = getContext();
        session = new SessionManager(context);

        initUI();

        return layout;
    }

    private void initUI() {

        edtTglDari = (EditText) layout.findViewById(R.id.edt_tgl_dari);
        ivTglDari = (ImageView) layout.findViewById(R.id.iv_tgl_dari);
        edtTglSampai = (EditText) layout.findViewById(R.id.edt_tgl_sampai);
        ivTglSampai = (ImageView) layout.findViewById(R.id.iv_tgl_sampai);
        ivNext = (ImageView) layout.findViewById(R.id.iv_next);
        lvTransaksi = (ListView) layout.findViewById(R.id.lv_transaksi);
        pbLoading = (ProgressBar) layout.findViewById(R.id.pb_loading);
        btnRefresh = (Button) layout.findViewById(R.id.btn_refresh);

        dateFrom = iv.sumDate(iv.getCurrentDate(FormatItem.formatDateDisplay), -1, FormatItem.formatDateDisplay) ;
        dateTo = iv.getCurrentDate(FormatItem.formatDateDisplay);

        edtTglDari.setText(dateFrom);
        edtTglSampai.setText(dateTo);
        dialogBox = new DialogBox(context);

        initEvent();

        getData();
    }

    private void initEvent() {

        ivTglDari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar customDate;
                SimpleDateFormat sdf = new SimpleDateFormat(FormatItem.formatDateDisplay);

                Date dateValue = null;

                try {
                    dateValue = sdf.parse(dateFrom);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                customDate = Calendar.getInstance();
                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        customDate.set(Calendar.YEAR,year);
                        customDate.set(Calendar.MONTH,month);
                        customDate.set(Calendar.DATE,date);

                        SimpleDateFormat sdFormat = new SimpleDateFormat(FormatItem.formatDateDisplay, Locale.US);
                        dateFrom = sdFormat.format(customDate.getTime());
                        edtTglDari.setText(dateFrom);
                    }
                };

                SimpleDateFormat yearOnly = new SimpleDateFormat("yyyy");
                new DatePickerDialog(context ,date , iv.parseNullInteger(yearOnly.format(dateValue)),dateValue.getMonth(),dateValue.getDate()).show();
            }
        });

        ivTglSampai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar customDate;
                SimpleDateFormat sdf = new SimpleDateFormat(FormatItem.formatDateDisplay);

                Date dateValue = null;

                try {
                    dateValue = sdf.parse(dateTo);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                customDate = Calendar.getInstance();
                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        customDate.set(Calendar.YEAR,year);
                        customDate.set(Calendar.MONTH,month);
                        customDate.set(Calendar.DATE,date);

                        SimpleDateFormat sdFormat = new SimpleDateFormat(FormatItem.formatDateDisplay, Locale.US);
                        dateTo = sdFormat.format(customDate.getTime());
                        edtTglSampai.setText(dateTo);
                    }
                };

                SimpleDateFormat yearOnly = new SimpleDateFormat("yyyy");
                new DatePickerDialog(context ,date , iv.parseNullInteger(yearOnly.format(dateValue)),dateValue.getMonth(),dateValue.getDate()).show();
            }
        });

        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dateFrom = edtTglDari.getText().toString();
                dateTo = edtTglSampai.getText().toString();

                getData();
            }
        });

        lvTransaksi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CustomItem item = (CustomItem) parent.getItemAtPosition(position);
                String nominal = item.getItem5();
                String rekening = item.getItem14();
                String atasnama = item.getItem16();
                String bank = item.getItem15();
                String expiredDate = item.getItem13();

                if(item.getItem17().equals("SD")){

                    showResultDialog(nominal, rekening, bank, atasnama, expiredDate);
                }else{
                    cetakNotaPerdana(item.getItem3(), item.getItem17());
                }
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

                ClipboardManager clipboard = (ClipboardManager) ((Activity)context).getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("nominal", tvNominal.getText().toString().replaceAll("[,.]", ""));
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Nominal disimpan di clipboard", Toast.LENGTH_LONG).show();
            }
        });

        ivRekening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClipboardManager clipboard = (ClipboardManager) ((Activity)context).getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Rekening", tvRekening.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Rekening disimpan di clipboard", Toast.LENGTH_LONG).show();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                if(alert != null) alert.dismiss();
            }
        });

        alert.show();
    }

    private void cetakNotaPerdana(final String nobukti, final String flag) {

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("nobukti", nobukti);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", flag.equals("PD") ? ServerURL.cetakNotaPerdana : ServerURL.cetakNotaNgrs, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");

                    if(status.equals("200")){

                        String file = response.getJSONObject("response").getString("file");
                        new DownloadFileFromURL(context,iv.getCurrentDate(FormatItem.formatTimestamp2)).execute(file);
                    }else{
                        dialogBox.showDialog(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                dialogBox.dismissDialog();
                            }
                        },"Ok","Order belum diproses sales");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    getTableList(null);

                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            cetakNotaPerdana(nobukti, flag);

                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                }
            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        cetakNotaPerdana(nobukti, flag);

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        });
    }

    private void getData() {

        dialogBox.showDialog(false);
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("tgl_mulai", iv.ChangeFormatDateString(dateFrom, FormatItem.formatDateDisplay, FormatItem.formatDate));
            jBody.put("tgl_selesai", iv.ChangeFormatDateString(dateTo, FormatItem.formatDateDisplay, FormatItem.formatDate));
            jBody.put("nomor", session.getUsername());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.viewTransaksi, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    listTransaksi = new ArrayList<>();
                    if(status.equals("200")){

                        JSONArray jsonArray = response.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length(); i ++){
                            JSONObject jo = jsonArray.getJSONObject(i);
                            listTransaksi.add(
                                    new CustomItem(jo.getString("id"),
                                            jo.getString("tgl"),
                                            jo.getString("nobukti"),
                                            jo.getString("keterangan"),
                                            jo.getString("total"),
                                            jo.getString("nomor"),
                                            jo.getString("keterangan_crbayar"),
                                            jo.getString("crbayar"),
                                            jo.getString("status_transaksi"),
                                            jo.getString("jam"),
                                            jo.getString("kode_lokasi"),
                                            (jo.getString("rekening").isEmpty() ? "" : jo.getString("rekening")+ " ("+ jo.getString("bank")+") a/n "+ jo.getString("atasnama"))
                                            ,jo.getString("expired_at")
                                            ,jo.getString("rekening")
                                            ,jo.getString("bank")
                                            ,jo.getString("atasnama")
                                            ,jo.getString("jenis")

                                    ));
                        }

                        getTableList(listTransaksi);

                    }else{
                        getTableList(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    getTableList(null);

                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            getData();

                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                }
            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        getData();

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        });
    }

    private void getTableList(List<CustomItem> listItem) {

        lvTransaksi.setAdapter(null);

        if(listItem != null && listItem.size() >0){

            ListTransaksiAdapter adapter = new ListTransaksiAdapter((Activity) context, listItem);
            lvTransaksi.setAdapter(adapter);
        }
    }
}
