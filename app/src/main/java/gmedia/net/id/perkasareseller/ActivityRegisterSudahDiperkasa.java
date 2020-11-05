package gmedia.net.id.perkasareseller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

//import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap;
import com.maulana.custommodul.CustomView.DialogBox;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.OptionItem;
import com.maulana.custommodul.SessionManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ActivityRegisterSudahDiperkasa extends AppCompatActivity {

    private TextView txt_nama, txt_alamat, txt_kota, txt_nomor, txt_nomorhp, txt_email, txt_bank
            , txt_rekening, txt_cp, txt_area, txt_tempo, txt_ktp, txt_kelurahan, txt_kecamatan, txt_digipos, txt_kategori_outlet, txt_jenis_outlet, txt_limit_order_malam, txt_limit_konsinyasi;
    private Context context;
    private DialogBox dialogBox;
    private ItemValidation iv = new ItemValidation();

    // Location
    private double latitude, longitude;
    private LocationManager locationManager;
    private Criteria criteria;
    private String provider;
    private Location location;
    private final int REQUEST_PERMISSION_COARSE_LOCATION = 2;
    private final int REQUEST_PERMISSION_FINE_LOCATION = 3;
    public boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1; // 1 minute
    private String jarak = "",range = "", latitudeOutlet = "", longitudeOutlet = "";

    //private FusedLocationProviderClient mFusedLocationClient;
    //private LocationCallback mLocationCallback;
    //private LocationRequest mLocationRequest;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    //private LocationSettingsRequest mLocationSettingsRequest;
    //private SettingsClient mSettingsClient;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private Boolean mRequestingLocationUpdates;
    private Location mCurrentLocation;
    private boolean isUpdateLocation = false;
    private String TAG = "DetailCustomer";
    public static final String flag = "DETAILCUSTOMER";
    private SessionManager session;
    private GoogleMap maps;
    private String address0 = "";
    private Button btnRefreshPosition;

    //private List<PhotoModel> listPhoto = new ArrayList<>();
    //private OverflowPagerIndicator opiPhoto;
    private RecyclerView rvPhoto;
    private ImageButton ibPhoto;
    //private PhotosAdapter adapterPhoto;
    private String imageFilePath = "";
    private File photoFile;
    private int RESULT_OK = -1;
    private int PICK_IMAGE_REQUEST = 1212;
    private final int REQUEST_IMAGE_CAPTURE = 2;
    private Uri photoURI;
    private File saveDirectory;
    private String filePathURI = "";
    private CheckBox cbTempo, cbKonsinyasi;
    private Spinner spnSegmentasi;
    private String kdcus = "", statusCustomer = "";
    private boolean isEdit = false;
    private Button btnTolak, btnSimpan;
    private RelativeLayout rlKtp;
    private Button btnKtp;
    private boolean isKtp = false;
    private ImageView ivKtp;
    private boolean isVerifikasi = false;
    private List<OptionItem> listSegmentasi = new ArrayList<>();
    private String fileKtp = "";
    private int defaultSelectedSegment = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_sudah_diperkasa);
    }
}