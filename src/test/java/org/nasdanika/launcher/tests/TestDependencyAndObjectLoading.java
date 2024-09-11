package org.nasdanika.launcher.tests;

import java.io.File;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.nasdanika.capability.CapabilityLoader;
import org.nasdanika.capability.CapabilityProvider;
import org.nasdanika.capability.maven.DependencyRequestRecord;
import org.nasdanika.common.PrintStreamProgressMonitor;
import org.nasdanika.common.ProgressMonitor;

public class TestDependencyAndObjectLoading {
	
	@Test
	public void testDependencyLoading() {
		CapabilityLoader capabilityLoader = new CapabilityLoader();
		DependencyRequestRecord requirement = new DependencyRequestRecord(
				new String[] { "org.apache.groovy:groovy-all:pom:4.0.22" }, 
				null, 
				null, 
				"target/test-repo");
		
		ProgressMonitor progressMonitor = new PrintStreamProgressMonitor();
		Iterable<CapabilityProvider<Object>> cpi = capabilityLoader.load(requirement, progressMonitor);
		for (CapabilityProvider<Object> cp: cpi) {
			@SuppressWarnings("unchecked")
			Collection<File> result = (Collection<File>) cp.getPublisher().blockFirst();
			System.out.println(result);
		}
	}

}
