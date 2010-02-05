import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.DefaultGrailsApplication
import org.codehaus.groovy.grails.commons.ConfigurationHolder

import org.codehaus.groovy.grails.commons.GrailsDomainClass;
import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler;

class GrailsLigthTestCase extends GroovyTestCase {
    GrailsApplication grailsApplication = null;
    ConfigObject classDiagramConfig = null;

    @Override
    protected void setUp() {
        super.setUp();
        GroovyClassLoader gcl = new GroovyClassLoader()
        setupGrailsApplicationClasses(gcl)
        classDiagramConfig = new ConfigSlurper().parse(gcl.loadClass('ClassDiagramConfig'))
        ConfigurationHolder.setConfig(classDiagramConfig)
        grailsApplication = new DefaultGrailsApplication(gcl.getLoadedClasses(),gcl);
        grailsApplication.initialise();
    }

    protected String setupGrailsApplicationClasses(GroovyClassLoader gcl) {
    }

}