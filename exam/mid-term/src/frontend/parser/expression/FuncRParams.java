package frontend.parser.expression;

import frontend.lexer.Token;
import frontend.parser.SyntaxNode;

import java.util.ArrayList;

/**
 * 函数实参表
 */
public class FuncRParams implements SyntaxNode {
    private final String name = "<FuncRParams>";
    private Exp first;
    /* commas 和 exps大小应当相同，且commas应当只有逗号一种Token */
    /* 这里可能需要进行某种验证 */
    private ArrayList<Token> commas;
    private ArrayList<Exp> exps;

    public FuncRParams(Exp first, ArrayList<Token> commas, ArrayList<Exp> exps) {
        this.first = first;
        this.commas = commas;
        this.exps = exps;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(first.syntaxOutput());
        if (commas != null && exps != null && commas.size() == exps.size()) {
            int len = commas.size();
            for (int i = 0; i < len; i++) {
                sb.append(commas.get(i).syntaxOutput());
                sb.append(exps.get(i).syntaxOutput());
            }
        }
        sb.append(this.name + "\n");
        return sb.toString();
    }
}
