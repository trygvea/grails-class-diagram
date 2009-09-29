import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH

/**
 * Service that takes grails domain classes and turns them into a domain class diagram.
 *
 * @author trygve.amundsen@gmail.com
 */
class ClassDiagramService {

	static transactional = false

	def props = [:]	// Bad with state in service, but we're single user  
	
	byte[] createGraphImage(domainClasses, prefs) {
		// Merge config and request params to avoid the confusion
		props = new HashMap(prefs.properties)
		props.putAll(CH.flatConfig.classDiagram)

		def skin = CH.config.classDiagram.skins?."${prefs.skin}"
		def cfg = CH.config.classDiagram.associations
		
		if (prefs.randomizeOrder) {
			domainClasses = domainClasses as List
			Collections.shuffle(domainClasses)
		}
		
		def dotBuilder = new DotBuilder()
		dotBuilder.digraph {
			// build default node and edge styles
			graph (skin.graphStyle)
			node ([shape:"record"] + [fontsize:props.fontsize] + skin.nodeStyle)
			edge ([fontsize:props.fontsize] + skin.edgeStyle)
			text ("rankdir=${props.graphOrientation};");
			

			// build node for any embedded classes. Note: These are not of type GrailsDomainClass
			if (!prefs.showEmbeddedAsProperty) {
	 			def embeddedClasses = domainClasses*.properties.flatten().findAll { it.embedded }.type.unique()
				embeddedClasses.each { embeddedClass ->
					// build node for domain class
					"${embeddedClass.simpleName}" ([label:formatNodeLabel(embeddedClass)])
				}
			}

			// build nodes for domain classes
			domainClasses.each { domainClass ->
				// build node for domain class
				"${domainClass.name}" ([label:formatNodeLabel(domainClass)])
				// build accociations
				getInterestingAssociations(domainClass).each { ass ->
				    from(domainClass.name).to(ass.referencedDomainClass?.name ?: ass.type.simpleName, getAssociationProps(ass))
				}
				// build inheritance 
				domainClass.subClasses.each { subClass ->
					from(domainClass.name).to(subClass.name, [arrowhead:cfg.arrows.none, arrowtail:cfg.arrows.inherits])
				}				
			}
		}.createImage(props.outputFormat?:"jpg")
	}
	
	/**
	 * @return the dot properties for the given association (which is a domainClass.property)
	 */
	private getAssociationProps(ass) {
		def cfg = CH.config.classDiagram.associations
		def arrowhead = !ass.bidirectional ? cfg.arrows.references : cfg.arrows.none
		def arrowtail = ass.embedded ? cfg.arrows.embedded : ass.owningSide ? cfg.arrows.belongsTo : cfg.arrows.none
		def headlabel = ass.oneToMany || ass.manyToMany ? cfg.decorators.hasMany  : cfg.decorators.hasOne
		def taillabel = !ass.bidirectional ? cfg.decorators.none : ass.manyToOne || ass.manyToMany ? cfg.decorators.hasMany  : cfg.decorators.hasOne
		def label = props?.showAssociationNames ? ass.name : ""
		[label:label, arrowhead:arrowhead, arrowtail:arrowtail, headlabel:headlabel, taillabel:taillabel]
	}

	/**
	 * @return Node label containing class name, properties, methods, and dividers
	 */
	private String formatNodeLabel(cls) {
		return (props.verticalOrientation ? "{" : "") +
				(cls instanceof GrailsDomainClass ? cls.name : cls.simpleName) +
		 		formatProperties(cls) +
		 		formatMethods(cls) +
		 		(props.verticalOrientation ? "}" : "")
	}

	private String formatProperties(cls) {
		if (props?.showProperties) {
			def label = "|" 
			label += getInterestingProperties(cls).collect {formatProperty(it)}.join("\\l")
			label += "\\l" // get weird formatting without this one
			return label
		} else {
			""
		}
	}
	
	private String formatProperty(property) {
		if (props.showPropertyType) {
			property.type.simpleName+" "+property.name
		} else {
			property.name
		}
	}

	private String formatMethods(cls) {
		if (props?.showMethods) {
			def label = "|" 
			label += getInterestingMethods(cls).collect {formatMethod(it)}.join("\\l")
			label += "\\l"
			return label
		} else {
			""
		}
	}
	
	private String formatMethod(method) {
		def returnType = props.showMethodReturnType ? method.returnType.simpleName + " " : ""
		def methodSignature = props.showMethodSignature ? method.parameterTypes.collect{it.simpleName}.join(',') : ""
		returnType + method.name+"(${methodSignature})"
	}

	private getInterestingProperties(cls) {
		if (cls instanceof GrailsDomainClass) {
			cls.properties.findAll { prop ->
				!["id","version"].contains(prop.name) && 
				(!prop.association || (prop.embedded && props.showEmbeddedAsProperty)) &&
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

	private getInterestingAssociations(GrailsDomainClass domainClass) {
		domainClass.properties.findAll { prop ->
			prop.association && // All associations
			!(prop.embedded && props.showEmbeddedAsProperty) && // except embedded if not configured so
			!prop.inherited && // except inherited stuff
			!(prop.bidirectional && domainClass.name > prop.referencedDomainClass.name) // bidirectionals should only be mapped once  
		}
	}

	/**
	 * Get methods declared in a class, filtering out all inherited and meta-added stuff.
	 * Quite a few assumptions are made.
	 * GrailsDomainClass or its super GrailsClass should have a getDeclaredMethods()!
	 */
	private getInterestingMethods(cls) {
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