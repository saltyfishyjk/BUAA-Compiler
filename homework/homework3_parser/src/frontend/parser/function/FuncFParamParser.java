package frontend.parser.function;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.declaration.BType;
import frontend.parser.declaration.BTypeParser;
import frontend.parser.expression.ConstExp;
import frontend.parser.expression.ConstExpParser;
import frontend.parser.terminal.Ident;
import frontend.parser.terminal.IdentParser;

import java.util.ArrayList;

public class FuncFParamParser {
    private TokenListIterator iterator;
    /* FuncFParam Attributes */
    private BType btype = null;
    private Ident ident = null;
    private Token leftBracketFirst = null;
    private Token rightBracketFirst = null;
    private ArrayList<Token> leftBrackets = new ArrayList<>();
    private ArrayList<ConstExp> constExps = new ArrayList<>();
    private ArrayList<Token> rightBrackets = new ArrayList<>();
    private FuncFParam funcFParam = null;

    public FuncFParamParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public FuncFParam parseFuncFParam() {
        this.leftBrackets = new ArrayList<>();
        this.constExps = new ArrayList<>();
        this.rightBrackets = new ArrayList<>();
        BTypeParser btypeParser = new BTypeParser(this.iterator);
        this.btype = btypeParser.parseBtype();
        IdentParser identParser = new IdentParser(this.iterator);
        this.ident = identParser.parseIdent();
        this.leftBracketFirst = this.iterator.readNextToken();
        if (this.leftBracketFirst.getType().equals(TokenType.LBRACK)) {
            this.rightBracketFirst = this.iterator.readNextToken();
            Token token = this.iterator.readNextToken();
            while (token.getType().equals(TokenType.LBRACK)) {
                this.leftBrackets.add(token);
                ConstExpParser constExpParser = new ConstExpParser(this.iterator);
                this.constExps.add(constExpParser.parseConstExp());
                this.rightBrackets.add(this.iterator.readNextToken());
                token = this.iterator.readNextToken();
            }
            this.iterator.unReadToken(1);
            this.funcFParam = new FuncFParam(this.btype, this.ident, this.leftBracketFirst,
                    this.rightBracketFirst, this.leftBrackets, this.constExps, this.rightBrackets);
        } else {
            this.iterator.unReadToken(1);
            this.funcFParam = new FuncFParam(this.btype, this.ident);
        }
        return this.funcFParam;
    }
}
