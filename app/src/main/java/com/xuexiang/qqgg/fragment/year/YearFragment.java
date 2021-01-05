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

package com.xuexiang.qqgg.fragment.year;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.model.GradientColor;
import com.xuexiang.qqgg.MyApp;
import com.xuexiang.qqgg.R;
import com.xuexiang.qqgg.adapter.base.delegate.ContentPage;
import com.xuexiang.qqgg.adapter.base.delegate.DayAxisValueFormatter;
import com.xuexiang.qqgg.adapter.base.delegate.MoneyValueFormatter;
import com.xuexiang.qqgg.adapter.base.delegate.XYMarkerView;
import com.xuexiang.qqgg.adapter.entity.DayMoney;
import com.xuexiang.qqgg.adapter.entity.MultiQuery;
import com.xuexiang.qqgg.core.BaseChartFragment;
import com.xuexiang.qqgg.fragment.AboutFragment;
import com.xuexiang.qqgg.utils.SqlLiteUtils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.picker.widget.TimePickerView;
import com.xuexiang.xui.widget.picker.widget.builder.TimePickerBuilder;
import com.xuexiang.xui.widget.tabbar.TabSegment;
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

import butterknife.BindView;

/**
 * @author xuexiang
 * @since 2019-10-30 00:18
 */
@Page(anim = CoreAnim.none)
public class YearFragment extends BaseChartFragment implements OnChartValueSelectedListener {

    @BindView(R.id.chart1)
    BarChart chart;

    private SqlLiteUtils sqlLiteUtils;
    @BindView(R.id.year_calender)
    RadiusImageView year_calender;

    @BindView(R.id.about)
    RadiusImageView about;
    @BindView(R.id.year_date)
    TextView year_date;
    @BindView(R.id.year_income)
    TextView year_income;
    @BindView(R.id.year_pay)
    TextView year_pay;

    private TimePickerView mDatePicker;
    @BindView(R.id.tabSegment1)
    TabSegment mTabSegment1;
    String[] pages = ContentPage.getPageNames();

    int index = 0;
    Calendar calendar = Calendar.getInstance();

    String startTime;
    String endTime;

    String date;

    BigDecimal inMoney=new BigDecimal(0);
    BigDecimal outMoney=new BigDecimal(0);

    List<DayMoney> moneyList = new ArrayList<>();

    List<DayMoney> moneyAllList = new ArrayList<>();
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
        return R.layout.fragment_year;
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
        isCreated = true;
        sqlLiteUtils = ((MyApp) Objects.requireNonNull(this.getContext()).getApplicationContext()).getSqlLiteUtils();
        initNoViewPagerTabSegment();

        int year = calendar.get(Calendar.YEAR);
        date = year + "年-" + (year + 1) + "年";
        year_date.setText(date);
        startTime = year + "-01-01 00:00:00";
        endTime = (year + 1) + "-01-01 00:00:00";
        year_calender.setOnClickListener(new View.OnClickListener() {
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

        initChartStyle();
        initChartLabel();
        setChartData();

        chart.setOnChartValueSelectedListener(this);
    }

    public void choseDate() {
        if (mDatePicker == null) {
            mDatePicker = new TimePickerBuilder(getContext(), (date, v) -> choseDateDetail(DateUtils.date2String(date, yyyy.get())))
                    .setTimeSelectChangeListener(date -> Log.i("pvTime", "onTimeSelectChanged"))
                    .setType(new boolean[]{true, false, false, false, false, false})
                    .setTitleText("年选择")
                    .build();
        }
        mDatePicker.show();

    }

    private void choseDateDetail(String date2String) {
        int year = Integer.parseInt(date2String);
        date = year + "年-" + (year + 1) + "年";
        year_date.setText(date);
        startTime = year + "-01-01 00:00:00";
        endTime = (year + 1) + "-01-01 00:00:00";
        initListeners();
    }

    /**
     * yyyy
     */
    public ThreadLocal<DateFormat> yyyy = new ThreadLocal<DateFormat>() {
        @SuppressLint("SimpleDateFormat")
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy");
        }
    };

    public void query() {
        inMoney=new BigDecimal(0);
        outMoney=new BigDecimal(0);
        MultiQuery multiQuery = new MultiQuery();

        multiQuery.setStartTime(startTime);
        multiQuery.setEndTime(endTime);
        moneyAllList = sqlLiteUtils.query(multiQuery);
        for (DayMoney dayMoney : moneyAllList) {
            if (dayMoney.getIncome() == 0) {
                outMoney=outMoney.add(new BigDecimal(Float.toString(dayMoney.getMoney())));
            }else {
                inMoney=inMoney.add(new BigDecimal(Float.toString(dayMoney.getMoney())));
            }
        }

        year_income.setText("年度总收入：¥" + inMoney);
        year_pay.setText("年度总支出：¥" + outMoney);
    }

    @Override
    protected void initListeners() {
        query();
        List<DayMoney> list = new ArrayList<>();
        for (DayMoney dayMoney : moneyAllList) {
            if (dayMoney.getIncome() == index) {
                list.add(dayMoney);

            }
        }
        moneyList = list;

        initChartStyle();
        setChartData();

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

    private void xx(int i) {
        index = i;
        List<DayMoney> list = new ArrayList<>();
        for (DayMoney dayMoney : moneyAllList) {
            if (dayMoney.getIncome() == index) {
                list.add(dayMoney);
            }
        }

        moneyList = list;

        initChartStyle();
        setChartData();

    }

    @Override
    public void onResume() {
        super.onResume();
        initListeners();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    /**
     * 初始化图表的样式
     */
    @Override
    protected void initChartStyle() {
        //关闭描述
        chart.getDescription().setEnabled(false);
        chart.setDrawBarShadow(false);

        //开启在柱状图顶端显示值
        chart.setDrawValueAboveBar(true);
        //设置显示值时，最大的柱数量
        chart.setMaxVisibleValueCount(60);

        //设置不能同时在x轴和y轴上缩放
        chart.setPinchZoom(false);
        //设置不画背景网格
        chart.setDrawGridBackground(false);

        initXYAxisStyle();
    }

    /**
     * 初始化图表X、Y轴的样式
     */
    private void initXYAxisStyle() {
        //设置X轴样式
        ValueFormatter xAxisFormatter = new DayAxisValueFormatter(chart);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        //间隔一天显示
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

        //设置Y轴的左侧样式
        ValueFormatter yAxisFormatter = new MoneyValueFormatter("¥");
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(yAxisFormatter);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        //设置Y轴的右侧样式
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(yAxisFormatter);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        //设置图表的数值指示器
        XYMarkerView mv = new XYMarkerView(getContext(), xAxisFormatter, yAxisFormatter);
        mv.setChartView(chart);
        chart.setMarker(mv);
    }

    /**
     * 初始化图表的 标题 样式
     */
    @Override
    protected void initChartLabel() {
        //设置图表 标题 的样式
        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
    }

    /**
     * 设置图表数据
     */
    @Override
    protected void setChartData() {

        Map<String, Float> map = new HashMap<>();
        map.put("1", 0f);
        map.put("2", 0f);
        map.put("3", 0f);
        map.put("4", 0f);
        map.put("5", 0f);
        map.put("6", 0f);
        map.put("7", 0f);
        map.put("8", 0f);
        map.put("9", 0f);
        map.put("10", 0f);
        map.put("11", 0f);
        map.put("12", 0f);
        for (DayMoney dayMoney : moneyList) {
            if (map.containsKey(dayMoney.getmMonth())) {
                map.put(dayMoney.getmMonth(), map.get(dayMoney.getmMonth()) + dayMoney.getMoney());

            }
        }


            int count = 12;
            float start = 1f;
            List<BarEntry> values = new ArrayList<>();
            //设置数据源
            for (int i = (int) start; i < start + count; i++) {
                float val = map.get(String.valueOf(i));
                if (Math.random() * 100 < 25) {
                    //设置图表，标星
                    values.add(new BarEntry(i, val, getResources().getDrawable(R.drawable.ic_add)));
                } else {
                    values.add(new BarEntry(i, val));
                }
            }

            BarDataSet set1;

            if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
                set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
                set1.setValues(values);
                set1.setLabel(date);
                set1.isDrawIconsEnabled();
                chart.getData().notifyDataChanged();
                chart.notifyDataSetChanged();
                chart.invalidate();

            } else {
                set1 = new BarDataSet(values, date);

                //设置是否画出图标
                set1.setDrawIcons(false);

                int startColor1 = ContextCompat.getColor(getContext(), android.R.color.holo_orange_light);
                int startColor2 = ContextCompat.getColor(getContext(), android.R.color.holo_blue_light);
                int startColor3 = ContextCompat.getColor(getContext(), android.R.color.holo_orange_light);
                int startColor4 = ContextCompat.getColor(getContext(), android.R.color.holo_green_light);
                int startColor5 = ContextCompat.getColor(getContext(), android.R.color.holo_red_light);
                int endColor1 = ContextCompat.getColor(getContext(), android.R.color.holo_blue_dark);
                int endColor2 = ContextCompat.getColor(getContext(), android.R.color.holo_purple);
                int endColor3 = ContextCompat.getColor(getContext(), android.R.color.holo_green_dark);
                int endColor4 = ContextCompat.getColor(getContext(), android.R.color.holo_red_dark);
                int endColor5 = ContextCompat.getColor(getContext(), android.R.color.holo_orange_dark);

                List<GradientColor> gradientColors = new ArrayList<>();
                gradientColors.add(new GradientColor(startColor1, endColor1));
                gradientColors.add(new GradientColor(startColor2, endColor2));
                gradientColors.add(new GradientColor(startColor3, endColor3));
                gradientColors.add(new GradientColor(startColor4, endColor4));
                gradientColors.add(new GradientColor(startColor5, endColor5));

                //设置渐变色
                set1.setGradientColors(gradientColors);

                //这里只设置了一组数据
                List<IBarDataSet> dataSets = new ArrayList<>();
                dataSets.add(set1);

                BarData data = new BarData(dataSets);
                data.setValueTextSize(10f);
                data.setBarWidth(0.9f);

                chart.setData(data);
            }
        chart.animateXY(2000, 2000);
        }

    }

