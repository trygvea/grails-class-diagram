package test

class Parent extends AbstractParent {
	static hasMany = [children: Child, foos: Foo]
	String name
	def getFooBarMethod() {}
}