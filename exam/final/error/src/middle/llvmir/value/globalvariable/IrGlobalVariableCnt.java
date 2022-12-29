package middle.llvmir.value.globalvariable;

public class IrGlobalVariableCnt {
    private static int cnt = 0;

    public static int getCnt() {
        int ret = cnt;
        cnt += 1;
        return ret;
    }
}
