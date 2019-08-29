import Jama.Matrix;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Perceptron {
    private static String fileName="Breast_cancer_data.csv";

    public static void main(String[] args){

        //Testing code
        ArrayList<double[]> dataSets =Utility.readData(fileName);
        Matrix result=findBestAccuracy(80.0);
        System.out.println("Accuracy: " + Utility.accuracy(result, dataSets));
        System.out.println("Training Error: " + (100-Utility.accuracy(result, dataSets)));
        System.out.print(result.get(0, 0) + ";" +
                result.get(0, 1) + "," +
                result.get(0, 2) + "," +
                result.get(0, 3) + "," +
                result.get(0, 4) + "," +
                result.get(0,5));
    }

    //if no matrix is above the accuracy threshhold, then return null. Otherwise return the matrix
    static Matrix findBestAccuracy(double accuracyThreshold){
        ArrayList<double[]> dataSets =Utility.readData(fileName);
        double max=Integer.MAX_VALUE,index=0;
        for(int i=1;i<1000000;i++) {

            Matrix result = perceptronAlgorithm(dataSets, i);
            double accuracy=Utility.accuracy(result, dataSets);

            if(accuracy>accuracyThreshold) {
                return result;
            }
        }
        return null;

    }



    // this is the actual batch perceptron algorithm
    static Matrix perceptronAlgorithm(List<double[]> dataSets, int maxIteration){
        Matrix w = new Matrix(1,6);
        for(int i=0;i<maxIteration;i++){
            boolean modified = false;
            for(double[] dataPoint: dataSets){
                //init for checking
                int y_i=(dataPoint[5]==0) ?-1 :1;

                Matrix x_i= Utility.convertDataToMatrix(dataPoint);

                double dotProdcutValue= w.times(x_i).get(0,0);

                if(dotProdcutValue*y_i <= 0){
                    w = w.plus(x_i.times(y_i).transpose());
                    modified = true;
                }
            }
            if(!modified){
                return w;
            }
        }
        return w;
    }



}
