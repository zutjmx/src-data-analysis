package examples;

import datan.*;
   
/**
* Examples for use of Householder transformations
*/
public class E4Mtx {

   public E4Mtx(){
   double vv[] = {1. , 2., 0., 4., 3., 4.};
   double cc[] = {1. , 2., 0., 4., 3., 4.};
   double param[] = new double[2];

   DatanFrame df = new DatanFrame("E4Mtx", "Examples for use of Householder transformations");
   DatanMatrix a = new DatanMatrix(1);

   df.writeLine("In the following a can be any matrix."); 
   df.writeLine("Note that these methods are only used within the class DatanMatrix.\n");

   DatanVector v = new DatanVector(vv);
   DatanVector c = new DatanVector(cc);

   df.writeLine("v = ");
   df.writeLine(v.toString());
   df.writeLine("c = ");
   df.writeLine(c.toString());
   df.writeLine("-------------------------------------------\n");

   df.writeLine("a.defineHouseholderTransformation(v, 2, 4, param); yields");
   a.defineHouseholderTransformation(v, 2, 4, param);
   df.writeLine("param[0] = up = " + param[0] + ", param[1] = b =" + param[1]);
   df.writeLine("");

   df.writeLine("a.applyHouseholderTransformation(v, c, 2, 4, param); yields c =");  
   a.applyHouseholderTransformation(v, c, 2, 4, param);
   df.writeLine(c.toString());
   
   }


   public static void main(String s[]) {
      new E4Mtx();
   }

}
