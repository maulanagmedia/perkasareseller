package gmedia.net.id.perkasareseller;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class RegisterNew extends AppCompatActivity {
    private Button btn_simpan;
    private Activity context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Registrasi Baru");

        context = this;

        //View
        btn_simpan = findViewById(R.id.btn_kirim);

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (RegisterNew.this, ActivityRegisterSudahDiperkasa.class);
                startActivity(intent);
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
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);
        super.onBackPressed();
    }
}