package test

class Foo {
    static belongsTo = [bar:Bar]
    static hasMany = [many:Many]
    static embedded = ['embeddedPart']

    EmbeddedPart embeddedPart

    Child child
    String description

    def getPropertyDeclaredAsGetterMethod() {
        "foo"
    }
    def getNotAProperty(String param) {
        "foo"
    }
    def doIt(a,b,c) {
        return "ohyeah"
    }
}

class EmbeddedPart {
    String name
    String embeddedValue
    void someEmbeddedMethod(String s) {
        
    }
}