class Complex {

    double r;
    double phi;

    double real;
    double imag;
    public Complex(double real, double img) {

        r = Math.sqrt(real*real + img*img);
        this.real = real;
        imag = img;
        phi = Math.atan(imag/real);
    }

    public Complex multi(Complex b) {
        double real = this.real * b.real - this.imag * b.imag;
        double img = this.real * b.imag + this.imag * b.real;
        return new Complex(real, img);
    }

    public static Complex flip(Complex z)
    {
        double real = z.real/(Math.pow(z.real,2)+Math.pow(z.imag,2));
        double img = z.imag/(Math.pow(z.real,2)+Math.pow(z.imag,2));
        return new Complex(real, -img);
    }

    public Complex add(Complex b)
    {
        double real = this.real + b.real;
        double img = this.imag + b.imag;
        return new Complex(real,img);
    }

/*    public static Complex pow(Complex j, Complex z)
    {


    }*/

    public static Complex pow(double j, Complex z){
        double a = z.real;
        double b = z.imag;

        double real = Math.pow(j,a) * Math.cos(Math.log(j)*b);
        double imag = Math.pow(j,a) * Math.sin(Math.log(j)*b);

        return new Complex(real,imag);
    }

    public static Complex ppow(Complex z, double j)
    {
        double real = Math.pow(z.r,j)*Math.cos(j* z.phi);
        double imag = Math.pow(z.r,j)*Math.sin(j*z.phi);

        return new Complex(real,imag);
    }


    public static Complex sqirt(Complex z)
    {
        return ppow(z,0.5);
    }

    public double abs()
    {
        return Math.sqrt(Math.pow(real,2) + Math.pow(imag,2));
    }
}