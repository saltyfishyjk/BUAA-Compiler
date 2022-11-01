package middle.symbol;

import java.util.ArrayList;

public class SymbolCon extends Symbol {
    private int initVal;
    private ArrayList<Integer> initval1;
    private ArrayList<ArrayList<Integer>> initval2;

    public SymbolCon(int lineNum, String name, SymbolType symbolType) {
        super(lineNum, name, symbolType);
    }

    public SymbolCon(int lineNum, String name, SymbolType symbolType, int dimension) {
        this(lineNum, name, symbolType);
        this.setDimension(dimension);
    }

    /* 用于中间代码生成，lineNum不重要 */
    public SymbolCon(String name, SymbolType symbolType, int dimension) {
        this(0, name, symbolType, dimension);
    }

    public void setInitVal(int initVal) {
        this.initVal = initVal;
    }

    public void setInitval1(ArrayList<Integer> initval1) {
        this.initval1 = initval1;
    }

    public void setInitval2(ArrayList<ArrayList<Integer>> initval2) {
        this.initval2 = initval2;
    }

    @Override
    public int getDimension() {
        return super.getDimension();
    }

    @Override
    public int getLineNum() {
        return super.getLineNum();
    }

    public int getInitVal() {
        return initVal;
    }

    public ArrayList<ArrayList<Integer>> getInitval2() {
        return initval2;
    }

    public ArrayList<Integer> getInitval1() {
        return initval1;
    }
}
