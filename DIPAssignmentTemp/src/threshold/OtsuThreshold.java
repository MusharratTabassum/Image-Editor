package threshold;

import utils.CommonTask;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class OtsuThreshold {
    private BufferedImage inputImage;
    private BufferedImage grayScaleImage;

    public OtsuThreshold(BufferedImage inputBufferedImage) {
        this.inputImage = inputBufferedImage;
        grayScaleImage = CommonTask.applyGrayScale(inputBufferedImage);
    }

    public BufferedImage applyOtsuThreshold() {

        BufferedImage outputImage = new BufferedImage(inputImage.getWidth(),
                inputImage.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        WritableRaster raster = grayScaleImage.getRaster();

        int total = raster.getHeight() * raster.getWidth();   /*total number of pixel*/

        int[] histogram = new int[256];
        int pixel;
        int maxLevelValue = 0;


        for (int row = 0; row < raster.getHeight(); row++) {
            for (int col = 0; col < raster.getWidth(); col++) {
                pixel = raster.getSample(col, row, 0);
                histogram[pixel]++;
                if (histogram[pixel] > maxLevelValue)
                    maxLevelValue = histogram[pixel];
            }
        }

        int[] int_sum = new int[histogram.length];

        int sum = 0;

        for (int t = 0; t < 256; t++) {
            sum += t * histogram[t];
            int_sum[t]=sum;
        }

        float sB = 0;
        int pB = 0;
        int pF = 0;
        float MaxVariance = 0;
        int threshold = 0;


        for (int t = 0; t < 256; t++) {
            pB += histogram[t];
            if (pB == 0)
                continue;

            pF = total - pB;                
            if (pF == 0)
                break;

            sB += (float) (t * histogram[t]);

            float mB = sB / pB;            // Mean Background
            float mF = (sum - sB) / pF;    // Mean Foreground

            float varianceBetween = (float) pB * (float) pF * (mB - mF) * (mB - mF);

            if (varianceBetween > MaxVariance) {
                MaxVariance = varianceBetween;
                threshold = t;
            }
        }
        for(int row=0;row<grayScaleImage.getHeight();row++){
            for(int col=0;col<grayScaleImage.getWidth();col++){
                pixel = raster.getSample(col, row, 0);
                if(pixel>threshold){
                    raster.setSample(col,row,0,1);
                }else{
                    raster.setSample(col,row,0,0);
                }
            }
        }

        outputImage.setData(raster);

        return outputImage;
    }
}
