package middle.llvmir.value.instructions;

public class IrLabelCnt {
    private static int cnt = 0;

    public static int getCnt() {
        int ret = cnt;
        cnt += 1;
        return cnt;
    }

    /* 不修改cnt */
    public static String cntToName(int num) {
        return "%Label_" + num;
    }

}
