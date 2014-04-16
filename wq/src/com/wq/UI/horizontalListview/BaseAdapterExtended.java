package com.wq.UI.horizontalListview;

import com.wq.UI.horizontalListview.DataSetObservableExtended;
import com.wq.UI.horizontalListview.DataSetObserverExtended;
import android.widget.BaseAdapter;

public abstract class BaseAdapterExtended<T> extends BaseAdapter {

	private final DataSetObservableExtended mDataSetObservableExtended = new DataSetObservableExtended();

	public void registerDataSetObserverExtended( DataSetObserverExtended observer ) {
		mDataSetObservableExtended.registerObserver( observer );
	}

	public void unregisterDataSetObserverExtended( DataSetObserverExtended observer ) {
		mDataSetObservableExtended.unregisterObserver( observer );
	}

	/**
	 * Notifies the attached observers that the underlying data has been changed and any View reflecting the data set should refresh
	 * itself.
	 */
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		mDataSetObservableExtended.notifyChanged();
	}

	/**
	 * Notifies the attached observers that the underlying data is no longer valid or available. Once invoked this adapter is no
	 * longer valid and should not report further data set changes.
	 */
	@Override
	public void notifyDataSetInvalidated() {
		super.notifyDataSetInvalidated();
		mDataSetObservableExtended.notifyInvalidated();
	}

	public void notifyDataSetAdded( int position ) {
		mDataSetObservableExtended.notifyAdded( position );
	}

	public void notifyDataSetRemoved( int position, int viewType ) {
		mDataSetObservableExtended.notifyRemoved( position, viewType );
	}
	
	public void notifyDataSetReplaced( int position, int viewType ) {
		mDataSetObservableExtended.notifyReplaced( position, viewType );
	}

	@Override
	public abstract T getItem( int position );
}
