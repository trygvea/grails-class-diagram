//
// This script is executed by Grails after plugin was installed to project.
// This script is a Gant script so you can use all special variables provided
// by Gant (such as 'baseDir' which points on project base dir). You can
// use 'ant' to access a global instance of AntBuilder
//
// For example you can create directory under project tree:
//
//    ant.mkdir(dir:"${basedir}/grails-app/jobs")
//
try { 
	"dot -V".execute().text
} catch (IOException ex){
	// TODO this exception doesnt make install fail
	throw new RuntimeException("Graphviz not on path. Install from http://graphviz.org/download", ex) 
}
