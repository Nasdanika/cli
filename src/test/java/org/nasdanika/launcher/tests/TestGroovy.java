package org.nasdanika.launcher.tests;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.jupiter.api.Test;

public class TestGroovy {
	
	@Test
	public void testAnnotations() throws ScriptException {
		String source = """
				
				class MyProcessor {
				
					@org.nasdanika.graph.processor.Processor
					def annotatedMethod() {
						z
					}
				
				}
				
				new MyProcessor();
				
				"""; 
		
		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine scriptEngine = sem.getEngineByName("groovy");
		scriptEngine.put("z", 185);
		Object result = scriptEngine.eval(source);
		System.out.println(result);		
		for (Method method: result.getClass().getDeclaredMethods()) {
			System.out.println(method);
			for (Annotation annotation: method.getAnnotations()) {
				System.out.println("\t" + annotation);
			}
		}
	}

}
