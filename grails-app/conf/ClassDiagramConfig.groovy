/**
 * Default properties. May be overridden in Config.properties for each project
 */
graphviz {
    dot.executable = "dot" // include full file path if not on path
}

classDiagram {
    preferences {
        defaults {
            // Defaults used in preferences form.
            skin = "regular"
            outputFormat = "png"    // Should be an image format from http://www.graphviz.org/doc/info/output.html
            showProperties = true
            showMethods = true
            showAssociationNames = true
            showMethodReturnType = true
            showMethodSignature = false
            showPropertyType = true
            showEmbeddedAsProperty = false
            showEnumAsProperty = true
            showPackages = false
            autoUpdate = true
            classSelection = "<all>"
            classSelectionIsRegexp = false
            graphOrientation = "TB" // See http://www.graphviz.org/doc/info/attrs.html#k:rankdir for valid values
            fontsize = 9
        }
    }
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
            hasOne = "1"
            hasMany = "*"
            none = ""
        }
    }
    skins {
        // See http://graphviz.org/doc/info/attrs.html for available properties on graph, node and edge.
        // You may use any property except the 'shape' property (it is set internally to [shape:'record']).
        // Also, if you use the fontsize property, the gui size slider will not be able to override it.
        classic {
            name = "Classic"
            graphStyle = [bgcolor:"white"]
            nodeStyle = [style:"rounded,filled", color:"blue", fillcolor:"azure2", fontname:"Verdana"]
            edgeStyle = [color:"gray40", fontname:"Verdana"]
            packageStyle = [style:"rounded,filled", color:"gray95", fontname:"Verdana"]
        }
        classicSpaced {
            name = "Classic Spaced"
            graphStyle = [bgcolor:"white", mclimit:100, nodesep:'1.5 equally', ranksep:'2 equally']
            nodeStyle = [style:"rounded,filled", color:"blue", fillcolor:"azure2", fontname:"Verdana", fontsize:18]
            edgeStyle = [color:"gray40", fontname:"Verdana", fontsize:18, labelfontsize:20, labeldistance:3.5]
            packageStyle = [style:"rounded,filled", color:"gray95"]
        }
        regular {
            name = "Regular"
            graphStyle = [bgcolor:"white"]
            nodeStyle = [style:"filled", color:"lightyellow3", fillcolor:"lightyellow", fontname:"Verdana"]
            edgeStyle = [color:"gray40", fontname:"Verdana"]
            packageStyle = [style:"filled", color:"gray95", fontname:"Verdana"]
        }
        white {
            name = "White on Gray"
            graphStyle = [bgcolor:"gray90"]
            nodeStyle = [style:"filled", color:"gray40", fillcolor:"white", fontname:"Verdana"]
            edgeStyle = [color:"gray40", fontname:"Verdana"]
            packageStyle = [style:"", color:"gray40", fontname:"Verdana"]
        }
        gray {
            name = "Gray"
            graphStyle = [bgcolor:"white"]
            nodeStyle = [style:"filled", color:"gray40", fillcolor:"gray90", fontname:"Verdana"]
            edgeStyle = [color:"gray40", fontname:"Verdana"]
            packageStyle = [style:"filled", color:"gray95", fontname:"Verdana"]
        }
    }
    legend {
        style {
            graphStyle = [bgcolor:"gray94", margin:"0,0", size:"7,7"]
            nodeStyle = [style:"filled", color:"gray50", fillcolor:"white", margin:"0,0", fontsize:15, fontname:"Verdana"]
            edgeStyle = [color:"gray50", fontsize:15, fontname:"Verdana"]
            packageStyle = [style:"filled", color:"gray94", fontname:"Verdana"]
        }
    }
}