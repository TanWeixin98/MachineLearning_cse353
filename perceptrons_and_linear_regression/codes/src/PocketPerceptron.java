import Jama.Matrix;

import java.util.ArrayList;
import java.util.List;

public class PocketPerceptron {

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


    static Matrix findBestAccuracy(double accuracyThreshHold){
        ArrayList<double[]> dataSets =Utility.readData(fileName);
        double max=Integer.MAX_VALUE,index=0;
        for(int i=1;i<1000000;i++) {

            Matrix result = pocketPerceptronAlgorithm(dataSets, i, .3);
            double accuracy=Utility.accuracy(result, dataSets);

            if(accuracy>accuracyThreshHold) {
                return result;
            }
        }
        return null;
    }




    static Matrix pocketPerceptronAlgorithm(List<double[]> dataSets, int maxIteration, double learningRate){
            Matrix w_pocket= new Matrix(1,6);
            Matrix w = new Matrix (1,6);
            for(int i=0;i<maxIteration;i++){

                for(double[] dataPoint: dataSets){
                    //init for checking
                    int y_i=(dataPoint[5]==0) ?-1 :1;

                    Matrix x_i= Utility.convertDataToMatrix(dataPoint);

                    double dotProdcutValue= w.times(x_i).get(0,0);

                    if(dotProdcutValue*y_i <= 0){
                        w = w.plus(x_i.times(y_i).times(learningRate).transpose());
                    }
                }
                if(Utility.accuracy(w_pocket,dataSets)<Utility.accuracy(w,dataSets)){
                    w_pocket=w;
                }
            }
            return w_pocket;
    }



}
