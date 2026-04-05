import org.nasdanika.capability.CapabilityFactory;
import org.nasdanika.launcher.XcoreCommandFactory;

module org.nasdanika.launcher {

	exports org.nasdanika.launcher;

	requires transitive org.nasdanika.cli;

	// For loading configuration
	requires ch.qos.logback.classic;
	requires ch.qos.logback.core;

	requires org.nasdanika.models.echarts.graph;
	requires java.sql;
	
	// For XcoreCommand
	requires org.nasdanika.xtext;
	requires org.eclipse.emf.ecore.xcore;
	requires com.google.guice;
	requires org.eclipse.xtext.common.types;
	requires org.apache.log4j;
	
	opens org.nasdanika.launcher to info.picocli, org.nasdanika.cli;

	provides CapabilityFactory with XcoreCommandFactory;
}