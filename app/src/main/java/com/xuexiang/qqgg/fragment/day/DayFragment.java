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

package com.xuexiang.qqgg.fragment.day;

import android.app.DatePickerDialog;
import android.text.InputType;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.alibaba.android.vlayout.layout.StickyLayoutHelper;
import com.xuexiang.qqgg.MyApp;
import com.xuexiang.qqgg.R;
import com.xuexiang.qqgg.adapter.base.delegate.SimpleDelegateAdapter;
import com.xuexiang.qqgg.adapter.base.delegate.SingleDelegateAdapter;
import com.xuexiang.qqgg.adapter.entity.DayMoney;
import com.xuexiang.qqgg.adapter.entity.MultiQuery;
import com.xuexiang.qqgg.core.BaseFragment;
import com.xuexiang.qqgg.utils.HttpUtils;
import com.xuexiang.qqgg.utils.SqlLiteUtils;
import com.xuexiang.qqgg.utils.Utils;
import com.xuexiang.qqgg.utils.XToastUtils;
import com.xuexiang.qqgg.utils.service.JsonSerializationService;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.dialog.bottomsheet.BottomSheet;
import com.xuexiang.xui.widget.dialog.materialdialog.GravityEnum;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.popupwindow.bar.CookieBar;
import com.xuexiang.xupdate.proxy.IUpdateHttpService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;

/**
 * 首页动态
 *
 * @author xuexiang
 * @since 2019-10-30 00:15
 */
@Page(anim = CoreAnim.none)
public class DayFragment extends BaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;


    private SimpleDelegateAdapter<DayMoney> mDayMoneyAdapter;

    private DelegateAdapter mDelegateAdapter;
    private List<DelegateAdapter.Adapter> mAdapters = new ArrayList<>();

    private SqlLiteUtils sqlLiteUtils;



    String startTime;
    String endTime;
    Calendar calendar = Calendar.getInstance();
    String date1;

    List<DayMoney> moneyList=new ArrayList<>();

    BigDecimal inMoney=new BigDecimal(0);

    BigDecimal outMoney=new BigDecimal(0);
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
        return R.layout.fragment_day;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        sqlLiteUtils=((MyApp) Objects.requireNonNull(this.getContext()).getApplicationContext()).getSqlLiteUtils();
        first();
        // 1.设置VirtualLayoutManager
        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(getContext());
        recyclerView.setLayoutManager(virtualLayoutManager);

        // 2.设置RecycledViews复用池大小（可选）
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        viewPool.setMaxRecycledViews(0, 20);
        recyclerView.setRecycledViewPool(viewPool);





        ////当日消费总结
        StickyLayoutHelper stickyLayoutHelper = new StickyLayoutHelper();
        stickyLayoutHelper.setStickyStart(true);
        SingleDelegateAdapter titleAdapter = new SingleDelegateAdapter(R.layout.adapter_day_time_item, stickyLayoutHelper) {
            @Override
            public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

                holder.click(R.id.day_calender,view -> choseDate(holder));
                holder.text(R.id.day_date,date1);
                holder.text(R.id.day_income,"当日收入：¥"+inMoney);
                holder.text(R.id.day_pay,"当日支出：¥"+outMoney);

               holder.click(R.id.day_add,view -> openNewPage(AddRecordFragment.class));
            }
        };

        //当日消费列表
        mDayMoneyAdapter = new SimpleDelegateAdapter<DayMoney>(R.layout.adapter_day_card_view_list_item, new LinearLayoutHelper()) {
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
                    holder.text(R.id.dayMoney_createTime,model.getCrateTime().substring(11,16));
                    //holder.click(R.id.dayMoney,v->operation(model.getId(),model.getMoney(),model.getSortType()));
                    holder.click(R.id.dayMoney,v->operation(model));
                }
            }
        };

        mDelegateAdapter = new DelegateAdapter(virtualLayoutManager);

        mAdapters.add(titleAdapter);
        mAdapters.add(mDayMoneyAdapter);

        // 3.设置DelegateAdapter
        recyclerView.setAdapter(mDelegateAdapter);


        //每日一言
        toMM();

    }


    public void toMM(){

        Map<String, Object> params=new HashMap<>();

        HttpUtils.asyncGet("http://open.iciba.com/dsapi/", params, new IUpdateHttpService.Callback() {
            @Override
            public void onSuccess(String result) {
                Map<String,Object> map= JsonSerializationService.jsonToMap(result);

                String content=map.get("note")+"\n"+map.get("content");
                showBar(content);
            }

            @Override
            public void onError(Throwable throwable) {
                XToastUtils.error("网络错误");
            }
        });



    }

    public void showBar(String content){
        CookieBar.builder(getActivity())
                .setTitle("MMMM")
                .setMessage(content)
                .setDuration(5000)
                .setBackgroundColor(R.color.colorPrimary)
                .setActionColor(android.R.color.white)
                .setTitleColor(android.R.color.white)
                .setAction("QQGG", view1 -> System.err.println(1))
                .show();
    }


    public void choseDate(RecyclerViewHolder holder){
        new DatePickerDialog(getContext()
                , DatePickerDialog.THEME_DEVICE_DEFAULT_LIGHT
                , (view, year, monthOfYear, dayOfMonth) -> chaxun(year,monthOfYear, dayOfMonth,holder)
                // 设置初始日期
                , Calendar.getInstance().get(Calendar.YEAR)
                , Calendar.getInstance().get(Calendar.MONTH)
                , Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                .show();
    }

    //操作弹出框
    public void operation(DayMoney model){
        new BottomSheet.BottomListSheetBuilder(getActivity())
                .setTitle("操作("+model.getSortType()+")")
                .addItem("修改金额")
                .addItem("修改备注")
                .addItem("查看备注")
                .addItem("删除记录")
                .setIsCenter(true)
                .setOnSheetItemClickListener((dialog, itemView, position, tag) -> {
                    dialog.dismiss();
                    operationDetail(position+1,model);
                })
                .build()
                .show();
    }

    public void operationDetail(int position,DayMoney model){

        /*
        * /* String[] key = {"money","remarks","sortType","sortImage","crateTime","income"};
        String[] values = {"999.99","测试","吃饭","1","2020-04-01 10:00:00","1"};*/
        if(position==1){
            //修改金额
            new MaterialDialog.Builder(getContext())

                    .title("修改金额").titleGravity(GravityEnum.CENTER)

                    .inputType(
                            InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER)
                    .input(
                            model.getMoney()+"",
                            "",
                            false,
                            ((dialog, input) -> changeMoney(model.getId(),input.toString())))
                    .inputRange(0, 10)
                    .positiveText("确认")
                    .negativeText("取消")
                    .cancelable(false)
                    .show();
        }else if(position==2){
            //修改备注
            new MaterialDialog.Builder(getContext())

                    .title("修改备注").titleGravity(GravityEnum.CENTER)

                    .inputType(
                            InputType.TYPE_CLASS_TEXT)
                    .input(
                            model.getRemarks()+"",
                            "",
                            false,
                            ((dialog, input) -> changeRemarks(model.getId(),input.toString())))
                    .inputRange(0, 80)
                    .positiveText("确认")
                    .negativeText("取消")
                    .cancelable(false)
                    .show();
        }else if(position==3){
            //查看备注
            DialogLoader.getInstance().showTipDialog(
                    getContext(),
                   "备注",
                    model.getRemarks(),
                    "确定");
        }else {
            //删除记录
            sqlLiteUtils.delData("id=?",new String[]{String.valueOf(model.getId())});
            initListeners();
        }


    }

    public void changeRemarks(long id,String remarks){
        sqlLiteUtils.updateRemarks(new String[]{remarks,String.valueOf(id)});
        initListeners();
    }

    private void changeMoney(long id,String money){
        sqlLiteUtils.updateMoney(new String[]{money,String.valueOf(id)});
        initListeners();
    }


    private void chaxun(int year, int monthOfYear, int dayOfMonth,@NonNull RecyclerViewHolder holder) {
        date1=year+"-"+Utils.autoGenericCode(monthOfYear+1,2)+"-"+Utils.autoGenericCode(dayOfMonth,2);
        startTime=date1+" 00:00:00";
        endTime=date1+" 23:59:59";
        holder.text(R.id.day_date,date1);
        initListeners();
    }


    public void first(){
        date1=calendar.get(Calendar.YEAR)+"-"+Utils.autoGenericCode(calendar.get(Calendar.MONTH)+1,2)+"-"+Utils.autoGenericCode(calendar.get(Calendar.DATE),2);
        startTime=date1+" 00:00:00";
        endTime=date1+" 23:59:59";

    }

    public void query(){
        inMoney=new BigDecimal(0);
        outMoney=new BigDecimal(0);
        MultiQuery multiQuery=new MultiQuery();

        multiQuery.setStartTime(startTime);
        multiQuery.setEndTime(endTime);
        moneyList=sqlLiteUtils.query(multiQuery);
        for(DayMoney dayMoney:moneyList){
                if(dayMoney.getIncome()==0){
                    outMoney=outMoney.add(new BigDecimal(Float.toString(dayMoney.getMoney())));
                }else {
                    inMoney=inMoney.add(new BigDecimal(Float.toString(dayMoney.getMoney())));
                }
        }
    }



    @Override
    protected void initListeners() {

        query();
        mDelegateAdapter.setAdapters(mAdapters);
        mDayMoneyAdapter.refresh(moneyList);

    }

    @Override
    public void onResume() {
        super.onResume();
        initListeners();
    }
}
