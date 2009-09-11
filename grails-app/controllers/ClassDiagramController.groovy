import java.awt.Image
import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH

/**
 * Create a class diagram from grails domain model.
 */
class ClassDiagramController {

	def classDiagramService
	def classDiagramLegendService

	def index = { 
		redirect action:'show'
	}

	def show = { ClassDiagramPreferences prefs ->
		bindData(prefs, params)
		def skins = [:] // Make skin as map<String name, String Description> doe easier   
		CH.config.classDiagram.skins.each {
			skins.put(it.key, it.value.name)
		}
		[prefs:prefs, skins:skins]
	}

	// ajax request for image template
	def image = { ClassDiagramPreferences prefs ->
		prefs.random = new Random().nextInt() // Need a change to force reload of img tag
		render template:'image', model:[prefs:prefs.declaredProperties], plugin:'classDiagram'
	}

	// request for image
	def model = { ClassDiagramPreferences prefs ->
		def image = classDiagramService.createGraphImage(grailsApplication.domainClasses, prefs)
		if (image.length > 0) {
		    response.contentLength = image.length
		    response.contentType = mimeTypeFor(prefs.outputFormat?:"jpg")
		    response.outputStream << image
		} else {
			// some error occured during image generation
			render "Something went wrong during class diagram generation" // TODO: doesnt work. I guess browser expects an image!
		}
	}	

	def legend = { ClassDiagramPreferences prefs ->
		def image = classDiagramLegendService.createLegendImage()
		if (image.length > 0) {
		    response.contentLength = image.length
		    response.contentType = mimeTypeFor(prefs.outputFormat?:"jpg")
		    response.outputStream << image
		} else {
			// some error occured during image generation
			render "Something went wrong during class diagram generation" // TODO: doesnt work. I guess browser expects an image!
		}
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

/**
 * Preferences for class diagram. Used as a Command Object for this controller.
 */
class ClassDiagramPreferences {
	String outputFormat = "jpg"

	boolean showProperties = true
	boolean showMethods = true
	boolean showAssociationNames = true
	boolean showMethodReturnType = true
	boolean showMethodSignature = false
	boolean showPropertyType = true
	boolean showEmbeddedAsProperty = false

	def classNamesToShow = []

	String skin = "regular"
	int fontsize = 10
	
	boolean autoUpdate = true
	boolean randomizeOrder = false
	int random 
	
	def getDeclaredProperties() {
		properties - ["declaredProperties", "class", "metaClass", "errors"] // Note: uses Map.minus added in plugin
	}
}