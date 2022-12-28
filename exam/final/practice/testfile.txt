const int N = 10;
int a[10] = {0,1,2,3,4,5,6,7,8,9};
int fib(int i) {
    if (i == 1) {
        return 1;
    }
    if (i == 2) {
        return 2;
    }
    return fib(i - 1) + fib(i - 2);
}
int main()
{    
    int i = 2,j = 5;
    const int a1 = 1, a2 = 2;
    i = getint();
    j = getint();
    i = (-(i * j) * fib(4) + 0 + a[1] * 1 - 1/2) * 5;
    j = 7*5923%56*57 - fib(fib(5)+2) + (a1+a2-(89/2*36-53) /1*6-2*(45*56/85-56+35*56/4-9));
    int k = +-+6;
    while (i <= 100) {
	a[0] = a[0] + k * k;
	a[1] = a[1] + k * k;
	a[2] = a[2] + k * k;
	a[3] = a[3] + k * k;
	a[4] = a[4] + k * k;
	a[5] = a[5] + k * k;
	a[6] = a[6] + k * k;
	a[7] = a[7] + k * k;
	a[8] = a[8] + k * k;
	a[9] = a[9] + k * k;
	i = i + 1;
    }
    i = 0;
    while (i < 10) {
	printf("%d, ", a[i]);
	i = i + 1;
    }
    printf("\n%d, %d, %d\n", i, j, k);
    return 0;
}