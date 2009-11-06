import groovy.util.BuilderSupport
import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH
import javax.imageio.ImageIO
import org.apache.log4j.Logger

/**
 * A simple builder for creating graphviz dot files. 
 * Note: This is *not* a full-featured builder with full dot language support.
 * Its technical depth is growing, and maybe a full rewrite is nearby. On the other hand, a
 * dotBuilder with complete dot language support will probably be a nasty beast :)   
 *
 * Thanks to Merlyn Albery-Speyer's blogs on the subject:
 *      http://curious-attempt-bunny.blogspot.com/2008/06/graphviz-dabbling-with-groovy-builders.html
 * The DotBuilder started as a copy of his work, and then some more features has been added. 
 */
class DotBuilder extends BuilderSupport {
    Logger log = Logger.getLogger(getClass())
    
    private def out
    private def outTarget
    private def from
    private boolean directional

    DotBuilder() {
        outTarget = new StringWriter()
        out = new PrintWriter(outTarget)
    }
  
    protected void setParent(Object parent, Object child) {
    }

    protected Object createNode(Object name) {
        if (name in ['graph', 'digraph']) {
            directional = (name == 'digraph')
            out.println "$name {"
            return name
        } else {
            out.println "${formatName(name)};"
            return name
        }
    }
    
    protected Object createNode(Object name, Object value) {
        if (name == 'from') {
            from = value
            return this
        } else if (name == 'subgraph') {
            out.println "$name \"$value\" {"
            return name
        } else if (name == 'text') {
            out.println value
            return name
        } else {
            out.println "${formatName(name)}=\"$value\";"
            return name
        }
    }

    protected Object createNode(Object name, Map attributes) {
        out.println "${formatName(name)} ${formatAttributes(attributes)};"
    }
      
    protected Object createNode(Object name, Map attributes, Object value) {
        if (name == 'to') {
            createAssociation(from, value, attributes)
            from = value // lets us chain them
        } else {
            createAssociation(name, value, attributes)
        }
    }

    protected void nodeCompleted(Object parent, Object node) {
        if (node in ['graph', 'digraph']) {
            out.println('}')
            outTarget.flush()
        } else if (node in ['subgraph']) {
            out.println('}')
        }
    }

    def to(Object name, Map attributes) {
        createAssociation(from, name, attributes)
        from = name // chain to's
        return this
    }  

    def to(Object name) {
        createAssociation(from, name)
        from = name // chain to's
        return this
    }  

    String getDotString() {
        outTarget
    }
    
    byte[] createDiagram(String outputFormat) {
        String dot = dotString
        log.debug "Graphviz dot:\n"+dot  
        def p
        try {
            def dotExe = CH.config.graphviz.dot.executable
            p = (dotExe+" -T"+outputFormat).execute()
        } catch (IOException ex) {
            throw new RuntimeException("Graphviz dot utility must be installed and on path, or path set in graphviz.dot.executable. Download from http://graphviz.org ",ex)
        } 

        p.outputStream.withStream { stream ->
            stream << dot
        }

        //    p.waitFor() // seems to hang on certain formats/os combinations (such as jpg/mac)
        def buf = new ByteArrayOutputStream()
        buf << p.inputStream
        buf.toByteArray()
    }

    private String formatName(name) {
        if (name =~ /\s/) { // contains whitespace
            "\"$name\""
        } else {
            name
        }
    }

    private String formatAttributes(attributes) {
        """[${attributes.collect { "$it.key=\"$it.value\"" }.join(", ")}]"""
    }

    private createAssociation(from, to) {
        createAssociation(from, to, null)
    }

    private createAssociation(from, to, attributes) {
        out.print "${formatName(from)} ${connect()} ${formatName(to)}"
        out.println attributes ? " ${formatAttributes(attributes)};" : ";"
    }
    
    private String connect() {
        directional ? "->" : "-"
    }



}