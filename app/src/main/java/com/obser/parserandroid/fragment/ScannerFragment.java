package com.obser.parserandroid.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
import com.obser.parserandroid.interpreter.Scanner;
import com.obser.parserandroid.activity.MainActivity;
import com.obser.parserandroid.bean.Token;
import com.obser.parserandroid.bean.TokenData;
import com.obser.parserandroid.global.BaseApplication;

import java.io.File;

/**
 * Created by Administrator on 2016/12/7 0007.
 */
public class ScannerFragment extends Fragment {
    private FloatingActionButton btScanner;
    @ViewInject(R.id.expand_scanner)
    private ExpandableLayout expandableLayout;

    @ViewInject(R.id.type)
    private TextView tvType;

    @ViewInject(R.id.lexeme)
    private TextView tvLexeme;

    @ViewInject(R.id.value)
    private TextView tvValue;

    private FloatingActionMenu faMenu;

    @ViewInject(R.id.fun)
    private TextView tvFun;

    @ViewInject(R.id.ll_scan)
    private LinearLayout llScan;


    private Token token;
    private Scanner mScanner;
    private final static int TYPE = 1;
    private final static int LEXEME = 2;
    private final static int VALUE = 3;
    private final static int FUN = 4;
    private final static int SCAN = 5;


    private File file;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SCAN:
                    Bundle data = msg.getData();
                    tvType.setText("记号类别" + data.getString("type"));
                    tvLexeme.setText("字符串" + data.getString("lexeme"));
                    tvValue.setText("常数值" + data.getString("value"));
                    tvFun.setText("函数" + data.getString("fun"));
                    btScanner.setClickable(true);
                    if(!expandableLayout.isOpened())
                        expandableLayout.show();
                    break;
            }
        }
    };

    private MainActivity mActivity;
    public void setScanner(Scanner scanner){
        mScanner = scanner;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        mActivity = (MainActivity) getActivity();
        View view = View.inflate(mActivity, R.layout.fragment_scanner, null);
        ViewUtils.inject(this, view);
        file = BaseApplication.getFile();
        return view;
    }


    private void scan() {
        StringBuilder sbType = new StringBuilder();
        StringBuilder sbLexeme = new StringBuilder();
        StringBuilder sbValue = new StringBuilder();
        StringBuilder sbFun = new StringBuilder();

        while(true){
            token = mScanner.getToken();
            if(token.getType() != TokenData.Token_Type.NONTOKEN){
                sbType.append('\n');    sbType.append(appendInfo(TYPE));
                sbLexeme.append('\n');  sbLexeme.append(appendInfo(LEXEME));
                sbValue.append('\n');   sbValue.append(appendInfo(VALUE));
                sbFun.append('\n');     sbFun.append(appendInfo(FUN));
            } else {
                break;
            }
        }
        Message message = new Message();
        message.what = SCAN;
        Bundle bundle = new Bundle();
        bundle.putString("type", sbType.toString());
        bundle.putString("lexeme", sbLexeme.toString());
        bundle.putString("value", sbValue.toString());
        bundle.putString("fun", sbFun.toString());
        message.setData(bundle);
        mHandler.sendMessage(message);
        sbType.delete(0, sbType.length());
        sbLexeme.delete(0, sbLexeme.length());
        sbValue.delete(0, sbValue.length());
        sbFun.delete(0, sbFun.length());
        mScanner.closeScanner();
    }
    private String appendInfo(int flag){
        switch (flag){
            case TYPE:
                return String.valueOf(token.getType());
            case LEXEME:
                return token.getLexeme();
            case VALUE:
                return String.valueOf(token.getValue());
            case FUN:
                String fun = token.getFun();
                if(TextUtils.isEmpty(fun))
                    return "";
                else
                    return fun;
            default:
                return "";
        }
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btScanner = mActivity.getBtScanner();
        faMenu = mActivity.getFaMenu();
        btScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(faMenu != null && faMenu.isOpened()){
                    faMenu.close(true);
                }
                if(mScanner.initScanner(file)){
                    btScanner.setClickable(false);
                    llScan.setVisibility(View.VISIBLE);
                    new Thread(){
                        @Override
                        public void run() {
                            scan();
                        }
                    }.start();
                }
            }
        });
    }
}
