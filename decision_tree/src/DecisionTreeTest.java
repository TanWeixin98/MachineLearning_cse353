import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.util.ArrayList;

public class DecisionTreeTest extends Application{

    private static String fileName="titanic.csv";
    private static int iteration = 12;


    double min= Double.MAX_VALUE;
    double max= Double.MIN_VALUE;

    @Override
    public void start(Stage primaryStage) throws Exception {
        //initalization
        primaryStage.setTitle("Accuracy Graph");
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Tree Depth");
        yAxis.setLabel("Accuracy");
        LineChart<Number,Number> lineChart = new LineChart<>(xAxis,yAxis);
        lineChart.setTitle("Accuracy of Training Data and Testing Data ");
        XYChart.Series[] seriesList = treeTest();
        XYChart.Series trainDataAccuracy = seriesList[1];
        XYChart.Series testDataAccuracy = seriesList[0];
        trainDataAccuracy.setName("Train Data");
        testDataAccuracy.setName("Test Data");

        xAxis.setLowerBound(0);
        xAxis.setLowerBound(iteration);

        yAxis.setLowerBound(min);
        yAxis.setLowerBound(max);
        yAxis.setForceZeroInRange(false);

        Scene scene =  new Scene(lineChart, 800,600);
        lineChart.getData().addAll(trainDataAccuracy,testDataAccuracy);

        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args){
        launch(args);
    }

    private XYChart.Series[] treeTest(){
        XYChart.Series testAccruacyData = new XYChart.Series();
        XYChart.Series trainAccuracyData = new XYChart.Series();

        ArrayList<String[]> data =  DataHandling.readData(fileName);
        //[0] is the the train data and [1] is sample data
        ArrayList[] partionedData = DataHandling.divideData(data,.6);

        System.out.println("Depth     Test                Train");
        for(int i=1;i<=iteration;i++){
            DecisionTree tree = new DecisionTree(i);
            tree.buildTree(partionedData[0]);
            Double testAccuracy = tree.testData(partionedData[1]);
            Double trainAccuracy = tree.testData(partionedData[0]);
            System.out.println(i+"  Test:"+testAccuracy+"   Train:"+trainAccuracy);

            testAccruacyData.getData().add(new XYChart.Data<>(i, testAccuracy));
            trainAccuracyData.getData().add(new XYChart.Data<>(i, trainAccuracy));

            max = (max>testAccuracy) ?max :testAccuracy;
            max =(max>trainAccuracy)?max : trainAccuracy;

            min =(min<testAccuracy)?min : testAccuracy;
            min = (min<trainAccuracy)?min : trainAccuracy;

        }
        return new XYChart.Series[] {testAccruacyData,trainAccuracyData} ;
    }


    private void  testData(DecisionTree decisionTree, String test){
        ArrayList<String[]> data =  DataHandling.readData(test);
        System.out.println(decisionTree.testData(data));
    }
    private DecisionTree train(String train, int depth){
        ArrayList<String[]> data =  DataHandling.readData(train);
        DecisionTree decisionTree = new DecisionTree(depth);
        decisionTree.buildTree(data);
        return decisionTree;
    }

}
