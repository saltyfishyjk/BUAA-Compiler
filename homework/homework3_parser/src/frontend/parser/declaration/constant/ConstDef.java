package frontend.parser.declaration.constant;

import frontend.lexer.Token;
import frontend.lexer.terminal.Ident;
import frontend.parser.SyntaxNode;
import frontend.parser.declaration.constant.constinitval.ConstInitVal;
import frontend.parser.expression.ConstExp;

import java.util.ArrayList;

public class ConstDef implements SyntaxNode {
    private final String name = "<ConstDef>";
    private Ident ident;
    private ArrayList<Token> leftBracks; // '['
    private ArrayList<ConstExp> constExps;
    private ArrayList<Token> rightBrackets; // ']'
    private Token eq; // '='
    private ConstInitVal constInitval;

    public ConstDef(Ident ident,
                    ArrayList<Token> leftBracks,
                    ArrayList<ConstExp> constExps,
                    ArrayList<Token> rightBrackets,
                    Token eq,
                    ConstInitVal constInitval) {
        this.ident = ident;
        this.leftBracks = leftBracks;
        this.constExps = constExps;
        this.rightBrackets = rightBrackets;
        this.eq = eq;
        this.constInitval = constInitval;
    }

    @Override
    public String syntaxOutput() {
        return null;
    }
}
