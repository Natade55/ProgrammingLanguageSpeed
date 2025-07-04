import time
import sys

BAR_WIDTH = 40

def format_number(n):
    return f"{n:,}"

def format_time(sec):
    m = int(sec // 60)
    s = int(sec % 60)
    return f"{m:02d}:{s:02d}"

def format_speed(speed):
    if speed > 1e6:
        return f"{speed / 1e6:.2f}M num/s"
    elif speed > 1e3:
        return f"{speed / 1e3:.2f}k num/s"
    else:
        return f"{speed:.0f} num/s"

def print_progress_bar(current, total, elapsed_sec, speed):
    progress = current / total
    filled = int(progress * BAR_WIDTH)
    bar = '#' * filled + ' ' * (BAR_WIDTH - filled)

    estimated_total = elapsed_sec / progress if progress > 0 else 0.0
    remaining_sec = max(0, estimated_total - elapsed_sec)

    elapsed_str = format_time(elapsed_sec)
    remain_str = format_time(remaining_sec)
    speed_str = format_speed(speed)
    curr_str = format_number(current)

    sys.stdout.write(
        f"\rPy Loop: {progress * 100:6.2f}%|{bar}| {elapsed_str}<{remain_str} |{speed_str} | Current: {curr_str}"
    )
    sys.stdout.flush()

def sum_with_progress(max_num):
    total = 0
    step = 10_000_000
    start_time = time.time()

    for i in range(1, max_num + 1):
        total += i
        if i % step == 0 or i == max_num:
            elapsed_sec = time.time() - start_time
            speed = i / elapsed_sec if elapsed_sec > 0 else 0.0
            print_progress_bar(i, max_num, elapsed_sec, speed)
    print()
    return total

def main():
    target_number = 10_000_000_000
    print('-' * 115)
    print(f"Py: Calculating the sum from 1 to {format_number(target_number)} (loop method)...")
    start_time = time.time()
    result = sum_with_progress(target_number)
    end_time = time.time()
    print(f"\nSum: {format_number(result)}")
    print(f"Elapsed time: {end_time - start_time:.2f} seconds")
    print('-' * 115)

if __name__ == "__main__":
    main()
