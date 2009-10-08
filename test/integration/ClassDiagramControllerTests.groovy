import grails.test.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH

class ClassDiagramControllerTests extends GrailsUnitTestCase {
    def transactional = false
    def classDiagramService
    
    //
    void testPngImage()  {
        def controller = new ClassDiagramController()
        controller.classDiagramService = classDiagramService
        controller.request.addParameter("outputFormat","png") // Is NOT converted into Command Object, as the doc suggest (http://grails.org/doc/1.2/guide/9.%20Testing.html#9.2%20Integration%20Testing)
        controller.model()
        assertEquals 200, controller.response.status 
        assertEquals "image/png", controller.response.contentType 
    }
    
}