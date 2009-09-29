
Backlog
-------------------------------------------------------------------------------

0.4 release notes
* Added property for graph orientation: top to bottom or left to right
* Graphviz 2.24: translates bgcolor:none to black, while prior versions translate to white! Fixed
* (cleaned up CSS a bit)
* Properties page now initially invisible
--
* dotBuilder now supports subgraphs
* classes can now be shown grouped in packages
* brushed up legend
* Non-image outputFormats like svg and pdf now works, even from form
* Default outputFormat is now png
* New skin, classicSpaced, that use graphviz options to get more 'space' around nodes. Contributed by Gavin .
* preferences defaults can now be configured
* legend not be loaded before required
* when hiding menu, request now has parameters.


Known Issues (to be described in FAQ or similar:)
* The image doesnt size when I move the size slider. 
	A: This may occur if your skin has hardcoded fontsize. Use another skin or create your own.  
* Image is blurry when in <img> tag. 
	A: This is probably because the browser has zoom on. Workaround: Either reset the zoom in the browser, or click the image
* Legend is blurry. Same as above. Try resetting zoom in the browser.
* Using fontsize in skin config does not work. Overriden by form size field. Workaround: none, fix coming
* Pressing enter in form hides form and menu.
* Which outputformats work? A: image formats: png, jpg, gif, bmp; Other: pdf, eps, svg 
* (NO ISSUE!) When selecting a format (such as svg) that opens a document in same window, the back-button return to menu, but without any image. 



------------------------------------------
0.4 backlog
MUST:
* make tests run or remove

May be postponed
* preferences: change skin tab. contains skin selection and fontsize (edge, node, package) sliders.
* Report errors on server (ex dot/format errors) as grails errors. Remove alert in ajax error
* Better enum support. Enums now outside packages
* editable combobox on outputFormat
* IE7: Formatting suck. But dont care - who use IE7?
* DRY on plugin home URL (in ClassDiagramPlugin and in menubar)
* GUI spinner should be bigger & positioned over the graph, and run all the time!
* test on windows
* Keyboard shortcuts on menu
---

Unassigned
 * pressing enter in outputFormat (and some other fields) causes form reload? => Possibly try default button: Update!
 * Write some tests. Get rid of domain classes. Add dynamically in test.
 * add packages in source to provide more consistent logging (according to http://grails.markmail.org/search/?q=list%3Adev+#query:list%3Adev%20+page:2+mid:uunlkazqvgpvhaqt+state:results)
 * Make gui for other prefs, such as those in the space skin
 * Reformat code to 4 spaces indentation, not tabs
 * Filter domain classes 
	- per class? per package ?
 * Extend to other classes
	* Services, controllers, etc. etc. 
	* different coloring schemes
 * prefix class name with package (in nodename) to avoid name collissions. 
 * examine on which os jpg+waitFor fails

Out on the limb
 * Cut restart time by having a separate server for the class diagram viewer (kind of like the grails console), that loads/reloads the domain classes only using application.addDomanClass/addArtifact or something similar?
	- Modularize and create eclipse-plugin ? 
 * Graphviz can attach urls to svg images. Here we could do a number of things:
	- add support for graph editing! (Or no? server reload takes too long!)
	- better: add support for browsing packages and classes, show source (intersting constrants?) etc.  (must set sourcepath first time)
	- ?filter classes
	- ?change order
 * Check out graphviz html format for url-enabelling the graph, to add things like
	- edit code (must set sourcepath first time)
	- filter out classes
	? change order 
* Draw seqeunce diagrams based on recording (using meta.invokeMethod), and possibly UMLGraph.org (think graphviz is unsuited)

