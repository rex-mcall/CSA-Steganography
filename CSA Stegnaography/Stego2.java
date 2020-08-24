import java.awt.Color;
import java.util.ArrayList;

public class Steganography {
    /**
    * Clear the lower (rightmost) two bits in a pixel.
    */
    public Steganography () {
        
    }
    public static void clearLow0 ( Pixel p ) {
        // int[] rgb = {p.getRed(), p.getGreen(), p.getBlue()};
        // for (int i = 0; i < 3; i++) {
        //     // rgb[i] /= 4;
        //     // rgb[i] *= 4;
        //     rgb[i] = rgb[i] & 0xFC; // because we know that the pixel value is between 0 and 255
        //     //rgb[i] &= 0xFC;
        //     // rgb[i] = rgb[i] >> 2;
        //     // rgb[i] = rgb[i] << 2;
        // }
        // p.setRed(rgb[0]);
        // p.setGreen(rgb[1]);
        // p.setBlue(rgb[2]);

        p.setRed( p.getRed() & 0xFC ); // because we know that the pixel value is between 0 and 255
        p.setBlue( p.getBlue() & 0xFC );
        p.setGreen( p.getGreen() & 0xFC );

    }
    public static void clearLow ( Pixel p ) {
        p.setRed( p.getRed() & 0xFC ); // because we know that the pixel value is between 0 and 255
        p.setBlue( p.getBlue() & 0xFC );
        p.setGreen( p.getGreen() & 0xFC );
    }
    public static void clearLowTester0 (Picture pic) {
        Pixel[][] pix = pic.getPixels2D();
        for (int x = 0; x < pix.length; x++) {
            for (int y = 0; y < pix[x].length; y++) {
                Pixel p = pix[x][y];
                if ((p.getRed() > 3)  && (p.getGreen() > 3) && (p.getBlue() > 3 ))
                {
                    clearLow(p);
                }
                else
                {
                    clearLow(p);
                }
                int rgb = 0;
                rgb += p.getRed() *  (int)(Math.pow(2, 16));
                rgb += p.getGreen() * (int)(Math.pow(2, 8));
                rgb += p.getBlue();

                //rgb = (p.getRed() << 16) | (p.getGreen() << 8) | (p.getBlue() << 0);
                pic.setBasicPixel(x, y, rgb);
            }
        }   
    }
    public static Picture clearLowTester ( Picture pic ) {
        Pixel[][] pixel = pic.getPixels2D();
        int rgb = 0;
        for (int x = 0; x < pixel[0].length; x++) {

            for (int y = 0; y < pixel.length; y++) {
                Pixel p = pixel[y][x];
                clearLow(p);
                rgb = 0;
                //Using a "magic number" instead of Math.pow to increase efficiency
                rgb += p.getAlpha() * 16777216; //(int)(Math.pow(2, 24))
                rgb += p.getRed() *  65536; //(int)(Math.pow(2, 16))
                rgb += p.getGreen() * 256; //(int)(Math.pow(2, 8))
                rgb += p.getBlue();
                
                pic.setBasicPixel(x, y, rgb);
            }
        }
        return pic;
    }
    /**
    * Set the lower 2 bits in a pixel to the highest 2 bits in c
    */
    public static void setLow (Pixel p, Color c) {
        clearLow(p);
        p.setRed(p.getRed() + (c.getRed() >> 6)); 
        p.setBlue(p.getBlue() + (c.getBlue() >> 6)); 
        p.setGreen(p.getGreen() + (c.getGreen() >> 6)); 
    }
    public static Picture testSetLow (Picture picture, Color color) {
        Pixel[][] pixel = picture.getPixels2D();
        int rgb = 0;
        for (int x = 0; x < pixel[0].length; x++) {
            for (int y = 0; y < pixel.length; y++) {
                Pixel p = pixel[y][x];
                setLow(pixel[y][x], color);
                rgb = 0;
                //Using a "magic number" instead of Math.pow to increase efficiency
                rgb += p.getAlpha() * 16777216; //(int)(Math.pow(2, 24))
                rgb += p.getRed() *  65536; //(int)(Math.pow(2, 16))
                rgb += p.getGreen() * 256; //(int)(Math.pow(2, 8))
                rgb += p.getBlue();               
                picture.setBasicPixel(x, y, rgb);
            }  
        }
        return picture;
    }
    

    /**
    Sets the highest two bits of each pixel’s colors
    to the lowest two bits of each pixel’s colors
    */
    public static Picture revealPicture (Picture hidden) {
        Picture copy = new Picture(hidden);
        Pixel[][] pixels = copy.getPixels2D();   
        Pixel[][] source = hidden.getPixels2D(); 
        int[] currRGB = {0, 0, 0};
        int[] cpRGB = {0, 0, 0};
        int rgb = 0;
        for (int r = 0; r < pixels.length; r++) {
            for (int c = 0; c < pixels[0].length; c++) {
                Color col = source[r][c].getColor();
                currRGB[0] = col.getRed();
                currRGB[1] = col.getGreen();
                currRGB[2] = col.getBlue();
                for (int i = 0; i < 3; i++) {
                    cpRGB[i] = currRGB[i];
                    cpRGB[i] = cpRGB[i] << 6;
                    cpRGB[i] &= 0x00C0;
                    currRGB[i] = currRGB[i] >> 2;
                    // cpRGB[i] += currRGB[i];
                }
                pixels[r][c].setRed(cpRGB[0]);
                pixels[r][c].setGreen(cpRGB[1]);
                pixels[r][c].setBlue(cpRGB[2]);
                rgb = 0;
                //Using a "magic number" instead of Math.pow to increase efficiency
                rgb += pixels[r][c].getAlpha() * 16777216; //(int)(Math.pow(2, 24))
                rgb += pixels[r][c].getRed() *  65536; //(int)(Math.pow(2, 16))
                rgb += pixels[r][c].getGreen() * 256; //(int)(Math.pow(2, 8))
                rgb += pixels[r][c].getBlue();               
                copy.setBasicPixel(c, r, rgb);
            }
        }
        return copy;
    }
    /**
    Determines whether secret can be hidden in source, which is
    true if source and secret are the same dimensions.
    @param source is not null
    @param secret is not null
    @return true if secret can be hidden in source, false otherwise.
    */
    public static boolean canHide (Picture source, Picture secret) {
        Pixel[][] src = source.getPixels2D();
        Pixel[][] scrt = secret.getPixels2D();
        if (src.length >= scrt.length && src[0].length >= scrt[0].length) { return true; }
        else { return false; }
    }
    /**
    Creates a new Picture with data from secret hidden in data from source
    @param source is not null
    @param secret is not null
    @return combined Picture with secret hidden in source
    precondition: source is same width and height as secret
    */
    public static Picture hidePicture (Picture source, Picture secret) {
        if ( canHide(source, secret) ) {
            Picture copy = source;
            Pixel[][] sourcePixels = source.getPixels2D();   
            Pixel[][] secretPixels = secret.getPixels2D(); 
            int rgb = 0;
            for (int r = 0; r < secretPixels.length; r++) {
                for (int c = 0; c < secretPixels[0].length; c++) {
                    Color col = secretPixels[r][c].getColor();
                    setLow(sourcePixels[r][c], col);

                    rgb = 0;
                    //Using a "magic number" instead of Math.pow to increase efficiency
                    rgb += sourcePixels[r][c].getAlpha() * 16777216; //(int)(Math.pow(2, 24))
                    rgb += sourcePixels[r][c].getRed() *  65536; //(int)(Math.pow(2, 16))
                    rgb += sourcePixels[r][c].getGreen() * 256; //(int)(Math.pow(2, 8))
                    rgb += sourcePixels[r][c].getBlue();               
                    copy.setBasicPixel(c, r, rgb);
                }
            }
            return copy;
        }
        else { return secret; }
    }
    public static Picture hidePictureInLocation (Picture source, Picture secret, int row, int column) {
        if ( canHide(source, secret) ) {
            Picture copy = source;
            Pixel[][] sourcePixels = source.getPixels2D();   
            Pixel[][] secretPixels = secret.getPixels2D(); 
            int rgb = 0;
            for (int r = 0; r < secretPixels.length; r++) {
                for (int c = 0; c < secretPixels[0].length; c++) {
                    Color col = secretPixels[r][c].getColor();
                    setLow(sourcePixels[r + row][c + column], col);

                    rgb = 0;
                    //Using a "magic number" instead of Math.pow to increase efficiency
                    // rgb += sourcePixels[r][c].getAlpha() * 16777216; //(int)(Math.pow(2, 24))
                    // rgb += sourcePixels[r][c].getRed() *  65536; //(int)(Math.pow(2, 16))
                    // rgb += sourcePixels[r][c].getGreen() * 256; //(int)(Math.pow(2, 8))
                    // rgb += sourcePixels[r][c].getBlue();  
                    rgb += sourcePixels[r][c].getAlpha() << 24; //(int)(Math.pow(2, 24))
                    rgb += sourcePixels[r][c].getRed() << 16; //(int)(Math.pow(2, 16))
                    rgb += sourcePixels[r][c].getGreen() << 8; //(int)(Math.pow(2, 8))
                    rgb += sourcePixels[r][c].getBlue();             
                    copy.setBasicPixel(c, r, rgb);
                }
            }
            return copy;
        }
        else { return secret; }
    }
    public static Picture hidePictureInRandomLocation (Picture source, Picture secret) {
        if ( canHide(source, secret) ) {
            Picture copy = source;
            Pixel[][] sourcePixels = source.getPixels2D();   
            Pixel[][] secretPixels = secret.getPixels2D(); 
            int rgb = 0;
            int randRow = (int) Math.random() * (sourcePixels.length - secretPixels.length);
            int randCol = (int) Math.random() * (sourcePixels[0].length - secretPixels[0].length);
            for (int r = 0; r < secretPixels.length; r++) {
                for (int c = 0; c < secretPixels[0].length; c++) {
                    Color col = secretPixels[r][c].getColor();
                    setLow(sourcePixels[r + randRow][c + randCol], col);

                    rgb = 0;
                    //Using a "magic number" instead of Math.pow to increase efficiency
                    // rgb += sourcePixels[r][c].getAlpha() * 16777216; //(int)(Math.pow(2, 24))
                    // rgb += sourcePixels[r][c].getRed() *  65536; //(int)(Math.pow(2, 16))
                    // rgb += sourcePixels[r][c].getGreen() * 256; //(int)(Math.pow(2, 8))
                    // rgb += sourcePixels[r][c].getBlue();  
                    rgb += sourcePixels[r][c].getAlpha() << 24; //(int)(Math.pow(2, 24))
                    rgb += sourcePixels[r][c].getRed() << 16; //(int)(Math.pow(2, 16))
                    rgb += sourcePixels[r][c].getGreen() << 8; //(int)(Math.pow(2, 8))
                    rgb += sourcePixels[r][c].getBlue();             
                    copy.setBasicPixel(c, r, rgb);
                }
            }
            return copy;
        }
        else { return secret; }
    }
    /**
     * tells you if all the pixels in both images are the same
     * @param a the picture to be compared
     * @param b the picture to be compared
     * @return true if same, false if not
     */
    public static boolean isSame (Picture a, Picture b) {
        Pixel[][] aPixels = a.getPixels2D();   
        Pixel[][] bPixels = b.getPixels2D();
        for(int r = 0; r < aPixels.length; r++) {
            for (int c = 0; c < aPixels[0].length; c++) {
                if (aPixels[r][c].getColor() != bPixels[r][c].getColor()) {
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * highlights the area where an image is hidden by comparing each pixel within the image
     * @param a - Picture object of the original picture
     * @param b - Picture object of the picture with possible hidden image
     * @return a picture with the hidden image area highlighted
     */
    public static Picture isSameWithBox (Picture a, Picture b) {
        Picture copy = a;
        Pixel[][] aPixels = a.getPixels2D();   
        Pixel[][] bPixels = b.getPixels2D();
        ArrayList<int[]> badPix = new ArrayList<>();
        int rgb = 0;
        Color colorSet = Color.red;
        for (int r = 0; r < aPixels.length; r++) {
            for (int c = 0; c < aPixels[0].length; c++) {
                if (aPixels[r][c].getRed() != bPixels[r][c].getRed()) {
                    int[] currBadPixel = {r, c};
                    badPix.add(currBadPixel);
                }
                else if (aPixels[r][c].getGreen() != bPixels[r][c].getGreen()) {
                    int[] currBadPixel = {r, c};
                    badPix.add(currBadPixel);
                }
                else if (aPixels[r][c].getBlue() != bPixels[r][c].getBlue()) {
                    int[] currBadPixel = {r, c};
                    badPix.add(currBadPixel);
                }
            }
        }
        for (int i = 0; i < badPix.size(); i++) {
            rgb = 0;
            rgb += colorSet.getAlpha() << 24; //(int)(Math.pow(2, 24))
            rgb += colorSet.getRed() << 16; //(int)(Math.pow(2, 16))
            rgb += colorSet.getGreen() << 8; //(int)(Math.pow(2, 8))
            rgb += colorSet.getBlue();             
            copy.setBasicPixel(badPix.get(i)[1], badPix.get(i)[0], rgb);
        }
        return copy;
    }
        /**
        Takes a string consisting of letters and spaces and
        encodes the string into an arraylist of integers.
        The integers are 1-26 for A-Z, 27 for space, and 0 for end of
        string. The arraylist of integers is returned.
        @param s string consisting of letters and spaces
        @return ArrayList containing integer encoding of uppercase
        version of s
        */
      public static ArrayList<Integer> encodeString(String s)
      {
         s = s.toUpperCase();
         String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
         ArrayList<Integer> result = new ArrayList<Integer>(); 
         for (int i = 0; i < s.length(); i++)
         { 
            if (s.substring(i,i+1).equals(" "))
            {
               result.add(27);
            }
            else
            {
               result.add(alpha.indexOf(s.substring(i,i+1))+1);
            }
         }
         result.add(0); return result;
      }
    /**
    Returns the string represented by the codes arraylist.
    1-26 = A-Z, 27 = space
    @param codes encoded string
    @return decoded string
    */
    public static String decodeString(ArrayList<Integer> codes)
    {
        String result="";
        String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i=0; i < codes.size(); i++)
        {
        if (codes.get(i) == 27)
        {
            result = result + " ";
        }
        else
        {
            result = result + alpha.substring(codes.get(i)-1,codes.get(i));
        }
        }
        return result;
    }
    /**
    Given a number from 0 to 63, creates and returns a 3-element
    int array consisting of the integers representing the
    pairs of bits in the number from right to left.
    @param num number to be broken up
    @return bit pairs in number
    */
    private static int[] getBitPairs(int num)
    {
        int[] bits = new int[3]; int code = num;
            for (int i = 0; i < 3; i++)
            {
        bits[i] = code % 4; code = code / 4;
            }
            return bits;
    }
    public static void setLowCharacter (Pixel p, int[] lowBits) {
        clearLow(p);
        p.setRed(p.getRed() + lowBits[0]); 
        p.setGreen(p.getGreen() + lowBits[2]); 
        p.setBlue(p.getBlue() + lowBits[1]); 
    }
    /**
    Hide a string (must be only capital letters and spaces) in a
    picture.
    The string always starts in the upper left corner.
    @param source picture to hide string in
    @param s string to hide
    @return picture with hidden string
    */
    public static Picture hideText (Picture source, String s) {
        ArrayList<Integer> encodedInt = encodeString(s);
        int[][] spltIntVal = new int[encodedInt.size()][3];
        for (int i = 0; i < spltIntVal.length; i++) {
            spltIntVal[i] = getBitPairs(encodedInt.get(i));
        }
        Picture copy = source;
        Pixel[][] sourcePixels = source.getPixels2D(); 
        for (int r = 0; r < encodedInt.size(); r++) {
            setLowCharacter(sourcePixels[r][0], spltIntVal[r]);       
            Color color = sourcePixels[r][0].getColor();       
            copy.setBasicPixel(0, r, color.getRGB());  
        }
        return copy;
    }
    /**
     * Reveals a string hidden in the bottom 2 bits of each color channel of pixels
     * @param hiddenTextImg Picture object that contains a hidden string
     * @return String of the hidden text
     * @author Rex McAllister
     */
    public static String revealText (Picture hiddenTextImg) {
        Pixel currPixColor;
        ArrayList<Integer> lowBits = new ArrayList<>();
        int currLowBits;
        OUTER_LOOP:
        for (int r = 0; r < hiddenTextImg.getWidth(); r++) {
            for (int c = 0; c < hiddenTextImg.getHeight(); c++) {
                currPixColor = hiddenTextImg.getPixel(r, c);
                currLowBits = 0;
                currLowBits += currPixColor.getGreen() & 0x03;
                currLowBits += currPixColor.getBlue() & 0x03;
                currLowBits += currPixColor.getRed() & 0x03;
                if (currLowBits != 0x00) {
                    lowBits.add(currLowBits);
                } 
                else {
                   break OUTER_LOOP;
                }
            } 
        }
        return decodeString(lowBits);
    }
    public static void main (String[] args) {
        Picture hiddenRoll = new Picture("beach.jpg");
        //hiddenRoll.explore();
        Picture hide = hideText(hiddenRoll, "hello world");
        //hide.explore();
        System.out.println(revealText(hide));
    }
}