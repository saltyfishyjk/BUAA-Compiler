package frontend.parser.statement;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.statement.blockitem.BlockItem;
import frontend.parser.statement.blockitem.BlockItemParser;
import middle.symbol.SymbolTable;

import java.util.ArrayList;

public class BlockParser {
    private TokenListIterator iterator;
    /* Block Attributes */
    private Token leftBrace = null;// '{'
    private ArrayList<BlockItem> blockItems = new ArrayList<>();
    private Token rightBrace = null; // '}'
    private SymbolTable curSymbolTable;
    private int checkReturn; // 0不关心return，1需要无返回值的return，2需要有int返回值的return

    public BlockParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public BlockParser(TokenListIterator iterator,
                       SymbolTable curSymbolTable,
                       int checkReturn) {
        this.iterator = iterator;
        this.curSymbolTable = curSymbolTable;
        this.checkReturn = checkReturn;
    }

    public Block parseBlock() {
        this.leftBrace = this.iterator.readNextToken();
        this.enterNewSymbolTable();
        // BlockItemParser blockItemParser = new BlockItemParser(this.iterator);
        BlockItemParser blockItemParser = new BlockItemParser(this.iterator, this.curSymbolTable);
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

    private void enterNewSymbolTable() {
        this.curSymbolTable = new SymbolTable(this.curSymbolTable);
    }

    public int checkReturn() {
        int len = this.blockItems.size();
        if (len == 0) {
            return 0;
        }
        return this.blockItems.get(len - 1).checkReturn();
    }

    public int getRightBraceLineNum() {
        return this.rightBrace.getLineNum();
    }

    public BlockItem getLastBlockItem() {
        int len = this.blockItems.size();
        return this.blockItems.get(len - 1);
    }
}
