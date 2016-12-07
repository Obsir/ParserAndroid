package com.obser.parserandroid;

import com.lidroid.xutils.util.IOUtils;
import com.obser.parserandroid.bean.Token;
import com.obser.parserandroid.bean.TokenData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2016/12/3 0003.
 */
public class Scanner {

    public int LineNo;
    public StringBuffer buff;
    private File inFile;
    private FileInputStream in;
    private BufferedReader br;
    private final static int LIMIT = 30;
    private TokenData tokenData;

    /**
     * 初始化词法分析器
     * @param file
     * @return
     */
    public boolean initScanner(File file){
        LineNo = 1;
        tokenData = new TokenData();
        inFile = file;
//        inFile = new File(fileName);
        buff = new StringBuffer();
        try {
            if(inFile.exists()){
                br = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
                return true;
            } else
                return false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 关闭词法分析器
     */
    public void closeScanner(){
        if(br != null){
            IOUtils.closeQuietly(br);
        }
    }

    /**
     * 从输入源程序中读入一个字符
     * @return
     */
    public int getChar(){
        try {
            br.mark(LIMIT);
            int c = br.read();
            return c;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 把预读的字符回退到输入源程序中
     */
    public void backChar(){
        try {
            br.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加入字符到记号缓冲区
     * @param c
     */
    public void addCharTokenString(char c){
        buff.append(c);
    }

    /**
     * 清空记号缓冲区
     */
    public void emptyTokenString(){
        int length = buff.length();
        buff.delete(0, length);
    }

    /**
     * 判断所给的字符串是否在符号表中
     * @param id
     * @return
     */
    public Token isKeyToken(String id) {
        for ( Token token : tokenData.TokenTab ) {
            if(id.equalsIgnoreCase(token.getLexeme())){
                return token;
            }
        }
        Token token = new Token();
        token.setType(TokenData.Token_Type.ERRTOKEN);
        return token;
    }

    public Token getToken() {

        Token token = new Token();
        int c;
        char ch;
        emptyTokenString();

        //过滤源程序中的空格、TAB、回车等，遇到文件结束符返回空
        for(;;){
            c = getChar();
            ch = (char) c;
            if(c == -1) {
                token.setType(TokenData.Token_Type.NONTOKEN);
                return token;
            }
            if(ch == '\n') LineNo++;
            if(!Character.isWhitespace(ch)) break;
        }

        //若不是空格、TAB、回车、文件结束符等，则先加入到记号的字符缓冲区中
        addCharTokenString(ch);

        // 若char是A-Za-z，则它一定是函数、关键字、PI、E等
        if(Character.isLetter(ch)){
            for(;;){
                ch = (char) getChar();
                if(Character.isLetterOrDigit(ch)) addCharTokenString(ch);
                else break;
            }
            backChar();
            token = isKeyToken(buff.toString());
            token.setLexeme(buff.toString());
            return token;
        } else if(Character.isDigit(ch)) {  // 若是一个数字，则一定是常量
            for(;;){
                ch = (char) getChar();
                if(Character.isDigit(ch)) addCharTokenString(ch);
                else break;
            }
            if(ch == '.'){
                addCharTokenString(ch);
                for(;;){
                    ch = (char) getChar();
                    if(Character.isDigit(ch)) addCharTokenString(ch);
                    else break;
                }
            }
            backChar();
            token.setType(TokenData.Token_Type.CONST_ID);
            token.setLexeme(buff.toString());
            token.setValue(Integer.parseInt(buff.toString()));
            return token;
        } else {    //不是字母和数字，则一定是符号
            switch(ch) {
                case ';' : token.setType(TokenData.Token_Type.SEMICO)    ;token.setLexeme(buff.toString()); break;
                case '(' : token.setType(TokenData.Token_Type.L_BRACKET) ;token.setLexeme(buff.toString()); break;
                case ')' : token.setType(TokenData.Token_Type.R_BRACKET) ;token.setLexeme(buff.toString()); break;
                case ',' : token.setType(TokenData.Token_Type.COMMA)     ;token.setLexeme(buff.toString()); break;
                case '+' : token.setType(TokenData.Token_Type.PLUS)      ;token.setLexeme(buff.toString()); break;
                case '-' :
                    c= getChar();
                    ch = (char) c;
                    if(ch == '-'){
                        while(ch != '\n' && c != -1){
                            c= getChar();
                            ch = (char) c;
                        }
                        backChar();
                        return getToken();
                    } else {
                        backChar();
                        token.setType(TokenData.Token_Type.MINUS);
                        token.setLexeme(buff.toString());
                        break;
                    }
                case '/' :
                    c= getChar();
                    ch = (char) c;
                    if(ch == '/'){
                        while(ch != '\n' && c != -1){
                            c= getChar();
                            ch = (char) c;
                        }
                        backChar();
                        return getToken();
                    } else {
                        backChar();
                        token.setType(TokenData.Token_Type.DIV);
                        token.setLexeme(buff.toString());
                        break;
                    }
                case '*' :
                    ch = (char) getChar();
                    if(ch == '*'){
                        addCharTokenString(ch);
                        token.setType(TokenData.Token_Type.POWER);
                        token.setLexeme(buff.toString());
                        break;
                    } else {
                        backChar();
                        token.setType(TokenData.Token_Type.MUL);
                        token.setLexeme(buff.toString());
                        break;
                    }
                default:
                    token.setType(TokenData.Token_Type.ERRTOKEN);
                    token.setLexeme(buff.toString());
                    break;
            }// end of switch
        }// end of else(不是字母和数字，则一定是符号)
        return token;
    }


}
