class ClassDiagramGrailsPlugin {
    // the plugin version
    def version = "0.3"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.1.1 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/*.gsp",
            "grails-app/domain/**"
    ]

    // TODO Fill in these fields
    def author = "Trygve Amundsen"
    def authorEmail = ""
    def title = "Creates class diagram of Grails domain model"
    def description = '''\\
Provides a class diagram of all the domain classes with their properties, methods, 
associations and inheritance to other domain classes. 
The excellent utility graphviz (http://www.graphviz.org/) is used for diagram layout.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/class-diagram"

    def doWithApplicationContext = { ctx ->
		// this one may be added in groovy 1.7 (see GROOVY-644)
		Map.metaClass.minus = { keys ->
		    delegate.findAll {!keys.contains(it.key)}
		}		
		// Just a convenience method. Note that ConfigObject is a map of maps unless it is flattened
		ConfigObject.metaClass.mergeNoReplace = { other ->
			other.merge(delegate)
			delegate.merge(other)
		}

		// Load default properties from ClassDiagramConfig
		GroovyClassLoader classLoader = new GroovyClassLoader(getClass().getClassLoader())
		ConfigObject classDiagramConfig = new ConfigSlurper().parse(classLoader.loadClass('ClassDiagramConfig'))

		// Allow plugin user to change properties in Config.groovy 
		application.config.mergeNoReplace(classDiagramConfig)
		
    }

}
