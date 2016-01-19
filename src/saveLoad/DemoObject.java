package saveLoad;

public class DemoObject implements Definable {
    public int idThatDefinesDemoObject;

    public DemoObject (DemoObjectDef def) {
        // Constructor using definition classes
        idThatDefinesDemoObject = def.getId();
    }

    public DemoObject (int i) {
        // Alternative constructor
        idThatDefinesDemoObject = i ;
    }

    // Interface method
    public Definition getDefinition () {
        return new DemoObjectDef(this.getClass(), idThatDefinesDemoObject);
    }
    
    // To test reload
    public void print() {
        //System.out.printf("Object:\tClass:%s\tID:%d\n", this.getClass().toString(), idThatDefinesDemoObject);
    }
}