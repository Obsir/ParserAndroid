package com.obser.parserandroid;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.obser.parserandroid.bean.Token;
import com.obser.parserandroid.bean.TokenData;
import com.obser.parserandroid.global.BaseApplication;
import com.obser.parserandroid.utils.LogUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @ViewInject(R.id.bt_scanner)
    private Button btScanner;

    @ViewInject(R.id.type)
    private TextView tvType;

    @ViewInject(R.id.lexeme)
    private TextView tvLexeme;

    @ViewInject(R.id.value)
    private TextView tvValue;

    @ViewInject(R.id.bt_parser)
    private Button btParser;

    @ViewInject(R.id.fun)
    private TextView tvFun;

    @ViewInject(R.id.ll_scan)
    private LinearLayout llScan;

    @ViewInject(R.id.ll_parse)
    private LinearLayout llParse;

    @ViewInject(R.id.ilog)
    private TextView tvILog;

    @ViewInject(R.id.elog)
    private TextView tvELog;

    @ViewInject(R.id.iv)
    private ImageView iv;

    @ViewInject(R.id.origin)
    private ImageView origin;

    private Token token;
    private Scanner mScanner;
    private Parser mParser;
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;
    private int left;
    private int top;

    private final static int TYPE = 1;
    private final static int LEXEME = 2;
    private final static int VALUE = 3;
    private final static int FUN = 4;
    private final static int SCAN = 5;
    private final static int PARSER = 6;
    public final static int DRAW = 7;
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
                    break;
                case PARSER:
                    tvILog.setText("分析日志" + mParser.getILog());
                    int err = mParser.getErrCount();
                    if(err == 0)
                        tvELog.setTextColor(0xff000000);
                    else
                        tvELog.setTextColor(0xffff4444);
                    tvELog.setText("错误日志" + mParser.getELog() + "\nError   : " + mParser.getErrCount());
                    btParser.setClickable(true);
                    break;
                case DRAW:
                    Bundle coordinate = msg.getData();
                    double[] result = coordinate.getDoubleArray("coordinate");
                    float x = (float) (result[0] + left);
                    float y = (float) (top - result[1]);
                    canvas.drawPoint(x, y, paint);
                    iv.setImageBitmap(bitmap);
                    LogUtils.e("DRAW");
                    break;
            }
        }
    };
    private void initCanvas(){
        iv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(Build.VERSION.SDK_INT<16){
                    iv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }else{
                    iv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                bitmap = Bitmap.createBitmap(iv.getWidth(), iv.getHeight(), Bitmap.Config.ARGB_8888);
                canvas = new Canvas(bitmap);
                canvas.drawColor(Color.WHITE);
                paint = new Paint();
                paint.setColor(Color.RED);
                paint.setStrokeWidth(5);
            }
        });
        origin.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(Build.VERSION.SDK_INT<16){
                    origin.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }else{
                    origin.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                left = origin.getLeft();
                top = origin.getTop();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);

        initCanvas();



        file = BaseApplication.getFile();

        mScanner = new Scanner();
        mParser = new Parser(mScanner, mHandler);
        btScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mScanner.initScanner(file)){
                    btScanner.setClickable(false);
                    llParse.setVisibility(View.GONE);
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
        btParser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btParser.setClickable(false);
                llScan.setVisibility(View.GONE);
                llParse.setVisibility(View.VISIBLE);

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
}
