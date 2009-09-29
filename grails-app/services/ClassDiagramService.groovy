import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH

/**
 * Service that takes grails domain classes and turns them into a domain class diagram.
 *
 * @author trygve.amundsen@gmail.com
 */
class ClassDiagramService {

    static transactional = false

    byte[] createDiagram(domainClasses, prefs) {
        def skin = CH.config.classDiagram.skins?."${prefs.skin}"
        
        domainClasses = orderDomain(domainClasses, prefs)

        def dotBuilder = new DotBuilder()
        dotBuilder.digraph {
            // build default node and edge styles
            graph (skin.graphStyle)
            node ([shape:"record"] + [fontsize:prefs.fontsize] + skin.nodeStyle)
            edge ([fontsize:prefs.fontsize] + skin.edgeStyle)
            rankdir ("${prefs.graphOrientation}");

            def embeddedClasses = domainClasses*.properties.flatten().findAll { it.embedded }.type.unique()
            if (!prefs.showPackages) {
                buildDomainClasses(dotBuilder, domainClasses, prefs)
                buildEmbeddedClasses(dotBuilder, embeddedClasses, prefs)
            } else {
                // build domain classes per package
                def allPackageNames = domainClasses.collect {it.packageName}.unique()

                // Find embedded classes in other packages than domain classes
                if (!prefs.showEmbeddedAsProperty) {
                     def embeddedClassPackages = embeddedClasses.collect {it.package?.name}.unique()
                    allPackageNames += embeddedClassPackages
                    allPackageNames.unique()
                }
                
                allPackageNames = orderPackageNames(allPackageNames, prefs)

                allPackageNames.each { packageName ->
                    subgraph("cluster_"+packageName) {
                        skin.packageStyle.each {
                            "${it.key}" ("${it.value}")
                        }
                        fontsize("${prefs.fontsize}")
                        labeljust("l")
                        label ("${packageName ?: '<root>'}")

                        buildDomainClasses(dotBuilder, domainClasses.findAll{ it.packageName == packageName}, prefs)
                        buildEmbeddedClasses(dotBuilder, embeddedClasses.findAll {it.package?.name == packageName}, prefs)
                    }
                }
            }

            buildRelations(dotBuilder, domainClasses, prefs)

        }
        dotBuilder.createDiagram(prefs.outputFormat?:"png")
    }

    private void buildEmbeddedClasses(dotBuilder, embeddedClasses, prefs) {
        if (!prefs.showEmbeddedAsProperty) {
            embeddedClasses.each { embeddedClass ->
                dotBuilder."${embeddedClass.simpleName}" ([label:formatNodeLabel(embeddedClass, prefs)])
            }
        }
    }

    private void buildDomainClasses(dotBuilder, domainClasses, prefs) {
        domainClasses.each { domainClass ->
            // build node for domain class
            dotBuilder."${domainClass.name}" ([label:formatNodeLabel(domainClass, prefs)])
        }
    }
    
    private void buildRelations(dotBuilder, domainClasses, prefs) {
        def cfg = CH.config.classDiagram.associations

        domainClasses.each { domainClass ->
            // build associations
            getInterestingAssociations(domainClass, prefs).each { ass ->
                dotBuilder.from(domainClass.name).to(ass.referencedDomainClass?.name ?: ass.type.simpleName, getAssociationProps(ass, prefs))
            }
            // build inheritance 
            domainClass.subClasses.each { subClass ->
                dotBuilder.from(domainClass.name).to(subClass.name, [arrowhead:cfg.arrows.none, arrowtail:cfg.arrows.inherits])
            }                
        }
    }
    
    /**
     * Order the domain classes according to preferneces
     */
    private List orderDomain(domainClasses, prefs) {
        if (prefs.randomizeOrder) {
            domainClasses = domainClasses as List
            Collections.shuffle(domainClasses)
        }
        domainClasses
    }
    
    /**
     * Order package names according to preferneces
     */
    private List orderPackageNames(packageNames, prefs) {
        if (prefs.randomizeOrder) {
            packageNames = packageNames as List
            Collections.shuffle(packageNames)
        }
        packageNames
    }
    
    /**
     * @return the dot properties for the given association (which is a domainClass.property)
     */
    private getAssociationProps(ass, prefs) {
        def cfg = CH.config.classDiagram.associations
        def arrowhead = !ass.bidirectional ? cfg.arrows.references : cfg.arrows.none
        def arrowtail = ass.embedded ? cfg.arrows.embedded : ass.owningSide ? cfg.arrows.belongsTo : cfg.arrows.none
        def headlabel = ass.oneToMany || ass.manyToMany ? cfg.decorators.hasMany  : cfg.decorators.hasOne
        def taillabel = !ass.bidirectional ? cfg.decorators.none : ass.manyToOne || ass.manyToMany ? cfg.decorators.hasMany  : cfg.decorators.hasOne
        def label = prefs?.showAssociationNames ? ass.name : ""
        [label:label, arrowhead:arrowhead, arrowtail:arrowtail, headlabel:headlabel, taillabel:taillabel]
    }

    /**
     * @return Node label containing class name, properties, methods, and dividers
     */
    private String formatNodeLabel(cls, prefs) {
        return (prefs.verticalOrientation ? "{" : "") +
                (cls instanceof GrailsDomainClass ? cls.name : cls.simpleName) +
                 formatProperties(cls, prefs) +
                 formatMethods(cls, prefs) +
                 (prefs.verticalOrientation ? "}" : "")
    }

    private String formatProperties(cls, prefs) {
        if (prefs?.showProperties) {
            def label = "|" 
            label += getInterestingProperties(cls, prefs).collect {formatProperty(it, prefs)}.join("\\l")
            label += "\\l" // get weird formatting without this one
            return label
        } else {
            ""
        }
    }
    
    private String formatProperty(property, prefs) {
        if (prefs.showPropertyType) {
            property.type.simpleName+" "+property.name
        } else {
            property.name
        }
    }

    private String formatMethods(cls, prefs) {
        if (prefs?.showMethods) {
            def label = "|" 
            label += getInterestingMethods(cls, prefs).collect {formatMethod(it, prefs)}.join("\\l")
            label += "\\l"
            return label
        } else {
            ""
        }
    }
    
    private String formatMethod(method, prefs) {
        def returnType = prefs.showMethodReturnType ? method.returnType.simpleName + " " : ""
        def methodSignature = prefs.showMethodSignature ? method.parameterTypes.collect{it.simpleName}.join(',') : ""
        returnType + method.name+"(${methodSignature})"
    }

    private getInterestingProperties(cls, prefs) {
        if (cls instanceof GrailsDomainClass) {
            cls.properties.findAll { prop ->
                !["id","version"].contains(prop.name) && 
                (!prop.association || (prop.embedded && prefs.showEmbeddedAsProperty)) &&
                (!prop.inherited)
            }
        } else {
            cls.declaredFields.findAll { field ->
                !field.name.startsWith("\$") && 
                !field.name.startsWith("__") && 
                !["metaClass"].contains(field.name)
            }
        }
    }

    private getInterestingAssociations(GrailsDomainClass domainClass, prefs) {
        domainClass.properties.findAll { prop ->
            prop.association && // All associations
            !(prop.embedded && prefs.showEmbeddedAsProperty) && // except embedded if not configured so
            !prop.inherited && // except inherited stuff
            !(prop.bidirectional && domainClass.name > prop.referencedDomainClass.name) // bidirectionals should only be mapped once  
        }
    }

    /**
     * Get methods declared in a class, filtering out all inherited and meta-added stuff.
     * Quite a few assumptions are made.
     * GrailsDomainClass or its super GrailsClass should have a getDeclaredMethods()!
     */
    private getInterestingMethods(cls, prefs) {
        if (cls instanceof GrailsDomainClass) {
            def methods = cls.clazz.declaredMethods
            def propertyNames = cls.properties*.name
            propertyNames += ["id","version", "hasMany", "belongsTo", "mappedBy", "mapping", "constraints", "embedded"] 
            getDeclaredMethods(methods, propertyNames)
        } else { // Assume regular java class 
            def methods = cls.declaredMethods
            def propertyNames = cls.declaredFields*.name
            getDeclaredMethods(methods, propertyNames)
        }
    }
    
    /**
     * Get methods declared in a class, filtering out all inherited and meta-added stuff.
     * Quite a few assumptions are made, no satisfactiory solution found. Hack!
     * The getDeclaredMethods() also includes decorated methods, which makes it essentialy useless. 
     * I think we need a new getUndecoratedDeclaredMethods() that gives us what we really coded in the class. 
     */
    private getDeclaredMethods(methods, propertyNames) {
        def filterMethods = methods.findAll { it.name =~ /\$/} // remove special methods containing $ 
        filterMethods += GroovyObject.methods.flatten() // remove metaClass, properties etc.
        filterMethods += Object.methods.flatten() // remove toString 

        // filter out property-related methods
        methods.each { method ->
            ["get","is","set","addTo","removeFrom"].each { prefix ->
                propertyNames.each{ propertyName ->
                    if (method.name == prefix+propertyName[0].toUpperCase()+propertyName[1..-1]) {
                        // TODO: Filter by signature, not just name 
                        filterMethods += method
                    }
                }
            }
        }

        // Apply filter
        filterMethods.each {filterMethod ->
            methods -= methods.find { hasSameSignature(it, filterMethod)}
        }

        return methods
    }
    

    /**
     * @returns true if the methods has the same signature, without looking at the class name. 
     */
    private boolean hasSameSignature(method1, method2)  {
        if (method1?.name != method2?.name) {
            return false
        }
        if (method1.parameterTypes != method2.parameterTypes) {
            return false
        }
        return method1.returnType == method2.returnType
    }        
    
}