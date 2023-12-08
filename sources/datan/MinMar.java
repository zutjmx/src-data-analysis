package datan;

/**
* A class determining the minimum of a function in n-dimensional space by Marquardt's method of.
* @author  Siegmund Brandt.
*/
public final class MinMar {
   static double EPSDEF = 1.E-12, LAMBDAS = 1.D-3, T = 1.E-18;
   static int MAXSTP = 1000;
   double a, lambda, fmin, fminl, f1, f2, eps, gamma, test, s;
   int imax, ired, istep, lstep, n, nst, nstep, nred;
   boolean converged, newpoint;
   boolean[] ok = {false};
   int[] list;
   DatanVector g, x0, x1, x1red, x2, x2red;
   DatanMatrix gm, hesse;
   DatanUserFunction muf;
   MinDir md;
   AuxGrad grad;
   AuxHesse ah;

   public MinMar(DatanVector x0, int[] list, DatanUserFunction muf) {   
      this(x0, list, 0, 0., muf);
   }

   public MinMar(DatanVector x0,  DatanUserFunction muf) {
      list = new int[x0.getNumberOfElements()];
      for(int i = 0; i < x0.getNumberOfElements(); i++){
         list[i] = 1;
      }
      this.x0 = new DatanVector(x0);
      this.muf = muf;
      eps = EPSDEF;
      nst = MAXSTP;
      initializeAndCompute();
   }

/**
* @param x0 initial point in n-space.
* @param list array containing the elements of a list specifying which of the n variables are fixed (list element = 0) and
* which are adjustable (list element = 1).
* @param nstep maximal step number. Default: if a value <= 0 is used, then 1000 is taken instead.
* @param epsilon accuracy. Default if a value <= 0. is used, then 1,E-8 is taken instead.
* @param muf user function which must be an extension of the abstract class DatanUserFunction.
*/
   public MinMar(DatanVector x0, int[] list, int nstep, double epsilon, DatanUserFunction muf) {
      eps = epsilon;
      this.x0 = new DatanVector(x0);
      nst = nstep;
      this.muf = muf;
      this.list = list;
      eps = epsilon;
      if(eps <= 0.)eps = EPSDEF;
      if(nst <= 0) nst = MAXSTP;
      initializeAndCompute();
   }

   private void initializeAndCompute(){
      initialize();
      if(nred == 0){
         trivial();
      }
      else{
         compute();      }

   }
   
   private void initialize(){
// initialize n (number of variables), nred (number of adjustable variables), and lambda (Marquardt parameter)
      n = x0.getNumberOfElements();
      nred = 0;
      for(int i = 0; i < n; i++){
         if(list[i] != 0) nred++;
      }
      lambda = LAMBDAS;
   }

   private void compute(){
      converged = true;
// initial value of minimum function
      fminl = muf.getValue(x0);
      grad = new AuxGrad(muf, list);
      ah = new AuxHesse(muf, list);
      x1red = new DatanVector(nred);
      x2red = new DatanVector(nred);
// iteration
      loop:
      for(istep = 1; istep <= nst; istep++){
         lstep = istep;
// compute minimum function for 2 values of lambda
         g = grad.getGradient(x0);
         hesse = ah.getHessian(x0);
         gm = new DatanMatrix(g);
         hesse.marquardt(g, lambda, x1red, x2red, 0., ok);
            if(! ok[0]){
               converged = false;
               break loop;
            }
         x1 = new DatanVector(n);
         x2 = new DatanVector(n);
         x1.putSubvector(x1red, list);
         x2.putSubvector(x2red, list);
         x1 = x0.sub(x1);
         x2 = x0.sub(x2);
         f1 = muf.getValue(x1);
         f2 = muf.getValue(x2);
// evaluate results
         if(f2 <= fminl){
// reduce lambda and accept new point
            lambda = 0.1 * lambda;
            x0 = x2;
            fmin = f2;
            newpoint = true;
         }
         else if(f2 > fminl + T && f1 <= fminl + T){
// keep current value of lambda and accept new point
            x0 = x1;
            fmin = f1;
            newpoint = true;
         }
         else{
// increase lambda and reject new point
            lambda = 10. * lambda;
            newpoint = false;
         }
         if(newpoint){
// test for break=off criterion
            if(Math.abs(fminl - fmin) < eps * Math.abs(fmin) + T){
               nstep = istep;
               converged = true;
               break loop;
            }
         }
         fminl = fmin;
      }
//System.out.println("istep = " + istep + ", nst = " + nst);
      if(istep == nst + 1 || ! converged){
         nst = -1;
      }
      else{
         nst = istep;
      }
   }
   
   private void trivial(){
// deals with trivial case (nred = 0)
      fmin = muf.getValue(x0);
      nst = 0;
      converged = true;
   }

/**
* @return the number of iteration steps needed, -1 if iteration failed.
*/
   public int getSteps(){
      return nst;
   }

/**
* @return true if iteration successful, false otherwise.
*/
   public boolean hasConverged(){
      return converged;
   }
   
/**
* @return the postion of the minimum.
*/
   public DatanVector getMinPosition(){
      return x0;
   }
 
/**
* @return the function value at the minimum.
*/
   public double getMinimum(){  
      return fmin;
   }   
   
}
