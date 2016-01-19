package engine;

import java.io.*;

/**
 * Creates deep clones of objects!
 * Where did this class come from?  How does it work?  
 * Why doesn't Java have something like this by default?  The world may never know.
 * 
 * NOTE: THIS WILL ONLY WORK IF THE OBJECTS YOU'RE TRYING TO CLONE ARE SERIALIZABLE ... 
 * @author Xin (???)
 *
 */
public class DeepCloner {
   // so that nobody can accidentally create an ObjectCloner object
   private DeepCloner(){}
   // returns a deep copy of an object
   static public Object deepCopy(Object oldObj) throws Exception {
	   ObjectOutputStream oos = null;
	   ObjectInputStream ois = null;
	   try {
		   ByteArrayOutputStream bos = new ByteArrayOutputStream(); // A
		   oos = new ObjectOutputStream(bos); // B
		   // serialize and pass the object
		   oos.writeObject(oldObj);   // C
		   oos.flush();               // D
		   ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray()); // E
		   ois = new ObjectInputStream(bin);                  // F
		   // return the new object
		   return ois.readObject(); // G
	   } catch(Exception e) {
		   System.out.println("Exception in ObjectCloner = " + e);
		   throw(e);
	   } finally {
		   oos.close();
		   ois.close();
	   }
   	}
}