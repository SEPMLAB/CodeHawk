package org.smell.rule.pluginregister;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.resources.File;
import org.sonar.samples.java.checks.BrokenModularizationRule;

//TODO
public class FileSensor implements Sensor {

	String filesDetected = "D:\\test\\filesDetected.txt";

	@Override
	public void describe(SensorDescriptor descriptor) {
		descriptor.name("Compute number of files");
	}

	@Override
	public void execute(SensorContext context) {
		FileSystem fs = context.fileSystem();
		// only "main" files, but not "tests"
		Iterable<InputFile> javaFiles = fs.inputFiles(fs.predicates().hasLanguage("java"));
		for (InputFile file : javaFiles) {
			java.io.File targetFile = file.file();
			logInformation(targetFile.getName() + "\r\n");
		}
	}

	private void logInformation(String log) {
		try {
			BrokenModularizationRule.logOnFile(filesDetected, log);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}