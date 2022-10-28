import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

public class Console {

    File file;
    BufferedImage fractal;
    JPanel p;
    BufferedImage displayedImg;
    Canvas c;
    BufferStrategy bs;
    Graphics g;

    JLabel tf;

    Boolean twitsch = false;

    public Console()
    {
        JFrame frame = new JFrame("Dimension Calculator");
        frame.setSize(1200,900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        p = new JPanel();


        c = new Canvas();
        Dimension s = new Dimension(640,640);
        c.setPreferredSize(s);
        c.setMaximumSize(s);
        c.setMinimumSize(s);

        frame.add(c, BorderLayout.NORTH);

        tf = new JLabel();

        JFileChooser fc = new JFileChooser();
        JButton chose = new JButton("chose Image");
        chose.addActionListener(e-> {
            int value = fc.showOpenDialog(frame);

            if(value == JFileChooser.APPROVE_OPTION){
                file = fc.getSelectedFile();
                try{
                    fractal = ImageIO.read(file);
                }catch(IOException o)
                {
                    o.printStackTrace();
                }
                scale();
                g.drawImage(displayedImg,0,0,640,640,null);
                bs.show();
            }
        });

        JButton calc = new JButton("calculate Dim");
        calc.addActionListener(e ->{
            calculate();
        });

        JButton swit = new JButton("switch methods");
        swit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                twitsch = !twitsch;
                if(twitsch){
                    swit.setBackground(new Color(255,255,0,0));
                }else{swit.setBackground(new Color(0x00FF00));}
            }
        });

        p.add(chose);
        p.add(calc);
        //p.add(swit);
        p.add(tf);



        frame.add(p);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        c.createBufferStrategy(2);
        bs  = c.getBufferStrategy();
        g = bs.getDrawGraphics();

        displayedImg = new BufferedImage(640,640, BufferedImage.TYPE_INT_ARGB);
    }


    public void calculate()
    {
        int numP = 0;
        double dim = 0;

        int[] sizes = new int[]{256,128,64,32,16,8,4,2,1};
        int[] numPs = new int[sizes.length];
        int k = 0;
        for(int i = 0; i < sizes.length;i++ ) {
            for (int y = 0; y < fractal.getHeight(); y += sizes[i]) {
                for (int x = 0; x < fractal.getWidth(); x+= sizes[i]) {
                    //System.out.println("################# " + sizes[i] + "  "+k + "    " + x);
                    int[]space = new int[sizes[i]*sizes[i]];
                    fractal.getRGB(x, y,sizes[i],sizes[i],space,0,sizes[i]);

                    for(int n :space){
                        if ( n == 0xff000000) {
                            numP += 1;
                            break;
                        }
                    }
                    k++;
                }
            }
            numPs[i] = numP;
            System.out.println("Number: "+numP +"  scale: "+ sizes[i]);
            numP = 0;
        }

        if(twitsch){
            dim = NDimension(numPs,sizes);
        }else{
            dim = Dimension(numPs,sizes);
        }

        tf.setText("Dimension: " + dim + " Avrg Number Box: " + Average(numPs));
    }


    private double Dimension(int[] no, int[] so)
    {
        double[] s = new double[so.length];
        for(int i = 0; i < so.length;i++)
        {
            s[i] = Math.log(so[i]*(2.54/8192));
            System.out.println(s[i]);
        }

        double[] n = new double[no.length];
        for(int d = 0; d < no.length;d++)
        {
            n[d] = Math.log(no[d]);
        }

        double xA = Average(s);
        double yA = Average(n);
        double top = 0;
        double bottom = 0;
        for(int x = 0; x < s.length;x++){
                top += (s[x]-xA)*(n[x]-yA);
                bottom += Math.pow((s[x]-xA),2);
        }
        return -top/bottom;
    }

    private double NDimension(int[] n, int[] s)
    {
        return Math.log(n[8])/-(Math.log(n[8]*3));
    }


    private double Average(int[] n)
    {
        double sum = 0;
        for (int j : n) {
            sum += j;
        }
        return sum/n.length;
    }
    private double Average(double[] n)
    {
        double sum = 0;
        for (double j : n) {
            sum += j;
        }
        return sum/n.length;
    }


    public void scale()
    {
        Graphics2D g2 = displayedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(fractal,0,0,640,640,null);
        g2.dispose();
    }
}
