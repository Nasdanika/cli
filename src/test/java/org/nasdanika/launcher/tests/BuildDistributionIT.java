package org.nasdanika.launcher.tests;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.nasdanika.cli.LauncherCommand;

import picocli.CommandLine;

public class BuildDistributionIT {
		
	@Test
	public void generateLauncher() throws IOException {
		CommandLine launcherCommandLine = new CommandLine(new LauncherCommand());
		launcherCommandLine.execute(
				"-j", "@java",
//				"-P", "C:/Users/Pavel/Apps/nsd/",
				"-b", "target/dist", 
				"-o", "nsd.bat");
		
		launcherCommandLine.execute(
				"-b", "target/dist", 
				"-o", "nsd",
				"-p", ":",
				"-a", "$@");		
		
	}

}
