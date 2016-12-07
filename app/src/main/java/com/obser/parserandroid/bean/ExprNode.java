package com.obser.parserandroid.bean;

/**
 * 语法树节点类型
 * Created by Administrator on 2016/12/4 0004.
 */
public class ExprNode {
    // PLUS, MINUS, MUL, DIV, POWER, FUNC, CONST_ID等
    private TokenData.Token_Type opCode;
    private ExprNode left;
    private ExprNode right;
    private ExprNode child;
    private double caseConst;
    private String mathFuncPtr;
    private Parameter caseParmPtr;


    public ExprNode(){
        opCode = null;
        left = null;
        right = null;
        child = null;
        caseParmPtr = new Parameter(0);
        mathFuncPtr = null;
        caseConst = 0;
    }

    public TokenData.Token_Type getOpCode() {
        return opCode;
    }

    public void setOpCode(TokenData.Token_Type opCode) {
        this.opCode = opCode;
    }

    public ExprNode getLeft() {
        return left;
    }

    public void setLeft(ExprNode left) {
        this.left = left;
    }

    public ExprNode getRight() {
        return right;
    }

    public void setRight(ExprNode right) {
        this.right = right;
    }

    public ExprNode getChild() {
        return child;
    }

    public void setChild(ExprNode child) {
        this.child = child;
    }

    public double getCaseConst() {
        return caseConst;
    }

    public void setCaseConst(double caseConst) {
        this.caseConst = caseConst;
    }

    public String getMathFuncPtr() {
        return mathFuncPtr;
    }

    public void setMathFuncPtr(String mathFuncPtr) {
        this.mathFuncPtr = mathFuncPtr;
    }

    public Parameter getCaseParmPtr() {
        return caseParmPtr;
    }

    public void setCaseParmPtr(Parameter caseParmPtr) {
        this.caseParmPtr = caseParmPtr;
    }
}
