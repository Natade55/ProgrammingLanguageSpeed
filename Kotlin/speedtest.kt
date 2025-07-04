import java.text.NumberFormat
import java.util.Locale

object SpeedTest {

    fun formatNumber(n: Long): String =
        NumberFormat.getInstance(Locale.US).format(n)

    fun formatTime(sec: Double): String {
        val m = (sec / 60).toInt()
        val s = (sec % 60).toInt()
        return "%02d:%02d".format(m, s)
    }

    fun formatSpeed(speed: Double): String =
        when {
            speed > 1_000_000 -> "%.2fM num/s".format(speed / 1_000_000)
            speed > 1_000     -> "%.2fk num/s".format(speed / 1_000)
            else              -> "%.0f num/s".format(speed)
        }

    fun printProgressBar(current: Long, total: Long, elapsedSec: Double, speed: Double) {
        val barWidth = 40
        val progress = current.toDouble() / total
        val filled = (progress * barWidth).toInt()

        val bar = buildString {
            for (i in 0 until barWidth) {
                append(if (i < filled) "#" else " ")
            }
        }

        val estimatedTotal = if (progress > 0) elapsedSec / progress else 0.0
        val remainingSec = kotlin.math.max(0.0, estimatedTotal - elapsedSec)

        print(
            "\rKotlin Loop: %6.2f%%|%s| %s<%s |%s | Current: %s".format(
                progress * 100,
                bar,
                formatTime(elapsedSec),
                formatTime(remainingSec),
                formatSpeed(speed),
                formatNumber(current)
            )
        )
        System.out.flush()
    }

    fun sumWithProgress(maxNum: Long): Long {
        var total = 0L
        val step = 10_000_000L
        val startTime = System.currentTimeMillis()

        for (i in 1..maxNum) {
            total += i
            if (i % step == 0L || i == maxNum) {
                val elapsedSec = (System.currentTimeMillis() - startTime) / 1000.0
                val speed = if (elapsedSec > 0) i / elapsedSec else 0.0
                printProgressBar(i, maxNum, elapsedSec, speed)
            }
        }
        println()
        return total
    }
}

fun main() {
    val targetNumber = 10_000_000_000L
    println("-".repeat(115))
    println("Kotlin: Calculating the sum from 1 to %,d (loop method)...".format(targetNumber))
    val startTime = System.currentTimeMillis()
    val result = SpeedTest.sumWithProgress(targetNumber)
    val endTime = System.currentTimeMillis()
    println("\nSum: %,d".format(result))
    println("Elapsed time: %.2f seconds".format((endTime - startTime) / 1000.0))
    println("-".repeat(115))
}
