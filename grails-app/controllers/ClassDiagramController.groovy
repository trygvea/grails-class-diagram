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
        
        def skins = [:] // skin as [name:description] makes client easier
        CH.config.classDiagram.skins.each {
            skins.put(it.key, it.value.name)
        }
        def graphOrientations = ["TB":"Top to Bottom", "LR": "Left to Right", "BT": "Bottom to Top", "RL": "Right to Left"] 
        [prefs:prefs, skins:skins, graphOrientations: graphOrientations]
    }

    // ajax request for image template
    def image = { ClassDiagramPreferences prefs ->
        if (prefs.outputFormat == 'pdf') {
            redirect action:model
        } else {
            prefs.random = new Random().nextInt() // Need a change to force reload of img tag
            render template:'image', model:[prefs:prefs.declaredProperties], plugin:'classDiagram'
        }
    }

    // request for image
    def model = { ClassDiagramPreferences prefs ->
        def image = classDiagramService.createDiagram(grailsApplication.domainClasses, prefs)
        if (image.length > 0) {
            response.contentLength = image.length
            response.contentType = mimeTypeFor(prefs.outputFormat?:"png")
            response.outputStream << image
        } else {
            render "Something went wrong during class diagram generation. Check the output format!" 
        }
    }    

    def legend = { ClassDiagramPreferences prefs ->
        def image = classDiagramLegendService.createLegend()
        if (image.length > 0) {
            response.contentLength = image.length
            response.contentType = mimeTypeFor("png")
            response.outputStream << image
        } else {
            // some error occured during image generation
            render "Something went wrong during legend generation"
        }
    }
    
    private String mimeTypeFor(fileFormat) {
        // TODO many weird libs have some ContentType class that does this, but we should instead be able to tap into grails mime types!
        // See for instance http://www.rgagnon.com/javadetails/java-0487.html
        def mimeType = [
            "jpg":"image/jpeg",
            "svg":"image/svg+xml",
            "pdf":"application/pdf",
            "eps":"application/postscript",
        ]
        mimeType[fileFormat] ?: "image/"+fileFormat // Take a chance
    }

    /*    private debugMethods(domainClass) {
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
    static def defaults = CH.config.classDiagram.preferences.defaults 
    
    String outputFormat = defaults.outputFormat

    boolean showProperties = defaults.showProperties
    boolean showMethods = defaults.showMethods
    boolean showAssociationNames = defaults.showAssociationNames
    boolean showMethodReturnType = defaults.showMethodReturnType
    boolean showMethodSignature = defaults.showMethodSignature
    boolean showPropertyType = defaults.showPropertyType
    boolean showEmbeddedAsProperty = defaults.showEmbeddedAsProperty
    boolean showPackages = defaults.showPackages

    def classNamesToShow = []

    String skin = CH.config.classDiagram.preferences.defaults.skin
    
    int fontsize = 10 //getSkinProperty("node","fontsize", CH.config.classDiagram.preferences.defaults.fontsize)
    
    boolean autoUpdate = defaults.autoUpdate
    String graphOrientation = defaults.graphOrientation

    boolean randomizeOrder = false
    int random 
    
    // Find properties on skin 
    def getSkinProperty(skinPart, propertyName, defaultValue) {
        println "######"+defaultValue
        println "######"+CH.config.classDiagram.skins."${skin}"."${skinPart}Style"["${propertyName}"]
        println "######"+CH.config.classDiagram.skins."${skin}"."graphStyle"["${propertyName}"]
        CH.config.classDiagram.skins."${skin}"."${skinPart}Style"["${propertyName}"] ?:
        CH.config.classDiagram.skins."${skin}"."graphStyle"["${propertyName}"] ?:
        defaultValue
    }
    
    def getDeclaredProperties() {
        properties - ["declaredProperties", "class", "metaClass", "errors"] // Note: uses Map.minus added in plugin
    }
    
    // Groovy bug? If we define method as isVertical..., it will not be part of this class's properties collection
    def getVerticalOrientation() { 
        graphOrientation in ["TB","BT"]
    }
}