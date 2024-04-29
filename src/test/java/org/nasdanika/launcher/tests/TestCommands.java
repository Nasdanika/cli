package org.nasdanika.launcher.tests;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.nasdanika.html.model.app.gen.cli.SiteCommand;

import picocli.CommandLine;

public class TestCommands {
	
	@Disabled
	@Test
	public void testGenerateAppSite() {
		CommandLine siteCommandLine = new CommandLine(new SiteCommand());
		String[] args = "-b C:\\Users\\Pavel\\git\\nasdanika.github.io-test -T model/page-template.yml -m https://docs.nasdanika.org -r 1 -e CNAME favicon.ico images/** -- model/nasdanika.drawio#/ docs".split(" ");
		siteCommandLine.execute(args);
	}

}
