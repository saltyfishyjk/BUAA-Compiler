package frontend.parser.declaration.constant.constinitval;

import frontend.lexer.Token;
import middle.symbol.SymbolTable;

import java.util.ArrayList;

/**
 * ConstInitVal -> '{' [ <ConstInitVal> { ',' <ConstInitVal> } ] '}'
 */
public class ConstInitValMulti implements ConstInitValEle {
    private Token leftBrace; // '{'
    private ConstInitVal first; // MAY exist
    private ArrayList<Token> commas; // MAY exist
    private ArrayList<ConstInitVal> constInitVals; // MAY exist
    private Token rightBrace; // '}'

    public ConstInitValMulti(Token leftBrace,
                             Token rightBrace) {
        this.leftBrace = leftBrace;
        this.rightBrace = rightBrace;
    }

    public ConstInitValMulti(Token leftBrace,
                             Token rightBrace,
                             ConstInitVal first) {
        this(leftBrace, rightBrace);
        this.first = first;
    }

    public ConstInitValMulti(Token leftBrace,
                             ConstInitVal first,
                             ArrayList<Token> commas,
                             ArrayList<ConstInitVal> constInitVals,
                             Token rightBrace) {
        this(leftBrace, rightBrace, first);
        this.commas = commas;
        this.constInitVals = constInitVals;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.leftBrace.syntaxOutput());
        if (this.first != null) {
            sb.append(this.first.syntaxOutput());
            if (this.commas != null && this.constInitVals != null
                    && this.commas.size() == this.constInitVals.size()) {
                int len = this.commas.size();
                for (int i = 0; i < len; i++) {
                    sb.append(this.commas.get(i).syntaxOutput());
                    sb.append(this.constInitVals.get(i).syntaxOutput());
                }
            }
        }
        sb.append(this.rightBrace.syntaxOutput());
        return sb.toString();
    }

    public ArrayList<ConstInitVal> getConstInitVals() {
        return constInitVals;
    }

    @Override
    public int calcNode(SymbolTable symbolTable) {
        System.out.println("ERROR in ConstInitValMulti.calcNode : should not calc Multi Val");
        return 0;
    }
}
