The ClassDiagram plugin provides an instant UML-like diagram of your grails domain classes.


Installation
-------------------------------------------------------------------------------
 *	Install the plugin:  grails install-pluging grails-class-diagram [-global]
	Use the -global flag if you want the class diagram to be available for all 
	your grails projects. 
 *	Install graphviz (http://www.graphviz.org/Download.php) and put its bin 
	folder on your path. (Or, alternatively, set the path to the graphviz dot 
	utility in graphviz.dot.executable property, see changing properties below).
	Graphviz is an excellent open source utility for visualizing graphs that 
	should be part of any programmers toolkit :) 


Usage
-------------------------------------------------------------------------------
Browse to the http://localhost:8080/yourApp/classDiagram, and you will hopefully 
see the class diagram. A preferences page is available, see screenshots for details.

You may add or change properties (such as colors and shapes) in your projects 
conf/Config.groovy. 

You can also change properties in the plugins conf/ClassDiagramConfig.groovy. 
If the plugin is installed globally you will find it exploded under something like
~/.grails/1.1.1/global-plugins/grails-class-diagram-0.1/conf/ClassDiagramConfig.groovy.


Credits
-------------------------------------------------------------------------------
Thanks to Merlyn Albery-Speyer's blogs on the subject. The DotBuilder is his brainwork:
	http://curious-attempt-bunny.blogspot.com/2008/06/graphviz-dabbling-with-groovy-builders.html
Also thanks to Frank Schoep for his blog on UML diagrams using graphviz:
	http://www.ffnn.nl/pages/articles/media/uml-diagrams-using-graphviz-dot.php

Release notes
-------------------------------------------------------------------------------
0.1 Initial release
0.2 Bugfix. Removed domain classes in sub-packages when packaging plugin.  
0.3 Brush-up. Added support for embedded objects, provided a GUI for changing
	properties, re-ordering


Backlog
-------------------------------------------------------------------------------

0.4 release notes
* Added property for graph orientation: top to bottom or left to right
* Graphviz 2.24: translates bgcolor:none to black, while prior versions translate to white!
* (cleaned up CSS a bit)
* Properties page not initially visible


Issues
* IE7: Formatting suck. But dont care - who use IE7?
* DRY on plugin home URL (in ClassDiagramPlugin and in menubar)
 


0.4 backlog
* test windows
* legend should not be loaded before required
* write one unit test!
* GUI spinner should be bigger & positioned over the graph, and run all the time!


Unassigned
 * Bad error reporting when user selects unrecognized output format
 * Write some tests. Get rid of domain classes. Add dynamically in test.
 * user should  be able to select domain class subset
 * Enum support
 * Package support
 	* Provide graphic support for pckages (See pavkages as sub-graphs)
 	* Filter domain classes per package ?
 	* include package prefix sometimes (such as for all classes not in default package). 
 * examine on which os jpg+waitFor fails
 * Check out graphviz html format for url-enabelling the graph, to add things like
	- edit code (must set sourcepath first time)
	- filter out classes
	? change order 

