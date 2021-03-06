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

package com.xuexiang.qqgg.core;

import com.github.mikephil.charting.charts.Chart;
import com.xuexiang.qqgg.utils.XToastUtils;
import com.xuexiang.xaop.annotation.Permission;


import static com.xuexiang.xaop.consts.PermissionConsts.STORAGE;

/**
 * 基础图表
 *
 * @author xuexiang
 * @since 2019/4/13 下午7:02
 */
public abstract class BaseChartFragment extends BaseFragment {


    /**
     * 初始化图表的样式
     */
    protected abstract void initChartStyle();


    /**
     * 初始化图表的 标题 样式
     */
    protected abstract void initChartLabel();

    /**
     * 设置图表数据
     */
    protected abstract void setChartData();

    /**
     * 图表保存
     *
     * @param chart
     * @param name
     */
    @Permission(STORAGE)
    public void saveToGallery(Chart chart, String name) {
        if (chart.saveToGallery(name + "_" + System.currentTimeMillis(), 70)) {
            XToastUtils.toast("保存成功!");

        } else {
            XToastUtils.toast("保存失败!");
        }
    }

}
