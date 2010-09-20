import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH

class ClassDiagramLegendService {
    
    static transactional = false

    byte[] createLegend() {
        def dotBuilder = createDotLegend()
        dotBuilder.createDiagram("png")
    }

    DotBuilder createDotLegend() {
        def style = CH.config.classDiagram.legend.style
        def cfg = CH.config.classDiagram.associations

        def dotBuilder = new DotBuilder()
        dotBuilder.digraph {
            // build default node and edge styles
            graph (style.graphStyle)
            node ([shape:"record"] + style.nodeStyle)
            edge (style.edgeStyle)

            rankdir("LR");
            subgraph("cluster_legend") {
                style.packageStyle.each {
                    "${it.key}" ("${it.value}")
                }

                def count = 1

                buildLegendItem(dotBuilder, count++, 
                    "one-way association",    "a contains a reference to b, but b have no references to a",
                    cfg.arrows.references, cfg.arrows.none, cfg.decorators.none, cfg.decorators.none);

                buildLegendItem(dotBuilder, count++, 
                    "two-way association",    "a references b and b references a",
                    cfg.arrows.none, cfg.arrows.none, cfg.decorators.none, cfg.decorators.none);

                buildLegendItem(dotBuilder, count++, 
                    "has one",    "a references a single b (object reference)",
                    cfg.arrows.none, cfg.arrows.none, cfg.decorators.hasOne, cfg.decorators.none);

                buildLegendItem(dotBuilder, count++, 
                    "hasMany",    "a hasMany b (using gorm static hasMany)",
                    cfg.arrows.none, cfg.arrows.none, cfg.decorators.hasMany, cfg.decorators.none);

                buildLegendItem(dotBuilder, count++, 
                    "belongsTo",    "a belongsTo b (using gorm static belongsTo)",
                    cfg.arrows.belongsTo, cfg.arrows.none, cfg.decorators.none, cfg.decorators.none);

                buildLegendItem(dotBuilder, count++, 
                    "embedded",    "a is embedded in b (using gorm static embedded)",
                    cfg.arrows.embedded, cfg.arrows.none, cfg.decorators.none, cfg.decorators.none);

                buildLegendItem(dotBuilder, count++, 
                    "extends",    "a extends b",
                    cfg.arrows.inherits, cfg.arrows.none, cfg.decorators.none, cfg.decorators.none);
            }

        }
        dotBuilder
    }

    private void buildLegendItem(dotBuilder, int count, String name, String description, String arrowhead, String arrowtail, String headlabel, String taillabel) {
        def nodeA = "s${count}"
        def nodeB = "t${count}"
        def legendNode = "l${count}"
        dotBuilder."${nodeA}" ([label:"a"])
        dotBuilder."${nodeB}" ([label:"b"])
        dotBuilder.from(nodeA).to(nodeB, [label:"${name}", arrowhead:"${arrowhead}", arrowtail:"${arrowtail}", headlabel:"${headlabel}", taillabel:"${taillabel}", dir:'both'])
        dotBuilder."${legendNode}" ([style:"none", width:"7", penwidth:"0", label:"${description}\\l"])
        dotBuilder.from(nodeB).to(legendNode, [label:"", penwidth:"0", arrowhead:"none"])
    } 

}