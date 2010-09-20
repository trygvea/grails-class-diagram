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
	println "WARNING: Graphviz dot utility is not on path. The class-diagram plugin will NOT work without it."
	println "         Install from http://graphviz.org/download or set full path to dot utility in property graphviz.dot.executable."
}
