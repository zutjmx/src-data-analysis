package datan;

/**
* A class performing the least-squares fit of a nonlinear function to a set of data,
* using an iterative procedure with step-size reduction.
* @author  Siegmund Brandt.
*/
public final class LsqGen {
   static double EPSILON = 1.E-8, T = 1.E-15, DELTA = 1.E-11, CUT = 1.E-9;
   boolean converged;
   int l, m, n, nst, nstep, nred, r;
   int[] list;
   DatanVector  b, d, mfvec, t, u, x, y, result;
   DatanMatrix a, cx, cy, cyimproved, e, f, fy, gb, help, help2, help3;
   double[] mm;
   double mf, mflast;  // minimum function
   boolean[] ok;
   DatanUserFunction uf;

/**
* @param y vector of measurements.
* @param cy covaariance matrix of measurements.
* @param x vector of first approximations of unknowns.
* @param list array containing the elements of a list specifying which of the n variables are fixed (list element = 0) and
* which are adjustable (list element = 1).
* m number of constaint equations
* @param uf user function which must be an extension of the abstract class DatanUserFunction.
*/
   public LsqGen(DatanVector y, DatanMatrix cy, DatanVector x, int[] list, int m, DatanUserFunction uf) {
      this.y = y;
      this.cy = cy;
      this.x = x;
      this.list = list;
      this.m = m;
      this.uf = uf;
      n = y.getNumberOfElements();
      r = list.length;
      nred = 0;
      for(int i = 0; i < r; i++){
         if(list[i] == 1) nred++;
      }
      l = n + nred;
      t = new DatanVector(l);
      f = new DatanMatrix(cy);
      f.choleskyInversion();
      fy = f.choleskyDecomposition();
      mfvec = new DatanVector(1);
      ok = new boolean[1];
      mf = 0.;
      nstep = 100;
      d = new DatanVector(m);
      loop:
      for(int istep = 0; istep < nstep; istep++){
         mflast = mf;
         f = new DatanMatrix(l, l);
         f.putSubmatrix(nred, nred, fy);
         e = new DatanMatrix(m, l);
         computeMatrixOfDerivatives();
         for(int k = 0; k < m; k++){
            d.setElement(k, - uf.getValue(y, x, k));
         }
         b = f.choleskyMultiply(t);
         b = b.multiply(-1.);
         u = f.leastSquaresWithConstraints(b, e, d, mfvec, 0., ok);
         converged = ok[0];
         if(! converged) break loop;
         mf = mfvec.getElement(0);
         if(nred > 0){
// update x
            int ired = -1;
            for(int i = 0; i < r; i++){
               if(list[i] != 0){
                  ired++;
                  x.setElement(i, x.getElement(i) + u.getElement(ired));
               }
            }
         }
// update y and t
         for(int i = 0; i < n; i++){
            y.setElement(i, y.getElement(i) + u.getElement(i + nred));
            t.setElement(i + nred, t.getElement(i + nred) + u.getElement(i + nred));
         }
// test for convergence
         if(istep > 0 && Math.abs(mf - mflast) < mf * EPSILON + T) break loop;
      }
      if(converged){
         computeMatrixOfDerivatives();
         a = e.getSubmatrix(m, n, 0, nred);
         help = cy.multiplyWithTransposed(a);
         gb = a.multiply(help);
         gb.choleskyInversion();
         if(nred > 0){
            help2 = e.getSubmatrix(m, nred, 0, 0);
            help3 = help2.multiplyTransposedWith(gb);
            cx = help3.multiply(help2);
            cx.choleskyInversion();
// cx contains covariance matrx of unknowns
         }
         else{
            cx = new DatanMatrix(1, 1);
// for nred ==0 the matrix cx is set to be the (1 x 1) zero matrix
         }
         a = e.getSubmatrix(m, n, 0, nred);
         help = a.multiply(cy);
         help2 = gb.multiply(a.multiply(cy));
         help3 = help.multiplyTransposedWith(help2);
         cyimproved = cy.sub(help3);
// cyimproved contains covariance matrix of "improved" measurements
      }
   }


   private void computeMatrixOfDerivatives(){
      double del, der, fp, fm, arg, sav;
      int i2, iy;
      fp = 0.;
      fm = 0.;
      for(int im = 0; im < m; im++){
         i2 = -1;
         for(int il = 0; il < n + r; il++){
            if(il < r){
               if(list[il] != 0){
                  i2++;
                  sav = x.getElement(il);
                  arg = Math.abs(sav);
                  if(arg < CUT) arg = CUT;
                  del = DELTA * arg;
                  x.setElement(il, sav + del);
                  fp = uf.getValue(y, x, im);
                  x.setElement(il, sav - del);
                  fm = uf.getValue(y, x, im);
                  der = (fp - fm) / (del + del);
                  e.setElement(im, i2, der);
                  x.setElement(il, sav);
               }
            }
            else{
               i2++;
               iy = il - r;
               sav = y.getElement(iy);
               arg = Math.abs(sav);
               if(arg < CUT) arg = CUT;
               del = DELTA * arg;
               y.setElement(iy, sav + del);
               fp = uf.getValue(y, x, im);
               y.setElement(iy, sav - del);
               fm = uf.getValue(y, x, im);
               der = (fp - fm) / (del + del);
               e.setElement(im, i2, der);
               y.setElement(iy, sav);
            }
         }
      }
   }

/**
* @return the vector of unknowns.
*/
   public DatanVector getResult(){
      return x;
   }

/**
* @return the covariance matrix ot the unknowns.
*/
   public DatanMatrix getCovarianceMatrix(){
      return cx;
   }
 
/**
* @return the value of the minimum function.
*/
   public double getChiSquare(){  
      return mf;
   }


/**
* @return true if iteration successful, false otherwise.
*/
   public boolean hasConverged(){
      return converged;
   }
 
/**
* @return the vector of "improved" measurements.
*/

   public DatanVector getImprovedMeasurements(){
      return y;
   }
 
/**
* @return the covariance matrix of "improved" measurements.
*/

   public DatanMatrix getCovarianceMatrixOfImprovedMeasurements(){
      return cyimproved;
   }      
   
}
