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

package com.xuexiang.qqgg.adapter.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 多条件查询
 */
public class MultiQuery {
    private Integer id;

    //当前费用类型 医疗
    private String sortType;

    //开始时间 2020-02-01 00:00:00
    private String startTime;
    //结束时间
    private String endTime;
    //0 支出 1 收入
    private Integer income;

    //第几页 从1开始
    private Integer page;

    private Integer limit;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getIncome() {
        return income;
    }

    public void setIncome(Integer income) {
        this.income = income;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {

        String sql="select * from money where";
        if(id!=null){
            if(rightSubstring(sql,5).equals("where")){
                sql+=" id=?";
            }else {
                sql+=" and id=?";
            }

        }
        if(sortType!=null){
            if(rightSubstring(sql,5).equals("where")){
                sql+=" sortType=?";
            }else {
                sql+=" and sortType=?";
            }

        }

        if(startTime!=null){
            if(rightSubstring(sql,5).equals("where")){

                sql+=" crateTime>? and crateTime< ?";
            }else {
                sql+=" and crateTime>? and crateTime< ?";
            }

        }

        if(income!=null){
            if(rightSubstring(sql,5).equals("where")){
                sql+=" income=?";
            }else {
                sql+=" and income=?";
            }
        }

        if(rightSubstring(sql,5).equals("where")){
            sql=sql.substring(0,sql.length()-5);

        }

        sql+=" order by crateTime";

        if(page!=null){

            sql+= " limit ?,?";

        }

        return sql;
    }

    public String[] getValues(){
        List<String> list=new ArrayList<>();

        if(id!=null){
            list.add(id.toString());

        }
        if(sortType!=null){
           list.add(sortType);

        }

        if(startTime!=null){
            list.add(startTime);
            list.add(endTime);

        }

        if(income!=null){
           list.add(income.toString());
        }



        if(page!=null){
            Integer start=(page-1)*limit;
            list.add(start.toString());

            list.add(limit.toString());
        }




        return list.toArray(new String[list.size()]);
    }


    private static String rightSubstring(String s, int num) {
        String snew = s;
        if(snew == null)
            return "";
        else if(snew.length()>num)
            return snew.substring(snew.length()-num,snew.length());
        else
            return snew;
    }
}
