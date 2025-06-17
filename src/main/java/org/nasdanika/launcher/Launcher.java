package org.nasdanika.launcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.nasdanika.capability.CapabilityLoader;
import org.nasdanika.capability.CapabilityProvider;
import org.nasdanika.capability.ServiceCapabilityFactory;
import org.nasdanika.capability.ServiceCapabilityFactory.Requirement;
import org.nasdanika.cli.ShellCommand;
import org.nasdanika.cli.SubCommandRequirement;
import org.nasdanika.common.Closeable;
import org.nasdanika.common.LoggerProgressMonitor;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.telemetry.GlobalOpenTelemetryCapabilityFactory;
import org.nasdanika.telemetry.TelemetryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import picocli.CommandLine;

public class Launcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);
	
	private static String getVersion() {
		Module module = Launcher.class.getModule();		
		if (module == null) {
			return "(unknown)";
		}
		
		return module.getDescriptor().toNameAndVersion();
	}
	
	public static void main(String[] args) {
		OpenTelemetry openTelemetry = GlobalOpenTelemetryCapabilityFactory.getGlobalOpenTelemetry();
		Tracer tracer = openTelemetry.getTracer(Launcher.class.getName(), getVersion());
		Attributes attributes = Attributes
				.builder()
				.put("arguments", args)
				.build();
		SpanBuilder spanBuilder = tracer.spanBuilder("Launcher").setAllAttributes(attributes);
		Span launcherSpan = TelemetryUtil.buildSpan(spanBuilder).startSpan();
		Integer exitCode = null;
		try (Scope scope = launcherSpan.makeCurrent()) {
			try (ProgressMonitor progressMonitor = new LoggerProgressMonitor(LOGGER)) {  //TracerProgressMonitor(tracer, "Launcher");
				CapabilityLoader capabilityLoader = new CapabilityLoader(Launcher.class.getModule().getLayer());
				
				// Sub-commands, sorting alphabetically
				List<CommandLine> rootCommands = new ArrayList<>();		
				Requirement<SubCommandRequirement, CommandLine> subCommandRequirement = ServiceCapabilityFactory.createRequirement(CommandLine.class, null,  new SubCommandRequirement(Collections.emptyList()));
				for (CapabilityProvider<Object> cp: capabilityLoader.load(subCommandRequirement, progressMonitor)) {
					cp.getPublisher().filter(Objects::nonNull).collectList().block().forEach(cmd -> rootCommands.add((CommandLine) cmd));
				}
				
				// Executing the first one
				for (CommandLine rootCommand: rootCommands) {	
					rootCommand.addSubcommand(new ShellCommand(rootCommand));
					try {
						exitCode = rootCommand.execute(args);
						launcherSpan.setAttribute("exit-code", exitCode);
					} finally {
						if (rootCommand instanceof Closeable) {
							((Closeable) rootCommand).close(progressMonitor.split("Closing root command", 1));
						}
						capabilityLoader.close(progressMonitor.split("Closing capability loader", 1));
					}
				}
				
				if (exitCode == null) {
					throw new UnsupportedOperationException("There are no root commands");
				}
			}
		} catch (Exception e) {
			launcherSpan.recordException(e);
	        launcherSpan.setStatus(StatusCode.ERROR);
			throw e;
		} finally {			
			launcherSpan.end();
		}
		System.exit(exitCode);
	}

}
