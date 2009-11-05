import test.FooBar
class RootPackagedClass {
    static embedded = ['rootEmbedded']

    RootEmbedded rootEmbedded
    FooBar fooBar
    RootStatus status
}

enum RootStatus {
    GOOD, BETTER, WORSE
}

class RootEmbedded {
    String name
}