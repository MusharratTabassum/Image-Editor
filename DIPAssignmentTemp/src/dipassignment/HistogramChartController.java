package dipassignment;

import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.util.ResourceBundle;

public class HistogramChartController implements Initializable {

    public BarChart histogramBarChart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    void showChart(int[] histogram){
        XYChart.Series data = new XYChart.Series();

        for(int index=0;index<histogram.length;index++){
            data.getData().add(new XYChart.Data<>(String.format("%d",index),histogram[index]));
        }

        histogramBarChart.getData().addAll(data);
    }
}
