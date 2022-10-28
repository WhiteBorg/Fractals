public class Util {


    public static int RGBtoHEX(int[] color)
    {
        return (color[0] << 24 | color[1] << 16 | color[2] << 8 | color[3]);
    }
    public static int[] HEXtoRGB(int hex)
    {
        int alpha = (hex >> 24 & 0xff);
        int red = (hex >> 16 & 0xff);
        int green = (hex >> 8 & 0xff);
        int blue = (hex & 0xff);

        int[] RGB = {alpha,red,green,blue};
        return RGB;
    }
    public static boolean isGreen(int[] color)
    {
        return color[1] == 0;
    }
    public static boolean isGreen(int color) {int[] c = HEXtoRGB(color); return c[1] < c[2];}

    public static boolean isDisjunct(int[][] ps, int[] orig)
    {
        System.out.println("testing for Disjunct:########################");
        System.out.println("printing color of tested pixel: "+orig);
        boolean isGreen = isGreen(orig);
        System.out.println("pixel is: "+ isGreen);

        for (int[] p : ps) {
            if (isGreen) {
                if (!isGreen(p)) {
                    System.out.println("pixel " + p + " is Red");
                    System.out.println("is Disjunct #########################");
                    return true;
                }else{
                    System.out.println("pixel "+p+ " is Green");
                }

            }else {
                if (isGreen(p)){
                    System.out.println("pixel " + p + " is green");
                    System.out.println("is Disjunct #########################");
                    return true;
                }else{
                    System.out.println("pixel "+p+ " is Red");
                }
            }
        }
        return false;
    }


    public static void BinaryFractal(int[][][] pxls)
    {
        boolean[][] px = new boolean[4096][4096];
        boolean gib;
        for(int x = 1; x <= pxls.length-1;x++)
        {
            for(int y = 1; y <= pxls.length-1;y++)
            {
                gib = true;
                int a = pxls[y][x][1];
                for(int i = y-1; i < y+1; i++)
                {
                    for(int j = x-1; j < x+1;j++)
                    {
                        int b = pxls[i][j][1];
                        if( a - b > 1)
                        {
                          px[y][x] = true;
                          gib = false;
                          break;
                        }
                    }
                }
                if(gib)
                {
                    px[y][x] = false;
                }
            }
        }
        for(int x = 1; x <= pxls.length-1;x++)
        {
            for(int y = 1; y <= pxls.length-1;y++) {
                if(px[y][x])
                {
                    pxls[y][x] = new int[]{255,0,0,0};
                }else
                {
                    pxls[y][x] = new int[]{255,255,255,255};
                }
            }
        }
    }
}
