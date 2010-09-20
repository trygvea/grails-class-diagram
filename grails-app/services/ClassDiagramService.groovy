import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH
import java.util.regex.Pattern

/**
 * Service that takes grails domain classes and turns them into a domain class diagram.
 *
 * @author trygve.amundsen@gmail.com
 */
class ClassDiagramService {

    static transactional = false

    byte[] createDiagram(domainClasses, prefs) {
        def dotBuilder = createDotDiagram(domainClasses, prefs)
        dotBuilder.createDiagram(prefs.outputFormat?:"png")
    }
    
    DotBuilder createDotDiagram(domainClasses, prefs) {
        def skin = CH.config.classDiagram.skins?."${prefs.skin}"
        
        domainClasses = randomizeOrder(domainClasses, prefs)
        domainClasses = classSelection(domainClasses, prefs)

        def dotBuilder = new DotBuilder()
        dotBuilder.digraph {
            buildGraphDefaults(dotBuilder, skin, prefs)
            rankdir ("${prefs.graphOrientation}")

            def embeddedClasses = domainClasses*.properties.flatten().findAll { it.embedded }.type.unique()
            def enumClasses = domainClasses*.properties.flatten().findAll { it.enum }.type.unique()

            if (!prefs.showPackages) {
                buildDomainClasses(dotBuilder, domainClasses, prefs)
                buildEmbeddedClasses(dotBuilder, embeddedClasses, prefs)
                buildEnumClasses(dotBuilder, enumClasses, prefs)

            } else { // build domain classes per package
                
                def allPackageNames = domainClasses.collect {getPackageName(it)} as Set
                if (!prefs.showEmbeddedAsProperty) {
                    // embedded classes may exist in other packages than domain classes
                    allPackageNames += embeddedClasses.collect {getPackageName(it)}.unique()
                }
                if (!prefs.showEnumAsProperty) {
                    // enum classes may exist in other packages than domain classes
                    allPackageNames += enumClasses.collect {getPackageName(it)}.unique()
                }
                
                allPackageNames = randomizeOrder(allPackageNames, prefs)

                allPackageNames.each { packageName ->
                    subgraph("cluster_"+packageName) {
                        skin.packageStyle.each {
                            "${it.key}" ("${it.value}")
                        }
                        fontsize("${prefs.fontsize}")
                        labeljust("l")
                        label ("${packageName ?: '<root>'}")

                        buildDomainClasses(dotBuilder, domainClasses.findAll{ getPackageName(it) == packageName}, prefs)
                        buildEmbeddedClasses(dotBuilder, embeddedClasses.findAll {getPackageName(it) == packageName}, prefs)
                        buildEnumClasses(dotBuilder, enumClasses.findAll {getPackageName(it) == packageName}, prefs)
                    }
                }
            }

            buildRelations(dotBuilder, domainClasses, prefs)
        }
        dotBuilder
    }
    
    private void buildGraphDefaults(dotBuilder, skin, prefs) {
        dotBuilder.graph (skin.graphStyle)
        dotBuilder.node ([shape:"record"] + [fontsize:prefs.fontsize] + skin.nodeStyle)
        dotBuilder.edge ([fontsize:prefs.fontsize] + skin.edgeStyle)
    }

    private String getPackageName(cls) {
        // Name of root package is inconsistent  
        if (cls instanceof GrailsDomainClass) {
            cls.packageName == "" ? "<root>" : cls.packageName
        } else {
            cls.package?.name ?: "<root>"
        }
    }
    
    private void buildEmbeddedClasses(dotBuilder, embeddedClasses, prefs) {
        if (!prefs.showEmbeddedAsProperty) {
            embeddedClasses.each { embeddedClass ->
                dotBuilder."${embeddedClass.simpleName}" ([label:formatNodeLabel(embeddedClass, prefs)])
            }
        }
    }

    private void buildEnumClasses(dotBuilder, enumClasses, prefs) {
        if (!prefs.showEnumAsProperty) {
            enumClasses.each { enumClass ->
                dotBuilder."${enumClass.simpleName}" ([label:formatNodeLabel(enumClass, prefs)])
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
                if (subClass.clazz.superclass == domainClass.clazz) { // GRAILSPLUGINS-1740: domainClass.subClasses also returns all sub-sub-classes! 
                    dotBuilder.from(domainClass.name).to(subClass.name, [arrowhead:cfg.arrows.none, arrowtail:cfg.arrows.inherits, dir:'both'])
                }
            }                
        }
    }
    
    /**
     * Order package names according to preferences
     */
    private Collection randomizeOrder(coll, prefs) {
        if (prefs.randomizeOrder) {
            def list = coll as List
            Collections.shuffle(list)
            return list
        } else {
            return coll
        }
    }

    /**
     * find subset of classes according to preferences
     */
    private Collection classSelection(domainClasses, prefs) {
        if (!prefs.classSelection || prefs.classSelection == "<all>") {
            return domainClasses
        }
        if (prefs.classSelectionIsRegexp) {
            def classSelectionPattern = Pattern.compile(addRegexpWildcardsWhereNeeded(prefs.classSelection))
            domainClasses.findAll { cls ->
                String fullName = cls.packageName + "." + cls.name
                fullName ==~ classSelectionPattern
            }
        } else {
            domainClasses.findAll { cls ->
                String fullName = cls.packageName + "." + cls.name
                fullName.indexOf(stripWildcardsOnEnds(prefs.classSelection)) >= 0
            }
        }
    }

    /**
     * strip preceeding and succeeding wildcards (*) from string 
     */
     String stripWildcardsOnEnds(String s) {
         if (s.startsWith('*')) {
             s = s[1..-1]
         }
         if (s.endsWith('*')) {
             s = s[0..-2]
         }
         s
     }

     /**
      * Add regexp wildcards (.*) before and after s, and before and after every '|' (regexp or).
      */
      String addRegexpWildcardsWhereNeeded(String s) {
          s.tokenize('|').collect {
              def sb = new StringBuilder()
              if (!it.startsWith('.*')) {
                  sb.append(".*")
              }
              sb.append(it)
              if (!it.endsWith('.*')) {
                  sb.append(".*")
              }
              sb.toString()
          }.join('|')
      }

    /**
     * @return the dot properties for the given association (which is a domainClass.property)
     */
    private getAssociationProps(ass, prefs) {
        def cfg = CH.config.classDiagram.associations
        def arrowhead = !ass.bidirectional ? cfg.arrows.references : (!ass.owningSide && ass.otherSide.owningSide) ? cfg.arrows.belongsTo : cfg.arrows.none
        def arrowtail = ass.embedded || ass.enum ? cfg.arrows.embedded : ass.owningSide ? cfg.arrows.belongsTo : cfg.arrows.none
        def headlabel = ass.oneToMany || ass.manyToMany ? cfg.decorators.hasMany  : cfg.decorators.hasOne
        def taillabel = !ass.bidirectional ? cfg.decorators.none : ass.manyToOne || ass.manyToMany ? cfg.decorators.hasMany  : cfg.decorators.hasOne
        def label = prefs?.showAssociationNames ? ass.bidirectional ? ass.otherSide.name + " / " + ass.name : ass.name : ""
        [label:label, arrowhead:arrowhead, arrowtail:arrowtail, headlabel:headlabel, taillabel:taillabel, dir:'both']
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
            if (cls instanceof Class && cls.enum) {
                label += getInterestingProperties(cls, prefs).collect {formatEnumProperty(it, prefs)}.join("\\l")
            } else { 
                label += getInterestingProperties(cls, prefs).collect {formatProperty(it, prefs)}.join("\\l")
            }
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

    private String formatEnumProperty(property, prefs) {
        if (property.type != property.declaringClass) {
            formatProperty(property, prefs)
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
                !(prop.name in ["id","version"]) && 
                (!prop.association || (prop.embedded && prefs.showEmbeddedAsProperty)) &&
                (!(prop.enum && !prefs.showEnumAsProperty)) &&
                (!prop.inherited)
            }
        } else if (cls.enum) {
            cls.declaredFields.findAll { field ->
                !field.name.startsWith("\$") && 
                !field.name.startsWith("__") && 
                !(field.name in ["metaClass", "MAX_VALUE", "MIN_VALUE"]) &&
                !field.name.startsWith("array\$\$") 
            }
        } else { // Assume regular java class
            cls.declaredFields.findAll { field ->
                !field.name.startsWith("\$") && 
                !field.name.startsWith("__") && 
                !(field.name in ["metaClass"])
            }
        }
    }

    private getInterestingAssociations(GrailsDomainClass domainClass, prefs) {
        domainClass.properties.findAll { prop ->
            (prop.association || prop.enum) && // All associations and enums
            !(prop.embedded && prefs.showEmbeddedAsProperty) && // except embedded if not configured so
            !(prop.enum && prefs.showEnumAsProperty) && // except enums if not configured so
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
        } else if (cls.enum) { 
            def methods = cls.declaredMethods
            methods -= methods.findAll { it.name in ["valueOf", "values", "next", "previous"]} // Removed even if overridden
            def propertyNames = cls.declaredFields*.name
            getDeclaredMethods(methods, propertyNames)
        } else { // Assume regular java class
            def methods = cls.declaredMethods
            def propertyNames = cls.declaredFields*.name
            getDeclaredMethods(methods, propertyNames)
        }
    }
    
    /**
     * Get methods declared in a class, filtering out all inherited and meta-added stuff.
     * Quite a few assumptions are made, no satisfactory solution found. Hack!
     * The Class.getDeclaredMethods() also includes decorated methods, which makes it essentially useless. 
     * I think we need a grails getUndecoratedDeclaredMethods() that gives us what we really coded in the class, if that is possible. 
     */
    private getDeclaredMethods(methods, propertyNames) {
        def filterMethods = methods.findAll { it.name =~ /\$/} // remove special methods containing $ 
        filterMethods += GroovyObject.methods.flatten() // remove metaClass, properties etc.
        filterMethods += Object.methods.flatten() // remove toString 

        // filter out property-related methods
        methods.each { method ->
            ["get","is","set","addTo","removeFrom"].each { prefix ->
                propertyNames.each{ propertyName ->
                    if (method.name == prefix+initCap(propertyName)) {
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
    
    // There may be a String method for this, but I didnt find it :) 
    private initCap(String s) {
        s ? s[0].toUpperCase() + (s.size() > 1 ? s[1..-1] : '') : s
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