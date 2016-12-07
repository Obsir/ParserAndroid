package com.obser.parserandroid.bean;
import com.obser.parserandroid.bean.TokenData.Token_Type;
/**
 * Created by Administrator on 2016/12/3 0003.
 */
public class Token {        // 记号与符号表结构

    private Token_Type type;// 记号的类别
    private String lexeme;  // 构成记号的字符串
    private double value;   // 若为常数，则是常数的值
    private String fun;     // 若为函数，则是函数的指针

    public Token(){
        type = null;
        lexeme = "";
        value = 0;
        fun = null;
    }

    public Token(Token_Type type, String lexeme, double value, String fun) {
        this.type = type;
        this.lexeme = lexeme;
        this.value = value;
        this.fun = fun;
    }

    public Token_Type getType() {
        return type;
    }

    public void setType(TokenData.Token_Type type) {
        this.type = type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getFun() {
        return fun;
    }

    public void setFun(String fun) {
        this.fun = fun;
    }
}

