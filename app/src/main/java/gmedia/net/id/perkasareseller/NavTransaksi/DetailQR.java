package gmedia.net.id.perkasareseller.NavTransaksi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.FormatItem;
import com.maulana.custommodul.ImageUtils;
import com.maulana.custommodul.ItemValidation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import gmedia.net.id.perkasareseller.R;
import gmedia.net.id.perkasareseller.Utils.ServerURL;

public class DetailQR extends AppCompatActivity {

    private Context context;
    private TextView tvNobukti;
    private ImageView ivQR;
    private ItemValidation iv = new ItemValidation();
    private String nobukti = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_q_r);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Promo");
        context = this;

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            nobukti = bundle.getString("nobukti", "");
        }

        initUI();
        getData();
    }

    private void initUI() {

        tvNobukti = (TextView) findViewById(R.id.tv_nobukti);
        ivQR = (ImageView) findViewById(R.id.iv_qr);

        tvNobukti.setText(nobukti);
    }

    private void getData() {

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("nobukti", nobukti);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getQR, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");

                    if(status.endsWith("200")){

                        String qr = response.getJSONObject("response").getString("qr");
                        ImageUtils iu = new ImageUtils();
                        iu.LoadRealImage(context, qr, ivQR);
                    }else{

                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

            }
        });
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
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
}