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

package com.xuexiang.qqgg.utils.sdkinit;

import android.app.Application;
import android.content.Context;

import com.xuexiang.qqgg.utils.update.CustomUpdateFailureListener;
import com.xuexiang.qqgg.utils.update.XHttpUpdateHttpServiceImpl;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.entity.UpdateError;
import com.xuexiang.xupdate.listener.OnUpdateFailureListener;
import com.xuexiang.xupdate.utils.UpdateUtils;
import com.xuexiang.xutil.tip.ToastUtils;

import static com.xuexiang.xupdate.entity.UpdateError.ERROR.CHECK_NO_NEW_VERSION;

/**
 * XUpdate 版本更新 SDK 初始化
 *
 * @author xuexiang
 * @since 2019-06-18 15:51
 */
public final class XUpdateInit {

    private XUpdateInit() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 应用版本更新的检查地址
     */
    private static final String KEY_UPDATE_URL = "http://ztm521.gitee.io/love/xiaomeng-cashbook-upload.json";

    public static void init(Application application) {


        XUpdate.get()
                .debug(true)
                .isWifiOnly(false)    //默认设置只在wifi下检查版本更新
                .isGet(true)         //默认设置使用get请求检查版本
                .isAutoMode(false)   //默认设置非自动模式，可根据具体使用配置
                .param("versionCode", UpdateUtils.getVersionCode(application)) //设置默认公共请求参数
                .param("appKey", application.getPackageName())
                .setOnUpdateFailureListener(new OnUpdateFailureListener() { //设置版本更新出错的监听
                    @Override
                    public void onFailure(UpdateError error) {
                        if (error.getCode() != CHECK_NO_NEW_VERSION) { //对不同错误进行处理
                            ToastUtils.toast(error.toString());
                        }
                    }
                })
                .supportSilentInstall(true)  //设置是否支持静默安装，默认是true
                .setIUpdateHttpService(new XHttpUpdateHttpServiceImpl()) //这个必须设置！实现网络请求功能。
                .init(application);
    }

    /**
     * 进行版本更新检查
     *
     * @param context
     */
    public static void checkUpdate(Context context, boolean needErrorTip) {

        XUpdate.newBuild(context)
                .updateUrl(KEY_UPDATE_URL)
                .update();
        XUpdate.get().setOnUpdateFailureListener(new CustomUpdateFailureListener(needErrorTip));

    }

}
