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

package com.xuexiang.qqgg;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.xuexiang.qqgg.utils.SqlLiteUtils;
import com.xuexiang.qqgg.utils.sdkinit.ANRWatchDogInit;
import com.xuexiang.qqgg.utils.sdkinit.XBasicLibInit;
import com.xuexiang.qqgg.utils.sdkinit.XUpdateInit;


/**
 * @author xuexiang
 * @since 2018/11/7 下午1:12
 */
public class MyApp extends Application {
    private SqlLiteUtils sqlLiteUtils;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //解决4.x运行崩溃的问题
        MultiDex.install(this);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        initLibs();
        sqlLiteUtils=new SqlLiteUtils(this);




    }


    /**
     * 后台进程终止，前台程序需要内存时调用此方法，用于释放内存
     * 用于关闭数据库连接
     * */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        sqlLiteUtils.getClose();
    }

    public SqlLiteUtils getSqlLiteUtils() {
        return sqlLiteUtils;
    }

    /**
     * 初始化基础库
     */
    private void initLibs() {
        XBasicLibInit.init(this);

        XUpdateInit.init(this);

        //运营统计数据运行时不初始化
        if (!MyApp.isDebug()) {

        }

        //ANR监控
        ANRWatchDogInit.init();


    }





    /**
     * @return 当前app是否是调试开发模式
     */
    public static boolean isDebug() {
        return BuildConfig.DEBUG;
    }


}
