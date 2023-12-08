package examples;

import datan.*;
   
/**
* Examples for Cholesky decomposition and inversion
*/
public class E6Mtx {

   public E6Mtx(){
   double aa[][] ={{1.,2.,3.},{2.,1.,-2.},{1.,1.,2.}};
   double tria[][] = {{1.,2.,3.},{0.,4.,5.},{0.,0.,6.}};
   double bb[][] = {{1.,7.,17.},{3.,11.,19.},{5.,13.,23.}};
   double cc[][] = {{1.,1.},{1.,1.},{1.,1.}};
   DatanMatrix a, b, c, help, help2, sym, tri, u;
   DatanFrame df = new DatanFrame(getClass().getName(), "Examples for Cholesky decomposition and inversion");
   
   a = new DatanMatrix(aa);
   tri= new DatanMatrix(tria);
   df.writeLine("Upper triangular matrix tri =");
   df.writeLine(tri.toString());
   df.writeLine("sym=tri.multiplyTransposedWith(tri); yields symmetric positive definite matrix sym =");
   sym=tri.multiplyTransposedWith(tri);
   df.writeLine(sym.toString());
   df.writeLine("u= sym.choleskyDecomposition(); yields u = ");
   u= sym.choleskyDecomposition();
   df.writeLine(u.toString());
   df.writeLine("Test of Cholesky decomposition: sym = u.multiplyTransposedWith(u); yields sym = ");
   sym = u.multiplyTransposedWith(u);
   df.writeLine(sym.toString());


   c = new DatanMatrix(cc);
   df.writeLine("c =");
   df.writeLine(c.toString());
   b = u.choleskyMultiply(c);
   df.writeLine("b = u.choleskyMultiply(c); yields b = ");
   df.writeLine(b.toString());


   help = new DatanMatrix(sym);
   sym.choleskyInversion();
   df.writeLine("sym.choleskyInversion(); yields");
   df.writeLine(sym.toString());
   help2 = help.multiply(sym);
   df.writeLine("Test of Cholesky inversion: Multiplication of original matrix with inverse yields:");
   df.writeLine(help2.toString());
   }


   public static void main(String s[]) {
      new E6Mtx();
   }

}
