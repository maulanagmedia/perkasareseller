package gmedia.net.id.perkasareseller.Utils;

/**
 * Created by Shinmaul on 1/25/2018.
 */

public class ServerURL {

    private static final String baseURL = "https://myperkasa.com/api/reseller/";
    //private static final String baseURL = "https://192.168.20.34:8062/";

    //private static final String baseURL = "http://119.2.53.122/mobilesalesforce/apiresv1/";
    //private static final String baseURL = "https://api.putmasaripratama.co.id/apiresv1/";
    //private static final String baseURL = "http://api.putmasaripratama.co.id/apiresv1/";

    public static final String login = baseURL + "Authentication/";
    public static final String getMenu = baseURL + "Authentication/get_menu/";
    public static final String register = baseURL + "Authentication/register/";
    public static final String getOTP = baseURL + "Authentication/kirim_otp/";
    public static final String resetPassword = baseURL + "Authentication/reset_password/";
    public static final String changePassword = baseURL + "Authentication/change_password/";
    public static final String getPromosi = baseURL + "Promo/";
    public static final String uploadFileURL = baseURL + "chat/send_file/";

    public static final String getChat = baseURL + "chat/";
    public static final String saveChat = baseURL + "chat/send_text/";
    public static final String deleteChat = baseURL + "chat/delele_chat/";

    public static final String getProfile = baseURL + "profile/view_profile/";
    public static final String getNews = baseURL + "Promosi/get_news";
    public static final String getNomor = baseURL + "profile/master_nomor/";
    public static final String getDenom = baseURL + "profile/master_harga/";
    public static final String ubahPin = baseURL + "profile/ubah_pin/";
    public static final String priceList = baseURL + "Perdana/get_perdana";
    public static final String priceListBaru = baseURL + "Perdana/get_perdana_group";
    public static final String priceListPerkode = baseURL + "Perdana/get_perdana_group_detail";
    public static final String getPSPInformastion = baseURL + "Profile/view_customer_service/";
    public static final String getTotalDeposit = baseURL + "profile/total_deposite/";
    public static final String saveInfoStok = baseURL + "Profile/simpan_stok/";
    public static final String getInfoStok = baseURL + "Profile/view_stok/";
    public static final String getSavedPin = baseURL + "Profile/view_pin/";
    public static final String savePinFlag = baseURL + "Profile/simpan_pin/";
    public static final String getPhonebook = baseURL + "Profile/view_phonebook/";

    public static final String beliMkios = baseURL + "Mkios/beli_mkios/";
    public static final String beliBulk = baseURL + "Mkios/beli_bulk/";
    public static final String beliTcash = baseURL + "Mkios/beli_tcash/";
    public static final String beliNGRS = baseURL + "Mkios/beli_ngrs/";
    public static final String beliSaldoTunai= baseURL + "Mkios/beli_deposit/";
    public static final String getHarga = baseURL + "mkios/get_harga/";
    public static final String viewTransaksi = baseURL + "Mkios/view_transaksi/";
    public static final String viewHistory = baseURL + "mkios/view_history/";
    public static final String topUpDeposit = baseURL + "Mkios/deposite_topup";
    public static final String viewDeposit = baseURL + "Mkios/deposite_saldo/";
    public static final String viewHistoryDeposit = baseURL + "Deposite/view_history/";
    public static final String getBarangDS = baseURL + "Direct_Sale/view_barang/";
    public static final String saveReplyDS = baseURL + "Direct_Sale/save_reply/";
    public static final String saveOrderDS = baseURL + "Direct_Sale/direct_order/";
    public static final String saveDSPerdana = baseURL + "Direct_Sale/jual_perdana/";
    public static final String checkSaldo = baseURL + "Profile/total_deposite/";
    //Mkios/cek_saldo
    public static final String getLatestVersion = baseURL + "Version/";
    public static final String getBukuPintar = baseURL + "Buku/";
    public static final String beliPerdana = baseURL + "Perdana/pre_order/";
    public static final String beliPerdanaGroup = baseURL + "Perdana/pre_order_group/";
    public static final String getCCID = baseURL + "Direct_Sale/scan_perdana/";
    public static final String getKategoriPPOB = baseURL + "Ppob/kategori/";
    public static final String getProdukPPOB = baseURL + "Ppob/produk/";
    public static final String payPPBOB = baseURL + "Ppob/transaksi/";
    public static final String saveHargaPPOB = baseURL + "Ppob/add_harga_custom/";
    public static final String getProvider = baseURL + "Ppob/provider/";
    public static final String getBankBayar = baseURL + "Bank/";
    public static final String getQR = baseURL + "Perdana/get_qr/";
    public static final String cetakNotaPerdana = baseURL + "Mkios/cetak_pembelian_perdana/";
    public static final String cetakNotaNgrs = baseURL + "Mkios/cetak_pembelian_ngrs/";

    //public static final String uploadFileURL = "http://192.168.12.147/psp/testupload/upload.php";
}
