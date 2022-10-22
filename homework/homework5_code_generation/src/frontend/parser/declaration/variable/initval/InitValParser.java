package frontend.parser.declaration.variable.initval;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.expression.ExpParser;
import middle.symbol.SymbolTable;

import java.util.ArrayList;

public class InitValParser {
    private TokenListIterator iterator;
    /* InitVal Attributes */
    private Token leftBrace = null; // '{'
    private InitVal first = null;
    private ArrayList<Token> commas = new ArrayList<>(); // ','
    private ArrayList<InitVal> initVals = new ArrayList<>();
    private Token rightBrace; // '}'
    private InitValEle initValEle = null;
    private SymbolTable curSymbolTable;

    public InitValParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public InitValParser(TokenListIterator iterator, SymbolTable curSymbolTable) {
        this.iterator = iterator;
        this.curSymbolTable = curSymbolTable;
    }

    public InitVal parseInitVal() {
        this.commas = new ArrayList<>();
        this.initVals = new ArrayList<>();
        this.leftBrace = this.iterator.readNextToken();
        if (!this.leftBrace.getType().equals(TokenType.LBRACE)) {
            this.iterator.unReadToken(1);
            // ExpParser expParser = new ExpParser(this.iterator);
            ExpParser expParser = new ExpParser(this.iterator, this.curSymbolTable);
            this.initValEle = expParser.parseExp();
        } else {
            Token token = this.iterator.readNextToken();
            if (!token.getType().equals(TokenType.RBRACE)) {
                this.iterator.unReadToken(1);
                // InitValParser initValParser = new InitValParser(this.iterator);
                InitValParser initValParser = new InitValParser(this.iterator, this.curSymbolTable);
                this.first = initValParser.parseInitVal();
                token = this.iterator.readNextToken();
                while (token.getType().equals(TokenType.COMMA)) {
                    this.commas.add(token);
                    this.initVals.add(initValParser.parseInitVal());
                    token = this.iterator.readNextToken();
                }
            }
            this.rightBrace = token;
            initValEle = new InitVals(this.leftBrace, this.first, this.commas,
                    this.initVals, this.rightBrace);
        }
        InitVal initVal = new InitVal(this.initValEle);
        return initVal;
    }
}
