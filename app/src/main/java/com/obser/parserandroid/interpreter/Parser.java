package com.obser.parserandroid.interpreter;

import android.os.Handler;

import com.obser.parserandroid.bean.ExprNode;
import com.obser.parserandroid.bean.Parameter;
import com.obser.parserandroid.bean.Token;
import com.obser.parserandroid.bean.TokenData;
import com.obser.parserandroid.global.BaseApplication;
import com.obser.parserandroid.utils.LogUtils;

/**
 * Created by Administrator on 2016/12/4 0004.
 */
public class Parser {
    private Scanner scanner;
    private Semantic semantic;
    private Token token;                    // 记号
    private double  origin_x=0,origin_y=0,  // 横、纵平移距离
                    scale_x=1, scale_y=1,   // 横、纵比例因子
                    rot_angle=0;            // 旋转角度
    public static Parameter parameter = new Parameter(0);           // 参数T的存储空间
    private StringBuffer iLog;
    private StringBuffer eLog;
    private static boolean DEBUG = true;
    public void setFlag(boolean flag){
        DEBUG = flag;
    }
    public Parser(Scanner scanner){
        this.scanner = scanner;
        semantic = new Semantic();
        iLog = new StringBuffer();
        eLog = new StringBuffer();
    }
    public void setHandler(Handler handler){
        semantic.setHandler(handler);
    }
    public void parser(){
        LogUtils.i("enter in parser");
        iLog.append("\n");iLog.append("enter in parser");
        if(!scanner.initScanner(BaseApplication.getFile())) return ;// 初始化词法分析器
        fetchToken();                       // 获取第一个记号
        program();                          // 递归下降分析
        scanner.closeScanner();             // 关闭词法分析器
        LogUtils.i("exit from parser");
        iLog.append("\n");iLog.append("exit from parser");
    }

    /* Program的递归子程序 */
    private void program() {
        LogUtils.i("enter in program");
        iLog.append("\n");iLog.append("enter in program");
        while(token.getType() != TokenData.Token_Type.NONTOKEN){
            statement();
            matchToken(TokenData.Token_Type.SEMICO);
        }
        LogUtils.i("exit from program");
        iLog.append("\n");iLog.append("exit from program");
    }

    /* 匹配记号 */
    private void matchToken(TokenData.Token_Type type){
        if(token.getType() != type) syntaxError(2);
        fetchToken();
    }

    /* statement的递归子程序 */
    private void statement(){
        LogUtils.i("enter in statement");
        iLog.append("\n");iLog.append("enter in statement");
        switch (token.getType()){
            case ORIGIN  : originStatement();  break;
            case SCALE   :  scaleStatement();  break;
            case ROT     :	  rotStatement();  break;
            case FOR     :	  forStatement();  break;
            default      :                     break;
//            default      :	  syntaxError(2);
        }
        LogUtils.i("exit from statement");
        iLog.append("\n");iLog.append("exit from statement");
    }

    /* OriginStatement的递归子程序 */
    private void originStatement() {
        LogUtils.i("enter in originStatement");
        iLog.append("\n");iLog.append("enter in originStatement");

        ExprNode tmp;

        matchToken(TokenData.Token_Type.ORIGIN);
        matchToken(TokenData.Token_Type.IS);
        matchToken(TokenData.Token_Type.L_BRACKET);
        tmp = expression();

        origin_x = semantic.getExprValue(tmp);           // 获取横坐标的平移距离
//        delExprTree(tmp);
        tmp = null;

        matchToken(TokenData.Token_Type.COMMA);
        tmp = expression();

        origin_y = semantic.getExprValue(tmp);           // 获取纵坐标的平移距离
//        delExprTree(tmp);
        tmp = null;

        matchToken(TokenData.Token_Type.R_BRACKET);

        LogUtils.i("exit from originStatement");
        iLog.append("\n");iLog.append("exit from originStatement");
    }



    /* ScaleStatement的递归子程序 */
    private void scaleStatement() {
        LogUtils.i("enter in scaleStatement");
        iLog.append("\n");iLog.append("enter in scaleStatement");
        ExprNode tmp;

        matchToken(TokenData.Token_Type.SCALE);
        matchToken(TokenData.Token_Type.IS);
        matchToken(TokenData.Token_Type.L_BRACKET);
        tmp = expression();

        scale_x = semantic.getExprValue(tmp);        // 获取横坐标的比例因子
//        delExprTree(tmp);
        tmp = null;

        matchToken(TokenData.Token_Type.COMMA);
        tmp = expression();

        scale_y = semantic.getExprValue(tmp);        // 获取纵坐标的比例因子
//        delExprTree(tmp);
        tmp = null;

        matchToken(TokenData.Token_Type.R_BRACKET);

        LogUtils.i("exit from scaleStatement");
        iLog.append("\n");iLog.append("exit from scaleStatement");

    }
    /* RotStatement的递归子程序 */
    private void rotStatement() {
        LogUtils.i("enter in rotStatement");
        iLog.append("\n");iLog.append("enter in rotStatement");

        ExprNode tmp;
        matchToken(TokenData.Token_Type.ROT);
        matchToken(TokenData.Token_Type.IS);
        tmp = expression();

        rot_angle = semantic.getExprValue(tmp);      // 获取旋转角度
//        delExprTree(tmp);
        tmp = null;

        LogUtils.i("exit from rotStatement");
        iLog.append("\n");iLog.append("exit from rotStatement");
    }
    /* ForStatement 的递归子程序 */
    private void forStatement() {
        LogUtils.i("enter in forStatement");
        iLog.append("\n");iLog.append("enter in forStatement");
        double start, end, step;                                // 绘图起点、终点、步长
        ExprNode start_ptr, end_ptr, step_ptr, x_ptr, y_ptr;    // 各表达式语法树根节点指针

        matchToken(TokenData.Token_Type.FOR);   call_match("FOR");
        matchToken(TokenData.Token_Type.T);     call_match("T");
        matchToken(TokenData.Token_Type.FROM);  call_match("FROM");
        start_ptr = expression();                               // 构造参数起始表达式语法树

        start = semantic.getExprValue(start_ptr);               // 计算参数起始表达式的值
//        delExprTree(start_ptr);
        start_ptr = null;                                       // 释放参数起始语法树所占空间

        matchToken(TokenData.Token_Type.TO);    call_match("TO");
        end_ptr = expression();                                 // 构造参数终结表达式语法树

        end = semantic.getExprValue(end_ptr);                   // 计算参数结束表达式的值
//        delExprTree(end_ptr);
        end_ptr = null;                                         // 释放参数结束语法树所占空间

        matchToken(TokenData.Token_Type.STEP);  call_match("STEP");
        step_ptr = expression();                                // 构造参数步长表达式语法树

        step = semantic.getExprValue(step_ptr);                // 计算参数步长表达式的值
//        delExprTree(step_ptr);
        step_ptr = null;                                        // 释放参数步长语法树所占空间

        matchToken(TokenData.Token_Type.DRAW);      call_match("DRAW");
        matchToken(TokenData.Token_Type.L_BRACKET); call_match("(");
        x_ptr = expression();                                   // 构造横坐标表达式语法树
        matchToken(TokenData.Token_Type.COMMA); call_match(",");
        y_ptr = expression();                                   // 构造纵坐标表达式语法树
        matchToken(TokenData.Token_Type.R_BRACKET); call_match(")");

        if(!DEBUG){
            semantic.init(origin_x, origin_y, scale_x, scale_y, rot_angle);
            semantic.drawLoop(start, end, step, x_ptr, y_ptr);      // 绘制图形
        }

//        delExprTree(x_ptr);
        x_ptr = null;                                           // 释放横坐标语法树所占空间
//        delExprTree(y_ptr);
        y_ptr = null;                                           // 释放纵坐标语法树所占空间

        LogUtils.i("exit from forStatement");
        iLog.append("\n");iLog.append("exit from forStatement");

    }

    private void call_match(String string) {
        LogUtils.i("matchtoken      " + string);
        iLog.append("\n");iLog.append("matchtoken      " + string);
    }

    /* Expression 的递归子程序 */
    private ExprNode expression(){
        LogUtils.i("enter in expression");
        iLog.append("\n");iLog.append("enter in expression");

        ExprNode left;                      //左子树节点
        ExprNode right;                     //右子树节点
        TokenData.Token_Type token_tmp;     //当前记号

        left = term();                      //分析左操作数且得到其语法树
        while(token.getType() == TokenData.Token_Type.PLUS || token.getType() == TokenData.Token_Type.MINUS) {
            token_tmp = token.getType();
            matchToken(token_tmp);
            right = term();                 //分析右操作数且得到其语法树
            left = makeExprNode(token_tmp, left, right);
                                         //构造运算的语法树，结果为左子树
        }
        if(left != null)
            tree_trace(left);                   //打印表达式的语法树
        LogUtils.i("exit from expression");
        iLog.append("\n");iLog.append("exit from expression");
        return left;                        //返回最终表达式的语法树
    }

    private void tree_trace(ExprNode exprNode) {
        printSyntaxTree(exprNode, 1);
    }

    /* Term 的递归子程序 */
    private ExprNode term() {

        ExprNode left;
        ExprNode right;
        TokenData.Token_Type token_tmp;

        left = factor();
        while(token.getType() == TokenData.Token_Type.MUL || token.getType() == TokenData.Token_Type.DIV){
            token_tmp = token.getType();
            matchToken(token_tmp);
            right = factor();
            left = makeExprNode(token_tmp, left, right);
        }

        return left;
    }

    /* Factor的递归子程序 */
    private ExprNode factor() {

        ExprNode left;
        ExprNode right;

        if(token.getType() == TokenData.Token_Type.PLUS){           // 匹配一元加运算
            matchToken(TokenData.Token_Type.PLUS);
            right = factor();                                       // 表达式退化为仅有右操作数的表达式
        } else if (token.getType() == TokenData.Token_Type.MINUS){  // 匹配一元减运算
            matchToken(TokenData.Token_Type.MINUS);                 // 表达式转化为二元减运算的表达式
            right = factor();
            left = new ExprNode();
            left.setOpCode(TokenData.Token_Type.CONST_ID);
            left.setCaseConst(0);
            right = makeExprNode(TokenData.Token_Type.MINUS, left, right);
        } else
        right = component();                                        // 匹配非终结符Component

        return right;
    }

    /* Component的递归子程序 */
    private ExprNode component() {
        ExprNode left;
        ExprNode right;

        left = atom();
        if(token.getType() == TokenData.Token_Type.POWER){
            matchToken(TokenData.Token_Type.POWER);
            right = component();                                    // 递归调用Component以实现POWER的右结合
            left = makeExprNode(TokenData.Token_Type.POWER, left, right);
        }

        return left;
    }

    /* Atom的递归子程序 */
    private ExprNode atom() {

        Token t = token;
        ExprNode address = null;
        ExprNode tmp;
        switch(token.getType()){
            case CONST_ID:
                matchToken(TokenData.Token_Type.CONST_ID);
                address = makeExprNode(TokenData.Token_Type.CONST_ID, t.getValue());
                break;
            case T:
                matchToken(TokenData.Token_Type.T);
                address = makeExprNode(TokenData.Token_Type.T);
                break;
            case FUNC:
                matchToken(TokenData.Token_Type.FUNC);
                matchToken(TokenData.Token_Type.L_BRACKET);
                tmp = expression();
                address = makeExprNode(TokenData.Token_Type.FUNC, t.getFun(), tmp);
                matchToken(TokenData.Token_Type.R_BRACKET);
                break;
            case L_BRACKET:
                matchToken(TokenData.Token_Type.L_BRACKET);
                address = expression();
                matchToken(TokenData.Token_Type.R_BRACKET);
                break;
            default:
                syntaxError(2);
        }

        return address;
    }

    /* 生成语法树的一个节点 */
    private ExprNode makeExprNode(TokenData.Token_Type opCode, Object... args){
        ExprNode exprNode = new ExprNode();
        exprNode.setOpCode(opCode);
        switch (opCode){
            case CONST_ID:
                exprNode.setCaseConst((Double) args[0]);
                break;
            case T:
                exprNode.setCaseParmPtr(parameter);
                break;
            case FUNC:
                exprNode.setMathFuncPtr((String) args[0]);
                exprNode.setChild((ExprNode) args[1]);
                break;
            default:
                exprNode.setLeft((ExprNode) args[0]);
                exprNode.setRight((ExprNode) args[1]);
                break;
        }

        return exprNode;
    }




    /* 通过词法分析器接口GetToken获取一个记号 */
    private void fetchToken(){
        token = scanner.getToken();
        if(token.getType() == TokenData.Token_Type.ERRTOKEN) syntaxError(1);
    }
    /* 语法错误处理 */
    private int errCount = 0;
    private void syntaxError(int case_of){
        errCount ++;
        switch (case_of) {
            case 1:
                LogUtils.e("Line " + scanner.LineNo + " : " + token.getLexeme() + " 错误记号 ");
                eLog.append("\n");eLog.append("Line " + scanner.LineNo + " : " + token.getLexeme() + " 是错误记号 ");
//                scanner.closeScanner();
                break;
            case 2:
                LogUtils.e("Line " + scanner.LineNo + " : "+  token.getLexeme() + " 不是预期记号" );
                eLog.append("\n");eLog.append("Line " + scanner.LineNo + " : "+  token.getLexeme() + " 不是预期记号" );
//                scanner.closeScanner();
                break;
        }
    }

    private void printSyntaxTree(ExprNode root, int indent){
        for(int temp = 1; temp <= indent; temp++)   LogUtils.i("");// 缩进
        switch (root.getOpCode()){                                 // 打印根节点
            case PLUS:  LogUtils.i("+");iLog.append("\n");iLog.append("+");    break;
            case MINUS: LogUtils.i("-");iLog.append("\n");iLog.append("-");    break;
            case MUL:   LogUtils.i("*");iLog.append("\n");iLog.append("*");    break;
            case DIV:   LogUtils.i("/");iLog.append("\n");iLog.append("/");    break;
            case POWER: LogUtils.i("**");iLog.append("\n");iLog.append("**");   break;
            case FUNC:  LogUtils.i(root.getMathFuncPtr() + ""); iLog.append("\n");iLog.append(root.getMathFuncPtr() + "");break;
            case CONST_ID:  LogUtils.i(root.getCaseConst() + "");iLog.append("\n");iLog.append(root.getCaseConst() + "");    break;
            case T:     LogUtils.i("T");iLog.append("\n");iLog.append("T");    break;
            default:    LogUtils.e("Error Tree Node !");eLog.append("\n");eLog.append("Error Tree Node !");
        }
        if(root.getOpCode() == TokenData.Token_Type.CONST_ID || root.getOpCode() == TokenData.Token_Type.T){// 叶子节点返回
            return;
        }
        if(root.getOpCode() == TokenData.Token_Type.FUNC){          // 递归打印一个孩子的节点
            printSyntaxTree(root.getChild(), indent + 1);
        } else {                                                    // 递归打印两个孩子的节点
          printSyntaxTree(root.getLeft(), indent + 1);
          printSyntaxTree(root.getRight(),indent + 1);
        }
    }
    public String getILog(){
        String i = iLog.toString();
        iLog.delete(0, iLog.length());
        return i;
    }
    public String getELog(){
        String e = eLog.toString();
        eLog.delete(0, eLog.length());
        return e;
    }
    public int getErrCount(){
        int e = errCount;
        errCount = 0;
        return e;
    }

}
