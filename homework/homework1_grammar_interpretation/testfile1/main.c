/*
// Created by emilyu on 2022/9/6.
// C
*/
#include <stdio.h>

const int Mod = 389, N = 100005;
int a_to_the_a, cnt = 0;
int n;

int getint() {
    int a;
    scanf("%d", &a);
    return a;
}

void move(int a, int b) {                           // void func with params
    cnt = cnt + 1;
    if (cnt % Mod == 0) {
        printf("funcTest: move disk from %d to %d\n", a, b);
    }
}

void hanoi(int n, int a, int b, int c) {            // void recursion
    if (n == 1) {
        move(a, c);
        return;
    }
    hanoi(n - 1, a, c, b);
    move(a, c);
    hanoi(n - 1, b, a, c);
}

int qpow(int a, int b) {                            // int func with params
    int ans = 1;

    while (b) {
        b = b / 2;
        a = (a * a) % Mod;
        if (b % 2) {
            ans = (ans * a) % Mod;
        }
    }

    return ans;
}

int gcd(int a, int b) {                             // int recursion
    if (!b) {
        return a;
    }
    return gcd(b, a % b);
}

int testExp() {                                     // int func without param
    a_to_the_a = n * n;
    int k = N / n;                                  // Exp
    n * n / n + n - n;                              // single exp without using its value;

    int b = a_to_the_a + 1;
    int c = - + - +2147483647, d = -1 - c;          // extreme nums
    int t = (((1 - + -a_to_the_a) * b / 3 - 2 + N) % Mod);      // useless braces
    int e = qpow(a_to_the_a, b);

    {                                               // block
        b = 10;
        c = 0;
        {
            b = 7;
            c = 8;
            printf("blockTest: 7 == %d, 8 == %d\n", b, c);
        }

        int i = 0;
        while (1) {
            i = i + 1;
            if (i % 2 != 0) {
                continue;
            }
            if (i >= b) {
                break;
            } else {
                if (c < 10) {
                    c = c + i;
                } else {
                    c = c - i;
                }
            }
        }
        printf("blockTest: 5 == %d, 12 == %d\n", b, c);
    }

    int f = n, g = 0;
    if (f < 0) {
        g = 10;
    } else {
        if (f > 10) {
            g = 20;
        } else {
            if (f == n) {
                g = 30;
            }
        }
    }
    if (f <= 10) {
        g = g + f;
    }

    printf("Exptest: %d %d %d %d %d", a_to_the_a, b, c, d, e);  // no \n
    printf(" %d %d %d\n", f, g, t);

    return gcd(f, g);                                    // a stmt not simplified
}

int main() {
    printf("20373569 the mafia~\n");

    n = getint();
    hanoi(n, 1, 2, 3);

    printf("Exptest: %d\n", testExp());
    return 0;
}
