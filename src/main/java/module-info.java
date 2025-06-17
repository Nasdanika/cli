module org.nasdanika.launcher {
	
	exports org.nasdanika.launcher;
	
	requires transitive org.nasdanika.cli;
	
	// For loading configuration
	requires ch.qos.logback.classic;
	requires ch.qos.logback.core;	
		
	requires org.nasdanika.models.echarts.graph;
	requires java.sql;
		
}