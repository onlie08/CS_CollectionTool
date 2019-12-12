package com.ch.cs_collectiontool;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class TipInfoAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public TipInfoAdapter(List<String> data) {
        super(R.layout.list_tip_item, data);
    }
    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_tip,item);
    }

}
