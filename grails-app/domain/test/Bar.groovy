package test

class Bar {
    static hasMany = [foos:Foo]
    String name
    int age
    String a
    BarStatus status

    def barIt() {
        "barbar"
    }
    
}

enum BarStatus {
    GOOD, BAD, UGLY
}