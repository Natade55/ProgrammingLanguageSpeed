String formatNumber(int n) {
  String s = n.toString();
  int len = s.length;
  if (len <= 3) return s;
  var buf = StringBuffer();
  int head = len % 3;
  if (head > 0) buf.write(s.substring(0, head));
  for (int i = head; i < len; i += 3) {
    if (buf.isNotEmpty) buf.write(',');
    buf.write(s.substring(i, i + 3));
  }
  return buf.toString();
}

String formatTime(double sec) {
  int m = sec ~/ 60;
  int s = sec.toInt() % 60;
  return '${m.toString().padLeft(2, '0')}:${s.toString().padLeft(2, '0')}';
}

String formatSpeed(double speed) {
  if (speed > 1e6) return '${(speed / 1e6).toStringAsFixed(2)}M num/s';
  if (speed > 1e3) return '${(speed / 1e3).toStringAsFixed(2)}k num/s';
  return '${speed.toStringAsFixed(0)} num/s';
}

void printProgressBar(int current, int total, double elapsedSec, double speed) {
  const barWidth = 40;
  double progress = current / total;
  int filled = (progress * barWidth).toInt();

  String bar = '';
  for (int i = 0; i < barWidth; i++) {
    bar += (i < filled) ? '#' : ' ';
  }

  double estimatedTotal = progress > 0 ? elapsedSec / progress : 0.0;
  double remainingSec = (estimatedTotal - elapsedSec).clamp(0, double.infinity);

  print(
    'Dart Loop: ${(progress * 100).toStringAsFixed(2).padLeft(6)}%|$bar| '
    '${formatTime(elapsedSec)}<${formatTime(remainingSec)} |'
    '${formatSpeed(speed)} | Current: ${formatNumber(current)}',
  );
}

int sumWithProgress(int maxNum) {
  int total = 0;
  const step = 1000000;
  final startTime = DateTime.now().millisecondsSinceEpoch;

  for (int i = 1; i <= maxNum; i++) {
    total += i;
    if (i % step == 0 || i == maxNum) {
      double elapsedSec =
          (DateTime.now().millisecondsSinceEpoch - startTime) / 1000.0;
      double speed = elapsedSec > 0 ? i / elapsedSec : 0.0;
      printProgressBar(i, maxNum, elapsedSec, speed);
    }
  }
  return total;
}

void main() {
  const targetNumber = 10000000000;
  print('-' * 113);
  print(
    'Dart: Calculating the sum from 1 to ${formatNumber(targetNumber)} (loop method)...',
  );
  final startTime = DateTime.now().millisecondsSinceEpoch;
  final result = sumWithProgress(targetNumber);
  final endTime = DateTime.now().millisecondsSinceEpoch;
  print('\nSum: ${formatNumber(result)}');
  print(
    'Elapsed time: ${((endTime - startTime) / 1000.0).toStringAsFixed(2)} seconds',
  );
  print('-' * 113);
}
