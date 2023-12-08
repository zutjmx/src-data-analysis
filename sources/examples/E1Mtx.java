package examples;

import datan.*;

/**
* Examples for simple matrix and vector operations
*/
public class E1Mtx {
   DatanMatrix a, b, c, r;
   DatanVector u, v, w, x, z;
   double d, s;

	public E1Mtx(){
   double aa[][] = {{1., 2., 3.}, {2., 1., 3.}};
	double bb[][] = {{2., 3., 1.}, {1. ,5., 4.}};
	double cc[][] = {{1., 5.}, {3., 4.}, {2., 3.}};
   double uu[] = {0. ,3. , 4.};
   double vv[] = {3., 1., 2.};
   double ww[] = {5., 2.};
   DatanFrame df = new DatanFrame("E1Mtx", "Examples for simple matrix and vector operations");



	a  = new DatanMatrix(aa);
	df.writeLine("a =");
	df.writeLine(a.toString());
   b  = new DatanMatrix(bb);
	df.writeLine("b =");
	df.writeLine(b.toString());
   c  = new DatanMatrix(cc);
	df.writeLine("c =");
	df.writeLine(c.toString());
   u  = new DatanVector(uu);
	df.writeLine("u =");
	df.writeLine(u.toString());
   v  = new DatanVector(vv);
	df.writeLine("v =");
	df.writeLine(v.toString());
   w  = new DatanVector(ww);
	df.writeLine("w =");
	df.writeLine(w.toString());
	df.writeLine("-------------------------------------------\n");
   df.writeLine("r = new DatanMatrix(a); yields r = ");
   r = new DatanMatrix(a);
	df.writeLine(r.toString());
   df.writeLine("r = a.add(b); yields r = ");
   r = a.add(b);
	df.writeLine(r.toString());
   df.writeLine("r = a.sub(b); yields r = ");
   r = a.sub(b);
	df.writeLine(r.toString());
   df.writeLine("r = a.multiply(0.5); yields r = ");
   r = a.multiply(0.5);
	df.writeLine(r.toString());
   df.writeLine("x = a.multiply(v); yields r = ");
   x = a.multiply(v);
	df.writeLine(x.toString());
   df.writeLine("r = a.multiply(c); yields x = ");
   r = a.multiply(c);
	df.writeLine(r.toString());
   df.writeLine("r = a.multiplyWithTransposed(b); yields r = ");
   r = a.multiplyWithTransposed(b);
	df.writeLine(r.toString());
   df.writeLine("r = a.multiplyTransposedWith(b); yields r = ");
   r = a.multiplyTransposedWith(b);
	df.writeLine(r.toString());
   df.writeLine("r = new DatanMatrix(2); yields r = ");
   r = new DatanMatrix(2);
	df.writeLine(r.toString());
   df.writeLine("r = new DatanMatrix(2, 3); yields r = ");
	r = new DatanMatrix(2, 3);
	df.writeLine(r.toString());
   df.writeLine("r = a.transpose(); yields r = ");
	r = a.transpose();
	df.writeLine(r.toString());
   df.writeLine("z = new DatanVector(w); yields z = ");
	z = new DatanVector(w);
	df.writeLine(z.toString());
   df.writeLine("x = u.add(v); yields x = ");
	x = u.add(v);
	df.writeLine(x.toString());
   df.writeLine("x = u.sub(v); yields x = ");
	x = u.sub(v);
	df.writeLine(x.toString());
	d = u.dot(v);
   df.writeLine("d = u.dot(v); yields d = " + d);
	s = u.norm();
   df.writeLine("s = u.norm(); yields s = " + s);
   df.writeLine("x = u.multiply(0.5); yields x = ");
	x = u.multiply(0.5);
	df.writeLine(x.toString());
   df.writeLine("x = new DatanVector(4); yields x = ");
	x = new DatanVector(4);
	df.writeLine(x.toString());  
   
   }
	

    public static void main(String s[]) {
      new E1Mtx();
    }

}
