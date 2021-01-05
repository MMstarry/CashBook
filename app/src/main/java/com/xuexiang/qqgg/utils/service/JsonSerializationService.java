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

package com.xuexiang.qqgg.utils.service;

import android.content.Context;
import android.util.Log;

import com.xuexiang.xrouter.annotation.Router;
import com.xuexiang.xrouter.facade.service.SerializationService;
import com.xuexiang.xutil.net.JsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author XUE
 * @since 2019/3/27 16:39
 */
@Router(path = "/service/json")
public class JsonSerializationService implements SerializationService {
    /**
     * 对象序列化为json
     *
     * @param instance obj
     * @return json string
     */
    @Override
    public String object2Json(Object instance) {
        return JsonUtil.toJson(instance);
    }

    /**
     * json反序列化为对象
     *
     * @param input json string
     * @param clazz object type
     * @return instance of object
     */
    @Override
    public <T> T parseObject(String input, Type clazz) {
        return JsonUtil.fromJson(input, clazz);
    }

    /**
     * 进程初始化的方法
     *
     * @param context 上下文
     */
    @Override
    public void init(Context context) {

    }


    public static Map<String, Object> jsonToMap(String content) {
        content = content.trim();
        Map<String, Object> result = new HashMap<>();
        try {
            if (content.charAt(0) == '[') {
                JSONArray jsonArray = new JSONArray(content);
                for (int i = 0; i < jsonArray.length(); i++) {
                    Object value = jsonArray.get(i);
                    if (value instanceof JSONArray || value instanceof JSONObject) {
                        result.put(i + "", jsonToMap(value.toString().trim()));
                    } else {
                        result.put(i + "", jsonArray.getString(i));
                    }
                }
            } else if (content.charAt(0) == '{'){
                JSONObject jsonObject = new JSONObject(content);
                Iterator<String> iterator = jsonObject.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    Object value = jsonObject.get(key);
                    if (value instanceof JSONArray || value instanceof JSONObject) {
                        result.put(key, jsonToMap(value.toString().trim()));
                    } else {
                        result.put(key, value.toString().trim());
                    }
                }
            }else {
                Log.e("异常", "json2Map: 字符串格式错误");
            }
        } catch (JSONException e) {
            Log.e("异常", "json2Map: ", e);
            result = null;
        }
        return result;
    }
}
