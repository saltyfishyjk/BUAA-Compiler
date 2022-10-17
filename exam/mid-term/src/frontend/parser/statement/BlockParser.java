package frontend.parser.statement;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.statement.blockitem.BlockItem;
import frontend.parser.statement.blockitem.BlockItemParser;

import java.util.ArrayList;

public class BlockParser {
    private TokenListIterator iterator;
    /* Block Attributes */
    private Token leftBrace = null;// '{'
    private ArrayList<BlockItem> blockItems = new ArrayList<>();
    private Token rightBrace = null; // '}'

    public BlockParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public Block parseBlock() {
        this.leftBrace = this.iterator.readNextToken();
        BlockItemParser blockItemParser = new BlockItemParser(this.iterator);
        Token token = this.iterator.readNextToken();
        while (!token.getType().equals(TokenType.RBRACE)) {
            this.iterator.unReadToken(1);
            this.blockItems.add(blockItemParser.parseBlockItem());
            token = this.iterator.readNextToken();
        }
        this.rightBrace = token;
        Block block = new Block(this.leftBrace, this.blockItems, this.rightBrace);
        return block;
    }
}
