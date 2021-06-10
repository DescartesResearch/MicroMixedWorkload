package microbenchmark;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import static microbenchmark.PID.getPID;

public class RunBenchmark {

	public static void main(String[] args) throws RunnerException, InterruptedException {

		//TODO print procfs before
		System.out.println(getPID());
		Options opt = new OptionsBuilder()
			.include(ImageBenchmark.class.getSimpleName())
			.build();

		new Runner(opt).run().stream().findFirst();

		//TODO print procfs after
	}



}
