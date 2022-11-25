package frontend.parser.expression.multiexp;

import frontend.lexer.Token;
import frontend.parser.SyntaxNode;
import middle.symbol.SymbolTable;
import middle.symbol.ValNode;

import java.util.ArrayList;

/**
 * 二元表达式顶层抽象类
 * 由MulExp, AddExp, RelExp, EqExp, LAndExp, LOrExp继承
 * 对于不同子类，T为其文法定义中的非终结符。具体地，MulExp->UnaryExp
 * 采用泛型对不同类别进行支持
 * 消除了左递归文法
 */
public class MultiExp<T extends SyntaxNode> implements SyntaxNode, ValNode {
    private T first; // 首字符，一定存在
    private ArrayList<Token> operators; // 操作符
    private ArrayList<T> operands; // 操作数
    private String name; // 语法类别名，为语法分析作业服务

    public MultiExp(T first, ArrayList<Token> operators, ArrayList<T> operands, String name) {
        this.first = first;
        this.operators = operators;
        this.operands = operands;
        this.name = name;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(first.syntaxOutput());
        sb.append(this.name + "\n");
        if (operators != null && operands != null && operators.size() == operands.size()) {
            int len = operators.size();
            for (int i = 0; i < len; i++) {
                sb.append(operators.get(i).syntaxOutput());
                sb.append(operands.get(i).syntaxOutput());
                sb.append(this.name + "\n");
            }
        }
        return sb.toString();
    }

    public T getFirst() {
        return first;
    }

    @Override
    public int calcNode(SymbolTable symbolTable) {
        return 0; // default 0
    }

    public ArrayList<T> getOperands() {
        return operands;
    }

    public ArrayList<Token> getOperators() {
        return operators;
    }

    public ArrayList<T> getAllOperands() {
        ArrayList<T> ret = new ArrayList<>();
        ret.add(this.getFirst());
        if (this.operands != null && this.operands.size() != 0) {
            ret.addAll(this.operands);
        }
        return ret;
    }
}
