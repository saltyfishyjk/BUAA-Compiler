package frontend.parser;

import frontend.lexer.Token;
import frontend.lexer.TokenList;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.declaration.Decl;
import frontend.parser.declaration.DeclParser;
import frontend.parser.function.FuncDef;
import frontend.parser.function.FuncDefParser;
import frontend.parser.function.MainFuncDef;
import frontend.parser.function.MainFuncDefParser;
import middle.symbol.SymbolTable;

import java.util.ArrayList;

/**
 * CompUnit解析器
 */
public class CompUnitParser {
    private TokenList tokens;
    private TokenListIterator iterator;
    /* CompUnit params */
    private ArrayList<Decl> decls;
    private ArrayList<FuncDef> funcDefs;
    private MainFuncDef mainFuncDef;
    private SymbolTable curSymbolTable;

    /* init CompUnitParser obj */
    private void initCompUnitParser() {
        //this.iterator = tokens.getTokens().listIterator();
        this.iterator = new TokenListIterator(this.tokens);
    }

    public CompUnitParser(TokenList tokens) {
        this.tokens = tokens;
        initCompUnitParser();
        this.decls = new ArrayList<>();
        this.funcDefs = new ArrayList<>();
        this.mainFuncDef = null;
        this.curSymbolTable = new SymbolTable();
    }

    public CompUnit parseCompUnit() {
        this.decls = new ArrayList<>();
        this.funcDefs = new ArrayList<>();
        /* parse decls */
        parseDecls();
        /* parse FuncDefs */
        parseFuncDefs();
        /* parse MainFuncDef */
        parseMainFuncDef();

        CompUnit compUnit = new CompUnit(this.decls, this.funcDefs, this.mainFuncDef);
        return compUnit;
    }

    private void parseDecls() {
        Token first = this.iterator.readNextToken();
        Token second = this.iterator.readNextToken();
        while (this.iterator.hasNext()) {
            Token third = this.iterator.readNextToken();
            if (third.getType().equals(TokenType.LPARENT)) {
                this.iterator.unReadToken(3);
                return;
            } else {
                this.iterator.unReadToken(1);
            }
            if ((first.getType().equals(TokenType.CONSTTK) &&
                    second.getType().equals(TokenType.INTTK)) ||
                    (first.getType().equals(TokenType.INTTK) &&
                            second.getType().equals(TokenType.IDENFR))) {
                /* first -> const && second -> int */
                /* first -> int && second -> IDENFR */
                this.iterator.unReadToken(2);
                //DeclParser declParser = new DeclParser(this.iterator);
                DeclParser declParser = new DeclParser(this.iterator, this.curSymbolTable);
                this.decls.add(declParser.parseDecl());
            } else {
                this.iterator.unReadToken(2);
                break;
            }
            first = this.iterator.readNextToken();
            second = this.iterator.readNextToken();
        }
    }

    private void parseFuncDefs() {
        Token first = this.iterator.readNextToken();
        Token second = this.iterator.readNextToken();
        while (this.iterator.hasNext()) {
            if ((first.getType().equals(TokenType.INTTK) ||
                    first.getType().equals(TokenType.VOIDTK)) &&
                second.getType().equals(TokenType.IDENFR)) {
                /* first -> int/void && second -> IDENFR */
                this.iterator.unReadToken(2);
                // FuncDefParser funcDefParser = new FuncDefParser(this.iterator);
                FuncDefParser funcDefParser = new FuncDefParser(this.iterator, this.curSymbolTable);
                this.funcDefs.add(funcDefParser.parseFuncDef());
            } else {
                this.iterator.unReadToken(2);
                break;
            }
            first = this.iterator.readNextToken();
            second = this.iterator.readNextToken();
        }
    }

    private void parseMainFuncDef() {
        MainFuncDefParser mainFuncDefParser = new MainFuncDefParser(this.iterator,
                this.curSymbolTable);
        //MainFuncDefParser mainFuncDefParser = new MainFuncDefParser(this.iterator);
        this.mainFuncDef = mainFuncDefParser.parseMainFuncDef();
    }

}