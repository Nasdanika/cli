package org.nasdanika.launcher.tests;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.junit.jupiter.api.Test;
import org.nasdanika.cli.LauncherCommand;
import org.nasdanika.launcher.Launcher;

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
		
		ModuleLayer layer = Launcher.class.getModule().getLayer();
		try (Writer writer = new FileWriter(new File("target/dist/modules"))) {
			for (String name: layer.modules().stream().map(Module::getName).sorted().toList()) {
				writer.write(name);
				writer.write(System.lineSeparator());
			};
		}
		
		CommandLine launcherCommandLine = new CommandLine(new LauncherCommand());
		launcherCommandLine.execute(
				"-j", "@java",
				"-m", "org.nasdanika.launcher",
				"-c", "org.nasdanika.launcher.Launcher",
				"-f", "options",
//				"-r", "org.nasdanika.**,com.azure.**,io.netty.**",
				"-b", "target/dist", 
				"-M", "target/dist/modules", 
				"-o", "nsd.bat");
		
		launcherCommandLine.execute(
				"-m", "org.nasdanika.launcher",
				"-c", "org.nasdanika.launcher.Launcher",
				"-j", "@java -Xdebug -Xrunjdwp:transport=dt_socket,address=8998,server=y",
				"-f", "options",
//				"-r", "org.nasdanika.**,com.azure.**,io.netty.**",
				"-b", "target/dist", 
				"-M", "target/dist/modules", 
				"-o", "nsd-debug.bat");
		
		launcherCommandLine.execute(
				"-b", "target/dist", 
				"-M", "target/dist/modules", 
				"-m", "org.nasdanika.launcher",
				"-c", "org.nasdanika.launcher.Launcher",
				"--add-modules", "ALL-SYSTEM",
				"-j", "#!/bin/bash\n\njava",
//				"-r", "org.nasdanika.**,com.azure.**,io.netty.**",
				"-o", "nsd",
				"-p", ":",
				"-a", "$@");		
		
		launcherCommandLine.execute(
				"-b", "target/dist", 
				"-M", "target/dist/modules", 
				"-m", "org.nasdanika.launcher",
				"-c", "org.nasdanika.launcher.Launcher",
				"--add-modules", "ALL-SYSTEM",
//				"-r", "org.nasdanika.**,com.azure.**,io.netty.**",
				"-j", "#!/bin/bash\n\njava -Xdebug -Xrunjdwp:transport=dt_socket,address=8998,server=y",
				"-o", "nsd-debug",
				"-p", ":",
				"-a", "$@");		
		
	}

}
