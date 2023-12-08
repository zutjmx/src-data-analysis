package datan;

/**
* A class porforming the least-squares fit of a polynomial to a set of data.
* @author  Siegmund Brandt.
*/
public final class LsqPol {
   boolean converged;
   int n, r;
   int[] nsv;
   DatanVector t, x, y, deltay;
   DatanMatrix cx;
   double[] mm;
   double m;
   boolean[] ok;

/**
* @param t vector of controlled variables
* @param y vector of the corresponding measured values.
* @param deltay vector of measurement errors.
* @param r number of unknowns (=1 for polynomial of order 0, =2 for order 1, etc.).
*/
   public LsqPol(DatanVector t, DatanVector y, DatanVector deltay, int r) {
      this.t = t;
      this.y = y;
      this.deltay = deltay;
      this.r = r;
      nsv = new int[r];
      ok = new boolean[1];
      mm = new double[1];
      n = t.getNumberOfElements();
// set up matrix A' and vector C'
      DatanMatrix a = new DatanMatrix(n, r);
      DatanVector c = new DatanVector(n);
      for(int i = 0; i < n; i++){
         for(int k = 0; k < r; k++){
            if(k == 0) a.setElement(i, k, -1. / deltay.getElement(i));
            else a.setElement(i, k, - Math.pow(t.getElement(i), (double)k) / deltay.getElement(i));            
         }
         c.setElement(i, - y.getElement(i) / deltay.getElement(i));
      }
      cx = a.multiplyTransposedWith(a);
      x = a.singularValueDecomposition(c, mm, 0., nsv, ok);
      m = mm[0];
      converged = ok[0];
      cx.choleskyInversion();
   }

/**
* @return the vector of fitted polynomial coefficients.
*/
   public DatanVector getResult(){
      return x;
   }

/**
* @return the covariance matrix of the polynomial coefficients.
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
