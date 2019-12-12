package com.ch.cs_collectiontool.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.ch.cs_collectiontool.R;
import com.ch.cs_collectiontool.adapter.SpinnerListAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

public class SpinnerController {
    private Context mContext;
    private SpinnerListener listener;

    public void setListener(SpinnerListener listener) {
        this.listener = listener;
    }

    public interface SpinnerListener{
        void selectResult(String date);
    }

    public SpinnerController(Context mContext) {
        this.mContext = mContext;
    }

    public void showSpinnerDialog(final List<String> strings){
        AlertDialog.Builder builder = new AlertDialog.Builder((Activity)mContext);
        View view = View
                .inflate((Activity)mContext, R.layout.spinner_view, null);

        RecyclerView recycler_spinner = view.findViewById(R.id.recycler_spinner);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_spinner.setLayoutManager(layoutManager);

        SpinnerListAdapter spinnerListAdapter = new SpinnerListAdapter(strings);
        recycler_spinner.setAdapter(spinnerListAdapter);

        builder.setView(view);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setLayout(dp2px(200f), LinearLayout.LayoutParams.WRAP_CONTENT);
        spinnerListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                listener.selectResult(strings.get(position));
                dialog.dismiss();
            }
        });

    }

    public int dp2px(float dpValue) {
        return (int) (0.5f + dpValue * Resources.getSystem().getDisplayMetrics().density);
    }
}
