/* C级：基本类型变量声明定义与算数表达式测试 */

const int ZERO = 0;
const int ONE = 1, TWO = 2, THREE = 4 * 7 - 5 * +5;

int one = 1, two = 2, three =  55 / 9 - 9 % 6;
int gvar;

int main() {
    const int ONE = 1;
    const int ABC123 = 2, _ABC = 3, _ABC123 = 4;
    int one = 1;
    int abc123 = 2, _abc = 8, _abc123 = 5;
    int var;

    printf("20373358\n");
    
    printf("Some global constants: %d %d %d %d\n", ZERO, ONE, TWO, THREE);
    printf("Some local constants: %d %d %d %d\n", ONE, ABC123, _ABC, _ABC123);
    printf("Some global variables: %d %d %d\n", one, two, three);
    printf("Some local variables: %d %d %d\n", abc123, _abc, _abc123);
    
    gvar = getint();
    var = getint();

    printf("Set global variable as %d\n", gvar);
    printf("Set local variable as %d\n", var);

    var = (var + 1) * -1;
    printf("Calculate %d\n", var);

    var = var + 56 - gvar * 9;
    printf("Calculate %d\n", var);

    var = var + -1;
    printf("Calculate %d\n", var);

    return 0;
}