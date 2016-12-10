package com.obser.parserandroid.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.obser.parserandroid.R;
import com.obser.parserandroid.activity.MainActivity;
import com.obser.parserandroid.interpreter.Parser;
import com.obser.parserandroid.utils.LogUtils;

import io.techery.progresshint.ProgressHintDelegate;
import io.techery.progresshint.addition.widget.SeekBar;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Administrator on 2016/12/7 0007.
 */
public class DrawFragment extends Fragment {
    @ViewInject(R.id.iv)
    private ImageView iv;
    @ViewInject(R.id.origin)
    private ImageView origin;
    @ViewInject(R.id.sb_hor)
    private SeekBar sbHor;
    @ViewInject(R.id.sb_ver)
    private ProgressHintDelegate.SeekBarHintDelegateHolder sbVer;
    @ViewInject(R.id.sb_root)
    private RelativeLayout sbRoot;

    private FloatingActionMenu faMenu;
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;
    private float left;
    private float top;
    public final static int DRAW = 7;
    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case DRAW:

                    left = (float) (origin.getX() + 0.5 * origin.getWidth());
                    top = (float) (origin.getY() + 0.5 * origin.getHeight());

                    Bundle coordinate = msg.getData();
//                    double[] result = coordinate.getDoubleArray("coordinate");
//                    float x = (float) (result[0] + left);
//                    float y = (float) (top - result[1]);
//                    canvas.drawPoint(x, y, paint);
//                    float[] result = CoordData.getPts();
                    float[] result = coordinate.getFloatArray("coordinate");
                    float[] pts = calFloat(result);
//                    for(float data : pts){
//                        LogUtils.d(data + "");
//                    }
                    canvas.drawPoints(pts, paint);
                    iv.setImageBitmap(bitmap);
                    mAttacher.update();
                    LogUtils.e("DRAW");
                    break;
            }
        }
    };



    private MainActivity mActivity;
    private Parser mParser;
    private FloatingActionButton btDraw;
    private PhotoViewAttacher mAttacher;
    private FloatingActionButton btDot;
    private int width;
    private int height;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        mActivity = (MainActivity) getActivity();
        View view = View.inflate(mActivity, R.layout.fragment_draw, null);
        ViewUtils.inject(this, view);
        initCanvas();
        return view;
    }

    private void initCanvas(){
        iv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(Build.VERSION.SDK_INT<16){
                    iv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }else{
                    iv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                width = iv.getWidth();
                height = iv.getHeight();

                bitmap = Bitmap.createBitmap(iv.getWidth(), iv.getHeight(), Bitmap.Config.ARGB_8888);
                canvas = new Canvas(bitmap);
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                paint = new Paint();
                paint.setColor(Color.BLUE);
                paint.setStrokeWidth(5);
                paint.setAntiAlias(true);
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
                left = (float) (origin.getX() + 0.5 * origin.getWidth());
                top = (float) (origin.getY() + 0.5 * origin.getHeight());
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mParser.setHandler(mHandler);

        btDraw = mActivity.getBtDraw();
        faMenu = mActivity.getFaMenu();
        btDot = mActivity.getBtDot();
        mAttacher = new PhotoViewAttacher(iv);
//        clPaint = new Paint();
//        clPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//        canvas.drawPaint(clPaint);
//        clPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

        btDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(faMenu != null && faMenu.isOpened()){
                    faMenu.close(true);
                }
                if(sbRoot.getVisibility() == View.VISIBLE){
                    sbRoot.setVisibility(View.GONE);
                } else {
                    sbRoot.setVisibility(View.VISIBLE);
                }
            }
        });
        btDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(faMenu != null && faMenu.isOpened()){
                    faMenu.close(true);
                }
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                mParser.setFlag(false);
                new Thread(){
                    @Override
                    public void run() {
                        mParser.parser();
                    }
                }.start();
            }
        });
        sbHor.getHintDelegate().setHintAdapter(new ProgressHintDelegate.SeekBarHintAdapter() {
            @Override
            public String getHint(android.widget.SeekBar seekBar, int progress) {
                origin.setX((float) (progress/100.0 * ( width - origin.getWidth() )));
                return null;
            }
        });
        sbVer.getHintDelegate().setHintAdapter(new ProgressHintDelegate.SeekBarHintAdapter() {
            @Override
            public String getHint(android.widget.SeekBar seekBar, int progress) {
                origin.setY((float) (progress/100.0 * ( height - origin.getHeight() )));
                return null;
            }
        });


    }

    private float[] calFloat(float[] pts){
        float[] calPts = new float[pts.length];
        for(int i = 0; i < pts.length ; i ++){
            if(i%2==0)
                calPts[i] = pts[i] + left;
            else
                calPts[i] = top - pts[i];
        }
        return calPts;
    }
    public void setmParser(Parser parser) {
        this.mParser = parser;
    }
}
