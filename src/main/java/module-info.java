module org.nasdanika.launcher {
	
	exports org.nasdanika.launcher;
	
	requires transitive org.nasdanika.cli;
		
	requires org.nasdanika.models.echarts.graph;
	requires java.sql;
		
}