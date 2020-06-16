package gmedia.net.id.perkasareseller.NavTransaksi.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.FormatItem;
import com.maulana.custommodul.ItemValidation;

import java.util.List;

import gmedia.net.id.perkasareseller.R;


/**
 * Created by Shin on 1/8/2017.
 */

public class ListTransaksiAdapter extends ArrayAdapter{

    private Activity context;
    private List<CustomItem> items;
    private ItemValidation iv = new ItemValidation();

    public ListTransaksiAdapter(Activity context, List<CustomItem> items) {
        super(context, R.layout.adapter_list_transaksi, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView tvItem1, tvItem2, tvItem3, tvItem4, tvItem5, tvItem6, tvStatus, tvNo, tvJam;
        private LinearLayout llDetail, llExpiration, llKeterangan;
        private ImageView ivCollapse;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_list_transaksi, null);

            holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_item1);
            holder.tvItem2 = (TextView) convertView.findViewById(R.id.tv_item2);
            holder.tvItem3 = (TextView) convertView.findViewById(R.id.tv_item3);
            holder.tvItem4 = (TextView) convertView.findViewById(R.id.tv_item4);
            holder.tvItem5 = (TextView) convertView.findViewById(R.id.tv_item5);
            holder.tvItem6 = (TextView) convertView.findViewById(R.id.tv_item6);
            holder.tvStatus = (TextView) convertView.findViewById(R.id.tv_status);
            holder.tvJam = (TextView) convertView.findViewById(R.id.tv_jam);
            holder.tvNo = (TextView) convertView.findViewById(R.id.tv_itemNo);
            holder.ivCollapse = (ImageView) convertView.findViewById(R.id.iv_collapse);
            holder.llDetail = (LinearLayout) convertView.findViewById(R.id.ll_detail);
            holder.llKeterangan = (LinearLayout) convertView.findViewById(R.id.ll_keterangan);
            holder.llExpiration = (LinearLayout) convertView.findViewById(R.id.ll_expiration);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomItem itemSelected = items.get(position);
        holder.tvItem1.setText(itemSelected.getItem4());
        holder.tvItem2.setText(itemSelected.getItem3());
        holder.tvNo.setText(itemSelected.getItem6());
        holder.tvJam.setText(iv.ChangeFormatDateString(itemSelected.getItem2(),FormatItem.formatDate,FormatItem.formatDateDisplay)+ ("/") + itemSelected.getItem10());
        holder.tvItem3.setText(iv.ChangeToRupiahFormat(itemSelected.getItem5()));

        if(itemSelected.getItem9().toUpperCase().trim().equals("BARU")){

            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.bg_status));
        }else if(itemSelected.getItem9().toUpperCase().trim().equals("DISETUJUI")){

            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.bg_status_yellow));
        }else if(itemSelected.getItem9().toUpperCase().trim().equals("PENDING")){

            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.bg_status_cocolate));
        }else if(itemSelected.getItem9().toUpperCase().trim().equals("GAGAL")){

            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.bg_status_pink));
        }else if(itemSelected.getItem9().toUpperCase().trim().equals("PROSES")){

            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.bg_status_cyan));
        }else if(itemSelected.getItem9().toUpperCase().trim().equals("BERHASIL")){

            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.bg_status_blue));
        }else if(itemSelected.getItem9().toUpperCase().trim().equals("BATAL")){

            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.bg_status_fail));
        }else{
            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.bg_status_red));
        }

        holder.tvItem4.setText(itemSelected.getItem7());
        if(itemSelected.getItem7().isEmpty()){

            holder.llExpiration.setVisibility(View.GONE);
            holder.tvItem4.setText(itemSelected.getItem9());
        }else{
            holder.llExpiration.setVisibility(View.VISIBLE);
            holder.tvItem5.setText(iv.ChangeFormatDateString(itemSelected.getItem13(), FormatItem.formatTimestamp, FormatItem.formatDateTimeDisplay));

        }
        holder.tvStatus.setText(itemSelected.getItem9());

        if(itemSelected.getItem12().equals("")){

            holder.llKeterangan.setVisibility(View.GONE);
        }else{

            holder.llKeterangan.setVisibility(View.VISIBLE);
            holder.tvItem6.setText(itemSelected.getItem12());
        }
        final ViewHolder finalHolder = holder;
        /*holder.ivCollapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(finalHolder.llDetail.getVisibility() == View.VISIBLE){

                    finalHolder.ivCollapse.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_collapse));
                    finalHolder.llDetail.setVisibility(View.GONE);
                }else{

                    finalHolder.ivCollapse.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_expand));
                    finalHolder.llDetail.setVisibility(View.VISIBLE);
                }
            }
        });*/
        return convertView;

    }
}
