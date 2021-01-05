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

package com.xuexiang.qqgg.utils;

import com.xuexiang.qqgg.R;
import com.xuexiang.qqgg.adapter.entity.Sort;
import com.xuexiang.xaop.annotation.MemoryCache;

import java.util.ArrayList;
import java.util.List;

/**
 * 演示数据
 *
 * @author xuexiang
 * @since 2018/11/23 下午5:52
 */
public class DemoDataProvider {



    @MemoryCache
    public static List<Sort> getAllSort0(){
        List<Sort> list = new ArrayList<>();
        list.add(new Sort(1,"其他", R.mipmap.sort_tianjia,0,true));

        list.add(new Sort(3,"餐饮", R.mipmap.sort_canyin,0,false));


        list.add(new Sort(5,"购物", R.mipmap.sort_gouwu,0,false));
        list.add(new Sort(6,"孩子", R.mipmap.sort_haizi,0,false));
        list.add(new Sort(7,"还款", R.mipmap.sort_huankuan,0,false));

        list.add(new Sort(10,"交通", R.mipmap.sort_jiaotong,0,false));
        list.add(new Sort(11,"酒水", R.mipmap.sort_jiushui,0,false));
        list.add(new Sort(12,"捐赠", R.mipmap.sort_juanzeng,0,false));
        list.add(new Sort(13,"居家", R.mipmap.sort_jujia,0,false));
        list.add(new Sort(14,"礼金", R.mipmap.sort_lijin,0,false));

        list.add(new Sort(16,"零食", R.mipmap.sort_lingshi,0,false));
        list.add(new Sort(17,"礼物", R.mipmap.sort_liwu,0,false));

        list.add(new Sort(19,"旅行", R.mipmap.sort_lvxing,0,false));
        list.add(new Sort(20,"美容", R.mipmap.sort_meirong,0,false));
        list.add(new Sort(21,"手续费", R.mipmap.sort_shouxufei,0,false));
        list.add(new Sort(22,"水果", R.mipmap.sort_shuiguo,0,false));
        list.add(new Sort(23,"数码", R.mipmap.sort_shuma,0,false));
        list.add(new Sort(24,"通讯", R.mipmap.sort_tongxun,0,false));
        list.add(new Sort(25,"维修", R.mipmap.sort_weixiu,0,false));
        list.add(new Sort(26,"违约金", R.mipmap.sort_weiyuejin,0,false));
        list.add(new Sort(27,"学习", R.mipmap.sort_xuexi,0,false));
        list.add(new Sort(28,"一般", R.mipmap.sort_yiban,0,false));
        list.add(new Sort(29,"医疗", R.mipmap.sort_yiliao,0,false));
        list.add(new Sort(30,"娱乐", R.mipmap.sort_yule,0,false));
        list.add(new Sort(31,"运动", R.mipmap.sort_yundong,0,false));

        list.add(new Sort(33,"住房", R.mipmap.sort_zhufang,0,false));
        list.add(new Sort(34,"办公", R.mipmap.sort_bangong,0,false));
        list.add(new Sort(35,"宠物", R.mipmap.sort_chongwu,0,false));
        return list;
    }

    @MemoryCache
    public static List<Sort> getAllSort1(){
        List<Sort> list = new ArrayList<>();

        list.add(new Sort(2,"其他", R.mipmap.sort_tianjia,1,true));


        list.add(new Sort(4,"利息", R.mipmap.sort_fanxian,1,false));


        list.add(new Sort(8,"奖金", R.mipmap.sort_jiangjin,1,false));
        list.add(new Sort(9,"兼职", R.mipmap.sort_jianzhi,1,false));

        list.add(new Sort(15,"零钱", R.mipmap.sort_lingqian,1,false));

        list.add(new Sort(18,"工资", R.mipmap.sort_lixi,1,false));

        list.add(new Sort(32,"长辈", R.mipmap.sort_zhangbei,1,false));

        return list;
    }

}
