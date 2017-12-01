package com.example.queston.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import com.example.queston.models.QuestListItem;

import java.util.ArrayList;

/**
 * Created by gaverdugo on 30/11/17.
 */

public class QuestsAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<QuestListItem> quests;

    public QuestsAdapter(Context context, ArrayList<QuestListItem> quests) {
        this.context = context;
        this.quests = quests;
    }

    @Override
    public int getCount() {
        return quests.size();
    }

    @Override
    public Object getItem(int i) {
        return quests.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TwoLineListItem twoLineListItem;

        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            twoLineListItem = (TwoLineListItem) inflater.inflate(android.R.layout.simple_list_item_2, null);
        } else {
            twoLineListItem = (TwoLineListItem) view;
        }

        TextView text1 = twoLineListItem.getText1();
        TextView text2 = twoLineListItem.getText2();

        text1.setText(quests.get(i).getTitle());
        text2.setText(quests.get(i).getDescription());

        return twoLineListItem;
    }
}
