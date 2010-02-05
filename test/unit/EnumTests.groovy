import java.util.regex.Matcher;

class EnumTests extends GrailsLigthTestCase {

    @Override 
    protected String setupGrailsApplicationClasses(GroovyClassLoader gcl) {
        gcl.parseClass('''
class User { Long id;Long version;
    static hasMany = [roles: Role]
    Role primaryRole
    String name
}
enum Role {
    ADMIN("0"), MANAGER("2"), EMPLOYEE("4")
    Role(String id) { this.id = id }
    final String id
}
''')
    }

    void testShowEnumAsNode() {
        def service = new ClassDiagramService()
        def domainClasses = grailsApplication.domainClasses
        classDiagramConfig.showEnumAsProperty = false
        def dot = service.createDotDiagram(domainClasses, classDiagramConfig).dotString

        assertTrue "enum expected to be both a node and a releation", (dot =~ /(?s)Role \[.*User -> Role/).size() > 0   // Where the (?s) makes the wildcard (.) match newlines, too. (http://java.sun.com/j2se/1.4.2/docs/api/java/util/regex/Pattern.html#DOTALL) 
    }

    void testShowEnumAsProperty() {
        def service = new ClassDiagramService()
        def domainClasses = grailsApplication.domainClasses
        classDiagramConfig.showEnumAsProperty = true
        def dot = service.createDotDiagram(domainClasses, classDiagramConfig).dotString

        assertFalse "enum not expected to be a node", (dot =~ /Role \[/).size() > 0    
        assertFalse "enum not expected to be a relation", (dot =~ /User -> Role/).size() > 0    
    }

}