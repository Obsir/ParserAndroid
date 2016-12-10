package com.obser.parserandroid.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andexert.expandablelayout.library.ExpandableLayout;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.obser.parserandroid.R;
import com.obser.parserandroid.activity.MainActivity;
import com.obser.parserandroid.global.BaseApplication;
import com.obser.parserandroid.interpreter.Parser;

import java.io.File;

/**
 * Created by Administrator on 2016/12/7 0007.
 */
public class ParseFragment extends Fragment {
    private FloatingActionButton btParser;
    private FloatingActionMenu faMenu;


    @ViewInject(R.id.expand_parser)
    private ExpandableLayout expandableLayout;
    @ViewInject(R.id.ll_parse)
    private LinearLayout llParse;

    @ViewInject(R.id.ilog)
    private TextView tvILog;

    @ViewInject(R.id.elog)
    private TextView tvELog;

    private Parser mParser;
    private final static int PARSER = 6;


    private File file;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case PARSER:
                    tvILog.setText("分析日志" + mParser.getILog());
                    int err = mParser.getErrCount();
                    if(err == 0)
                        tvELog.setTextColor(0xff000000);
                    else
                        tvELog.setTextColor(0xffff4444);
                    tvELog.setText("错误日志" + mParser.getELog() + "\nError   : " + err);
                    if(!expandableLayout.isOpened())
                        expandableLayout.show();
                    btParser.setClickable(true);
                    break;
            }
        }
    };

    private MainActivity mActivity;
    public void setParser(Parser parser){
        mParser = parser;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        mActivity = (MainActivity) getActivity();
        View view = View.inflate(mActivity, R.layout.fragment_parse, null);
        ViewUtils.inject(this, view);
        file = BaseApplication.getFile();
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btParser = mActivity.getBtParser();
        faMenu = mActivity.getFaMenu();

        btParser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btParser.setClickable(false);
                if(faMenu != null && faMenu.isOpened()){
                    faMenu.close(true);
                }
                llParse.setVisibility(View.VISIBLE);
                mParser.setFlag(true);
                new Thread(){
                    @Override
                    public void run() {
                        mParser.parser();
                        Message message = new Message();
                        message.what = PARSER;
                        mHandler.sendMessage(message);
                    }
                }.start();
            }
        });
    }


}
