package datan;

/**
* A class porforming the least-squares fit of a linear function to a set of data.
* @author  Siegmund Brandt.
*/
public final class LsqLin {
   boolean converged;
   int n, r;
   int[] nsv;
   DatanVector t, x, c, deltay;
   DatanMatrix a, cx;
   double[] mm;
   double m;
   boolean[] ok;

/**
* @param t vector, the elements of which are the controlled variables t_i
* @param c vector, the elements c_i = y_i + a0_i of which are the s=ums of the measurements y_i and the constant terms a0_i.
* @param a matrix of coefficients.
* @param deltay vector of measurement errors.
*/
   public LsqLin(DatanVector t, DatanVector c, DatanVector deltay, DatanMatrix a) {
      this.t = t;
      this.c = c;
      this.a = a;
      this.deltay = deltay;
      r = a.getNumberOfColumns();
      nsv = new int[r];
      ok = new boolean[1];
      mm = new double[1];
      n = t.getNumberOfElements();
// set up matrix A' and vector C'
      DatanMatrix aprime = new DatanMatrix(a);
      DatanVector cprime = new DatanVector(c);
      for(int i = 0; i < n; i++){
         for(int k = 0; k < r; k++){
            aprime.setElement(i, k, a.getElement(i, k) / deltay.getElement(i));            
         }
         cprime.setElement(i, c.getElement(i) / deltay.getElement(i));
      }
      cx = aprime.multiplyTransposedWith(aprime);
      x = aprime.singularValueDecomposition(cprime, mm, 0., nsv, ok);
      m = mm[0];
      converged = ok[0];
      cx.choleskyInversion();
   }

/**
* @return the vector of fitted unknowns.
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
