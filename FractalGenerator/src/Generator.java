
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


// Fraktaler Generator für polynominal iterative Fraktale basierend auf Orbit Traps
// designt von Leopold Winkelbach GITHUB:WhiteBorg
// copyright, please reference if using for research purposes


public class Generator {

    boolean time = false;

    int numBox = 0;

    //Anzahl an iterationen
    int maxIt = 256;

    float timer = 0;

    double scaling = 1;

    Complex cSaved;

    boolean mandelm;

    boolean locked = false;

    //fenstermaße
    static int X = 4096/8 ; static int Y = X;


    // Complexer projizierter bereich
    double xa = -2; double xb = 0.54;
    double ya = -1.27;double yb = 1.27;


    double[] ym = new double[Y];
    double[] xm = new double[X];

    int[][][] pxs = new int[Y][X][];


    JFrame frame;

    JFrame GFOutput;


    Canvas c;

    BufferStrategy bs;
    Graphics g;

    BufferedImage Output;
    BufferedImage displayedImg;
    Renderer r;

    public Generator(){
        cSaved = new Complex(0,0);
       makeMandel(new Complex(0,0),true);

        frame = new JFrame("controller");
        frame.setSize(500,200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel label = new JLabel();
        label.setText("c: ");

        JLabel dimen = new JLabel();
        dimen.setText("x: ("+ xa+" , "+xb+")"+";"+"y: "+"("+ ya+" , "+yb+")");

        JLabel resolution = new JLabel();
        resolution.setText(X+" o "+Y);

        JButton choseC = new JButton("chose C");
        choseC.addActionListener(e-> {
            String real = (String) JOptionPane.showInputDialog(
                    frame,
                    "give real",
                    "chose C",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "example"
            );
            String imag= (String) JOptionPane.showInputDialog(
                    frame,
                    "give imaginary",
                    "chose C",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "example"
            );

            double r = Double.parseDouble(real);
            double i = Double.parseDouble(imag);

            cSaved = new Complex(r,i);
            label.setText("c: " + r+"+"+"i"+i);

        });

        JButton generate = new JButton("generate");
        generate.addActionListener(e->{if(!locked){
            makeMandel(cSaved,mandelm);
            r.update(this);
            render();
        }});

        JButton swap = new JButton("switch generation");
        swap.addActionListener(e-> mandelm = !mandelm);

        JButton reso = new JButton("change res");
        reso.addActionListener(e-> {
            String real = (String) JOptionPane.showInputDialog(
                    frame,
                    "length of window",
                    "chose X",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "example"
            );
            String imag= (String) JOptionPane.showInputDialog(
                    frame,
                    "give Height",
                    "chose Y",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "example"
            );

            X = (int)Double.parseDouble(real);
            Y = (int)Double.parseDouble(imag);

            double[] ym = new double[Y];
            double[] xm = new double[X];

            int[][][] pxs = new int[Y][X][];

            resolution.setText(X+" o "+Y);
        });

        JButton inter = new JButton("chose Interval");
        inter.addActionListener(e->{
            String xl = (String) JOptionPane.showInputDialog(
                    frame,
                    "min x",
                    "chose X",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "example"
            );
            String xr = (String) JOptionPane.showInputDialog(
                    frame,
                    "max x",
                    "chose X",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "example"
            );
            String yl = (String) JOptionPane.showInputDialog(
                    frame,
                    "min y",
                    "chose Y",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "example"
            );
            String yr = (String) JOptionPane.showInputDialog(
                    frame,
                    "max y",
                    "chose Y",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "example"
            );

            xa = Double.parseDouble(xl);
            xb = Double.parseDouble(xr);
            ya = Double.parseDouble(yl);
            yb = Double.parseDouble(yr);
            dimen.setText("x: ("+ xa+" , "+xb+")"+";"+"y: "+"("+ ya+" , "+yb+")");
        });

        JButton print = new JButton("print");
        print.addActionListener(e->{
            File outputfile = new File("image.png");
            try {
                ImageIO.write(Output, "png", outputfile);
            }catch(IOException eo)
            {
                eo.printStackTrace();
            }
        });

        JButton binar = new JButton("Binary");
        binar.addActionListener(e->{
            Util.BinaryFractal(pxs);
            render();
        });

        JPanel panel = new JPanel();
        panel.add(choseC);
        panel.add(generate);
        panel.add(swap);
        panel.add(reso);
        panel.add(inter);
        panel.add(label);
        panel.add(dimen);
        panel.add(resolution);
        panel.add(binar);
        panel.add(print);


        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        GFOutput = new JFrame("Output");
        GFOutput.setSize(512,512);
        GFOutput.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GFOutput.setLayout(new BorderLayout());


        Output = new BufferedImage(X,Y,BufferedImage.TYPE_INT_ARGB);

        r = new Renderer(this);

        c = new Canvas();
        Dimension s = new Dimension(512,512);
        c.setPreferredSize(s);
        c.setMaximumSize(s);
        c.setMinimumSize(s);

        GFOutput.add(c, BorderLayout.NORTH);

        GFOutput.pack();
        GFOutput.setLocationRelativeTo(null);
        GFOutput.setVisible(true);

        c.createBufferStrategy(2);
        bs  = c.getBufferStrategy();
        g = bs.getDrawGraphics();

        displayedImg = new BufferedImage(512,512, BufferedImage.TYPE_INT_ARGB);

    }

    private void makeMandel(Complex c, boolean mandel)
    {
        locked = true;
        numBox = 0;
        for(int i = 0; i < xm.length; i++)
        {
            xm[i] = xa + (xb - xa) * i /X;
        }
        for (int i = 0; i < ym.length; i++)
        {
            ym[i] = ya + (yb - ya) * i /Y;
        }

        for(int y = 0; y < ym.length;y++)
        {
            for(int x = 0; x < xm.length; x++)
            {
                // hier bitte starting c eintragen
                //Complex c = new Complex(-2,0);
                //Complex c =  new Complex(-0.765,0.12);
                if (mandel) pxs[y][x] = mandel(new Complex(xm[x],ym[y]),c);
                else pxs[y][x] = mandel(c, new Complex(xm[x],ym[y]));
                System.out.println(x + "     " + y);
            }
        }

        // dimensional data

       /* for( int y = 1; y < Y-1; y++)
        {
            for (int x = 1; x < X-1 ; x++)
            {
                int[][] p = {pxs[y-1][x-1], pxs[y-1][x], pxs[y-1][x+1],
                             pxs[y][x-1],                pxs[y][x+1],
                             pxs[y+1][x-1], pxs[y+1][x], pxs[y+1][x+1]
                            };
                if(Util.isDisjunct(p,pxs[y][x])){
                    pxs[y][x] = Util.HEXtoRGB(0xffff0000);
                    numBox += 1;
                }
            }
        }*/

        // TEST BINARY

        locked = false;
    }

    private Complex sum(Complex z)
    {
        Complex em = new Complex(0,0);
        for(int i = 0; i < maxIt; i++)
        {
            em = em.add(Complex.flip(Complex.pow(i,z)));
        }

        return em;
    }

    private int[] mandel(Complex c, Complex z)
    {
        for(int i = 0; i < maxIt; i++)
        {
            Complex a = z.multi(z);
            z = a.add(c);

            if (z.abs() > 20)
            {
                if(i > ((255)^2))
                {
                    i -= ((255)^2);
                    return new int[]{255, 255, 255, i};
                }else if(i > 255)
                {
                    i -= 255;
                    return new int[]{255,255,i,0};
                }else{
                    return new int[]{255,i,0,0};
                }
            }
            if(z.abs() == 0)
            {
                int[] col = {255,0,0,i};
                return col;
            }
        }
        // Farbzuweisung
        int[] col = {255,0,0,0};
        System.out.print("(" + z.real + "+i" + z.imag + ")");
        System.out.print("Betrag: " + z.abs());
        return col;
    }

    public void render() {

        for(int y = 0; y < Y;y++)
        {
            for(int x = 0; x < X; x++)
            {
                r.setPixel(x,y,pxs[y][x]);
            }
        }
        scale();
        g.drawImage(displayedImg,0,0,640,640,null);
        bs.show();
    }

    public void scale()
    {
        Graphics2D g2 = displayedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(Output,0,0,512,512,null);
        g2.dispose();
    }

    public static void main(String[] args){

        Generator g = new Generator();
    };
}

