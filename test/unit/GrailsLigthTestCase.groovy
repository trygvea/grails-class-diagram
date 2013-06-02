import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.DefaultGrailsApplication

import grails.util.Holders

import org.codehaus.groovy.grails.commons.GrailsDomainClass;
import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler;

//import grails.util.Holders

class GrailsLigthTestCase extends GroovyTestCase {
    GrailsApplication grailsApplication = null;
    ConfigObject classDiagramConfig = null;
	

    @Override
    protected void setUp() {
        super.setUp();
        GroovyClassLoader gcl = new GroovyClassLoader()
        setupGrailsApplicationClasses(gcl)
        classDiagramConfig = new ConfigSlurper().parse(gcl.loadClass('ClassDiagramConfig'))
        Holders.setConfig(classDiagramConfig)
        grailsApplication = new DefaultGrailsApplication(gcl.getLoadedClasses(),gcl);
        grailsApplication.initialise();
    }

    protected String setupGrailsApplicationClasses(GroovyClassLoader gcl) {
    }

}