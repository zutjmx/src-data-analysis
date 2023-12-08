package examples;

import datan.*;
   
/**
* Examples for computation of psudoinverse of a square matrix
*/
public class E7Mtx {

   public E7Mtx(){
   double aa[][] ={{1.,2.,3.},{2.,1.,-2.},{1.,1.,2.}};
   double aii[][] ={{1.,2.,2.},{2.,1.,1.},{1.,1.,1.}};
   DatanMatrix a, b, help, help1, x;
   DatanVector d, r;

   DatanFrame df = new DatanFrame(getClass().getName(), "Examples for computation of psudoinverse of a square matrix");

   df.writeLine("Case A: Original matrix is not singular:");
   a = new DatanMatrix(aa);
   df.writeLine("a = ");
   df.writeLine(a.toString());
   x = a.pseudoInverse();
   df.writeLine("x = a.pseudoInverse() yields x =");
   df.writeLine(x.toString());  
   df.writeLine("-------------------------------------------\n");

   df.writeLine("Case B: Original matrix is singular:");   
   a = new DatanMatrix(aii);
   df.writeLine("a = ");
   df.writeLine(a.toString());
   df.writeLine("x = a.pseudoInverse(); yields x =");
   x = a.pseudoInverse();
   df.writeLine(x.toString()); 
   df.writeLine("-------------------------------------------\n");

   df.writeLine("More details for case B:");   
   a = new DatanMatrix(aii);
   df.writeLine("a = ");
   df.writeLine(a.toString());
   d = new DatanVector(3);
   r = new DatanVector(3);
   DatanMatrix[] u = new DatanMatrix[1];
   DatanMatrix[] v = new DatanMatrix[1];
   DatanMatrix dd = new DatanMatrix(3);
   int nsv[] = {0};
   boolean ok[] = {false};
   df.writeLine("x = a.pseudoInverse(r, 0.000001, nsv, ok, d, uu, vv); yields: ");
   x = a.pseudoInverse(r, 0.000001, nsv, ok, d, u, v);
   df.writeLine("Convergence of method: ok[0] = " + ok[0] + ", Number of singular values nsv[0] = " +nsv[0]);
   df.writeLine("Solution x = ");
   df.writeLine(x.toString());
   df.writeLine("Vector r of residuals = ");
   df.writeLine(r.toString());
   df.writeLine("Vector d of diagonal elements = ");
   df.writeLine(d.toString());
   df.writeLine("Orth. matrix u = ");
   df.writeLine(u[0].toString());
   df.writeLine("Orth. matrix v = ");
   df.writeLine(v[0].toString());
   dd.setElement(0, 0, d.getElement(0));
   dd.setElement(1, 1, d.getElement(1));
   dd.setElement(2, 2, d.getElement(2));
   df.writeLine("Check:");
   df.writeLine("D is a matrix with only diagonal elements, which are the elements of d, i. e., D=");
   help = dd.multiplyWithTransposed(v[0]);
   help1 = u[0].multiply(help);
   df.writeLine(dd.toString());
   df.writeLine("and u D v(tranposed) = ");
   df.writeLine(help1.toString());
   df.writeLine("i. e., the original matrix a.");

   }


   public static void main(String s[]) {
      new E7Mtx();
   }

}
