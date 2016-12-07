package com.obser.parserandroid.bean;


import java.util.ArrayList;

/**
 * Created by Administrator on 2016/12/3 0003.
 */
public class TokenData {
    public enum Token_Type{						// 记号种类
        ORIGIN, SCALE, ROT, IS,   TO,			// 保留字
        STEP,   DRAW,  FOR,	FROM,				// 保留字
        T,										// 参数
        SEMICO,L_BRACKET, R_BRACKET, COMMA,		// 分隔符号
        PLUS, MINUS, MUL, DIV, POWER,			// 运算符
        FUNC,									// 函数
        CONST_ID,								// 常数
        NONTOKEN,								// 空记号
        ERRTOKEN;                                // 出错记号
    }

    public ArrayList<Token> TokenTab;// 符号表内容

    public TokenData() {
        TokenTab = new ArrayList<Token>();
        TokenTab.add(new Token(Token_Type.CONST_ID,	    "PI",		3.1415926,	null));
        TokenTab.add(new Token(Token_Type.CONST_ID,	     "E",		2.71828,	null));
        TokenTab.add(new Token(Token_Type.T,		     "T",		0.0,		null));
        TokenTab.add(new Token(Token_Type.FUNC,		   "SIN",		0.0,		"sin"));
        TokenTab.add(new Token(Token_Type.FUNC,		   "COS",		0.0,		"cos"));
        TokenTab.add(new Token(Token_Type.FUNC,		   "TAN",		0.0,		"tan"));
        TokenTab.add(new Token(Token_Type.FUNC,		    "LN",		0.0,		"ln"));
        TokenTab.add(new Token(Token_Type.FUNC,		   "EXP",		0.0,		"exp"));
        TokenTab.add(new Token(Token_Type.FUNC,	      "SQRT",		0.0,		"sqrt"));
        TokenTab.add(new Token(Token_Type.ORIGIN,   "ORIGIN",	    0.0,		null));
        TokenTab.add(new Token(Token_Type.SCALE,	 "SCALE",	    0.0,		null));
        TokenTab.add(new Token(Token_Type.ROT,		   "ROT",		0.0,		null));
        TokenTab.add(new Token(Token_Type.IS,		    "IS",		0.0,		null));
        TokenTab.add(new Token(Token_Type.FOR,		   "FOR",		0.0,		null));
        TokenTab.add(new Token(Token_Type.FROM,	      "FROM",		0.0,		null));
        TokenTab.add(new Token(Token_Type.TO,		    "TO",		0.0,		null));
        TokenTab.add(new Token(Token_Type.STEP,	      "STEP",		0.0,		null));
        TokenTab.add(new Token(Token_Type.DRAW,	      "DRAW",		0.0,		null));
    }




}
