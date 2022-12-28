package frontend.parser.expression.multiexp;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import middle.symbol.SymbolTable;

import java.util.ArrayList;

/**
 * <RelExp> -> <AddExp> { ('<' | '>' | '<=' | '>=') <AddExp> }
 */
public class RelExpParser {
    private TokenListIterator iterator;
    /* RelExp Attributes */
    private AddExp first = null;
    private ArrayList<Token> operators = new ArrayList<>();
    private ArrayList<AddExp> operands = new ArrayList<>();
    private SymbolTable curSymbolTable;

    public RelExpParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public RelExpParser(TokenListIterator iterator, SymbolTable curSymbolTable) {
        this.iterator = iterator;
        this.curSymbolTable = curSymbolTable;
    }

    public RelExp parseRelExp() {
        this.operands = new ArrayList<>();
        this.operators = new ArrayList<>();
        // AddExpParser addExpParser = new AddExpParser(this.iterator);
        AddExpParser addExpParser = new AddExpParser(this.iterator, this.curSymbolTable);
        first = addExpParser.parseAddExp();
        Token token = this.iterator.readNextToken();
        while (token.getType().equals(TokenType.LSS) || // <
                token.getType().equals(TokenType.GRE) || // >
                token.getType().equals(TokenType.LEQ) || // <=
                token.getType().equals(TokenType.GEQ)) { // >=
            this.operators.add(token);
            this.operands.add(addExpParser.parseAddExp());
            token = this.iterator.readNextToken();
        }
        this.iterator.unReadToken(1);
        RelExp relExp = new RelExp(this.first, this.operators, this.operands);
        return relExp;
    }
}
