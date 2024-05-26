module org.nasdanika.launcher {
	
	exports org.nasdanika.launcher;
	
	requires transitive org.nasdanika.cli;
	requires transitive org.nasdanika.html.model.app.gen.cli;
	requires transitive org.nasdanika.models.rules.cli;
		
	requires org.nasdanika.models.echarts.graph;
		
}