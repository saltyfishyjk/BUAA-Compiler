package frontend.parser.statement.stmt;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.expression.Exp;
import frontend.parser.expression.ExpParser;
import frontend.parser.terminal.FormatString;

import java.util.ArrayList;

public class StmtPrintParser {
    private TokenListIterator iterator;
    /* StmtPrint Attributes */
    private Token printf; // 'printf'
    private Token leftParent; // '('
    private FormatString formatString;
    private ArrayList<Token> commmas = new ArrayList<>(); // ','
    private ArrayList<Exp> exps = new ArrayList<>();
    private Token rightParent; // ')'
    private Token semicn; // ';'

    public StmtPrintParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public StmtPrint parseStmtPrint() {
        this.commmas = new ArrayList<>();
        this.exps = new ArrayList<>();
        this.printf = this.iterator.readNextToken();
        if (!this.printf.getType().equals(TokenType.PRINTFTK)) {
            System.out.println("EXPEXT PRINTF IN STMTPRINTFPARSER");
        }
        this.leftParent = this.iterator.readNextToken();
        if (!this.leftParent.getType().equals(TokenType.LPARENT)) {
            System.out.println("EXPECT LPARENT IN STMTPRINTFPARSER");
        }
        this.formatString = new FormatString(this.iterator.readNextToken());
        Token token = this.iterator.readNextToken();
        while (token.getType().equals(TokenType.COMMA)) {
            this.commmas.add(token);
            ExpParser expParser = new ExpParser(this.iterator);
            this.exps.add(expParser.parseExp());
            token = this.iterator.readNextToken();
        }
        this.rightParent = token;
        this.semicn = this.iterator.readNextToken();
        StmtPrint stmtPrint = new StmtPrint(this.printf, this.leftParent,
                this.formatString, this.commmas, this.exps, this.rightParent, this.semicn);
        return stmtPrint;
    }
}
