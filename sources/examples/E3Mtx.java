package examples;

import datan.*;
   
/**
* Examples for use of Givens transformations
*/
public class E3Mtx {

   public E3Mtx(){
   double u[] = {3. , 4.};
   double w[] = {1. , 1.};
   double cs[] = new double[2];
   DatanFrame df = new DatanFrame("E3Mtx", "Examples for use of Givens transformations");
   DatanMatrix a = new DatanMatrix(1);

   df.writeLine("In the following a can be any matrix."); 
   df.writeLine("Note that these methods are only used within the class DatanMatrix.\n");
   df.writeLine("u[0] = " + u[0] + ", u[1] = " + u[1]);
   df.writeLine("w[0] = " + w[0] + ", w[1] = " + w[1]);
   df.writeLine("-------------------------------------------\n");

   df.writeLine("a.defineGivensTransformation(u, cs); yields");
   a.defineGivensTransformation(u, cs);
   df.writeLine("cs[0] = " + cs[0] + ", cs[1] = " + cs[1]);
   df.writeLine("");
   df.writeLine(" using the resulting values of cs a.applyTransformation(u, cs); yields");   
   a.applyGivensTransformation(u, cs);
   df.writeLine("u[0] = " + u[0] + ", u[1] = " + u[1]);
   df.writeLine("");
   df.writeLine("a.defineAndApplyGivensTransformation(w, cs); yields");   
   a.defineAndApplyGivensTransformation(w, cs);
   df.writeLine("w[0] = " + w[0] + ", w[1] = " + w[1] + " and");
   df.writeLine("cs[0] = " + cs[0] + ", cs[1] = " + cs[1]);
   
   }


   public static void main(String s[]) {
      new E3Mtx();
   }

}
