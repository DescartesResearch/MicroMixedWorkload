package microbenchmark;

import apm.APM;
import apm.Proc;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

// Build command: mvn clean compile assembly:single
public class RunBenchmark {

	public static void main(String[] args) throws RunnerException {

		//TODO print procfs before
		Proc.start();
		//APM.start();

		Options opt = new OptionsBuilder()
			.include(ImageBenchmark.class.getSimpleName())
			.build();

		new Runner(opt).run().stream().findFirst();

		//TODO print procfs after
		Proc.stop();
		//APM.stop();
	}



}
