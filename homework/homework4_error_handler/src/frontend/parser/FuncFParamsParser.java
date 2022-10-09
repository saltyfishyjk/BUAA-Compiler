package frontend.parser;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.function.FuncFParam;
import frontend.parser.function.FuncFParamParser;
import frontend.parser.function.FuncFParams;
import middle.symbol.SymbolTable;

import java.util.ArrayList;

public class FuncFParamsParser {
    private TokenListIterator iterator;
    /* FunfFParams Attributes */
    private FuncFParam first = null;
    private ArrayList<Token> commas = new ArrayList<>();
    private ArrayList<FuncFParam> funcFParams = new ArrayList<>();
    private SymbolTable curSymbolTabl;

    public FuncFParamsParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public FuncFParamsParser(TokenListIterator iterator, SymbolTable curSymbolTabl) {
        this.iterator = iterator;
        this.curSymbolTabl = curSymbolTabl;
    }

    public FuncFParams parseFuncFParams() {
        this.commas = new ArrayList<>();
        this.funcFParams = new ArrayList<>();
        // FuncFParamParser funcFParamParser = new FuncFParamParser(this.iterator);
        FuncFParamParser funcFParamParser = new FuncFParamParser(this.iterator, this.curSymbolTabl);
        this.first = funcFParamParser.parseFuncFParam();
        Token token = this.iterator.readNextToken();
        while (token.getType().equals(TokenType.COMMA)) {
            this.commas.add(token);
            this.funcFParams.add(funcFParamParser.parseFuncFParam());
            token = this.iterator.readNextToken();
        }
        this.iterator.unReadToken(1);
        return new FuncFParams(this.first, this.commas, this.funcFParams);
    }
}
