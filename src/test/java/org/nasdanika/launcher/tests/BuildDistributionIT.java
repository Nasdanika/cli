package org.nasdanika.launcher.tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.junit.jupiter.api.Test;
import org.nasdanika.cli.LauncherCommand;

import picocli.CommandLine;

public class BuildDistributionIT {
		
	@Test
	public void generateLauncher() throws IOException {
		for (File tf: new File("target").listFiles()) {
			if (tf.getName().endsWith(".jar") && !tf.getName().endsWith("-sources.jar") && !tf.getName().endsWith("-javadoc.jar")) {
				Files.copy(
						tf.toPath(), 
						new File(new File("target/dist/lib"), tf.getName()).toPath(), 
						StandardCopyOption.REPLACE_EXISTING);		
			}
		}
		
		CommandLine launcherCommandLine = new CommandLine(new LauncherCommand());
		launcherCommandLine.execute(
				"-j", "@java",
				"-m", "org.nasdanika.launcher",
				"-c", "org.nasdanika.launcher.Launcher",
				"-f", "options",
				"-r", "org.nasdanika.**",
				"-b", "target/dist", 
				"-o", "nsd.bat");
		
		launcherCommandLine.execute(
				"-j", "@java",
				"-m", "org.nasdanika.launcher",
				"-c", "org.nasdanika.launcher.Launcher",
				"-j", "@java -Xdebug -Xrunjdwp:transport=dt_socket,address=8998,server=y",
				"-f", "options",
				"-r", "org.nasdanika.**",
				"-b", "target/dist", 
				"-o", "nsd-debug.bat");
		
		launcherCommandLine.execute(
				"-b", "target/dist", 
				"-m", "org.nasdanika.launcher",
				"-c", "org.nasdanika.launcher.Launcher",
				"-r", "org.nasdanika.**",
				"-o", "nsd",
				"-p", ":",
				"-a", "$@");		
		
		launcherCommandLine.execute(
				"-b", "target/dist", 
				"-m", "org.nasdanika.launcher",
				"-c", "org.nasdanika.launcher.Launcher",
				"-r", "org.nasdanika.**",
				"-j", "@java -Xdebug -Xrunjdwp:transport=dt_socket,address=8998,server=y",
				"-o", "nsd-debug",
				"-p", ":",
				"-a", "$@");		
		
	}

}
