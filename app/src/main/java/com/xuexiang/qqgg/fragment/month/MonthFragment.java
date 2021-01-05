/*
 * Copyright (C) 2019 xuexiangjys(xuexiangjys@163.com)
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

package com.xuexiang.qqgg.fragment.month;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.xuexiang.qqgg.MyApp;
import com.xuexiang.qqgg.R;
import com.xuexiang.qqgg.adapter.base.delegate.ContentPage;
import com.xuexiang.qqgg.adapter.base.delegate.SimpleDelegateAdapter;
import com.xuexiang.qqgg.adapter.entity.DayMoney;
import com.xuexiang.qqgg.adapter.entity.MultiQuery;
import com.xuexiang.qqgg.core.BaseChartFragment;
import com.xuexiang.qqgg.fragment.AboutFragment;
import com.xuexiang.qqgg.utils.SqlLiteUtils;
import com.xuexiang.qqgg.utils.Utils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.picker.widget.TimePickerView;
import com.xuexiang.xui.widget.picker.widget.builder.TimePickerBuilder;
import com.xuexiang.xui.widget.tabbar.TabSegment;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;
import com.xuexiang.xutil.data.DateUtils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import butterknife.BindView;

/**
 * 月
 * @author xuexiang
 * @since 2019-10-30 00:19
 */
@Page(anim = CoreAnim.none)
public class MonthFragment extends BaseChartFragment implements OnChartValueSelectedListener {

    @BindView(R.id.tabSegment1)
    TabSegment mTabSegment1;
    String[] pages = ContentPage.getPageNames();
    private TimePickerView mDatePicker;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private SqlLiteUtils sqlLiteUtils;
    private DelegateAdapter mDelegateAdapter;
    private List<DelegateAdapter.Adapter> mAdapters = new ArrayList<>();

    private SimpleDelegateAdapter<DayMoney> mMonthMoneyAdapter;
    @BindView(R.id.month_calender)
    RadiusImageView month_calender;

    @BindView(R.id.about)
    RadiusImageView about;
    @BindView(R.id.month_date)
    TextView month_date;
    @BindView(R.id.month_income)
    TextView month_income;
    @BindView(R.id.month_pay)
    TextView month_pay;
    @BindView(R.id.month_balance)
    TextView month_balance;

    @BindView(R.id.month_type)
    SuperTextView month_type;

    String startTime;
    String endTime;
    Calendar calendar = Calendar.getInstance();
    String date1;

    BigDecimal inMoney=new BigDecimal(0);
    BigDecimal outMoney=new BigDecimal(0);

    int index=0;


    @BindView(R.id.chart1)
    PieChart chart;

    List<PieEntry> entries;

    List<DayMoney> moneyList=new ArrayList<>();

    List<DayMoney> moneyAllList=new ArrayList<>();


    /**
     * 切换刷新
     */
    protected boolean isCreated = false;

    /**
     * @return 返回为 null意为不需要导航栏
     */
    @Override
    protected TitleBar initTitle() {
        return null;
    }

    /**
     * 布局的资源id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_month;
    }


    /**
     * 此方法目前仅适用于标示ViewPager中的Fragment是否真实可见
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (!isCreated) {
            return;
        }

        if (isVisibleToUser) {
            initListeners();
        }
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        // 标记
        isCreated = true;
        sqlLiteUtils=((MyApp) Objects.requireNonNull(this.getContext()).getApplicationContext()).getSqlLiteUtils();
        first();
        // 1.设置VirtualLayoutManager
        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(getContext());
        recyclerView.setLayoutManager(virtualLayoutManager);

        // 2.设置RecycledViews复用池大小（可选）
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        viewPool.setMaxRecycledViews(0, 20);
        recyclerView.setRecycledViewPool(viewPool);

        //当yue消费列表
        mMonthMoneyAdapter = new SimpleDelegateAdapter<DayMoney>(R.layout.adapter_day_card_view_list_item, new LinearLayoutHelper()) {
            @Override
            protected void bindData(@NonNull RecyclerViewHolder holder, int position, DayMoney model) {
                if (model != null) {

                    holder.image(R.id.dayMoney_sortImage,model.getSortImage());
                    holder.text(R.id.dayMoney_sortType,model.getSortType());
                    holder.text(R.id.dayMoney_remarks,model.getRemarks());
                    if(model.getIncome()==0){
                        holder.text(R.id.dayMoney_money,"-"+model.getMoney());
                    }else {
                        holder.text(R.id.dayMoney_money,"+"+model.getMoney());
                    }
                    holder.text(R.id.dayMoney_createTime,model.getCrateTime().substring(0,10));

                }
            }
        };

        mDelegateAdapter = new DelegateAdapter(virtualLayoutManager);

        mAdapters.add(mMonthMoneyAdapter);

        // 3.设置DelegateAdapter
        recyclerView.setAdapter(mDelegateAdapter);

        initNoViewPagerTabSegment();

        month_calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choseDate();
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewPage(AboutFragment.class);
            }
        });

        month_date.setText(date1);

        initChartStyle();
        initChartLabel();
        setChartData();

        chart.animateY(1400, Easing.EaseInOutQuad);
        chart.setOnChartValueSelectedListener(this);

    }

    public void xx(int i){
        index=i;

        List<DayMoney> list=new ArrayList<>();
        for(DayMoney dayMoney:moneyAllList){
            if(dayMoney.getIncome()==index){
                list.add(dayMoney);
            }
        }

        moneyList=list;

        mMonthMoneyAdapter.refresh(moneyList);
        initChartStyle();
        setChartData();

        if(moneyList.size()==0){
            month_type.setVisibility(View.GONE);
        }else {
            month_type.setVisibility(View.VISIBLE);
            month_type.setLeftIcon(R.drawable.ic_login_close);
            month_type.setLeftString("总分类");
            month_type.setCenterString("共"+moneyList.size()+"个记录");
        }


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

    public void choseDate(){
        if (mDatePicker == null) {
            mDatePicker = new TimePickerBuilder(getContext(), (date, v) -> choseDateDetail(DateUtils.date2String(date, yyyyMM.get())))
                    .setTimeSelectChangeListener(date -> Log.i("pvTime", "onTimeSelectChanged"))
                    .setType(new boolean[]{true, true, false, false, false, false})
                    .setTitleText("年月选择")
                    .build();
        }
        mDatePicker.show();

    }

    private void choseDateDetail(String yearAndMonth) {
        String[] s =  yearAndMonth.split("-");
        int year=Integer.parseInt(s[0]);
        int month=Integer.parseInt(s[1]);

        date1=year+"年"+ month+"月";
        startTime=year+"-"+Utils.autoGenericCode(month,2)+"-01 00:00:00";
        if(month==12){
            int year1=year+1;
            endTime=year1+"-"+Utils.autoGenericCode(1,2)+"-01 00:00:00";
        }else {
            endTime=year+"-"+Utils.autoGenericCode(month+1,2)+"-01 00:00:00";
        }

        month_date.setText(date1);
        initListeners();

    }

    public void first(){
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        date1=year+"年"+ month+"月";
        startTime=year+"-"+Utils.autoGenericCode(month,2)+"-01 00:00:00";
        if(month==12){
            int year1=year+1;
            endTime=year1+"-"+Utils.autoGenericCode(1,2)+"-01 00:00:00";
        }else {
            endTime=year+"-"+Utils.autoGenericCode(month+1,2)+"-01 00:00:00";
        }

    }

    @Override
    protected void initListeners() {
        query();
        mDelegateAdapter.setAdapters(mAdapters);

        List<DayMoney> list=new ArrayList<>();
        for(DayMoney dayMoney:moneyAllList){
            if(dayMoney.getIncome()==index){
                list.add(dayMoney);
            }
        }

        moneyList=list;

        mMonthMoneyAdapter.refresh(moneyList);
        initChartStyle();
        setChartData();
        if(moneyList.size()==0){
            month_type.setVisibility(View.GONE);
        }else {
            month_type.setVisibility(View.VISIBLE);
            month_type.setLeftIcon(R.drawable.ic_login_close);
            month_type.setLeftString("总分类");
            month_type.setCenterString("共"+moneyList.size()+"个记录");
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        initListeners();
    }

    public void query(){
        inMoney=new BigDecimal(0);
        outMoney=new BigDecimal(0);
        MultiQuery multiQuery=new MultiQuery();

        multiQuery.setStartTime(startTime);
        multiQuery.setEndTime(endTime);
        moneyAllList=sqlLiteUtils.query(multiQuery);
        for(DayMoney dayMoney:moneyAllList){
            if(dayMoney.getIncome()==0){
                outMoney=outMoney.add(new BigDecimal(Float.toString(dayMoney.getMoney())));
            }else {
                inMoney=inMoney.add(new BigDecimal(Float.toString(dayMoney.getMoney())));
            }
        }

        month_income.setText("本月总收入：¥"+inMoney);
        month_pay.setText("本月总支出：¥"+outMoney);

        if(inMoney.compareTo(outMoney) > -1){
            //收入大于等于支出
            month_balance.setText("本月剩余：¥"+(inMoney.subtract(outMoney)));
        }else {
            month_balance.setText("本月剩余：¥-"+(outMoney.subtract(inMoney)));
        }




    }



    /**
     * yyyy-MM
     */
    public  ThreadLocal<DateFormat> yyyyMM = new ThreadLocal<DateFormat>() {
        @SuppressLint("SimpleDateFormat")
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM");
        }
    };




    /**
     * 初始化图表的样式
     */
    @Override
    protected void initChartStyle() {
        //使用百分百显示
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);

        //设置拖拽的阻尼，0为立即停止
        chart.setDragDecelerationFrictionCoef(0.95f);

        //设置图标中心文字
        chart.setCenterText(generateCenterSpannableText());
        chart.setDrawCenterText(true);
        //设置图标中心空白，空心
        chart.setDrawHoleEnabled(true);
        //设置空心圆的弧度百分比，最大100
        chart.setHoleRadius(58f);
        chart.setHoleColor(Color.WHITE);
        //设置透明弧的样式
        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);
        chart.setTransparentCircleRadius(61f);

        //设置可以旋转
        chart.setRotationAngle(0);
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);
    }

    /**
     * 初始化图表的 标题
     */
    @Override
    protected void initChartLabel() {
        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        chart.setEntryLabelColor(Color.WHITE);
        chart.setEntryLabelTextSize(12f);
    }

    /**
     * 设置图表数据
     *
    */
    @Override
    protected void setChartData() {


        List<String> partiesList=new ArrayList<>();
        Map<String,Float> map=new HashMap<>();

        for(DayMoney dayMoney:moneyList){
            if(map.containsKey(dayMoney.getSortType())){
                map.put(dayMoney.getSortType(),map.get(dayMoney.getSortType())+dayMoney.getMoney());
            }else {
                map.put(dayMoney.getSortType(),dayMoney.getMoney());
                partiesList.add(dayMoney.getSortType());
            }
        }

        String[] parties=partiesList.toArray(new String[partiesList.size()]);
        entries = new ArrayList<>();
        for (int i = 0; i < partiesList.size(); i++) {
            //设置数据源
            entries.add(new PieEntry(map.get(parties[i]), parties[i % parties.length], getResources().getDrawable(R.drawable.ic_add)));
        }

        PieDataSet dataSet = new PieDataSet(entries, "分类");
        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        List<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.JOYFUL_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.COLORFUL_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.LIBERTY_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.PASTEL_COLORS) {
            colors.add(c);
        }
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(chart));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        chart.setData(data);

        // undo all highlights
        chart.highlightValues(null);

        chart.setDrawEntryLabels(false);
        for (IDataSet<?> set : chart.getData().getDataSets()) {
            set.setDrawValues(false);
        }

        //动画
        chart.animateXY(1400, 1400);
        chart.invalidate();
    }


    /**
     * 生成饼图中间的文字
     *
     * @return
     */
    private SpannableString generateCenterSpannableText() {
        String text="";
        if(index==0){
            text="\n支出次数\n";
        }else {
            text="\n收入次数\n";
        }
        SpannableString s = new SpannableString(date1+text+moneyList.size());
        s.setSpan(new RelativeSizeSpan(.8f), 0, 14, 0);
       /* s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
        s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);*/

        return s;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onValueSelected(Entry e, Highlight h) {


        List<PieEntry> list2=entries.stream().filter(item -> item.getY()==e.getY())
                .collect(Collectors.toList());



        String type=list2.get(0).getLabel();
        List<DayMoney> list=new ArrayList<>();
        for(DayMoney dayMoney:moneyList){
            if(dayMoney.getSortType().equals(type)){
                list.add(dayMoney);
            }
        }

        mMonthMoneyAdapter.refresh(list);
        month_type.setLeftIcon(list.get(0).getSortImage());
        month_type.setLeftString(list.get(0).getSortType());
        month_type.setCenterString("共"+list.size()+"个记录");
    }

    @Override
    public void onNothingSelected() {

    }

}
