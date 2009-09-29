package test

class Many {
    static belongsTo = Foo
    static hasMany = [foos:Foo]
    static embedded = ['embeddedPart']

    EmbeddedPart embeddedPart
    String string
}