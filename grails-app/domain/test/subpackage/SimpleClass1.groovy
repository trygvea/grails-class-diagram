package test.subpackage

import test.EmbeddedPart

class SimpleClass1 {
    static embedded = ['embeddedPart']
    EmbeddedPart embeddedPart

    String name
    SimpleClass2 class2
    def foobar() {}
}