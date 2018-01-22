package com.example.sacoappversion2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by gestevez76 on 5/18/2017.
 */

public class ExpenseAdapter extends ArrayAdapter<String>{
    ArrayList<String> names;
    ArrayList<String> flags;
    Context mContext;

    public ExpenseAdapter(Context context, ArrayList<String> countryNames, ArrayList<String> countryFlags){
        super(context, R.layout.expenselist_layout);
        this.names = countryNames;
        this.flags = countryFlags;
        this.mContext = context;
    }

    @Override
    public int getCount(){
        return super.getCount();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder mViewHolder = new ViewHolder();
        if(convertView==null) {
            LayoutInflater mInflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflator.inflate(R.layout.expenselist_layout, parent, false);
            mViewHolder.mFlag = (TextView) convertView.findViewById(R.id.tvName);
            mViewHolder.mName = (TextView) convertView.findViewById(R.id.tvAmount);
            convertView.setTag(mViewHolder);
        }else{
            mViewHolder = (ViewHolder)convertView.getTag();
        }
            mViewHolder.mFlag.setText(flags.get(position));
            mViewHolder.mName.setText(names.get(position));

        return convertView;
    }

    static class ViewHolder{
        TextView mFlag, mName;

    }
}
