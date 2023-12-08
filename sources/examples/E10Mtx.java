package examples;

import datan.*;
   
/**
* Example for the solution of the least squares problem with constraints
*/
public class E10Mtx {

   public E10Mtx(){
   double bb[] = {89., 31., 62.};
   double dd[] = {180.};
   DatanMatrix a, e;
   DatanVector b, d, r, x;
   double frac = 0.;
   boolean ok[] = {false};

   DatanFrame df = new DatanFrame(getClass().getName(), "Example for the solution of the least squares problem with constraints", "");

   a = new DatanMatrix(3);
   e = new DatanMatrix(1,3);
   e.setElement(0, 0, 1.);
   e.setElement(0, 1, 1.);
   e.setElement(0, 2, 1.);
   b = new DatanVector(bb);
   d = new DatanVector(dd);
   r = new DatanVector(1);
   df.writeLine("a = ");
   df.writeLine(a.toString());
   df.writeLine("b = ");
   df.writeLine(b.toString());
   df.writeLine("d = ");
   df.writeLine(d.toString());
   df.writeLine("e = ");
   df.writeLine(e.toString());
   df.writeLine("frac = " + frac);    
   df.writeLine("-------------------------------------------\n");


   x = a.leastSquaresWithConstraints(b, e, d, r, frac, ok);
   df.writeLine("a.leastSquaresWithConstraints(b, e, d, r, frac, ok); yields:");
   df.writeLine("Convergence of method: ok[0] = " + ok[0]);
   df.writeLine("x =");
   df.writeLine(x.toString());
   df.writeLine("r =");
   df.writeLine(r.toString());;
   }


   public static void main(String s[]) {
      new E10Mtx();
   }

}
