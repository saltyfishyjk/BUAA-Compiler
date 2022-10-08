package frontend.parser.statement.blockitem;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.declaration.DeclParser;
import frontend.parser.statement.stmt.StmtParser;

public class BlockItemParser {
    private TokenListIterator iterator;
    /* BlockItemEle */
    private BlockItemEle blockItemEle = null;

    public BlockItemParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public BlockItem parseBlockItem() {
        Token token = this.iterator.readNextToken();
        if (token.getType().equals(TokenType.CONSTTK) ||
            token.getType().equals(TokenType.INTTK)) {
            this.iterator.unReadToken(1);
            DeclParser declParser = new DeclParser(this.iterator);
            this.blockItemEle = declParser.parseDecl();
        } else {
            this.iterator.unReadToken(1);
            StmtParser stmtParser = new StmtParser(this.iterator);
            this.blockItemEle = stmtParser.parseStmt();
        }
        BlockItem blockItem = new BlockItem(this.blockItemEle);
        return blockItem;
    }
}
