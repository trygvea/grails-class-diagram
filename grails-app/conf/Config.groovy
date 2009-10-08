log4j = {
    appenders {
        console name:'stdout', layout:pattern(conversionPattern: '%c{2}: %m%n')
    }
    root {
        info 'stdout'
        additivity = true
    }

    error   'org.codehaus.groovy.grails.commons',       //Core artefact information such as class loading etc.
            'org.codehaus.groovy.grails.web.mapping',   // URL mapping debugging
            'org.codehaus.groovy.grails.web.servlet',   // controllers
            'org.codehaus.groovy.grails.web.pages',     // GSP
            'org.codehaus.groovy.grails.plugins',       // Log plugin activity
            'org.springframework',                      // See what Spring is doing
            'org.hibernate'                             // See what Hibernate is doing
            
    warn    'org.mortbay.log' 
    
    debug    'DotBuilder' // root

}
