package datan;

/**
* A class performing the least-squares fit of a nonlinear function to a set of data,
* using an iterative procedure with step-size reduction.
* @author  Siegmund Brandt.
*/
public final class LsqNon {
   static double EPSILON = 1.E-8, T = 1.E-15;
   boolean converged;
   int n, nst, nstep, r, nred;
   int[] list, nsv;
   DatanVector c, t, x, x1red, x2, y, deltay, result;
   DatanMatrix a, cx;
   double[] mm;
   double m, m1;  // minimum function
   boolean[] ok;
   DatanUserFunction f;
   AuxDri ad;

/**
* @param t vector, the elements of which are the controlled variables t_i
* @param y vector of the corresponding measured values.
* @param deltay vector of measurement errors.
* @param x vector of first approximations of unknowns.
* @param list array containing the elements of a list specifying which of the n variables are fixed (list element = 0) and
* which are adjustable (list element = 1).
* @param luf user function which must be an extension of the abstract class DatanUserFunction.
*/
   public LsqNon(DatanVector t, DatanVector y, DatanVector deltay, DatanVector x, int[] list, DatanUserFunction luf) {
      this.t = t;
      this.y = y;
      this.deltay = deltay;
      this.x = x;
      this.list = list;
      n = t.getNumberOfElements();
      r = list.length;
      nred = 0;
      for(int i = 0; i < r; i++){
         if(list[i] == 1) nred++;
      }
      nsv = new int[nred];
      ok = new boolean[1];
      mm = new double[1];
      f = luf;
// initial value of minimum function
      m = minimumFunction(x);
      nstep = 100;
      if(nred == 0){
// trivial case: all variables are fixed
         cx = new DatanMatrix(1, 1);
         cx.setElement(0, 0, 0.);
         converged = true;
      }
      else{
         ad = new AuxDri(f, t, y, list);
// iteration loop
         loop:
         for(int istep = 0; istep < nstep; istep++){
// matrix of derivatives
            a = ad.getMatrixOfDerivatives(x);
            for(int i = 0; i < n; i++){
               for(int k = 0; k < nred; k++){
                  a.setElement(i, k, a.getElement(i, k) / deltay.getElement(i));
               }
            }
            c = new DatanVector(n);
            for(int i = 0; i < n; i++){
               c.setElement(i, (y.getElement(i) - f.getValue(x, t.getElement(i))) / deltay.getElement(i));
            }
// solve matrix equation
            x1red = a.singularValueDecomposition(c, mm, 0., nsv, ok);
            converged = ok[0];
            if(! converged) break loop;
// construct vector x2
            x2 = new DatanVector(r);
            x2.putSubvector(x1red, list);
            x2 = x2.add(x);
            m1 = minimumFunction(x2);
            nst = 0;
            while(m1 > m * (1. + EPSILON) + T && nst < 10){
// minimum function has increased; reduce step by factor of 2
               nst++;
               x1red = x1red.multiply(0.5);
               x2 = new DatanVector(r);
               x2.putSubvector(x1red, list);
               x2 = x.add(x2);
               m1 = minimumFunction(x2);
            }
            if(nst == 10){
               converged = false;
               break loop;
            }
            x2 = new DatanVector(r);
            x2.putSubvector(x1red, list);
            x = x.add(x2);
// test for break-off criterion
            if(Math.abs(m - m1) < EPSILON * Math.abs(m) + T){
// compute covarance matrix cx of unknowns
               a = ad.getMatrixOfDerivatives(x);
               for(int i = 0; i < n; i++){
                  for(int k = 0; k < nred; k++){
                     a.setElement(i, k, a.getElement(i, k) / deltay.getElement(i));
                  }
               }
               cx = a.multiplyTransposedWith(a);
               cx.choleskyInversion();
               break loop;
            }
            else{
               m = m1;
            }  
         }
      }
      result = new DatanVector(x);
   }

   private double minimumFunction(DatanVector x){
      double mf = 0.;
      for(int i = 0; i < n; i++){
         mf = mf + Math.pow((y.getElement(i) - f.getValue(x, t.getElement(i)))/deltay.getElement(i), 2.);
      }
      return mf;
   }

/**
* @return the vector of unknowns.
*/
   public DatanVector getResult(){
      return result;
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
      return m;
   }


/**
* @return true if iteration successful, false otherwise.
*/
   public boolean hasConverged(){
      return converged;
   }   
   
}
