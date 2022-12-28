package frontend.parser;

/**
 * 语法结点接口，每一个语法成分类都实现该接口及其方法，通过递归调用实现语法分析作业的输出
 */
public interface SyntaxNode {
    String syntaxOutput();
}
