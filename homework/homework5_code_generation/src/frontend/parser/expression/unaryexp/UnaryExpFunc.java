package frontend.parser.expression.unaryexp;

import frontend.lexer.Token;
import frontend.parser.terminal.Ident;
import frontend.parser.expression.FuncRParams;
import middle.symbol.SymbolTable;

/**
 * <Ident> '(' [<FuncRParams>] ')'
 */
public class UnaryExpFunc implements UnaryExpEle {
    private Ident ident;
    private FuncRParams funcRParams = null; // MAY exist
    private Token leftParent;
    private Token rightParent;
    private int dimension; // 维数

    public UnaryExpFunc(Ident ident,
                        Token leftParent,
                        Token rightParent) {
        this.ident = ident;
        this.leftParent = leftParent;
        this.rightParent = rightParent;
    }

    public UnaryExpFunc(Ident ident,
                        Token leftParent,
                        Token rightParent,
                        int dimension) {
        this(ident, leftParent, rightParent);
        this.dimension = dimension;
    }

    public UnaryExpFunc(Ident ident,
                        FuncRParams funcRParams,
                        Token leftParent,
                        Token rightBracker) {
        this(ident, leftParent, rightBracker);
        this.funcRParams = funcRParams;
    }

    public UnaryExpFunc(Ident ident,
                        FuncRParams funcRParams,
                        Token leftParent,
                        Token rightParent,
                        int dimension) {
        this(ident, funcRParams, leftParent, rightParent);
        this.dimension = dimension;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(ident.syntaxOutput());
        sb.append(leftParent.syntaxOutput());
        if (funcRParams != null) {
            sb.append(this.funcRParams.syntaxOutput());
        }
        sb.append(rightParent.syntaxOutput());
        return sb.toString();
    }

    @Override
    public int getDimension() {
        /* TODO */
        /* 根据函数类别的不同返回-1（void）或0（int），需要检查符号表 */
        return this.dimension;
    }

    @Override
    public int calcNode(SymbolTable symbolTable) {
        System.out.println("ERROR in UnaryExpFunc.calcNode : should not call this func");
        return 0;
    }

    public String getFunctionName() {
        return this.ident.getName();
    }

    public FuncRParams getFuncRParams() {
        return funcRParams;
    }
}
