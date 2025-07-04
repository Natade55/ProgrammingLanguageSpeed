#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#define BAR_WIDTH 40

#ifdef _WIN32
#include <windows.h>
double get_time_sec()
{
    LARGE_INTEGER freq, count;
    QueryPerformanceFrequency(&freq);
    QueryPerformanceCounter(&count);
    return (double)count.QuadPart / freq.QuadPart;
}
#else
#include <sys/time.h>
double get_time_sec()
{
    struct timeval tv;
    gettimeofday(&tv, NULL);
    return tv.tv_sec + tv.tv_usec * 1e-6;
}
#endif

void format_number(long long n, char *buf, size_t buflen)
{
    char tmp[32];
    snprintf(tmp, sizeof(tmp), "%lld", n);
    int len = strlen(tmp);
    int commas = (len - 1) / 3;
    int outlen = len + commas;
    buf[outlen] = '\0';
    int i = len - 1, j = outlen - 1, c = 0;
    while (i >= 0)
    {
        buf[j--] = tmp[i--];
        if (++c == 3 && i >= 0)
        {
            buf[j--] = ',';
            c = 0;
        }
    }
}

void format_time(double sec, char *buf, size_t buflen)
{
    int m = (int)(sec / 60);
    int s = (int)sec % 60;
    snprintf(buf, buflen, "%02d:%02d", m, s);
}

void format_speed(double speed, char *buf, size_t buflen)
{
    if (speed > 1e6)
        snprintf(buf, buflen, "%.2fM num/s", speed / 1e6);
    else if (speed > 1e3)
        snprintf(buf, buflen, "%.2fk num/s", speed / 1e3);
    else
        snprintf(buf, buflen, "%.0f num/s", speed);
}

void print_progress_bar(long long current, long long total, double elapsedSec, double speed)
{
    double progress = (double)current / total;
    int filled = (int)(progress * BAR_WIDTH);

    char bar[BAR_WIDTH + 1];
    for (int i = 0; i < BAR_WIDTH; i++)
    {
        bar[i] = (i < filled) ? '#' : ' ';
    }
    bar[BAR_WIDTH] = '\0';

    double estimatedTotal = progress > 0 ? elapsedSec / progress : 0.0;
    double remainingSec = estimatedTotal - elapsedSec;
    if (remainingSec < 0)
        remainingSec = 0;

    char elapsedStr[16], remainStr[16], speedStr[32], currStr[32];
    format_time(elapsedSec, elapsedStr, sizeof(elapsedStr));
    format_time(remainingSec, remainStr, sizeof(remainStr));
    format_speed(speed, speedStr, sizeof(speedStr));
    format_number(current, currStr, sizeof(currStr));

    printf("\rC++ Loop: %6.2f%%|%s| %s<%s |%s | Current: %s",
           progress * 100, bar, elapsedStr, remainStr, speedStr, currStr);
    fflush(stdout);
}

long long sum_with_progress(long long maxNum)
{
    long long total = 0;
    long long step = 10000000LL;
    double startTime = get_time_sec();

    for (long long i = 1; i <= maxNum; i++)
    {
        total += i;
        if (i % step == 0 || i == maxNum)
        {
            double elapsedSec = get_time_sec() - startTime;
            double speed = (elapsedSec > 0) ? i / elapsedSec : 0.0;
            print_progress_bar(i, maxNum, elapsedSec, speed);
        }
    }
    printf("\n");
    return total;
}

int main()
{
    long long targetNumber = 10000000000LL;
    for (int i = 0; i < 115; i++)
        putchar('-');
    putchar('\n');
    printf("C++: Calculating the sum from 1 to %'lld (loop method)...\n", targetNumber);

    double startTime = get_time_sec();
    long long result = sum_with_progress(targetNumber);
    double endTime = get_time_sec();

    char resultStr[64];
    format_number(result, resultStr, sizeof(resultStr));
    printf("\nSum: %s\n", resultStr);
    printf("Elapsed time: %.2f seconds\n", endTime - startTime);

    for (int i = 0; i < 115; i++)
        putchar('-');
    putchar('\n');
    return 0;
}
