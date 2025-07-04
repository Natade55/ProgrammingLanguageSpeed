using System;
using System.Diagnostics;
using System.Globalization;
using System.Text;

class SpeedTest
{
    const int BAR_WIDTH = 40;

    static string FormatNumber(long n)
    {
        return n.ToString("N0", CultureInfo.InvariantCulture);
    }

    static string FormatTime(double sec)
    {
        int m = (int)(sec / 60);
        int s = (int)(sec % 60);
        return $"{m:D2}:{s:D2}";
    }

    static string FormatSpeed(double speed)
    {
        if (speed > 1e6)
            return $"{speed / 1e6:F2}M num/s";
        else if (speed > 1e3)
            return $"{speed / 1e3:F2}k num/s";
        else
            return $"{speed:F0} num/s";
    }

    static void PrintProgressBar(long current, long total, double elapsedSec, double speed)
    {
        double progress = (double)current / total;
        int filled = (int)(progress * BAR_WIDTH);

        var bar = new StringBuilder(BAR_WIDTH);
        for (int i = 0; i < BAR_WIDTH; i++)
            bar.Append(i < filled ? '#' : ' ');

        double estimatedTotal = progress > 0 ? elapsedSec / progress : 0.0;
        double remainingSec = estimatedTotal - elapsedSec;
        if (remainingSec < 0) remainingSec = 0;

        string elapsedStr = FormatTime(elapsedSec);
        string remainStr = FormatTime(remainingSec);
        string speedStr = FormatSpeed(speed);
        string currStr = FormatNumber(current);

        Console.Write($"\rC# Loop: {progress * 100,6:F2}%|{bar}| {elapsedStr}<{remainStr} |{speedStr} | Current: {currStr}");
    }

    static long SumWithProgress(long maxNum)
    {
        long total = 0;
        long step = 10000000L;
        var stopwatch = Stopwatch.StartNew();

        for (long i = 1; i <= maxNum; i++)
        {
            total += i;
            if (i % step == 0 || i == maxNum)
            {
                double elapsedSec = stopwatch.Elapsed.TotalSeconds;
                double speed = (elapsedSec > 0) ? i / elapsedSec : 0.0;
                PrintProgressBar(i, maxNum, elapsedSec, speed);
            }
        }
        Console.WriteLine();
        return total;
    }

    static void Main()
    {
        long targetNumber = 10000000000L;
        Console.WriteLine(new string('-', 115));
        Console.WriteLine($"C#: Calculating the sum from 1 to {FormatNumber(targetNumber)} (loop method)...");
        var startTime = Stopwatch.StartNew();
        long result = SumWithProgress(targetNumber);
        startTime.Stop();

        Console.WriteLine($"\nSum: {FormatNumber(result)}");
        Console.WriteLine($"Elapsed time: {startTime.Elapsed.TotalSeconds:F2} seconds");
        Console.WriteLine(new string('-', 115));
    }
}
