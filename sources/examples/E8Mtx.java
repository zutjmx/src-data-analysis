package examples;

import datan.*;
   
/**
* Examples for the solution of matrix equations by singular value decomposition
*/
public class E8Mtx {

   public E8Mtx(){
   double EPSILON = 1.e-12, FRAC = 1.e-10;
   double cc0[] = {1., 2., 1.};
   double cc1[] = {2., 1., 1.};
   double cc2[] = {3., -2., 2.};
   DatanMatrix a, asave, b, check, s, u, v, x;
   DatanVector c0, c1, c2, d, c, r;
   int m, n;

   c0 = new DatanVector(cc0);
   c1 = new DatanVector(cc1);
   c2 = new DatanVector(cc2);
   m = 1;
   n = 1;
   a = new DatanMatrix(1);

   DatanFrame df = new DatanFrame(getClass().getName(), "Examples for the solution of matrix equations by singular value decomposition");

   for(int i = 0; i < 3; i++){
      for(int i1 = 0; i1 < 3; i1++){
         if(i == 0) df.writeLine("n = m, (case 1)");
         if(i == 1) df.writeLine("n < m, (case 2)"); 
         if(i == 2) df.writeLine("n > m, (case 3)");  
         if(i1 == 0) df.writeLine("a has full rank, (case a)");  
         if(i1 == 1) df.writeLine("a has smaller than full rank, (case b)");   
         if(i1 == 2) df.writeLine("a has smaller than full pseudorank, (case c)");
// prepare input
         if(i == 0){
// case 1
            m = 3;
            n = 3;
            a = new DatanMatrix(m, n);
            a.putColumn(0, c0);
            a.putColumn(1, c1);
            a.putColumn(2, c2);
            if(i1 == 1) a.putColumn(2, c1);
            if(i1 == 2){
               c = new DatanVector(c1);
               c.setElement(2, c.getElement(2) + EPSILON);
               a.putColumn(2, c);
            }
         }
         else if(i == 1){
// case 2
            m = 3;
            n = 2;
            a = new DatanMatrix(m, n);
            a.putColumn(0, c0);
            a.putColumn(1, c1);
            if(i1 == 1) a.putColumn(1, c0);
            if(i1 == 2){
               c = new DatanVector(c0);
               c.setElement(2, c.getElement(2) + EPSILON);
               a.putColumn(1, c);
            }
         }
         else if(i == 2){
// case 3
// the case (m = 2, n = 3) is handled by adding a line of zeroes to the 
// original 2 x 3 matrix and treating it as a 3 x 3 matrix
            m = 3;
            n = 3;
            a = new DatanMatrix(m, n);
            a.putRow(0, c0);
            a.putRow(1, c1);
            if(i1 == 1) a.putRow(1, c0);
            if(i1 == 2){
               c = new DatanVector(c0);
               c.setElement(2, c.getElement(2) + EPSILON);
               a.putRow(1, c);
            }
         }
         asave = new DatanMatrix(a);
         df.writeLine("EPSILON = " + EPSILON + ", FRAC = " + FRAC);
//         df.writeLine("m = " + m + ", n = " + n);
         df.writeLine("a =");
         df.writeLine(a.toString("%23.18f"));
// demonstrate singular value decomposition
         b = new DatanMatrix(m);
         df.writeLine("b is the unit matrix, i.e., b =");
         df.writeLine(b.toString());
         r = new DatanVector(m);
         int nsv[] = {0};
         boolean ok[] = {false};
//System.out.println("In E8Mtx: Case i = " + i + ", i1 = " + i1);
         x = a.singularValueDecomposition(b, r, FRAC, nsv, ok);
         df.writeLine("x = a.singularValueDecomposition(b, r, FRAC, nsv, ok); yields:");
         df.writeLine("Convergence of method: ok[0] = " + ok[0] + ", Number of singular values nsv[0] = " +nsv[0]);
         df.writeLine("Solution x = ");
         df.writeLine(x.toString("%23.18f"));
         df.writeLine("Vector r of residuals = ");
         df.writeLine(r.toString("%23.18f"));
         df.writeLine("...........................................................................................\n");
         a = new DatanMatrix(asave);
         b = new DatanMatrix(m);
         x = a.singularValueDecomposition(b, r, 0., nsv, ok);
         df.writeLine("x = a.singularValueDecomposition(b, r, 0., nsv, ok); (corresponding to FRAC = 1.E-15) yields:");
         df.writeLine("Convergence of method: ok[0] = " + ok[0] + ", Number of singular values nsv[0] = " +nsv[0]);
         df.writeLine("Solution x = ");
         df.writeLine(x.toString("%23.18f"));
         df.writeLine("Vector r of residuals = ");
         df.writeLine(r.toString("%23.18f"));
         df.writeLine("-------------------------------------------------------------------------------------------\n");


      }
   }
   }


   public static void main(String s[]) {
      new E8Mtx();
   }

}
