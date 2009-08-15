/**
 * A utility that takes grails domain classes and turns them into a domain class diagram.
 *
 * @author trygve.amundsen@gmail.com
 */
class ClassDiagram {

	def config // injected
	def props = [:]	
	
	byte[] createGraphImage(domainClasses, Map params) {
		// Merge config and request params to avoid the confusion
		props = new HashMap(params)
		props.putAll(config.classDiagram)
		
		def dotBuilder = new DotBuilder(config)
		dotBuilder.digraph {
			// build default node style
			node (config?.classDiagram?.defaultNode)
			
			domainClasses.each { domainClass ->

				// build node for domain class
				"${domainClass.name}" ([label:nodeLabel(domainClass, props)])

				// build accociations
				getInterestingAssociations(domainClass).each { ass ->
				    from(domainClass.name).to(ass.referencedDomainClass?.name ?: ass.type.simpleName, getAssociationProps(ass))
				}
				
				// build inheritance 
				domainClass.subClasses.each { subClass ->
					from(domainClass.name).to(subClass.name, [arrowhead:"none", arrowtail:"onormal"])
				}				
			}
		}.createImage(props.outputFormat?:"jpg")
	}
	
	/**
	 * @return the dot properties for the given association (which is a domainClass.property)
	 */
	private getAssociationProps(ass) {
		def arrowhead = !ass.bidirectional ? "open" : "none"
		def arrowtail = ass.embedded ? "diamond" : ass.owningSide ? "odiamond" : "none" 
		def headlabel = ass.oneToMany || ass.manyToMany ? "    *"  : "    1" 
		def taillabel = !ass.bidirectional ? "" : ass.manyToOne || ass.manyToMany ? "    *"  : "    1" 
		[label:ass.name, arrowhead:arrowhead, arrowtail:arrowtail, headlabel:headlabel, taillabel:taillabel]
	}

	/**
	 * @return Node label containing class name, properties, methods, and dividers
	 */
	private String nodeLabel(domainClass, props) {
		def label = "{"+domainClass.name
		if (!props?.hideProperties) {
			label += "|" 
			label += getInterestingProperties(domainClass).collect {it.type.simpleName+" "+it.name}.join("\\l")
			label += "\\l" // get weird formatting without this one
		}
		if (!props?.hideMethods) {
			label += "|" 
			label += getDeclaredMethods(domainClass).collect { it.name+"(${formatParameters(it)})" }.join("\\l")
			label += "\\l"
		}
		label += "}"
		return label
	}
	
	private String formatParameters(method) {
		if (props.showMethodParameters) {
			method.parameterTypes.collect{it.simpleName}.join(',')		
		} else {
			""
		}
	}

	private getInterestingAssociations(domainClass) {
		domainClass.properties.findAll { prop ->
			prop.association && // All associations
			!prop.inherited && // except inherited stuff
			!(prop.bidirectional && domainClass.name > prop.referencedDomainClass.name) // bidirectionals should only be mapped once  
		}
	}

	private getInterestingProperties(domainClass) {
		domainClass.properties.findAll { prop ->
			(!["id","version"].contains(prop.name) && !prop.association && (!prop.inherited)) 
		}
	}

	/**
	 * Get methods declared in a grails domain class, and not all other inherited and meta-added stuff.
	 * No satisfactory solution found, this implementation is a big hack!
	 * GrailsDomainClass or its super GrailsClass should have a getDeclaredMethods()!
	 */
	private getDeclaredMethods(domainClass) {
		def methods = domainClass.clazz.declaredMethods
		def filterMethods = methods.findAll { it.name =~ /\$/} // remove special methods containing $ 
		filterMethods += GroovyObject.methods.flatten() // metaClass, properties etc.
		filterMethods += Object.methods.flatten() // toString 

		// filter out property-related methods
		def propertyNames = domainClass.properties*.name
		propertyNames += ["id","version", "hasMany", "belongsTo", "mappedBy", "mapping", "constraints"] 
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