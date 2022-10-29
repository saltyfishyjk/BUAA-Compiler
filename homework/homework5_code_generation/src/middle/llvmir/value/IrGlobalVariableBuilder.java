package middle.llvmir.value;

import frontend.parser.declaration.Decl;
import frontend.parser.declaration.DeclEle;
import frontend.parser.declaration.constant.ConstDecl;
import frontend.parser.declaration.constant.ConstDef;
import frontend.parser.declaration.variable.VarDecl;
import frontend.parser.declaration.variable.vardef.VarDef;
import middle.symbol.SymbolTable;

import java.util.ArrayList;

public class IrGlobalVariableBuilder {
    private SymbolTable symbolTable;
    private Decl decl;

    public IrGlobalVariableBuilder(SymbolTable symbolTable, Decl decl) {

    }

    /**
     * ---------- 生成LLVM IR GlobalVariable ----------
     * 因为一条声明语句可能含有多个声明，因此返回的是List
     */
    public ArrayList<IrGlobalVariable> genIrGlobalVariable() {
        ArrayList<IrGlobalVariable> variables = new ArrayList<>();
        DeclEle ele = this.decl.getDeclEle();
        if (ele instanceof ConstDecl) {
            ConstDecl constDecl = (ConstDecl) ele;
            for (ConstDef def : constDecl.getConstDefs()) {
                variables.add(genIrConstGlobalVariable(def));
            }
        } else if (ele instanceof VarDecl) {
            VarDecl varDecl = (VarDecl) ele;
            for (VarDef def : varDecl.getVarDefs()) {
                variables.add(genIrVarGlobalVariable(def));
            }
        } else {
            System.out.println("ERROR in IrBuilder.genIrGlobalVariable! Need Const or Var Decl");
        }
        return variables;
    }

    /* 通过一条ConstDef生成一条IrGlobalVariable */
    private IrGlobalVariable genIrConstGlobalVariable(ConstDef def) {
        /* TODO : fill contents */
        return null;
    }

    /* 通过一条VarDef生成一条IrGlobalVariable */
    private IrGlobalVariable genIrVarGlobalVariable(VarDef def) {
        /* TODO : fill contents */
        return null;
    }
}
