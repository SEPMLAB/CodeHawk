package org.codehawk.plugin.java.functioningClass;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;

public class FileCounts implements Sensor{
	
	public static int fileCount;
	
	@Override
	public void describe(SensorDescriptor descriptor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute(SensorContext context) {
		FileSystem fs = context.fileSystem();
	    Iterable<InputFile> javaFiles = fs.inputFiles(fs.predicates().hasLanguage("java"));
	    
	}

}
