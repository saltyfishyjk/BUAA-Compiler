package frontend.parser.statement.stmt;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;

public class StmtParser {
    private TokenListIterator iterator;
    /* Stmt Attributes */
    private StmtEle stmtEle = null;

    public StmtParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public Stmt parseStmt() {
        Token first = this.iterator.readNextToken();
        switch (first.getType()) {
            case IFTK: // 'if'
                this.iterator.unReadToken(1);
                StmtCondParser stmtCondParser = new StmtCondParser(this.iterator);
                this.stmtEle = stmtCondParser.parseStmtCond();
                break;
            case WHILETK: // 'while'
                this.iterator.unReadToken(1);
                StmtWhileParser stmtWhileParser = new StmtWhileParser(this.iterator);
                this.stmtEle = stmtWhileParser.parseStmtWhile();
                break;
            case BREAKTK: // 'break'
                this.iterator.unReadToken(1);
                StmtBreakParser stmtBreakParser = new StmtBreakParser(this.iterator);
                this.stmtEle = stmtBreakParser.parseStmtBreak();
                break;
            case CONTINUETK: // 'continue'
                this.iterator.unReadToken(1);
                StmtContinueParser stmtContinueParser = new StmtContinueParser(this.iterator);
                this.stmtEle = stmtContinueParser.parseStmtContinue();
                break;
            case RETURNTK: // 'return'
                this.iterator.unReadToken(1);
                StmtReturnParser stmtReturnParser = new StmtReturnParser(this.iterator);
                this.stmtEle = stmtReturnParser.parseStmtReturn();
                break;
            case PRINTFTK: // 'printf'
                this.iterator.unReadToken(1);
                StmtPrintParser stmtPrintParser = new StmtPrintParser(this.iterator);
                this.stmtEle = stmtPrintParser.parseStmtPrint();
                break;
            case SEMICN: // ';'
                this.iterator.unReadToken(1);
                StmtNullParser stmtNullParser = new StmtNullParser(this.iterator);
                this.stmtEle = stmtNullParser.pasreStmtNull();
                break;
                /* TODO : handle exp and lval*/
        }
        Stmt stmt = new Stmt(this.stmtEle);
        return stmt;
    }
}
