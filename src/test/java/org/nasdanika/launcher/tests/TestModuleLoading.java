package org.nasdanika.launcher.tests;

import java.io.File;
import java.lang.module.Configuration;
import java.lang.module.ModuleDescriptor.Requires;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class TestModuleLoading {
		
	@Test
//	@Disabled
	public void testModuleLoading() throws Exception {
        List<ModuleLayer> parentLayers = List.of(ModuleLayer.boot());        
        File libsDir = new File("target/libs");
        ModuleFinder finder = ModuleFinder.of(libsDir.toPath());
        Map<String, ModuleReference> allModules = new HashMap<>();
        for (ModuleReference ref: finder.findAll()) {
        	allModules.put(ref.descriptor().name(), ref);
        }                
        
        Set<URI> properModules = new HashSet<>();
        
        for (Entry<String, ModuleReference> me: new ArrayList<>(allModules.entrySet())) {
        	if (!me.getValue().descriptor().isAutomatic()) {
        		properModules.add(me.getValue().location().get());
        		for (Requires req: me.getValue().descriptor().requires()) {
        			ModuleReference reqModule = allModules.remove(req.name());
        			if (reqModule != null) {
        				properModules.add(reqModule.location().get());
        			}
        		}
        		allModules.remove(me.getKey());
        	}
        }
        
		List<Path> properPaths = new ArrayList<>();
		for (File lib : libsDir.listFiles()) {
			if (properModules.contains(lib.toURI())) {
				properPaths.add(lib.toPath());
			}
		}
		
                
        ModuleFinder properFinder = ModuleFinder.of(properPaths.toArray(new Path[properPaths.size()]));
        List<String> properRoots = properFinder.findAll()
                .stream()
                .map(m -> m.descriptor().name())
                .toList();

        Configuration appConfig = Configuration.resolve(
                properFinder,
                parentLayers.stream().map(ModuleLayer::configuration).collect(Collectors.toList()),
                ModuleFinder.of(),
                properRoots);
        
        List<URL> urls = new ArrayList<>();
        for (URI uri: allModules.values().stream().map(mr -> mr.location().get()).toList()) {
        	urls.add(uri.toURL());
        }        
		URLClassLoader classLoader = new URLClassLoader(urls.toArray(size -> new URL[size]));                
		ModuleLayer layer = ModuleLayer.defineModulesWithOneLoader(appConfig, parentLayers, classLoader).layer();
		
		Class<?> appClass = layer.findLoader("org.nasdanika.cli").loadClass("org.nasdanika.cli.Application");
		
		System.out.println(appClass.getName());
		Module module = appClass.getModule();
		System.out.println(module.getName());
		ModuleLayer appLayer = module.getLayer();
		appLayer.modules().forEach(System.out::println);
		System.out.println(appLayer.modules());
		
		Method mainMethod = appClass.getMethod("main", String[].class);
		Object arg = new String[] { "help" };
		mainMethod.invoke(null, arg);
		
	}

}
