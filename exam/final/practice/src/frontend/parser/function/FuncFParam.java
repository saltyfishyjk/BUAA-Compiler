package frontend.parser.function;

import frontend.lexer.Token;
import frontend.parser.terminal.Ident;
import frontend.parser.SyntaxNode;
import frontend.parser.declaration.BType;
import frontend.parser.expression.ConstExp;

import java.util.ArrayList;

public class FuncFParam implements SyntaxNode {
    private final String name = "<FuncFParam>";
    private BType btype;
    private Ident ident;
    private Token leftBracketFirst = null; // '[' MAY exist
    private Token rightBracketFirst = null; // ']' MAY exist
    private ArrayList<Token> leftBrackets = null; // '[' MAY exist
    private ArrayList<ConstExp> constExps = null; // MAY exist
    private ArrayList<Token> rightBrackets = null; // ']' MAY exist

    public FuncFParam(BType btype,
                      Ident ident) {
        this.btype = btype;
        this.ident = ident;
    }

    public FuncFParam(BType btype,
                      Ident ident,
                      Token leftBracketFirst,
                      Token rightBrackFirst) {
        this(btype, ident);
        this.leftBracketFirst = leftBracketFirst;
        this.rightBracketFirst = rightBrackFirst;
    }

    public FuncFParam(BType btype,
                      Ident ident,
                      Token leftBracketFirst,
                      Token rightBrackFirst,
                      ArrayList<Token> leftBrackets,
                      ArrayList<ConstExp> constExps,
                      ArrayList<Token> rightBrackets) {
        this(btype, ident, leftBracketFirst, rightBrackFirst);
        this.leftBrackets = leftBrackets;
        this.constExps = constExps;
        this.rightBrackets = rightBrackets;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.btype.syntaxOutput());
        sb.append(this.ident.syntaxOutput());
        if (this.leftBracketFirst != null && this.rightBracketFirst != null) {
            sb.append(this.leftBracketFirst.syntaxOutput());
            sb.append(this.rightBracketFirst.syntaxOutput());
            if (this.leftBrackets != null && this.constExps != null && this.rightBrackets != null &&
                this.leftBrackets.size() == this.constExps.size() &&
                this.constExps.size() == this.rightBrackets.size()) {
                int len = this.leftBrackets.size();
                for (int i = 0; i < len; i++) {
                    sb.append(this.leftBrackets.get(i).syntaxOutput());
                    sb.append(this.constExps.get(i).syntaxOutput());
                    sb.append(this.rightBrackets.get(i).syntaxOutput());
                }
            }
        }
        sb.append(this.name + "\n");
        return sb.toString();
    }

    /* 在代码生成作业1中用于获知函数的参数类型为0或1或2维数组 可能后续需要修改 */
    public int getDimension() {
        if (this.leftBracketFirst == null) {
            return 0;
        } else {
            if (this.leftBrackets == null || this.leftBrackets.size() == 0) {
                return 1;
            } else {
                return 2;
            }
        }
    }

    public String getName() {
        return this.ident.getName();
    }

    public ArrayList<ConstExp> getConstExps() {
        return this.constExps;
    }
}
