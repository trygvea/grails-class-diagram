import java.awt.Image

/**
 * Create a class diagram from grails domain model.
 */
class ClassDiagramController {

	def classDiagram = new ClassDiagram()

	def index = {
		redirect action:model, params:[outputFormat:"jpg"]
	}

	
	def model = {
//		def image = createTestGraphImage(outputFormat)
		def image = classDiagram.createGraphImage(grailsApplication.domainClasses, params)
		
	    response.contentLength = image.length
	    response.contentType = mimeTypeFor(params.outputFormat?:"jpg")
	    response.outputStream << image
	}	

	private String mimeTypeFor(fileFormat) {
		// TODO many weird libs have some ContentType class that does this, but we should instead be able to tap into grails mime types!
		// See for instance http://www.rgagnon.com/javadetails/java-0487.html
		def mimeType = [
			"jpg":"image/jpeg",
			"svg":"image/svg+xml",
		]
		mimeType[fileFormat] ?: "image/"+fileFormat // Take a chance
	}



	/*	private debugMethods(domainClass) {
							println "0"+domainClass.metaClass.theClass.declaredMethods*.name.minus{it =~ /\$/}.sort()
							println "1"+domainClass.metaClass.methods*.name.sort()
							println "2"+domainClass.metaClass.metaMethods*.name.sort()
							println "3"+ inheritedMethods(domainClass).sort()
							println "4"+ domainClass.clazz.superclass.methods*.name.sort() // remove supers methods
							println "5"+ GroovyObject.methods*.name.sort()
							println "6"+ domainClass.clazz.methods*.name.sort()
							println "7"+ domainClass.clazz.declaredMethods*.name.sort()
		}
	*/
}