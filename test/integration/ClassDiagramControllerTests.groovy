import grails.test.*

class ClassDiagramControllerTests extends GrailsUnitTestCase {
    def transactional = false
    def classDiagramService
	def grailsApplication
    
    @Override
    protected void setUp() {
        super.setUp();
        GroovyClassLoader gcl = new GroovyClassLoader()
        def classDiagramConfig = new ConfigSlurper().parse(gcl.loadClass('ClassDiagramConfig'))
        grailsApplication.setConfig(classDiagramConfig)
    }
    //
    void testPngImage()  {
        def controller = new ClassDiagramController()
        controller.classDiagramService = classDiagramService
        controller.params.outputFormat = "png"
        controller.model()
        assertEquals 200, controller.response.status 
        assertEquals "image/png", controller.response.contentType 
    }

    void testPdf()  {
        def controller = new ClassDiagramController()
        controller.classDiagramService = classDiagramService
        controller.params.outputFormat = "pdf"
        controller.model()
        assertEquals 200, controller.response.status 
        assertEquals "application/pdf", controller.response.contentType 
    }
    
}