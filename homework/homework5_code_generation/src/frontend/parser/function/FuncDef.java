package frontend.parser.function;

import frontend.lexer.Token;
import frontend.parser.terminal.Ident;
import frontend.parser.SyntaxNode;
import frontend.parser.function.functype.FuncType;
import frontend.parser.statement.Block;

public class FuncDef implements SyntaxNode {
    private final String name = "<FuncDef>";
    private FuncType funcType;
    private Ident ident;
    private Token leftParent; // '('
    private FuncFParams funcFParams = null; // MAY exist
    private Token rightParent; // ')'
    private Block block;

    public FuncDef(FuncType funcType,
                   Ident ident,
                   Token leftParent,
                   Token rightParent,
                   Block block) {
        this.funcType = funcType;
        this.ident = ident;
        this.leftParent = leftParent;
        this.rightParent = rightParent;
        this.block = block;
    }

    public FuncDef(FuncType funcType,
                   Ident ident,
                   Token leftParent,
                   FuncFParams funcFParams,
                   Token rightParent,
                   Block block) {
        this(funcType, ident, leftParent, rightParent, block);
        this.funcFParams = funcFParams;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.funcType.syntaxOutput());
        sb.append(this.ident.syntaxOutput());
        sb.append(this.leftParent.syntaxOutput());
        if (this.funcFParams != null) {
            sb.append(this.funcFParams.syntaxOutput());
        }
        sb.append(this.rightParent.syntaxOutput());
        sb.append(this.block.syntaxOutput());
        sb.append(this.name + "\n");
        return sb.toString();
    }

    public String getRetType() {
        return this.funcType.getRetType();
    }

    public FuncFParams getFuncFParams() {
        return this.funcFParams;
    }

    public Block getBlock() {
        return this.block;
    }

    public String getName() {
        return this.ident.getName();
    }

    public FuncType getFuncType() {
        return funcType;
    }
}
