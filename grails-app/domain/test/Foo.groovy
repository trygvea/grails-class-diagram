package test

class Foo {
	static hasMany = [bars:Bar]
	static embedded = ['embeddedPart']

	EmbeddedPart embeddedPart
	String name
	int age

	def foobar() {
		"ojoj"
	}
	
}

class EmbeddedPart {
	String name
	String embeddedValue
	void someEmbeddedMethod(String s) {
		
	}
}