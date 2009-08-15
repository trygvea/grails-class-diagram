import groovy.util.BuilderSupport

/**
 * A simple builder for creating graphviz dot files.
 * Thanks to Merlyn Albery-Speyer's blogs on the subject. The DotBuilder is his brainwork:
 * 	 http://curious-attempt-bunny.blogspot.com/2008/06/graphviz-dabbling-with-groovy-builders.html
 */
class DotBuilder extends BuilderSupport {
  private def config 
  private def out
  private def outTarget
  private def from

  DotBuilder(config) {
    outTarget = new StringWriter()
    out = new PrintWriter(outTarget)
	this.config = config
  }
  
  protected void setParent(Object parent, Object child) {}
    
  protected Object createNode(Object name) {
    if (name == 'digraph') {
		return this
	} else {
      from = name
	  return this
	}
  }
    
  protected Object createNode(Object name, Object value) {
    if (name == 'from') {
      from = value
	  return this
    } else {
      out.println """$name="$value";"""
    }
  }

  protected Object createNode(Object name, Map attributes) {
	if (name =~ /\s/) { // contains whitespace
		name = "\"$name\""
	}
 	out.println """$name [${attributes.collect { "$it.key=\"$it.value\"" }.join(", ")}];"""
  }
      
  protected Object createNode(Object name, Map attributes, Object value) {
	if (name == 'to') {
	  createAssociation(from, value, attributes)
      from = value // lets us chain them
	} else {
	  createAssociation(name, value, attributes)
	}
  }
    
  protected void nodeCompleted(Object parent, Object node) {  }

  def to(Object name, Map attributes) {
	createAssociation(from, name, attributes)
    from = name // chain to's
	return this
  }  
 
  private createAssociation(from, to, attributes) {
    out.println """"$from" -> "$to" [${attributes.collect { "$it.key=\"$it.value\"" }.join(", ")}];"""
  }

  def to(Object name) {
    out.println """"$from" -> "$name";"""
    from = name // chain to's
	return this
  }  

  def createImage(outputFormat) {
	outTarget.flush()
	def graph = "digraph G { $outTarget }"
	println "Graphviz dot:\n"+graph
	def p
	try {
	  def dotExe = config?.graphviz?.dot?.executable
	  p = (dotExe+" -T"+outputFormat).execute()
	} catch (IOException ex) {
	  throw new RuntimeException("Graphviz dot utility must be installed and on path, or path set in graphviz.dot.executable. Download from http://graphviz.org ",ex)
	} 

    p.outputStream.withStream { stream ->
      stream << graph
    }
//    p.waitFor() // drop this for jpg (at least on mac)
	def imageBuffer = new ByteArrayOutputStream()
	imageBuffer << p.inputStream
	byte[] image = imageBuffer.toByteArray()
	return image
  }

}