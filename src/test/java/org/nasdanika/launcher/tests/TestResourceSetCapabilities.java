package org.nasdanika.launcher.tests;

import java.util.Map.Entry;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.junit.jupiter.api.Test;
import org.nasdanika.capability.CapabilityLoader;
import org.nasdanika.capability.CapabilityProvider;
import org.nasdanika.capability.ServiceCapabilityFactory;
import org.nasdanika.capability.ServiceCapabilityFactory.Requirement;
import org.nasdanika.capability.emf.ResourceSetRequirement;
import org.nasdanika.common.PrintStreamProgressMonitor;
import org.nasdanika.common.ProgressMonitor;

public class TestResourceSetCapabilities {
	
	@Test
	public void testResourceSetCapabilityFactory() {
		CapabilityLoader capabilityLoader = new CapabilityLoader();
		ProgressMonitor progressMonitor = new PrintStreamProgressMonitor();
		
		ResourceSetRequirement serviceRequirement = new ResourceSetRequirement(null, contributor -> {
			System.out.println(contributor);
			return true;
		});
		
		Requirement<ResourceSetRequirement, ResourceSet> requirement = ServiceCapabilityFactory.createRequirement(ResourceSet.class, null, serviceRequirement);		
		for (CapabilityProvider<?> cp: capabilityLoader.load(requirement, progressMonitor)) {
			System.out.println(cp);
			cp.getPublisher().subscribe(rs -> {
				ResourceSet resSet = (ResourceSet) rs;
				System.out.println("--- Resource set: " + resSet);
				System.out.println("\tEPackages");
				for (Entry<String, Object> pre: resSet.getPackageRegistry().entrySet()) {
					System.out.println("\t\t" + pre);
				}
				System.out.println("\tResource factories");
				System.out.println("\t\tExtensions");
				for (Entry<String, Object> pre: resSet.getResourceFactoryRegistry().getExtensionToFactoryMap().entrySet()) {
					System.out.println("\t\t\t" + pre);
				}
			});
		}
	}
	
	@Test
	public void testResourceSetCapabilityFactoryNoServiceRequirement() {
		CapabilityLoader capabilityLoader = new CapabilityLoader();
		ProgressMonitor progressMonitor = new PrintStreamProgressMonitor();
		Requirement<ResourceSetRequirement, ResourceSet> requirement = ServiceCapabilityFactory.createRequirement(ResourceSet.class);		
		for (CapabilityProvider<?> cp: capabilityLoader.load(requirement, progressMonitor)) {
			System.out.println(cp);
			cp.getPublisher().subscribe(System.out::println);
		}
	}

}
