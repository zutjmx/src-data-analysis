package examples;

import datan.*;
   
/**
* Examples for manipulation of submatrices and subvectors
*/
public class E2Mtx {
   DatanMatrix a, d, s;
   DatanVector u, w, x, z;

	public E2Mtx(){
   double aa[][] = {{1., 2., 3.}, {2., 1., 3.}};
	double dd[][] = {{0., 2.}, {1., 3.}};
   double uu[] = {0. ,3. , 4.};
   double ww[] = {5., 2.};
   int[] list = {1, 0, 1};
   DatanFrame df = new DatanFrame("E2Mtx", "Examples for manipulation of submatrices and subvectors");



	a  = new DatanMatrix(aa);
	df.writeLine("a =");
	df.writeLine(a.toString());
   d  = new DatanMatrix(dd);
	df.writeLine("d =");
	df.writeLine(d.toString());
   u  = new DatanVector(uu);
	df.writeLine("u =");
	df.writeLine(u.toString());
   w  = new DatanVector(ww);
	df.writeLine("w =");
	df.writeLine(w.toString());
   df.writeLine("list[] = {1,0,1}");
	df.writeLine("-------------------------------------------\n");
   df.writeLine("s = a.getSubmatrix(2, 2, 0, 1); yields s = ");
   s = a.getSubmatrix(2, 2, 0, 1);
	df.writeLine(s.toString());
   df.writeLine("a.putSubmatrix(0, 0, d); yields a = ");
   a.putSubmatrix(0, 0, d);
	df.writeLine(a.toString());
   a  = new DatanMatrix(aa);
   df.writeLine("x = a.getColumn(1); yields x = ");
   x = a.getColumn(1);
	df.writeLine(x.toString());
   df.writeLine("a.putColumn(0, w); yields a = ");
   a.putColumn(1, w);
	df.writeLine(a.toString());
   a  = new DatanMatrix(aa);
   df.writeLine("x = a.getRow(1); yields x = ");
   x = a.getRow(1);
	df.writeLine(x.toString());
   df.writeLine("a.putRow(0, u); yields a = ");
   a.putRow(0, u);
	df.writeLine(a.toString());
   df.writeLine("z = u.getSubvector(list); yields z = ");
   z = u.getSubvector(list);
	df.writeLine(z.toString());
   df.writeLine("u.putSubvector(w, list); yields u = ");
   u.putSubvector(w, list);
	df.writeLine(u.toString());
   
   }
	

    public static void main(String s[]) {
      new E2Mtx();
    }

}
