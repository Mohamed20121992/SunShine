package com.example.mohamed.sunshine.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by mohamed on 12/10/16.
 */

public class ProductsAdapter extends BaseAdapter {

    Context  context;
    ArrayList<Product_item>arrayList;
    public ProductsAdapter(Context context, ArrayList<Product_item> arrayList) {
        this.context=context;
        this.arrayList=arrayList;

    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if(convertView==null){

            LayoutInflater Inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView=Inflater.inflate(R.layout.products_items,null);

            holder=new Holder();
            holder.item_day = (TextView) convertView.findViewById(R.id.item_day);
            holder.item_temp_min=(TextView)convertView.findViewById(R.id.item_temp_min);
            holder.item_temp_max=(TextView)convertView.findViewById(R.id.item_temp_max);
            holder.item_image=(ImageView)convertView.findViewById(R.id.item_image);
            holder.description=(TextView)convertView.findViewById(R.id.description);


            convertView.setTag(holder);

        }
        else {
            holder = (ProductsAdapter.Holder) convertView.getTag();
        }

        final Product_item product_item=arrayList.get(position);


        SetInfo setInfo=new SetInfo(product_item,holder);
        setInfo.execute();

        return convertView;
    }
    public class Holder{
        public TextView item_temp_min;
        public TextView item_day;
        public TextView description;
        public TextView item_temp_max;
        public ImageView item_image;
    }
    class SetInfo extends AsyncTask{
        ProgressDialog progressBar;
        Product_item product_item;
        String temp_min,temp_max,day,url,description;
        Bitmap bitmap=null;
        Holder holder;
        SetInfo(Product_item product_item,Holder holder){
            this.product_item=product_item;
            this.holder=holder;

        }




        @Override
        protected Object doInBackground(Object[] params) {

            try {
               url=product_item.getUrl();
                temp_min=product_item.getTemp_min();
                temp_max=product_item.getTemp_max();
                day=product_item.getDay();
                description=product_item.getDescription();
                bitmap= BitmapFactory.decodeStream((InputStream)new URL(url).getContent());
            }catch (Exception ex){
              // Toast.makeText(context,ex.toString(),Toast.LENGTH_LONG).show();
            }

        return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            holder.item_temp_min.setText(product_item.getTemp_min());
            holder.item_day.setText(product_item.getDay());
            holder.item_temp_max.setText(product_item.getTemp_max());
            holder.description.setText(product_item.getDescription());
            if(bitmap!=null){
                holder.item_image.setImageBitmap(bitmap);

            }
        }
    }
}
