package datan;

/**
* A class determining the covariance matrix / confidence matrix
* for a set of parameters determined by minimization.
* @author  Siegmund Brandt.
*/
public final class MinCov {
   static double EPSDEF = 1.E-10;
   int[] list;
   DatanVector x0;
   DatanMatrix cx, hesse;
   DatanUserFunction muf;
   
/**
* @param x0 position of minimum.
* @param list array containing the elements of a list specifying which of the n variables are fixed (list element = 0) and
* which are adjustable (list element = 1).
* @param muf user function which must be an extension of the abstract class DatanUserFunction.
*/
   public MinCov(DatanVector x0, int[] list, DatanUserFunction muf) {
      this.x0 = new DatanVector(x0);
      this.muf = muf;
      this.list = list;
   }

/**
* @param x0 position of minimum (all elements of list are set to 1).
* @param muf user function which must be an extension of the abstract class DatanUserFunction.
*/
   public MinCov(DatanVector x0, DatanUserFunction muf) {
      this.x0 = new DatanVector(x0);
      this.muf = muf;
      int[] list = new int[x0.getNumberOfElements()];
      for(int i = 0; i < x0.getNumberOfElements(); i++){
         list[i] = 1;
      }
      this.list = list;
   }
 
/**
* @return the covarariance matrix / confidence matrix.
* @param fact factor by which covariance matrix is multiplied to yield condidence matrix to a given confidence level.
*/
   public DatanMatrix getCovarianceMatrix(double fact){
      if(fact < EPSDEF) fact = 1.;
      hesse = new AuxHesse(muf, list).getHessian(x0);
      cx = hesse.pseudoInverse();
      cx = cx.multiply(fact);
      return cx;
   }   
   
}
