package frontend.parser.expression.multiexp;

import frontend.lexer.Token;

import java.util.ArrayList;

/**
 * 二元表达式顶层抽象类
 * 由MulExp, AddExp, RelExp, EqExp, LAndExp, LOrExp继承
 * 采用泛型对不同类别进行支持
 * 消除了左递归文法
 */
public class MultiExp<T> {
    private T first;
    private ArrayList<Token> operators; // 操作符
    private ArrayList<T> operands; // 操作数
    private String name; // 语法类别名，为语法分析作业服务

    public MultiExp(T first, ArrayList<Token> operators, ArrayList<T> operands, String name) {
        this.first = first;
        this.operators = operators;
        this.operands = operands;
        this.name = name;
    }

}
