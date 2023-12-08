package examples;

import datan.*;
   
/**
* Examples for solving matrix equations
*/
public class E5Mtx {

   public E5Mtx(){
   double aa[][] = {{1.,2.,3.},{2.,1.,-2.},{1.,1.,2.}};
   double bb[][] = {{1.},{0.},{0.}};
   DatanFrame df = new DatanFrame(getClass().getName(), "Examples for solving matrix equations", "E5Mtx.txt");
   DatanMatrix a, b, inv;
    
   a = new DatanMatrix(aa);
   b = new DatanMatrix(bb);
   df.writeLine("For a =");
   df.writeLine(a.toString());
   df.writeLine("and b =");
   df.writeLine(b.toString());
   df.writeLine("b.matrixEquation(a); yields b =");
   b.matrixEquation(a);
   df.writeLine(b.toString());
   a = new DatanMatrix(aa);
   df.writeLine("-------------------------------------------\n");
   
   a = new DatanMatrix(aa);
   b = new DatanMatrix(3);
   df.writeLine("For a =");
   df.writeLine(a.toString());
   df.writeLine("and b =");
   df.writeLine(b.toString());
   df.writeLine("b.matrixEquation(a); yields b =");
   b.matrixEquation(a);
   df.writeLine(b.toString());
   df.writeLine("-------------------------------------------\n");
   
   a = new DatanMatrix(aa);
   df.writeLine("The same result is obtained by inv = a.inverse(); namely inv =");
   inv = a.inverse();
   df.writeLine(inv.toString());
   
   }


   public static void main(String s[]) {
      new E5Mtx();
   }

}
