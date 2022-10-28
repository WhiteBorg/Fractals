
import java.awt.image.DataBufferInt;
import java.util.Arrays;
// dieser code stammt zum größten teil aus der Youtube serie von majoolwip in der eine 2D Game Engine programmiert wird

// Methoden für das editieren des implementierten Canvas objects in der Window klasse um bilder darzustellen

public class Renderer {

    private int pW, pH;
    private int[] p;
    private int[] zb;
    private int zDepth = 0;

    public Renderer(Generator g) {

        pW = g.X;
        pH = g.Y;
        p = ((DataBufferInt) g.Output.getRaster().getDataBuffer()).getData();
        zb = new int[p.length];
    }

    public void update(Generator g)
    {
        pW = g.X;
        pH = g.Y;
        p = ((DataBufferInt) g.Output.getRaster().getDataBuffer()).getData();

    }

    //leert den Canvas
    public void clear() {

        Arrays.fill(p, 0);
    }

    // im canvas werden einzelne pixel nach x und y koordinate und einem Farbwert gesetzt, Hexadecimalschreibweise
    public void setPixel(int x, int y, int value) {

        int alpha = ((value >> 24) & 0xff);

        if ((x < 0 || x >= pW || y < 0 || y >= pH) || alpha == 0) {
            return;
        }

        if (alpha == 255) {
            p[x + y * pW] = value;
        } else {

            int pixelColor = p[x + y * pW];

            int newRed = (pixelColor >> 16 & 0xff) - (int) (((pixelColor >> 16 & 0xff) - (value >> 16 & 0xff)) * (alpha / 255f));
            int newGreen = (pixelColor >> 8 & 0xff) - (int) (((pixelColor >> 8 & 0xff) - (value >> 8 & 0xff)) * (alpha / 255f));
            int newBlue = (pixelColor & 0xff) - (int) (((pixelColor & 0xff) - (value & 0xff)) * (alpha / 255f));

            p[x + y * pW] = (255 << 24 | newRed << 16 | newGreen << 8 | newBlue);
        }
    }

    public void setPixel(int x, int y, int[] color) {

        int alpha = color[0];

        if ((x < 0 || x >= pW || y < 0 || y >= pH) || alpha == 0) {
            return;
        }

        if (alpha == 255) {

            p[x + y * pW] = (255 << 24 | color[1] << 16 | color[2] << 8 | color[3]);
        } else {

            int pixelColor = p[x + y * pW];

            int newRed = (pixelColor >> 16 & 0xff) - (int) (((pixelColor >> 16 & 0xff) - (color[1])) * (alpha / 255f));
            int newGreen = (pixelColor >> 8 & 0xff) - (int) (((pixelColor >> 8 & 0xff) - (color[2])) * (alpha / 255f));
            int newBlue = (pixelColor & 0xff) - (int) (((pixelColor & 0xff) - (color[3])) * (alpha / 255f));

            p[x + y * pW] = (255 << 24 | newRed << 16 | newGreen << 8 | newBlue);
        }
    }

    // nimmt als input eine String und ordnet jedem Zeichen ein bildteil aus der Font-Datei hinzu


    // grundlegendes malen von bildern, iteriert durch die pixeldaten des bereitgestellten Image objektes und
    // malt diese ausgehend von der linken oberen ecke des bildes auf den canvas


    //zum Rendern von animationen, wird über die Animationsklasse ausgeführt


    //anders als in drawImageTile(), oder drawImage() wird hier durch eine for loop ein rechteck
    //von dem linken oberen eckpunkt durch eine breite in x richtung und höhe in y Richtung definiert
    //
    public void drawRect(int width, int height, int offx, int offy, int color, boolean fill, double rot) {
        //offscreen don't render
        if (offx < -width) return;
        if (offy < -height) return;
        if (offx >= pW) return;
        if (offy >= pH) return;

        //clipping
        int newX = 0;
        int newY = 0;
        int newWidth = width;
        int newHeight = height;

        if (offx < 0) {
            newX -= offx;
        }
        if (offy < 0) {
            newY -= offy;
        }
        if (newWidth + offx > pW) {
            newWidth -= newWidth + offx - pW;
        }
        if (newHeight + offy > pH) {
            newHeight -= newHeight + offy - pH;
        }

        if (fill) {
            // fill all pixels in rect
            for (int y = newY; y <= newHeight; y++) {
                for (int x = newX; x <= newWidth; x++) {
                    setPixel(x + offx, y + offy, color);
                }
            }
        } else {

            //draw left and right line
            for (int y = newY; y <= newHeight; y++) {
                setPixel(offx, y + offy, color);
                setPixel(offx + width, y + offy, color);
            }
            for (int x = newX; x <= newWidth; x++) {
                setPixel(x + offx, offy, color);
                setPixel(x + offx, offy + height, color);
            }

        }
    }
    public void drawRect(int width, int height, int offx, int offy, int[] color, boolean fill, double rot) {
        //offscreen don't render
        if (offx < -width) return;
        if (offy < -height) return;
        if (offx >= pW) return;
        if (offy >= pH) return;

        //clipping
        int newX = 0;
        int newY = 0;
        int newWidth = width;
        int newHeight = height;

        if (offx < 0) {
            newX -= offx;
        }
        if (offy < 0) {
            newY -= offy;
        }
        if (newWidth + offx > pW) {
            newWidth -= newWidth + offx - pW;
        }
        if (newHeight + offy > pH) {
            newHeight -= newHeight + offy - pH;
        }

        if (fill) {
            // fill all pixels in rect
            for (int y = newY; y <= newHeight; y++) {
                for (int x = newX; x <= newWidth; x++) {
                    setPixel(x + offx, y + offy, color);
                }
            }
        } else {

            //draw left and right line
            for (int y = newY; y <= newHeight; y++) {
                setPixel(offx, y + offy, color);
                setPixel(offx + width, y + offy, color);
            }
            for (int x = newX; x <= newWidth; x++) {
                setPixel(x + offx, offy, color);
                setPixel(x + offx, offy + height, color);
            }

        }
    }


    public void drawLine(int x0, int y0, int x1, int y1, int color) {
            for(int x = x0; x < x1; x++)
            {
                setPixel(x,(y0-y1)/(x0-x1)*x + y0,color);
            }
    }/*else {
            for(int x = x1; x < x0; x++)
            {
                double y = (dy*x)/dx + y1;
                if(y % Math.floor(y) >= 0.5)
                {
                    y = Math.ceil(y);
                }else
                {
                    y = Math.floor(y);
                }
                if(y > 1)
                {
                    for(int i = 0; i < y; i++)
                    {
                        setPixel(x,i,color);
                    }
                }else
                {
                    setPixel(x, (int) y,color);
                }
            }*/


    public int getzDepth() {
        return zDepth;
    }

    public void setzDepth(int zDepth) {
        this.zDepth = zDepth;
    }

}
