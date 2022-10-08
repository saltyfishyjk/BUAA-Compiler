package middle.error;

/**
 * 错误类别枚举类
 */
public enum ErrorType {
    ILLEGAL_CHAR("a"),
    DUPLICATED_IDENT("b"),
    UNDEFINED_IDENT("c"),
    MISMATCH_PARAM_NUM("d"),
    MISMATCH_PARAM_TYPE("e"),
    RETURN_VALUE_VOID("f"),
    MISSING_RETURN("g"),
    ALTER_CONST("h"),
    MISSING_SEMICN("i"),
    MISSING_R_PARENT("j"),
    MISSING_R_BACKET("k"),
    MISMATCCH_PRINTF("l"),
    MISUSE_END_LOOP("m");


    private String code;

    ErrorType(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
