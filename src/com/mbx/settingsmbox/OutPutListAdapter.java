package com.mbx.settingsmbox;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class OutPutListAdapter extends BaseAdapter {
    private String TAG = "OutPutListAdapter";
    private Context  mContext = null;
    private ArrayList<String> mModeList= null;
    private int mCurrentSelectId = -1;

    private static final String[] MODE_TITLE = { "       ",
			"        ", "        ", "        ", "1280*720", "1920*1080",
			"1920*1080", "1280*720", "1920*1080", "1920*1080"};
    
    public OutPutListAdapter(Context context , ArrayList<String> list , int curID){
        mContext = context ;
        mModeList = list;
        mCurrentSelectId= curID;
    }

    private class OutPutModeHolder {
        TextView title = null;
        TextView value = null;
        ImageView checkImage = null;
    }

	public void setSelectItem(int index) {
    	mCurrentSelectId = index;
    	notifyDataSetChanged();
    }

	@Override
	public int getCount() {
		if(mModeList==null)
            return 0;
        else
		    return mModeList.size();
	}

	@Override
	public Object getItem(int index) {
		if(mModeList==null)
            return null;
        else
		    return mModeList.get(index);
	}

	@Override
	public long getItemId(int arg0) {
		
		return 0;
	}

	@Override
	public View getView(int position, View contentview, ViewGroup parent) {
            OutPutModeHolder holder = new OutPutModeHolder();
            Log.d(TAG,"===== getView, index : " + position);
			LayoutInflater listContainer;
			listContainer = LayoutInflater.from(mContext);
			contentview = listContainer.inflate(R.layout.output_item, null);         
			holder.title = (TextView) contentview.findViewById(R.id.title);
			holder.checkImage = (ImageView) contentview.findViewById(R.id.check);
            holder.title.setText(mModeList.get(position));
            holder.value = (TextView) contentview.findViewById(R.id.value);
		    //holder.value.setText(MODE_TITLE[position]);
			if (position == mCurrentSelectId) {
				holder.checkImage.setVisibility(View.VISIBLE);
			} else {
				holder.checkImage.setVisibility(View.INVISIBLE);
			}

			return contentview;
	}

}
