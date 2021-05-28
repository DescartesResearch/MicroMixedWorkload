package microbenchmark;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class RunBenchmark {

	public static void main(String[] args) throws RunnerException {

		//TODO print procfs before

		Options opt = new OptionsBuilder()
			.include(ImageBenchmark.class.getSimpleName())
			.build();

		new Runner(opt).run().stream().findFirst();

		//TODO print procfs after
	}

}
