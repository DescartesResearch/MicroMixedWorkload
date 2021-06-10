package microbenchmark;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import static microbenchmark.PID.getPID;
import static microbenchmark.PID.writePID;

public class RunBenchmark {

	public static void main(String[] args) throws RunnerException {

		//TODO print procfs before
		int pid = getPID();
		System.out.println(pid);
		writePID(pid);

		Options opt = new OptionsBuilder()
			.include(ImageBenchmark.class.getSimpleName())
			.build();

		new Runner(opt).run().stream().findFirst();

		//TODO print procfs after
	}



}
