package test

class Bar {
    static hasMany = [foos:Foo]
    String name
    int age

    def barIt() {
        "barbar"
    }
    
}

