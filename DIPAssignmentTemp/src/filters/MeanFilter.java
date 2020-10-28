package filters;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class MeanFilter {

    private BufferedImage inputImage;
    private ArrayList<Integer> tempPixel;
    private int[] redPixel, greenPixel, bluePixel;

    public MeanFilter(BufferedImage inputBufferedImage) {
        this.inputImage = inputBufferedImage;

        tempPixel = new ArrayList<>();

        redPixel= new int[9];
        greenPixel = new int[9];
        bluePixel = new int[9];

    }


    public BufferedImage applyMeanFilter() {
        BufferedImage outputImage =
                new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        for(int row=1;row<inputImage.getHeight()-1;row++){
            for(int col=1;col<inputImage.getWidth()-1;col++){
                for(int kernelX=-1;kernelX<=1;kernelX++){
                    for(int kernelY=-1;kernelY<=1;kernelY++){
                        tempPixel.add(inputImage.getRGB(col + kernelY, row + kernelX));
                    }
                }
                for(int index=0;index<tempPixel.size();index++){
                    redPixel[index] = (tempPixel.get(index)>>16)&0xFF;
                    greenPixel[index] = (tempPixel.get(index)>>8)&0xFF;
                    bluePixel[index] = (tempPixel.get(index))&0xFF;
                }

                int meanRedPixel = (int) Arrays.stream(redPixel).min().getAsInt();
                int meanGreenPixel = (int) Arrays.stream(greenPixel).min().getAsInt();
                int meanBluePixel = (int) Arrays.stream(bluePixel).min().getAsInt();

                outputImage.setRGB(col, row, (meanRedPixel<<16)|(meanGreenPixel<<8)|(meanBluePixel));

                tempPixel.clear();
            }
        }


        return outputImage;
    }


}
