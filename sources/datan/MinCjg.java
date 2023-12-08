package datan;

/**
* A class determining the minimum of a function in n-dimensional space by the method of conjugate directions.
* @author  Siegmund Brandt.
*/
public final class MinCjg {
   static double EPSDEF = 1.E-12, T = 1.E-18;
   static int MAXSTP = 1000;
   double a, fmin, fminl, eps, gamma, test, s;
   int imax, ired, istep, lstep, n, nst, nstep, nred;
   boolean converged;
   int[] list;
   DatanVector x0, d, g, gr, h, hfull;
   DatanUserFunction muf;
   MinDir md;
   AuxGrad grad;

   public MinCjg(DatanVector x0, int[] list, DatanUserFunction muf) {   
      this(x0, list, 0, 0., muf);
   }

   public MinCjg(DatanVector x0,  DatanUserFunction muf) {
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
   public MinCjg(DatanVector x0, int[] list, int nstep, double epsilon, DatanUserFunction muf) {
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
// initialize n (number of variables), nred (number of adjustable variables), and m (number of simplex corner points)
      n = x0.getNumberOfElements();
      nred = 0;
      for(int i = 0; i < n; i++){
         if(list[i] != 0) nred++;
      }
   }

   private void compute(){
      fmin = muf.getValue(x0);
// Initially directions g and h are identical to gradient at x0
      grad = new AuxGrad(muf, list);
      g = grad.getGradient(x0);
      g = g.multiply(- 1.);
      h = new DatanVector(g);
// iteration
      loop:
      for(istep = 1; istep <= nst; istep++){
         lstep = istep;
         if(istep > 1){
            if(Math.abs(fminl - fmin) < eps * Math.abs(fmin) + T){
               nstep = istep;
               converged = true;
               break loop;               
            }
            fminl = fmin;
         }
         hfull = new DatanVector(n);
         hfull.putSubvector(h, list);
         if(hfull.dot(hfull) < eps){
            converged = true;
            break loop;
         }
         md = new MinDir(x0, hfull, nst, eps, muf);
         if(! md.hasConverged()){
            converged = false;
            break loop;
         }
// x0 is position of minimum along direction h
         x0 = md.getMinPosition();
         fmin = md.getMinimum();
// gr is gradient at x0
         gr = grad.getGradient(x0);
// compute next set of directions g and h
         gr = gr.multiply(- 1.);
         d = gr.sub(g);
         s = d.dot(gr);
         a = g.dot(g);
         if(a < eps){
            converged = true;
            break loop;
         }
         gamma = s / a;
         h = h.multiply(gamma);
         h = h.add(gr);
         g = new DatanVector(gr);
      }
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
