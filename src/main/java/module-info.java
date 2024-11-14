module org.nasdanika.launcher {
	
	exports org.nasdanika.launcher;
	
	requires transitive org.nasdanika.cli;
	requires transitive org.nasdanika.models.app.cli;
	requires transitive org.nasdanika.models.rules.cli;
	requires transitive org.nasdanika.models.architecture;
	requires transitive org.nasdanika.models.ecore.cli;
		
	requires org.nasdanika.models.echarts.graph;
		
}