package frontend.parser;

import frontend.parser.declaration.Decl;
import frontend.parser.function.FuncDef;
import frontend.parser.function.MainFuncDef;

import java.util.ArrayList;

public class CompUnit implements SyntaxNode {
    private final String name = "<CompUnit>";
    private ArrayList<Decl> decls = null; // MAY exist
    private ArrayList<FuncDef> funcDefs = null; // MAY exist
    private MainFuncDef mainFuncDef;

    public CompUnit(MainFuncDef mainFuncDef) {
        this.mainFuncDef = mainFuncDef;
    }

    public CompUnit(ArrayList<Decl> decls,
                    ArrayList<FuncDef> funcDefs,
                    MainFuncDef mainFuncDef) {
        this(mainFuncDef);
        this.decls = decls;
        this.funcDefs = funcDefs;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        if (this.decls != null) {
            int len = this.decls.size();
            for (int i = 0; i < len; i++) {
                sb.append(this.decls.get(i).syntaxOutput());
            }
        }
        if (this.funcDefs != null) {
            int len = this.funcDefs.size();
            for (int i = 0; i < len; i++) {
                sb.append(this.funcDefs.get(i).syntaxOutput());
            }
        }
        sb.append(this.mainFuncDef.syntaxOutput());
        sb.append(this.name + "\n");
        return sb.toString();
    }
}
