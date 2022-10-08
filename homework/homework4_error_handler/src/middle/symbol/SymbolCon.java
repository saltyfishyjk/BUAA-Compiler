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

    public void setInitVal(int initVal) {
        this.initVal = initVal;
    }

    public void setInitval1(ArrayList<Integer> initval1) {
        this.initval1 = initval1;
    }

    public void setInitval2(ArrayList<ArrayList<Integer>> initval2) {
        this.initval2 = initval2;
    }
}
