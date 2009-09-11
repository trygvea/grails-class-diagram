import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH

class ClassDiagramLegendService {
	
	static transactional = false
<<<<<<< HEAD
	
=======

>>>>>>> git svn trouble - core 0.3 in
	byte[] createLegendImage() {
		def style = CH.config.classDiagram.legend.style
		def cfg = CH.config.classDiagram.associations

		def dotBuilder = new DotBuilder()
		dotBuilder.digraph {
			// build default node and edge styles
			graph (style.graphStyle)
			node ([shape:"record"] + style.nodeStyle)
			edge (style.edgeStyle)

			text ("rankdir=LR;");
			text ("subgraph cluster_legend {");

			def count = 1

			text (getLegendItem(count++, "one-way association",	"a contains a reference to b, but b have no references to a",
				cfg.arrows.references, cfg.arrows.none, cfg.decorators.none, cfg.decorators.none));

			text (getLegendItem(count++, "two-way association",	"a references b and b references a",
				cfg.arrows.none, cfg.arrows.none, cfg.decorators.none, cfg.decorators.none));

			text (getLegendItem(count++, "has one",	"a references a single b (object reference)",
				cfg.arrows.none, cfg.arrows.none, cfg.decorators.hasOne, cfg.decorators.none));

			text (getLegendItem(count++, "hasMany",	"a hasMany b (using gorm static hasMany)",
				cfg.arrows.none, cfg.arrows.none, cfg.decorators.hasMany, cfg.decorators.none));

			text (getLegendItem(count++, "belongsTo",	"a belongsTo b (using gorm static belongsTo)",
				cfg.arrows.belongsTo, cfg.arrows.none, cfg.decorators.none, cfg.decorators.none));

			text (getLegendItem(count++, "embedded",	"a is embedded in b (using gorm static embedded)",
				cfg.arrows.embedded, cfg.arrows.none, cfg.decorators.none, cfg.decorators.none));

			text (getLegendItem(count++, "extends",	"a extends b",
				cfg.arrows.inherits, cfg.arrows.none, cfg.decorators.none, cfg.decorators.none));

			text ("}");

		}.createImage("jpg")
	}

	private String getLegendItem(int count, String name, String description, String arrowhead, String arrowtail, String headlabel, String taillabel) {
		"""
	 	s${count} [label="a"]; 
		t${count} [label="b"];  
		s${count} -> t${count} [label="${name}", arrowhead="${arrowhead}", arrowtail="${arrowtail}", headlabel="${headlabel}", taillabel="${taillabel}"];
		l${count} [style="none", width="4", penwidth="0", label="${description}\\l"]; 
		t${count} -> l${count} [label="", penwidth="0", arrowhead="none"];
		"""
	} 

}