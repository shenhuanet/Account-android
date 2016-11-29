package com.shenhua.account;

import com.shenhua.account.dao.MyDataBase;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

public class Fragment_History extends Fragment {

	private int[] group_checked = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	private View view;
	private ExpandableListView e_list;
	private TextView tvEmpty;
	private MyDataBase dataBase;
	private MyCursrTreeAdapter myCursorTreeAdapter;
	private static final int groupName_index = 1;
	private static final int time_index = 3;
	private static final int type_index = 1;
	private static final int money_index = 2;
	private static final int mark_index = 6;
	int groupNameIndex;
	String mygroupName;
	Cursor groupCursor;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.content_history, container, false);
			tvEmpty = (TextView) view.findViewById(R.id.history_list_empty);
			e_list = (ExpandableListView) view.findViewById(R.id.history_list);
			e_list.setGroupIndicator(null);
			e_list.setEmptyView(tvEmpty);
			dataBase = new MyDataBase(getContext());
			dataBase.open();
		}
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}
		initAdapterView();
		return view;
	}

	public class MyCursrTreeAdapter extends CursorTreeAdapter {

		public MyCursrTreeAdapter(Cursor cursor, Context context,
				boolean autoRequery) {
			super(cursor, context, autoRequery);
		}

		@Override
		protected void bindGroupView(View view, Context context, Cursor cursor,
				boolean isExpanded) {
			// TODO bindGroupView
			TextView group_title = (TextView) view
					.findViewById(R.id.group_title);
			String group = cursor.getString(groupName_index);
			group_title.setText(group);
			TextView groupCount = (TextView) view
					.findViewById(R.id.group_count);
			int count = dataBase.getCount(3, "accounts", group);
			groupCount.setText("[" + count + "]");
			ImageView group_state = (ImageView) view
					.findViewById(R.id.group_state);
			if (isExpanded) {
				group_state.setBackgroundResource(R.drawable.group_up);
			} else {
				group_state.setBackgroundResource(R.drawable.group_down);
			}
		}

		@Override
		protected View newGroupView(Context context, Cursor cursor,
				boolean isExpanded, ViewGroup parent) {
			LayoutInflater inflate = LayoutInflater.from(getContext());
			View view = inflate.inflate(
					R.layout.content_history_e_list_parent_item, parent, false);

			bindGroupView(view, context, cursor, isExpanded);

			return view;
		}

		@Override
		protected Cursor getChildrenCursor(Cursor groupCursor) {
			String groupName = groupCursor.getString(groupName_index);
			Cursor childCursor = dataBase.getAccountByGroups(1, groupName);
			return childCursor;
		}

		@Override
		protected View newChildView(Context context, Cursor cursor,
				boolean isLastChild, ViewGroup parent) {
			LayoutInflater inflate = LayoutInflater.from(getContext());
			View view = inflate.inflate(
					R.layout.content_history_e_list_child_item, parent, false);
			bindChildView(view, context, cursor, isLastChild);
			return view;
		}

		@Override
		protected void bindChildView(View view, Context context, Cursor cursor,
				boolean isLastChild) {
			// TODO bindChildView
			TextView time = (TextView) view.findViewById(R.id.child_text_time);
			time.setText(cursor.getString(time_index));
			TextView type = (TextView) view.findViewById(R.id.child_text_type);
			type.setTextKeepState(cursor.getString(type_index));
			TextView money = (TextView) view
					.findViewById(R.id.child_text_money);
			money.setTextKeepState(cursor.getString(money_index) + " "
					+ getString(R.string.rmb));
			TextView mark = (TextView) view.findViewById(R.id.child_text_mark);
			mark.setTextKeepState(cursor.getString(mark_index));
		}
	}

	private void initAdapterView() {
		// TODO initAdapterView
		groupCursor = dataBase.getAccountByGroups(0, "");
		// getActivity().startManagingCursor(groupCursor);
		groupNameIndex = groupCursor.getColumnIndexOrThrow("_month");
		myCursorTreeAdapter = new MyCursrTreeAdapter(groupCursor, getContext(),
				true);
		e_list.setAdapter(myCursorTreeAdapter);
		e_list.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				group_checked[groupPosition] = group_checked[groupPosition] + 1;
				((BaseExpandableListAdapter) myCursorTreeAdapter)
						.notifyDataSetChanged();
				return false;
			}
		});
		e_list.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {

				((BaseExpandableListAdapter) myCursorTreeAdapter)
						.notifyDataSetChanged();
				return false;
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		groupCursor.close();
		dataBase.close();
	}

}
