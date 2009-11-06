import java.util.regex.Matcher;

class EmbeddedTests extends GrailsLigthTestCase {

    @Override 
    protected String setupGrailsApplicationClasses(GroovyClassLoader gcl) {
        gcl.parseClass('''
class User { Long id;Long version;
    static hasMany = [roles: Role]
    String name
}
class Role {
    String name
}
''')
    }

    void testShowEmbeddedAsNode() {
        def service = new ClassDiagramService()
        def domainClasses = grailsApplication.domainClasses
        classDiagramConfig.showEmbeddedAsProperty = false
        def dot = service.createDotDiagram(domainClasses, classDiagramConfig).dotString

        assertTrue "Embedded expected to be both a node and a releation", (/(?s)Role \[.*User -> Role/).size() > 0  // Where the (?s) makes the wildcard (.) match newlines, too. (http://java.sun.com/j2se/1.4.2/docs/api/java/util/regex/Pattern.html#DOTALL) 
    }

    void testShowEmbeddedAsProperty() {
        def service = new ClassDiagramService()
        def domainClasses = grailsApplication.domainClasses
        classDiagramConfig.showEmbeddedAsProperty = true
        def dot = service.createDotDiagram(domainClasses, classDiagramConfig).dotString

        assertFalse "Embedded not expected to be a node", (dot =~ /Role \[/).size() > 0    
        assertFalse "Embedded not expected to be a relation", (dot =~ /User -> Role/).size() > 0    
    }
}