package frontend.parser.statement;

import frontend.lexer.Token;
import frontend.parser.statement.blockitem.BlockItem;
import frontend.parser.statement.stmt.StmtEle;

import java.util.ArrayList;

public class Block implements StmtEle {
    private final String name = "<Block>";
    private Token leftBrace; // '{'
    private ArrayList<BlockItem> blockItems;
    private Token rightBrace; // '}'

    public Block(Token leftBrace,
                 ArrayList<BlockItem> blockItems,
                 Token rightBrace) {
        this.leftBrace = leftBrace;
        this.blockItems = blockItems;
        this.rightBrace = rightBrace;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.leftBrace.syntaxOutput());
        if (blockItems != null && blockItems.size() != 0) {
            int len = blockItems.size();
            for (int i = 0; i < len; i++) {
                sb.append(this.blockItems.get(i).syntaxOutput());
            }
        }
        sb.append(this.rightBrace.syntaxOutput());
        sb.append(this.name + "\n");
        return sb.toString();
    }

    public ArrayList<BlockItem> getBlockItems() {
        return blockItems;
    }
}
