package com.google.code.trafficdroid.trattelist;

import java.util.ArrayList;
import java.util.List;

import com.google.code.trafficdroid.data.Tratta;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class TrattaListAdapter extends BaseAdapter
{
	/** Remember our context so we can use it when constructing views. */
	private Context mContext;
	
	private List<Tratta> mItems = new ArrayList<Tratta>();
	
	public TrattaListAdapter(Context context)
	{
		mContext = context;
	}
	
	public void addItem(Tratta it)
	{
		mItems.add(it);
	}
	
	public void setListItems(List<Tratta> lit)
	{
		mItems = lit;
	}
	
	/** @return The number of items in the */
	public int getCount()
	{
		return mItems.size();
	}
	
	public Object getItem(int position)
	{
		return mItems.get(position);
	}
	
	public boolean areAllItemsSelectable()
	{
		return false;
	}
	
	/** Use the array index as a unique id. */
	public long getItemId(int position)
	{
		return position;
	}
	
	/**
	 * @param convertView The old view to overwrite, if one is passed
	 * @returns a IconifiedTextView that holds wraps around an IconifiedText
	 */
	public View getView(int position, View convertView, ViewGroup parent)
	{
		TrattaView btv;
		if (convertView == null)
		{
			btv = new TrattaView(mContext, mItems.get(position));
		}
		else
		{
			// Reuse/Overwrite the View passed
			// We are assuming(!) that it is castable!
			btv = (TrattaView) convertView;
			btv.setTratta(mItems.get(position).getTratta());
			btv.setVelocitaSx(mItems.get(position).getVelocitaSx());
			btv.setVelocitaDx(mItems.get(position).getVelocitaDx());
		}
		return btv;
	}
}