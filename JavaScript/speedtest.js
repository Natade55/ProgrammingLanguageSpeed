const cliProgress = require('cli-progress');
const { performance } = require('perf_hooks');

function formatTime(sec) {
    const m = Math.floor(sec / 60);
    const s = Math.floor(sec % 60);
    return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
}

function formatSpeed(speed) {
    if (speed > 1_000_000) return `${(speed / 1_000_000).toFixed(2)}M num/s`;
    if (speed > 1_000) return `${(speed / 1_000).toFixed(2)}k num/s`;
    return `${speed.toFixed(0)} num/s`;
}

function makeSmoothBar(progress, barLength = 40) {
    const filledLength = progress * barLength;
    const full = Math.floor(filledLength);
    const partial = filledLength - full;
    const blocks = ['', '▏', '▎', '▍', '▌', '▋', '▊', '▉', '█'];
    const partialBlock = blocks[Math.round(partial * 8)];
    return '█'.repeat(full) + partialBlock + ' '.repeat(barLength - full - (partialBlock ? 1 : 0));
}

function progressFormat(options, params, payload) {
    const progress = params.value / params.total;
    const bar = makeSmoothBar(progress, 40);
    const elapsed = payload.elapsed ?? 0;
    const remaining = payload.remaining ?? 0;
    return `Java Script Loop: ${(progress * 100).toFixed(2)}%|${bar}| ${elapsed}<${remaining} | ${payload.speed} | Current: ${payload.currentNum}`;
}

function sumWithSmoothProgress(maxNum) {
    let total = 0n;
    const loopStartTime = performance.now();
    let lastUpdateTime = 0;
    const minUpdateInterval = 33.33;
    const progressBar = new cliProgress.SingleBar({
        format: progressFormat,
        fps: 30,
        hideCursor: true,
        barsize: 40
    });

    progressBar.start(Number(maxNum), 0, {
        currentNum: "0",
        speed: "N/A",
        elapsed: "00:00",
        remaining: "??:??"
    });

    const updateStep = 50_000_000n;

    for (let i = 1n; i <= maxNum; i++) {
        total += i;
        if (i % updateStep === 0n || i === maxNum) {
            const currentTime = performance.now();
            if (currentTime - lastUpdateTime > minUpdateInterval) {
                const elapsedSec = (currentTime - loopStartTime) / 1000;
                const progress = Number(i) / Number(maxNum);
                const estimatedTotal = progress > 0 ? elapsedSec / progress : 0;
                const remainingSec = Math.max(0, estimatedTotal - elapsedSec);
                const currentSpeed = elapsedSec > 0 ? Number(i) / elapsedSec : 0;

                progressBar.update(Number(i), {
                    currentNum: i.toLocaleString(),
                    speed: formatSpeed(currentSpeed),
                    elapsed: formatTime(elapsedSec),
                    remaining: formatTime(remainingSec)
                });
                lastUpdateTime = currentTime;
            }
        }
    }

    const finalElapsedTime = (performance.now() - loopStartTime) / 1000;
    progressBar.update(Number(maxNum), {
        currentNum: maxNum.toLocaleString(),
        speed: formatSpeed(Number(maxNum) / finalElapsedTime),
        elapsed: formatTime(finalElapsedTime),
        remaining: formatTime(0)
    });
    progressBar.stop();
    return total;
}

const targetNumber = 10000000000n;
console.log("-".repeat(119));
console.log(`Java Script: Calculating the sum from 1 to ${targetNumber.toLocaleString()} (loop method)...`);
const overallStartTime = performance.now();
const result = sumWithSmoothProgress(targetNumber);
const overallEndTime = performance.now();
console.log(`\nSum: ${result.toLocaleString()}`);
console.log(`Elapsed time: ${((overallEndTime - overallStartTime) / 1000).toFixed(2)} seconds`);
console.log("-".repeat(119));
