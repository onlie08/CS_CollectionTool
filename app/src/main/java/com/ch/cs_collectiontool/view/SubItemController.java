package com.ch.cs_collectiontool.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ch.cs_collectiontool.R;
import com.ch.cs_collectiontool.adapter.SubRoomAdapter;
import com.ch.cs_collectiontool.base.ViewController;
import com.ch.cs_collectiontool.bean.Room;
import com.ch.cs_collectiontool.bean.SubRoom;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubItemController extends ViewController<List<SubRoom>> {
    SubRoomAdapter subRoomAdapter;
    @BindView(R.id.recycler_subitem)
    RecyclerView recyclerSubitem;

    public interface OnDeleteListener{
        void delete();
        void setOwnerName(int pos,String ownerName);
    }

    OnDeleteListener listener;

    public void setListener(OnDeleteListener listener) {
        this.listener = listener;
    }

    public SubItemController(Context context) {
        super(context);
    }

    @Override
    protected int resLayoutId() {
        return R.layout.subitem_list;
    }

    @Override
    protected void onCreatedView(View view) {
        ButterKnife.bind(this, view);
        initView();
    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerSubitem.setLayoutManager(layoutManager);
    }

    @Override
    protected void onBindView(List<SubRoom> data) {
        subRoomAdapter = new SubRoomAdapter(data);
        recyclerSubitem.setAdapter(subRoomAdapter);
        subRoomAdapter.setListener(new SubRoomAdapter.OnDeleteListener() {
            @Override
            public void delete() {
                listener.delete();
            }

            @Override
            public void setOwnerName(int pos, String ownerName) {
                listener.setOwnerName(pos,ownerName);
            }
        });
    }
}
