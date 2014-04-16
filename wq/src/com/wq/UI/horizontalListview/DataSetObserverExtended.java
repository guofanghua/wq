package com.wq.UI.horizontalListview;

import android.database.DataSetObserver;

public abstract class DataSetObserverExtended extends DataSetObserver {

	public void onAdded( int position ) {}

	public void onRemoved( int position, int viewType ) {}
	
	public void onReplaced( int position, int viewType ) {}
}
