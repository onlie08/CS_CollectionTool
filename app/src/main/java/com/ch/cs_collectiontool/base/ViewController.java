package com.ch.cs_collectiontool.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class ViewController<T> {
    private T mData;
    private Context mContext;
    private View mView;

    public ViewController(Context context) {
        this.mContext = context;
    }

    public void attachRoot(ViewGroup root) {
        initView(root);
        root.addView(mView);
        onCreatedView(mView);
    }

    public void attachRoot(ViewGroup root, int index) {
        initView(root);
        root.addView(mView, index);
        onCreatedView(mView);
    }

    public void attachRoot(ViewGroup root, int width, int height) {
        initView(root);
        root.addView(mView, width, height);
        onCreatedView(mView);
    }

    public void attachRoot(ViewGroup root, ViewGroup.LayoutParams params) {
        initView(root);
        root.addView(mView, params);
        onCreatedView(mView);
    }


    public void attachRoot(ViewGroup root, int index, ViewGroup.LayoutParams params) {
        initView(root);
        root.addView(mView, index, params);
        onCreatedView(mView);
    }

    private void initView(ViewGroup root) {
        int resLayoutId = resLayoutId();
        if (resLayoutId <= 0) {
            throw new IllegalStateException("Please check your layout id in resLayoutId() method");
        }
        if (mView != null) {
            throw new IllegalStateException("a viewController can't attachRoot twice");
        }
        mView = LayoutInflater.from(mContext).inflate(resLayoutId, root, false);
    }

    public void fillData(T data) {
        this.mData = data;
        if (mData != null) {
            onBindView(data);
        }
    }

    public void detachedRoot() {
        onDestoryView(mView);
    }

    /**
     * indicate layout id of this ViewControl
     *
     * @return layout id
     */
    protected abstract int resLayoutId();

    /**
     * view has been created
     *
     * @param view real view
     */
    protected abstract void onCreatedView(View view);

    /**
     * bind data to view
     *
     * @param data data
     */
    protected abstract void onBindView(T data);

    /**
     * view has been Destory
     *
     * @param view
     */
    protected void onDestoryView(View view) {

    }

    public Context getContext() {
        return mContext;
    }

    public View getView() {
        return mView;
    }

    public T getData() {
        return mData;
    }

}
