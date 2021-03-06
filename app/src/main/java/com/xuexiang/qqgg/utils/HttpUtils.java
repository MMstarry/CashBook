/*
 * Copyright (C) 2021 xuexiangjys(xuexiangjys@163.com)
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

import androidx.annotation.NonNull;

import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.callback.SimpleCallBack;
import com.xuexiang.xhttp2.exception.ApiException;
import com.xuexiang.xupdate.proxy.IUpdateHttpService;
import com.xuexiang.xutil.net.JsonUtil;

import java.util.Map;

public class HttpUtils {



    public static void asyncGet(@NonNull String url, @NonNull Map<String, Object> params, @NonNull final IUpdateHttpService.Callback callBack) {
        XHttp.get(url)
                .params(params)
                .keepJson(true)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onSuccess(String response) throws Throwable {
                        callBack.onSuccess(response);
                    }
                    @Override
                    public void onError(ApiException e) {
                        callBack.onError(e);
                    }
                });
    }


    public static void asyncPost(@NonNull String url, @NonNull Map<String, Object> params, @NonNull final IUpdateHttpService.Callback callBack) {
        XHttp.post(url)
                .upJson(JsonUtil.toJson(params))
                .keepJson(true)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onSuccess(String response) throws Throwable {
                        callBack.onSuccess(response);
                    }

                    @Override
                    public void onError(ApiException e) {
                        callBack.onError(e);
                    }
                });
    }

}