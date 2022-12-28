package middle.llvmir.value.function;

public class IrFunctionCnt {
    private int cnt = 0;

    public IrFunctionCnt() {}

    public int getCnt() {
        int ret = cnt;
        cnt += 1;
        return ret;
    }
}
