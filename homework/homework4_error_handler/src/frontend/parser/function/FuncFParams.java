package frontend.parser.function;

import frontend.lexer.Token;
import frontend.parser.SyntaxNode;

import java.util.ArrayList;

public class FuncFParams implements SyntaxNode {
    private final String name = "<FuncFParams>";
    private FuncFParam first;
    private ArrayList<Token> commas = null; // ',' MAY exist
    private ArrayList<FuncFParam> funcFParams = null; // MAY exist

    public FuncFParams(FuncFParam first) {
        this.first = first;
    }

    public FuncFParams(FuncFParam first,
                       ArrayList<Token> commas,
                       ArrayList<FuncFParam> funcFParams) {
        this(first);
        this.commas = commas;
        this.funcFParams = funcFParams;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.first.syntaxOutput());
        if (this.commas != null && this.funcFParams != null &&
            this.commas.size() == this.funcFParams.size()) {
            int len = this.commas.size();
            for (int i = 0; i < len; i++) {
                sb.append(this.commas.get(i).syntaxOutput());
                sb.append(this.funcFParams.get(i).syntaxOutput());
            }
        }
        sb.append(this.name + "\n");
        return sb.toString();
    }
}
