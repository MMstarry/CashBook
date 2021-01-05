/*
 * Copyright (C) 2020 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.xuexiang.qqgg.fragment.day;

import android.app.DatePickerDialog;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.xuexiang.qqgg.MyApp;
import com.xuexiang.qqgg.R;
import com.xuexiang.qqgg.adapter.base.delegate.ContentPage;
import com.xuexiang.qqgg.adapter.base.delegate.SimpleDelegateAdapter;
import com.xuexiang.qqgg.adapter.entity.Sort;
import com.xuexiang.qqgg.core.BaseFragment;
import com.xuexiang.qqgg.utils.DemoDataProvider;
import com.xuexiang.qqgg.utils.SqlLiteUtils;
import com.xuexiang.qqgg.utils.Utils;
import com.xuexiang.qqgg.utils.XToastUtils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.widget.edittext.MultiLineEditText;
import com.xuexiang.xui.widget.imageview.ImageLoader;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.shadow.ShadowTextView;
import com.xuexiang.xui.widget.tabbar.TabSegment;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;

@Page(name = "记一笔")
public class AddRecordFragment extends BaseFragment {
    @BindView(R.id.tabSegment1)
    TabSegment mTabSegment1;
    String[] pages = ContentPage.getPageNames();
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.addRecord_money)
    SuperTextView addRecord_money;
    @BindView(R.id.addRecord_date)
    SuperTextView addRecord_date;
    @BindView(R.id.addRecord_save)
    ShadowTextView addRecord_save;
    @BindView(R.id.addRecord_remarks)
    MultiLineEditText remarks;
    List<Sort> sorts0=DemoDataProvider.getAllSort0();
    List<Sort> sorts1=DemoDataProvider.getAllSort1();

    List<Sort> sorts=sorts0;

    SimpleDelegateAdapter<Sort> commonAdapter;
    int income=0;

    String mMonth;

    int img;
    String type;

    Calendar calendar = Calendar.getInstance();
    private SqlLiteUtils sqlLiteUtils;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_add_record;
    }


    @Override
    protected void initViews() {
        sqlLiteUtils=((MyApp) Objects.requireNonNull(this.getContext()).getApplicationContext()).getSqlLiteUtils();



        addRecord_date.setCenterString(calendar.get(Calendar.YEAR)+"-"+Utils.autoGenericCode(calendar.get(Calendar.MONTH)+1,2)+"-"+Utils.autoGenericCode(calendar.get(Calendar.DATE),2)+" "+Utils.autoGenericCode(calendar.get(Calendar.HOUR_OF_DAY),2)+":"+Utils.autoGenericCode(calendar.get(Calendar.MINUTE),2));
        mMonth=String.valueOf(calendar.get(Calendar.MONTH)+1);
        addRecord_date.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClick(SuperTextView superTextView) {
                new DatePickerDialog(getContext()
                        , DatePickerDialog.THEME_DEVICE_DEFAULT_LIGHT
                        , (view, year, monthOfYear, dayOfMonth) -> setDate(year, monthOfYear, dayOfMonth,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE))
                        // 设置初始日期
                        , Calendar.getInstance().get(Calendar.YEAR)
                        , Calendar.getInstance().get(Calendar.MONTH)
                        , Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(getContext());
        recyclerView.setLayoutManager(virtualLayoutManager);
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        recyclerView.setRecycledViewPool(viewPool);
        viewPool.setMaxRecycledViews(0, 10);

        //九宫格菜单
        GridLayoutHelper gridLayoutHelper = new GridLayoutHelper(8);
        gridLayoutHelper.setPadding(0, 16, 0, 0);
        gridLayoutHelper.setVGap(10);
        gridLayoutHelper.setHGap(0);

        commonAdapter = new SimpleDelegateAdapter<Sort>(R.layout.adapter_common_grid_item, gridLayoutHelper, sorts) {
            @Override
            protected void bindData(@NonNull RecyclerViewHolder holder, int position, Sort item) {
                if (item != null) {
                    RadiusImageView imageView = holder.findViewById(R.id.riv_item);
                    imageView.setCircle(false);
                    ImageLoader.get().loadImage(imageView, item.getImg());

                    holder.text(R.id.tv_sub_title, item.getName());

                    holder.click(R.id.riv_item,view -> changeDo(item));

                }
            }
        };
        DelegateAdapter delegateAdapter = new DelegateAdapter(virtualLayoutManager);

        initNoViewPagerTabSegment();
        delegateAdapter.addAdapter(commonAdapter);
        changeDo(sorts.get(0));
        recyclerView.setAdapter(delegateAdapter);

        remarks.setHintText("备注");
        remarks.setMaxCount(80);

        addRecord_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String money=addRecord_money.getCenterEditValue();
                if(money.equals("")){
                    XToastUtils.error("请填写金额");
                }else {
                    String[] key = {"money","remarks","sortType","sortImage","crateTime","income","mMonth"};
                    String[] values = {money,remarks.getContentText(),type,img+"",addRecord_date.getCenterTextView().getText().toString()+":00",income+"",mMonth};
                    sqlLiteUtils.addData("money",key,values);
                    popToBack();
                }



            }
        });

    }

    private void setDate(int year, int monthOfYear, int dayOfMonth, int i, int i1) {
        addRecord_date.setCenterString(year+"-"+Utils.autoGenericCode(monthOfYear+1,2)+"-"+Utils.autoGenericCode(dayOfMonth,2)+" "+Utils.autoGenericCode(i,2)+":"+Utils.autoGenericCode(i1,2));
        mMonth=String.valueOf(monthOfYear+1);
    }



    public void changeDo(Sort item){
        img=item.getImg();
        type=item.getName();
        addRecord_money.setLeftIcon(img);
        addRecord_money.setLeftString(type);

    }

    public void xx(int index){
        if(index==0){
            income=0;
            sorts=sorts0;

        }else {
            income=1;
            sorts=sorts1;
        }

        changeDo(sorts.get(0));
        commonAdapter.refresh(sorts);
    }

    /**
     * 不使用ViewPager的情况
     */
    private void initNoViewPagerTabSegment() {
        for (String page : pages) {
            mTabSegment1.addTab(new TabSegment.Tab(page));
        }
        mTabSegment1.setMode(TabSegment.MODE_FIXED);
        mTabSegment1.setOnTabClickListener(index -> xx(index));
        //不使用ViewPager的话，必须notifyDataChanged，否则不能正常显示
        mTabSegment1.notifyDataChanged();
        mTabSegment1.selectTab(0);
        mTabSegment1.addOnTabSelectedListener(new TabSegment.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int index) {
                if (mTabSegment1 != null) {
                    mTabSegment1.hideSignCountView(index);
                }
            }

            @Override
            public void onTabUnselected(int index) {

            }

            @Override
            public void onTabReselected(int index) {
                if (mTabSegment1 != null) {
                    mTabSegment1.hideSignCountView(index);
                }
            }

            @Override
            public void onDoubleTap(int index) {

            }
        });

    }
}
