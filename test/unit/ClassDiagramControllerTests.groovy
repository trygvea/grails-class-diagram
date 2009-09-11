import grails.test.*

class ClassDiagramControllerTests extends ControllerUnitTestCase {

	void testDotUtility()  {
		def controller = new ClassDiagramController()
		controller.params.outputFormat = "jpg"
		controller.model()
		assertEquals "image/jpeg", controller.response.mimeType 
	}
	
}