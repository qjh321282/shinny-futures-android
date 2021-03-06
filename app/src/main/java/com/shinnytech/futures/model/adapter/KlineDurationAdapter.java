package com.shinnytech.futures.model.adapter;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.shinnytech.futures.R;
import com.shinnytech.futures.application.BaseApplication;
import com.shinnytech.futures.constants.SettingConstants;
import com.shinnytech.futures.databinding.ItemKlineDurationBinding;
import com.shinnytech.futures.model.listener.ItemTouchHelperListener;
import com.shinnytech.futures.utils.SPUtils;
import com.shinnytech.futures.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * date: 7/9/17
 * author: chenli
 * description: 设置页周期列表适配器
 * version:
 * state: done
 */
public class KlineDurationAdapter extends RecyclerView.Adapter<KlineDurationAdapter.ItemViewHolder> implements ItemTouchHelperListener {
    private Context sContext;
    private List<String> mData = new ArrayList<>();
    private ItemTouchHelper itemTouchHelper;

    public KlineDurationAdapter(Context context, List<String> data) {
        this.sContext = context;
        this.mData.addAll(data);
    }

    public void updateList(List<String> data) {
        this.mData.clear();
        this.mData.addAll(data);
        notifyDataSetChanged();
    }

    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper;
    }

    public void saveDurationList() {
        String data = TextUtils.join(",", mData);
        SPUtils.putAndApply(BaseApplication.getContext(), SettingConstants.CONFIG_KLINE_DURATION_DEFAULT, data);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        String ins = mData.remove(fromPosition);
        mData.add(toPosition, ins);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemKlineDurationBinding binding = DataBindingUtil.inflate(LayoutInflater
                .from(sContext), R.layout.item_kline_duration, parent, false);
        ItemViewHolder holder = new ItemViewHolder(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        holder.update();
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private ItemKlineDurationBinding mBinding;

        public ItemViewHolder(View itemView) {
            super(itemView);
        }

        public ItemKlineDurationBinding getBinding() {
            return this.mBinding;
        }

        public void setBinding(ItemKlineDurationBinding binding) {
            this.mBinding = binding;
        }

        public void update() {
            if (mData == null || mData.size() == 0) return;
            String duration = mData.get(getLayoutPosition());
            if (duration.isEmpty()) return;
            mBinding.tvIdDialog.setText(duration);

            mBinding.ivDrag.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    try {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            if (itemTouchHelper != null)
                                itemTouchHelper.startDrag(ItemViewHolder.this);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            });

            mBinding.ivTop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int index = getLayoutPosition();
                        if (index >= 0 && index < getItemCount()) {
                            onItemMove(index, 0);
                            saveDurationList();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            mBinding.ivCut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int index = getLayoutPosition();
                        if (index == 0 && getItemCount() == 1){
                            ToastUtils.showToast(sContext, "至少保留一个周期");
                            return;
                        }
                        if (index >= 0 && index < getItemCount()) {
                            mData.remove(index);
                            notifyItemRemoved(index);
                            saveDurationList();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }
}
