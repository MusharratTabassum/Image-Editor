package dipassignment;

import filters.MeanFilter;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import threshold.BGThreshold;
import threshold.OtsuThreshold;
import utils.CommonTask;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import static java.lang.Math.round;

public class DipAssignmentTemplateController {

    public BorderPane root_layout;
    public ImageView imported_imageview;
    public ImageView output_imageview;
    public MenuBar menu_bar;

    private BufferedImage inputBufferedImage;
    private int[] histogram;


    public void doImportImageFromComputer(ActionEvent actionEvent) throws IOException {
        Stage parent = (Stage) root_layout.getScene().getWindow();
        File imageFile = CommonTask.importImage(parent);
        imported_imageview.setImage(new Image(new FileInputStream(imageFile)));
        inputBufferedImage = ImageIO.read(imageFile);
    }

    public void doImageRotateLeft(ActionEvent actionEvent) {
        int height = inputBufferedImage.getHeight();
        int width = inputBufferedImage.getWidth();
        BufferedImage outputBufferedImage =
                new BufferedImage(height,width,BufferedImage.TYPE_3BYTE_BGR);
        int[][] outputImage = new int[width][height]; //3,4

        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                outputImage[width-col-1][row] = inputBufferedImage.getRGB(col,row);
            }
        }

       for(int row=0;row<width;row++){
            for(int col=0;col<height;col++){
                outputBufferedImage.setRGB(col,row,outputImage[row][col]);
            }
        }

        output_imageview.setImage(SwingFXUtils.toFXImage(outputBufferedImage, null));

    }

    public void doImageRotateRight(ActionEvent actionEvent) {
        int height = inputBufferedImage.getHeight();
        int width = inputBufferedImage.getWidth();
        BufferedImage outputBufferedImage =
                new BufferedImage(height, width, BufferedImage.TYPE_3BYTE_BGR);
        int[][] outputImage = new int[width][height]; //3,4

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                outputImage[col][height - row - 1] = inputBufferedImage.getRGB(col, row);
            }
        }

        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                outputBufferedImage.setRGB(col, row, outputImage[row][col]);
            }
        }

        output_imageview.setImage(SwingFXUtils.toFXImage(outputBufferedImage, null));
    }

    public void doImageFullRotate(ActionEvent actionEvent) {
    }

    public void RGBToGraConversion(ActionEvent actionEvent) {
        int height = inputBufferedImage.getHeight();
        int width = inputBufferedImage.getWidth();
        BufferedImage outputBufferedImage =
                new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int pixel = inputBufferedImage.getRGB(col, row);

                int Red = (pixel >> 8) & 0xFF;
                int Green = (pixel >> 16) & 0xFF;
                int Blue = pixel & 0xFF;

                int gray = (Red + Green + Blue) / 3;
                outputBufferedImage.setRGB(col, row, (gray << 8) | (gray << 16) | gray);
            }
        }

        output_imageview.setImage(SwingFXUtils.toFXImage(outputBufferedImage, null));
    }

    public void doHistogramChart(ActionEvent actionEvent) throws IOException {
        histogram = new int[256];

        WritableRaster raster = inputBufferedImage.getRaster();

        for(int row=0;row<inputBufferedImage.getHeight();row++){
            for(int col=0;col<inputBufferedImage.getWidth();col++){
                int pixel = raster.getSample(col, row, 0);
                histogram[pixel]++;
            }
        }


        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("dipHistogramChart.fxml"));
        Parent parentView = fxmlLoader.load();

        HistogramChartController chartController = (HistogramChartController) fxmlLoader.getController();

        Scene sceneView = new Scene(parentView);
        Stage  stage = (Stage) menu_bar.getScene().getWindow();
        stage.setScene(sceneView);

        chartController.showChart(histogram);
    }
    public void doHistoGramEqualization(ActionEvent actionEvent) {

        BufferedImage outputBufferedImage = new BufferedImage(inputBufferedImage.getWidth(), inputBufferedImage.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);

        WritableRaster raster1 = inputBufferedImage.getRaster();
        WritableRaster raster2 = outputBufferedImage.getRaster();

        histogram = new int[256];
        for (int row = 0; row < inputBufferedImage.getHeight(); row++) {
            for (int col = 0; col < inputBufferedImage.getWidth(); col++) {
                int pixel = raster1.getSample(col, row, 0);
                histogram[pixel]++;
            }
        }

        int total_pixel = raster1.getWidth() * raster1.getHeight();

        int[] histogram_cdf = new int[256];
        histogram_cdf[0] = histogram[0];
        for (int i = 1; i < 256; i++) {
            histogram_cdf[i] = histogram_cdf[i - 1] + histogram[i];
        }
        int min = Arrays.stream(histogram_cdf).min().getAsInt();

        float[] arr = new float[256];
        for (int i = 0; i < 256; i++) {
            arr[i] = round((float) ((histogram_cdf[i]-min) * 255.0) / (float) (total_pixel-min));
        }
        for (int row = 0; row < raster1.getWidth(); row++) {
            for (int col = 0; col < raster1.getHeight(); col++) {
                int value = (int) arr[raster1.getSample(row, col, 0)];
                raster2.setSample(row, col, 0, value);
            }
        }
        outputBufferedImage.setData(raster2);
        output_imageview.setImage(SwingFXUtils.toFXImage(outputBufferedImage, null));

    }


    public void doBGT(ActionEvent actionEvent) {
        BGThreshold bgThreshold = new BGThreshold(inputBufferedImage);
        BufferedImage outputImage = bgThreshold.applyThreshold();
        output_imageview.setImage(SwingFXUtils.toFXImage(outputImage, null));
    }

    public void doOtsusMethod(ActionEvent actionEvent) {

        OtsuThreshold otsuThreshold = new OtsuThreshold(inputBufferedImage);
        BufferedImage outputImage = otsuThreshold.applyOtsuThreshold();
        output_imageview.setImage(SwingFXUtils.toFXImage(outputImage, null));

    }
    public void doMeanFiltering(ActionEvent actionEvent) {
        MeanFilter meanFilter = new MeanFilter(inputBufferedImage);
        BufferedImage outputImage = meanFilter.applyMeanFilter();
        output_imageview.setImage(SwingFXUtils.toFXImage(outputImage, null));
    }

    public void doGaussianFiltering(ActionEvent actionEvent) {
        // TODO try your self
    }
}
