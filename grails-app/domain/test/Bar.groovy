package test

class Bar {
	static belongsTo = [foo:Foo]
	static hasMany = [many:Many]
	static embedded = ['embeddedHereToo']

	EmbeddedPart embeddedHereToo

	Child child
	String description

	def didit(a,b,c) {
		return "ohyeah"
	}
}