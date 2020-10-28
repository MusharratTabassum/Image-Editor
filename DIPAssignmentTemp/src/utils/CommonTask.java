package utils;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CommonTask {

    public static File importImage(Stage stage) throws IOException {
        File selectedFile = null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
          new FileChooser.ExtensionFilter("Image File","*.jpg")
        );
        File selectedDirectory = fileChooser.showOpenDialog(stage);
        if(selectedDirectory != null){
            selectedFile = selectedDirectory.getAbsoluteFile();
        }

        return selectedFile;
    }

    public static BufferedImage applyGrayScale(BufferedImage inputImage){
        BufferedImage outputImage = new BufferedImage(inputImage.getWidth(),
                inputImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for(int row=0;row<inputImage.getHeight();row++){
            for(int col=0;col<inputImage.getWidth();col++){
                int rgb = inputImage.getRGB(col, row);

                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = (rgb) & 0xFF;

                int gray = (red+green+blue)/3;

                outputImage.setRGB(col, row, (gray<<16)|(gray<<8)|gray);
            }
        }
        return outputImage;
    }
}
