package test

class Bar {
    static hasMany = [foos:Foo]
    String name
    int age
    String a

    def barIt() {
        "barbar"
    }
    
}
