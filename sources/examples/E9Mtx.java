package examples;

import datan.*;
   
/**
* Example for use of Marquardt's method in connection with singular value decomposition
*/
public class E9Mtx {

   public E9Mtx(){
   double aa[][] ={{1.,2.,3.},{2.,1.,-2.},{1.,1.,2.}};
   double bb[] = {1., 0., 0.};
   DatanMatrix a;
   DatanVector b, x1, x2;
   double lambda = 0.001, frac = 0.;
   boolean ok[] = {false};

   DatanFrame df = new DatanFrame(getClass().getName(), "Example for use of Marquardt's method in connection with singular value decomposition");

   x1 = new DatanVector(3);
   x2 = new DatanVector(3);
   a = new DatanMatrix(aa);
   df.writeLine("a = ");
   df.writeLine(a.toString());
   b = new DatanVector(bb);
   df.writeLine("b = ");
   df.writeLine(b.toString());
   df.writeLine("lambda = " + lambda + ", frac = " + frac);    
   df.writeLine("-------------------------------------------\n");


   a.marquardt(b, lambda, x1,  x2, frac, ok);
   df.writeLine("a.marquardt(b, lambda, x1,  x2, frac, ok); yields:");
   df.writeLine("Convergence of method: ok[0] = " + ok[0]);
   df.writeLine("x1 =");
   df.writeLine(x1.toString());
   df.writeLine("x2 =");
   df.writeLine(x2.toString());
   }


   public static void main(String s[]) {
      new E9Mtx();
   }

}
