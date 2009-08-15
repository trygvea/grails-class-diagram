class ClassDiagramGrailsPlugin {
    // the plugin version
    def version = "0.2"
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
    def documentation = "http://grails.org/ClassDiagram+Plugin"

    def doWithSpring = {
		// Load default properties from ClassDiagramConfig
		GroovyClassLoader classLoader = new GroovyClassLoader(getClass().getClassLoader())
		ConfigObject classDiagramConfig = new ConfigSlurper().parse(classLoader.loadClass('ClassDiagramConfig'))

		// Allow plugin user to change properties in Config.groovy 
		classDiagramConfig.merge(application.config)

		classDiagram(ClassDiagram) {
			config = classDiagramConfig
	  	}
    }

}
