import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH
class ClassDiagramGrailsPlugin {
    // the plugin version
    def version = "0.5.2"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.4 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/*.gsp",
            "grails-app/domain/**"
    ]

    def author = "Trygve Amundsen"
    def authorEmail = "trygve.amundsen at gmail.com"
    def title = "Creates a class diagram from grails domain classes"
    def description = '''\\
Provides a class diagram of all the domain classes with their properties, methods, 
associations and inheritance to other domain classes. 
The excellent utility graphviz (http://www.graphviz.org/) is used for diagram layout.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/class-diagram"

    def doWithSpring = { 
        // Merge config early. Note that it's too late in doWithApplicationContext because grails artifacts are already loaded  
        GroovyClassLoader classLoader = new GroovyClassLoader(getClass().getClassLoader())
        ConfigObject classDiagramConfig = new ConfigSlurper().parse(classLoader.loadClass('ClassDiagramConfig'))
        classDiagramConfig.merge(application.config)
        application.config.merge(classDiagramConfig)

        // this one may be added in groovy 1.7 (see GROOVY-644)
        Map.metaClass.minus = { keys ->
            delegate.findAll {!keys.contains(it.key)}
        }        
    }
    
    def doWithApplicationContext = { ctx ->
    }

}
