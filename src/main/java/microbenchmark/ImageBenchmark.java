package microbenchmark;

import apm.APM;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import apm.Proc;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;

public class ImageBenchmark {

    public static ImageWorker iw = new ImageWorker();
    public static String imageName = "benchmarkImage";

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(1)
    @Warmup(iterations = 0)
    @Measurement(iterations = 60, time = 10)
    public void measureThroughput() throws IOException {
        // use apm measurements, store in memory
        //APM.start();
        //Proc.start();
        iw.workOnImage(imageName);
        // use apm measurements
        //Proc.stop();
        //APM.stop();
    }
}
