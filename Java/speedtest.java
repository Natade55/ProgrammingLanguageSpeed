package Java;

import java.text.NumberFormat;
import java.util.Locale;

public class speedtest {

    public static String formatNumber(long n) {
        return NumberFormat.getInstance(Locale.US).format(n);
    }

    public static String formatTime(double sec) {
        int m = (int) (sec / 60);
        int s = (int) (sec % 60);
        return String.format("%02d:%02d", m, s);
    }

    public static String formatSpeed(double speed) {
        if (speed > 1_000_000)
            return String.format("%.2fM num/s", speed / 1_000_000);
        if (speed > 1_000)
            return String.format("%.2fk num/s", speed / 1_000);
        return String.format("%.0f num/s", speed);
    }

    public static void printProgressBar(long current, long total, double elapsedSec, double speed) {
        int barWidth = 40;
        double progress = (double) current / total;
        int filled = (int) (progress * barWidth);

        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < barWidth; i++) {
            bar.append(i < filled ? "#" : " ");
        }

        double estimatedTotal = progress > 0 ? elapsedSec / progress : 0;
        double remainingSec = Math.max(0, estimatedTotal - elapsedSec);

        System.out.printf(
                "\rJava Loop: %6.2f%%|%s| %s<%s |%s | Current: %s",
                progress * 100,
                bar.toString(),
                formatTime(elapsedSec),
                formatTime(remainingSec),
                formatSpeed(speed),
                formatNumber(current));
        System.out.flush();
    }

    public static long sumWithProgress(long maxNum) {
        long total = 0;
        long step = 10_000_000;
        long startTime = System.currentTimeMillis();

        for (long i = 1; i <= maxNum; i++) {
            total += i;
            if (i % step == 0 || i == maxNum) {
                double elapsedSec = (System.currentTimeMillis() - startTime) / 1000.0;
                double speed = elapsedSec > 0 ? i / elapsedSec : 0;
                printProgressBar(i, maxNum, elapsedSec, speed);
            }
        }
        System.out.println();
        return total;
    }

    public static void main(String[] args) {
        long targetNumber = 10_000_000_000L;
        System.out.println("-".repeat(113));
        System.out.printf("Java: Calculating the sum from 1 to %,d (loop method)...\n", targetNumber);
        long startTime = System.currentTimeMillis();
        long result = sumWithProgress(targetNumber);
        long endTime = System.currentTimeMillis();
        System.out.printf("\nSum: %,d\n", result);
        System.out.printf("Elapsed time: %.2f seconds\n", (endTime - startTime) / 1000.0);
        System.out.println("-".repeat(113));
    }
}
