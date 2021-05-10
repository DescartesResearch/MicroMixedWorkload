package microbenchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ImageBenchmark {

	public static ImageWorker iw = new ImageWorker();
	public static String imageName = "benchmarkImage";

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.SECONDS)
	@Fork(1)
	public void measureThroughput() throws IOException {
		iw.workOnImage(imageName);
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.SECONDS)
	@Fork(1)
	public void measureAvgTime() throws IOException {
		iw.workOnImage(imageName);
	}
}
