/**
 * A this time, there are a bug (in fact, many ones according grails's JIRA) about the createLink
 * http://jira.grails.org/browse/GRAILS-9643
 * 
 * This taglib is a workarround
 * */
 
class ParamsLinksTagLib {
	def mappedParams = { attrs, body ->
		
		String paramsEncoded = ""
		String type
		for ( e in attrs.params ) {
			type = e.value.getClass().getName()
			if( !type.is("null") && (type.is("java.lang.Integer") ||
				type.is("java.lang.String") ||
				type.is("java.lang.Boolean") ||
				type.is("java.util.ArrayList")))
			{
				paramsEncoded += e.key.toString()
				paramsEncoded += "=" + e.value
				paramsEncoded += "&"
			} 
		}
		

	   out << body() <<   attrs.action + "?" + paramsEncoded
	}
}