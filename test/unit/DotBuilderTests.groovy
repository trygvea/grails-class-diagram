class DotBuilderTests extends GroovyTestCase {
    
    void testSimpleDigraph() {
        def dotBuilder = new DotBuilder()
        dotBuilder.digraph {
            from("Foo").to("Bar")
        }    
        assertEquals dotBuilder.dotString, """digraph {
Foo -> Bar;
}
"""
    }

    void testSimpleUndirectionalGraph() {
        def dotBuilder = new DotBuilder()
        dotBuilder.graph {
            from("Foo").to("Bar")
        }    
        assertEquals dotBuilder.dotString, """graph {
Foo - Bar;
}
"""
    }

    void testDigraph() {
        def dotBuilder = new DotBuilder()
        dotBuilder.digraph {
            rankdir ("LR")
            graph ([bgcolor:"white"])
            node ([fontsize:10])
            edge ([fontsize:11])
            subgraph("cluster_packageName") {
                labeljust("l")
                label ("package.a")
                Foo ([label:"Entity:Foo"])
                "Bar" ([label:"Entity:Bar"])
                FooBar([:])
                "Name with spaces" ([:])
                "Simple Entity with empty params" ()
            }
            from("Foo").to("Bar", [arrowhead:"none", arrowtail:"none"])
        }    
        assertEquals dotBuilder.dotString, """digraph {
rankdir="LR";
graph [bgcolor="white"];
node [fontsize="10"];
edge [fontsize="11"];
subgraph "cluster_packageName" {
labeljust="l";
label="package.a";
Foo [label="Entity:Foo"];
Bar [label="Entity:Bar"];
FooBar [];
"Name with spaces" [];
"Simple Entity with empty params";
}
Foo -> Bar [arrowhead="none", arrowtail="none"];
}
"""
    }
}