package com.obser.parserandroid.utils;

/**
 * Created by Administrator on 2016/12/5 0005.
 */
public class FunUtils {
    private static final String SIN = "sin",
                                COS = "cos",
                                TAN = "tan",
                                LN  = "ln" ,
                                EXP = "exp",
                                SQRT= "sqrt";
    public static double matchFun(String fun, double arg){
        if(fun.equalsIgnoreCase(SIN))
            return Math.sin(arg);
        else if(fun.equalsIgnoreCase(COS))
            return Math.cos(arg);
        else if(fun.equalsIgnoreCase(TAN))
            return Math.tan(arg);
        else if(fun.equalsIgnoreCase(LN))
            return Math.log(arg);
        else if(fun.equalsIgnoreCase(EXP))
            return Math.exp(arg);
        else if(fun.equalsIgnoreCase(SQRT))
            return Math.sqrt(arg);
        return 0;
    }
}
