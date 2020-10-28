package threshold;

import utils.CommonTask;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class BGThreshold {
    private BufferedImage inputImage;
    private BufferedImage grayScaleImage;
    int[][] m1;
    int[][] m2;

    /**
     * this is a constructor with one parameter
     * @param inputBufferedImage
     */
    public BGThreshold(BufferedImage inputBufferedImage) {
        this.inputImage = inputBufferedImage;
        grayScaleImage = CommonTask.applyGrayScale(inputBufferedImage);
        m1 = new int[inputImage.getHeight()][inputImage.getWidth()];
        m2 = new int[inputImage.getHeight()][inputImage.getWidth()];
    }

    public BufferedImage applyThreshold() {
        int threshold = findThreshold(initialThresholdingValue());

        System.out.println("Threshold value is: "+threshold);

        BufferedImage outputImage = new BufferedImage(inputImage.getWidth(),
                inputImage.getHeight(),BufferedImage.TYPE_BYTE_BINARY);
        WritableRaster raster = grayScaleImage.getRaster();
        for(int row=0;row<grayScaleImage.getHeight();row++){
            for(int col=0;col<grayScaleImage.getWidth();col++){
                int pixel = raster.getSample(col, row, 0);
                if(pixel>threshold){
                    raster.setSample(col,row,0,255);
                }else{
                    raster.setSample(col,row,0,0);
                }
            }
        }

        outputImage.setData(raster);

        return outputImage;
    }

    private int findThreshold(int T_Old){
        int countOfM1=0,countOfM2=0;

        WritableRaster raster = grayScaleImage.getRaster();
        for(int row=0;row<grayScaleImage.getHeight();row++){
            for(int col=0;col<grayScaleImage.getWidth();col++){
                int pixel = raster.getSample(col, row, 0);
                if(pixel>T_Old){
                    m1[row][col] = pixel;
                    m2[row][col] = 0;
                    countOfM1++;
                }else{
                    m2[row][col] = pixel;
                    m1[row][col]=0;
                    countOfM2++;

                }
            }
        }

        float sigma1 = cumulativeSum(m1)/countOfM1;
        float sigma2 = cumulativeSum(m2)/countOfM2;

        int T_New = (int) ((sigma1+sigma2)/2);
        if(Math.abs(T_Old-T_New)>=0 && Math.abs(T_Old-T_New)<=1){
            return T_New;
        }else{
            T_Old = T_New;
            T_Old = findThreshold(T_Old);
        }
        return T_New;
    }

    private float cumulativeSum(int[][] data) {
        int sum = 0;
        for(int row=0;row<inputImage.getHeight();row++){
            for(int col=0;col<inputImage.getWidth();col++){
                sum+=data[row][col];
            }
        }
        return sum;
    }


    private int initialThresholdingValue(){
        int[] histogram = new int[256];
        WritableRaster raster = grayScaleImage.getRaster();
        for(int r=0;r<raster.getHeight();r++){
            for(int c=0;c<raster.getWidth();c++){
                histogram[raster.getSample(c, r, 0)]++;
            }
        }

        int[] c_sum = new int[histogram.length];
        int[] int_sum = new int[histogram.length];
        int sumc = 0,sumi = 0;

        for(int i=0;i<histogram.length;i++){
            sumc += histogram[i];
            c_sum[i] = sumc;
        }

        for(int i=0;i<histogram.length;i++){
            sumi += (i * histogram[i]);
            int_sum[i] = sumi;
        }

        System.out.println((int_sum[255]/c_sum[255]));
        return (int)(int_sum[255]/c_sum[255]);
    }


}
