package saveLoad;

public class DemoObjectDef extends Definition {
    public int idThatDefinesDemoObject;

    public DemoObjectDef (Class<?> c, int i) {
        idThatDefinesDemoObject = i;
    }

    public int getId () {
        return idThatDefinesDemoObject;
    }
}
