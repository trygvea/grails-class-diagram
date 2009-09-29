package test

class Parent extends AbstractParent {
    static hasMany = [children: Child, bars: Bar]
    String name
    def getFooBarMethod() {}
}