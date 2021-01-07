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

package com.xuexiang.qqgg.fragment;

import android.content.Intent;
import android.net.Uri;
import android.widget.TextView;

import com.xuexiang.qqgg.R;
import com.xuexiang.qqgg.core.BaseFragment;
import com.xuexiang.qqgg.utils.Utils;
import com.xuexiang.qqgg.utils.XToastUtils;
import com.xuexiang.qqgg.utils.sdkinit.XUpdateInit;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.grouplist.XUIGroupListView;
import com.xuexiang.xutil.app.AppUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;

@Page(name = "关于")
public class AboutFragment extends BaseFragment {
    @BindView(R.id.version)
    TextView mVersionTextView;
    @BindView(R.id.about_list)
    XUIGroupListView mAboutGroupListView;
    @BindView(R.id.copyright)
    TextView mCopyrightTextView;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_about;
    }

    @Override
    protected void initViews() {
        mVersionTextView.setText(String.format("版本号：%s", AppUtils.getAppVersionName()));
        XUIGroupListView.newSection(getContext())
                .addItemView(mAboutGroupListView.createItemView("版本更新"), v -> XUpdateInit.checkUpdate(getContext(), true))
                .addItemView(mAboutGroupListView.createItemView("赞助项目"), v -> openPage(SponsorFragment.class))
                .addItemView(mAboutGroupListView.createItemView("作者QQ"), v -> addQQ())
                .addItemView(mAboutGroupListView.createItemView("GitHub主页"), v -> Utils.goWeb(getContext(), "https://github.com/MMstarry/CashBook/"))
                .addTo(mAboutGroupListView);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.CHINA);
        String currentYear = dateFormat.format(new Date());
        mCopyrightTextView.setText(String.format(getResources().getString(R.string.about_copyright), currentYear));

    }

    public void addQQ(){
        try {
            //第二种方式：可以跳转到添加好友，如果qq号是好友了，直接聊天
            String url = "mqqwpa://im/chat?chat_type=wpa&uin=1668846604";//uin是发送过去的qq号码
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            e.printStackTrace();
            XToastUtils.info("请检查是否安装QQ");
        }
    }
}
