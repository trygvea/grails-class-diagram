/**
 * Default properties. May be overridden in Config.properties for each project
 */
graphviz {
	dot.executable = "dot" // include full file path if not on path
}

classDiagram {
	associations {
		arrows {
			// See http://www.graphviz.org/doc/info/arrows.html for available arrowheads and their visual appearance 
			references = "open"
			belongsTo = "odiamond"
			embedded = "diamond"
			inherits = "onormal"
			none = "none"
		}
		decorators {
			// plain text to be shown on edge ends
			hasOne = "    1"
			hasMany = "    *"
			none = ""
		}
	}
	skins {
		// See http://www.graphviz.org/Documentation.php for available properties on graph, node and edge
		// Note: the following properties have a special meaning and should be changed with care: {shape, fontSize}. 
		classic {
			name = "Classic"
			graphStyle = [bgcolor:"white"]
			nodeStyle = [style:"rounded,filled", color:"blue", fillcolor:"azure2", fontname:"Verdana"]
			edgeStyle = [color:"gray40", fontname:"Verdana"]
		}
		regular {
			name = "Regular"
			graphStyle = [bgcolor:"white"]
			nodeStyle = [style:"filled", color:"lightyellow3", fillcolor:"lightyellow", fontname:"Verdana"]
			edgeStyle = [color:"gray40", fontname:"Verdana"]
		}
		white {
			name = "White on Gray"
			graphStyle = [bgcolor:"gray90"]
			nodeStyle = [style:"filled", color:"gray40", fillcolor:"white", fontname:"Verdana"]
			edgeStyle = [color:"gray40", fontname:"Verdana"]
		}
		gray {
			name = "Gray"
			graphStyle = [bgcolor:"white"]
			nodeStyle = [style:"filled", color:"gray40", fillcolor:"gray90", fontname:"Verdana"]
			edgeStyle = [color:"gray40", fontname:"Verdana"]
		}
	}
	legend {
		style {
			graphStyle = [bgcolor:"gray94"]
			nodeStyle = [style:"filled", color:"gray50", fillcolor:"white", fontsize:8, fontname:"Verdana"]
			edgeStyle = [color:"gray50", fontsize:8, fontname:"Verdana"]
		}
	}
}