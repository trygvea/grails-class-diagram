import java.util.regex.Matcher;

class PackageTests extends GrailsLigthTestCase {

    @Override 
    protected String setupGrailsApplicationClasses(GroovyClassLoader gcl) {
        gcl.parseClass('''
package a.b.c
class Foo { Long id;Long version;
    String name
}
''')

        gcl.parseClass('''
package a.b.d
class Bar { Long id;Long version;
    String name
}
''')
    }

    void testShowPackage() {
        def service = new ClassDiagramService()
        def domainClasses = grailsApplication.domainClasses
        classDiagramConfig.showPackages = true
        def dot = service.createDotDiagram(domainClasses, classDiagramConfig).dotString
        
        assertTrue "package a.b.c expected", (dot =~ /subgraph "cluster_a.b.c" \{/).size() > 0 
        assertTrue "package a.b.d expected", (dot =~ /subgraph "cluster_a.b.d" \{/).size() > 0 
    }

    void testNoShowPackage() {
        def service = new ClassDiagramService()
        def domainClasses = grailsApplication.domainClasses
        classDiagramConfig.showPackages = false
        def dot = service.createDotDiagram(domainClasses, classDiagramConfig).dotString
        println "##"+dot
        
        assertFalse "no packages (subgraphs) expected", (dot =~ /subgraph/).size() > 0 
        assertFalse "package a.b.d expected", (dot =~ /cluster/).size() > 0 
    }
}