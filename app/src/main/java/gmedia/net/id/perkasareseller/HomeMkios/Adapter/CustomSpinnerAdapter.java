package gmedia.net.id.perkasareseller.HomeMkios.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.maulana.custommodul.OptionItem;

import java.util.List;

import gmedia.net.id.perkasareseller.R;

public class CustomSpinnerAdapter extends ArrayAdapter<OptionItem> {

    LayoutInflater flater;

    public CustomSpinnerAdapter(Activity context, int resouceId, List<OptionItem> list){

        super(context,resouceId, list);
//        flater = context.getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return rowview(convertView,position);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return rowview(convertView,position);
    }

    private View rowview(View convertView , int position){

        OptionItem rowItem = getItem(position);

        viewHolder holder ;
        View rowview = convertView;
        if (rowview==null) {

            holder = new viewHolder();
            flater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowview = flater.inflate(R.layout.adapter_bank, null, false);

            holder.tvItem1 = (TextView) rowview.findViewById(R.id.tv_item1);
            holder.tvItem2 = (TextView) rowview.findViewById(R.id.tv_item2);

            rowview.setTag(holder);
        }else{
            holder = (viewHolder) rowview.getTag();
        }

        holder.tvItem1.setText(rowItem.getText());
        holder.tvItem2.setText(rowItem.getAtt1());

        return rowview;
    }

    private class viewHolder{
        TextView tvItem1, tvItem2;
    }
}


