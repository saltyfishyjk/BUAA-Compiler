package backend.instruction;

import java.util.ArrayList;

/**
 * Mips .Asciiz
 * 声明在.data段的字符串常量，主要用于IO输出
 * 形如 str_0 : .asciiz ="hello world"
 * 其中
 * - str_0 -> name
 * - "hello world" -> content
 * - cnt -> 0
 */
public class Asciiz extends MipsInstruction {
    private String name; // 字符串常量名
    private String content;
    private int cnt; // 标记这是第几个字符串常量

    public Asciiz(String name, String content) {
        super(".asciiz");
        this.name = name;
        this.content = content;
    }

    @Override
    public ArrayList<String> mipsOutput() {
        ArrayList<String> ret = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        this.content = this.content.replaceAll("\n", "\\\\n");
        sb.append(this.name + ": .asciiz \"" + content + "\"\n");
        ret.add(sb.toString());
        return ret;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }
}
