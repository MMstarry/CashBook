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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xuexiang.qqgg.adapter.entity.DayMoney;
import com.xuexiang.qqgg.adapter.entity.MultiQuery;

import java.util.ArrayList;
import java.util.List;

public class SqlLiteUtils {

    private SqlLiteHelper du;
    private SQLiteDatabase db;

    public SqlLiteUtils(Context context){
        du = new SqlLiteHelper(context);
        db = du.getWritableDatabase();
    }

    /**
     * 添加数据
     * */
    public void addData(String tableName,String[] key,String[] values){
        ContentValues contentValues = new ContentValues();
        for(int i = 0; i < key.length; i ++){
            contentValues.put(key[i],values[i]);
        }
        db.insert(tableName,null,contentValues);
        contentValues.clear();


    }





    public int queryCount(MultiQuery multiQuery){
        int count=0;
        Cursor cursor = db.rawQuery(multiQuery.toString(),multiQuery.getValues());
        while(cursor.moveToNext()){

            count++;

        }
        return count;
    }



    /**
     * 查询数据
     * */
    public List<DayMoney> query(MultiQuery multiQuery){
        List<DayMoney> list = new ArrayList<>();
        Cursor cursor = db.rawQuery(multiQuery.toString(),multiQuery.getValues());
        while(cursor.moveToNext()){

            long id=cursor.getLong(0);
            float money=cursor.getFloat(1);
            String remarks=cursor.getString(2);
            String sortType=cursor.getString(3);
            int sortImg=cursor.getInt(4);
            String createTime=cursor.getString(5);
            int income=cursor.getInt(6);
            String mMonth=cursor.getString(7);

            DayMoney dayMoney=new DayMoney();
            dayMoney.setId(id);
            dayMoney.setMoney(money);
            dayMoney.setRemarks(remarks);
            dayMoney.setSortType(sortType);
            dayMoney.setSortImage(sortImg);
            dayMoney.setCrateTime(createTime);
            dayMoney.setIncome(income);
            dayMoney.setmMonth(mMonth);

            list.add(dayMoney);

        }

        return list;
    }
    /**
     * 删除数据
     * */
    public int delData(String where,String[] values){
        int del_data;
        del_data = db.delete("money",where,values);
        return del_data;
    }

    /**
     * 修改数据 金额
     * */
    public void updateMoney(String[] values){
        db.execSQL("update money set money=? where id=? ",values);
    }

    /**
     * 修改数据 备注
     * */
    public void updateRemarks(String[] values){
        db.execSQL("update money set remarks=? where id=? ",values);
    }

    /**
     * 关闭数据库连接
     * */
    public void getClose(){
        if(db != null){
            db.close();
        }
    }


}
