package com.ch.cs_collectiontool.adapter;

import com.ch.cs_collectiontool.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class SpinnerListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public SpinnerListAdapter(List<String> data) {
        super(R.layout.spinner_list_item, data);
    }
    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_spinner_value,item);
    }

}
