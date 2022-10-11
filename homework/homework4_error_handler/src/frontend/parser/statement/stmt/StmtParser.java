package frontend.parser.statement.stmt;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.statement.BlockParser;
import middle.error.Error;
import middle.error.ErrorTable;
import middle.error.ErrorType;
import middle.symbol.SymbolTable;

public class StmtParser {
    private TokenListIterator iterator;
    /* Stmt Attributes */
    private StmtEle stmtEle = null;
    private SymbolTable curSymbolTable;

    public StmtParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public StmtParser(TokenListIterator iterator,
                      SymbolTable curSymbolTable) {
        this.iterator = iterator;
        this.curSymbolTable = curSymbolTable;
    }

    public Stmt parseStmt() {
        Token first = this.iterator.readNextToken();
        switch (first.getType()) {
            case IFTK: // 'if'
                this.iterator.unReadToken(1);
                StmtCondParser stmtCondParser = new StmtCondParser(this.iterator,
                        this.curSymbolTable);
                this.stmtEle = stmtCondParser.parseStmtCond();
                break;
            case WHILETK: // 'while'
                this.iterator.unReadToken(1);
                StmtWhileParser stmtWhileParser = new StmtWhileParser(this.iterator,
                        this.curSymbolTable);
                this.stmtEle = stmtWhileParser.parseStmtWhile();
                break;
            case BREAKTK: // 'break'
                this.iterator.unReadToken(1);
                StmtBreakParser stmtBreakParser = new StmtBreakParser(this.iterator,
                        this.curSymbolTable);
                this.stmtEle = stmtBreakParser.parseStmtBreak();
                break;
            case CONTINUETK: // 'continue'
                this.iterator.unReadToken(1);
                StmtContinueParser stmtContinueParser = new StmtContinueParser(this.iterator,
                        this.curSymbolTable);
                this.stmtEle = stmtContinueParser.parseStmtContinue();
                break;
            case RETURNTK: // 'return'
                this.iterator.unReadToken(1);
                StmtReturnParser stmtReturnParser = new StmtReturnParser(this.iterator,
                        this.curSymbolTable);
                this.stmtEle = stmtReturnParser.parseStmtReturn();
                break;
            case PRINTFTK: // 'printf'
                this.iterator.unReadToken(1);
                StmtPrintParser stmtPrintParser = new StmtPrintParser(this.iterator,
                        this.curSymbolTable);
                this.stmtEle = stmtPrintParser.parseStmtPrint();
                break;
            case SEMICN: // ';'
                this.iterator.unReadToken(1);
                StmtNullParser stmtNullParser = new StmtNullParser(this.iterator);
                this.stmtEle = stmtNullParser.pasreStmtNull();
                break;
                /* TODO : handle exp and lval*/
            case IDENFR:
                caseIdenfr(first);
                break;
            case LBRACE: // '{'
                this.iterator.unReadToken(1);
                BlockParser blockParser = new BlockParser(this.iterator, this.curSymbolTable, 0);
                this.stmtEle = blockParser.parseBlock();
                break;
            case LPARENT: case INTCON: case PLUS: case MINU: // (, num, +, -
                this.iterator.unReadToken(1);
                StmtExpParser stmtExpParser = new StmtExpParser(this.iterator, this.curSymbolTable);
                this.stmtEle = stmtExpParser.parseStmtExp();
                break;
            default: // 如果没有匹配到任何有效字符，说明当前应当为缺少分号的i类错误
                handleIError(first);
        }
        return new Stmt(this.stmtEle);
    }

    private void caseIdenfr(Token first) {
        /* need to distinguish LVal = Exp, LVal = getint and [Exp] */
        int cnt = 1;
        int mode = 0; // 0:assign 1:input
        boolean flag = false; // LVal = Exp; || LVal = getint();
        Token token = first;
        while (!token.getType().equals(TokenType.SEMICN)) {
            token = this.iterator.readNextToken();
            cnt += 1;
            if (token.getType().equals(TokenType.ASSIGN)) {
                flag = true;
            }
            if (token.getType().equals(TokenType.GETINTTK)) {
                mode = 1;
            }
        }
        this.iterator.unReadToken(cnt);
        if (flag) {
            if (mode == 0) {
                // StmtAssignParser stmtAssignParser = new StmtAssignParser(this.iterator);
                StmtAssignParser stmtAssignParser = new StmtAssignParser(this.iterator,
                        this.curSymbolTable);
                this.stmtEle = stmtAssignParser.parseStmtAssign();
            } else if (mode == 1) {
                // StmtGetIntParser stmtGetIntParser = new StmtGetIntParser(this.iterator);
                StmtGetIntParser stmtGetIntParser = new StmtGetIntParser(this.iterator,
                        this.curSymbolTable);
                this.stmtEle = stmtGetIntParser.parseStmtGetInt();
            } else {
                System.out.println("REACHED UNEXPECTED BRANCH");
            }
        } else {
            // StmtExpParser stmtExpParser = new StmtExpParser(this.iterator);
            StmtExpParser stmtExpParser = new StmtExpParser(this.iterator, this.curSymbolTable);
            this.stmtEle = stmtExpParser.parseStmtExp();
        }
    }

    private void handleIError(Token token) {
        if (token.getType().equals(TokenType.SEMICN)) {
            this.iterator.unReadToken(2); // 后退两格以方便确定分号上一个非终结符位置
            Error error = new Error(this.iterator.readNextToken().getLineNum(),
                    ErrorType.MISSING_SEMICN);
            ErrorTable.addError(error);
        }
    }
}
