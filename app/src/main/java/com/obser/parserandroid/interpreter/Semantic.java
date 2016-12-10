package com.obser.parserandroid.interpreter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.obser.parserandroid.bean.ExprNode;
import com.obser.parserandroid.fragment.DrawFragment;
import com.obser.parserandroid.utils.FunUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/12/5 0005.
 */
public class Semantic {

    private double
            origin_x=0,origin_y=0,  // 横、纵平移距离
            scale_x=1, scale_y=1,   // 横、纵比例因子
            rot_angle=0;            // 旋转角度
    private Handler handler;
    private int left, top;

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public Semantic(){

    }

    /* 计算被绘制点的坐标 */
    public double[] calcCoord(ExprNode hor_Exp, ExprNode ver_Exp){
        double horCord, verCord, hor_tmp;

        // 计算表达式的值，得到点的原始坐标
        horCord = getExprValue(hor_Exp);
        verCord = getExprValue(ver_Exp);

        // 进行比例变换
        horCord *= scale_x;
        verCord *= scale_y;

        // 进行旋转变换
        hor_tmp = horCord * Math.cos(rot_angle) - verCord * Math.sin(rot_angle);
        verCord = verCord * Math.cos(rot_angle) + horCord * Math.sin(rot_angle);
        horCord = hor_tmp;

        // 进行平移变换
        horCord += origin_x;
        verCord += origin_y;

        // 返回变换后点的坐标
        double[] result = new double[]{horCord, verCord};
        return result;
    }

    /* 循环绘制点坐标 */
//    public void drawLoop(double start, double end, double step, ExprNode horPtr, ExprNode verPtr){
//
//        for(Parser.parameter.caseParmPtr = start; Parser.parameter.caseParmPtr <= end; Parser.parameter.caseParmPtr += step){
//            double[] result = calcCoord(horPtr, verPtr);
//            drawPixel(result);
//        }
//    }

    public void drawLoop(double start, double end, double step, ExprNode horPtr, ExprNode verPtr){
        ArrayList<Float> pts = new ArrayList<Float>();
        for(Parser.parameter.caseParmPtr = start; Parser.parameter.caseParmPtr <= end; Parser.parameter.caseParmPtr += step){
            double[] result = calcCoord(horPtr, verPtr);
            pts.add((float) result[0]);
            pts.add((float) result[1]);
        }
        drawPixel(pts);
    }

//    public void drawLoop(double start, double end, double step, ExprNode horPtr, ExprNode verPtr){
//        ArrayList<Float> pts = new ArrayList<Float>();
//        for(Parser.parameter.caseParmPtr = start; Parser.parameter.caseParmPtr <= end; Parser.parameter.caseParmPtr += step){
//            double[] result = calcCoord(horPtr, verPtr);
//            if(Parser.parameter.caseParmPtr == start){
//                pts.add((float) result[0]);
//                pts.add((float) result[1]);
//            } else if(Parser.parameter.caseParmPtr == end){
//                pts.add((float) result[0]);
//                pts.add((float) result[1]);
//                drawPixel(pts);
//            } else {
//                pts.add((float) result[0]);
//                pts.add((float) result[1]);
//                pts.add((float) result[0]);
//                pts.add((float) result[1]);
//            }
//
//        }
//    }

    private void drawPixel(ArrayList<Float> list) {
        int size = list.size();
        float[] pts = new float[size];
        for(int i = 0; i < size; i ++){
            pts[i] = list.get(i);
        }
//        CoordData.setPts(pts);
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putFloatArray("coordinate", pts);
        message.what = DrawFragment.DRAW;
        message.setData(bundle);
        handler.sendMessage(message);
//        Message message = Message.obtain();
//        Bundle bundle = new Bundle();
//        message.what = DrawFragment.DRAW;
//        bundle.putFloatArray("coordinate", pts);
//        message.setData(bundle);
//        handler.sendMessage(message);
    }


    private void drawPixel(double[] result) {
        Message message = Message.obtain();
        Bundle bundle = new Bundle();
        message.what = DrawFragment.DRAW;
        bundle.putDoubleArray("coordinate", result);
        message.setData(bundle);
        handler.sendMessage(message);
    }

    /* 计算表达式的值 */
    public double getExprValue(ExprNode root){
        if(root == null)    return 0;
        switch (root.getOpCode()){
            case PLUS:
                return getExprValue(root.getLeft()) + getExprValue(root.getRight());
            case MINUS:
                return getExprValue(root.getLeft()) - getExprValue(root.getRight());
            case MUL:
                return getExprValue(root.getLeft()) * getExprValue(root.getRight());
            case DIV:
                return getExprValue(root.getLeft()) / getExprValue(root.getRight());
            case POWER:
                return Math.pow(getExprValue(root.getLeft()), getExprValue(root.getRight()));
            case FUNC:
                return FunUtils.matchFun(root.getMathFuncPtr(), getExprValue(root.getChild()));
            case CONST_ID:
                return root.getCaseConst();
            case T:
                return root.getCaseParmPtr().caseParmPtr;
            default:
                return 0;
        }
    }

    public void init(double origin_x, double origin_y, double scale_x, double scale_y, double rot_angle) {
        this.origin_x = origin_x;
        this.origin_y = origin_y;
        this.scale_x = scale_x;
        this.scale_y = scale_y;
        this.rot_angle = rot_angle;
    }

//    /* 删除一棵语法树 */
//    public static void delExprTree(ExprNode root){
//        if(root == null)    return;
//        switch(root.getOpCode()){
//            case PLUS:              // 两个孩子的内部节点
//            case MINUS:
//            case MUL:
//            case DIV:
//            case POWER:
//                delExprTree(root.getLeft());
//                delExprTree(root.getRight());
//                break;
//            case FUNC:              // 一个孩子的内部节点
//                delExprTree(root.getChild());
//                break;
//            default:                // 叶子节点
//                break;
//        }
//                                    // 删除叶子节点
//    }
}
