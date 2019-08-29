import Jama.EigenvalueDecomposition;
import Jama.Matrix;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LinearRegression {
    private static String fileName="Breast_cancer_data.csv";

    public static void main(String[] args){
        //test
        Matrix[] vectors = getRegressionVectors();
        ArrayList<double[]> dataSets = Utility.readData(fileName);
        System.out.println("Accuracy: " +linearRegressionAccuracy(vectors[0],vectors[1],dataSets));
        System.out.println("Training Error: " +(100-linearRegressionAccuracy(vectors[0],vectors[1],dataSets)));

    }

    // return Matrix[] for the 2 vectors
    // Matrix[0] is the vector for possitive data set
    // Matrix[1] is the vector for negative data set
    static Matrix[] getRegressionVectors(){
        Matrix[] weightVectors =new Matrix[2];
        ArrayList<double[]> dataSets = Utility.readData(fileName);
        ArrayList<List> splitedData = splitData(dataSets);
        LinkedList<double[]> posData = (LinkedList<double[]>) splitedData.get(0);
        LinkedList<double[]> negData = (LinkedList<double[]>) splitedData.get(1);
        Matrix w_positve = LinearRegressionLoss(posData);
        Matrix w_negative = LinearRegressionLoss(negData);

        weightVectors[0]=w_positve;
        weightVectors[1]=w_negative;
        return weightVectors;
    }

    //beign(0) data is at list[1] and malignant(1) data at list[0]
    private static ArrayList<List> splitData(List<double[]> dataSets){
        ArrayList<List> partionedData = new ArrayList<>();
        LinkedList<double[]> posDataSet=new LinkedList<>();
        LinkedList<double[]> negDataSet= new LinkedList<>();
        for(double[] data: dataSets){
            if(data[5]==0){
                negDataSet.add(data);
            }else{
                posDataSet.add(data);
            }
        }
        partionedData.add(posDataSet);
        partionedData.add(negDataSet);
        return partionedData;

    }

    static Matrix LinearRegressionLoss(List<double[]> dataSets){
        Matrix dataMatrix= new Matrix(5,dataSets.size());
        Matrix resultMatrix= new Matrix(dataSets.size(),1);
        int i=0;
        for(double[] dataPoint: dataSets){
            dataMatrix.set(0,i,1);
            dataMatrix.set(1,i,dataPoint[0]);
            dataMatrix.set(2,i,dataPoint[1]);
            dataMatrix.set(3,i,dataPoint[2]);
            dataMatrix.set(4,i,dataPoint[3]);
            resultMatrix.set(i,0, dataPoint[4]);
            i++;
        }

        Matrix A = dataMatrix.times(dataMatrix.transpose());
        Matrix B = dataMatrix.times(resultMatrix);
        //check singularity
        //if determinant=0 && is square matrix, then its singular, otherwise non singular
        if(A.det()==0 && A.getColumnDimension()==A.getRowDimension()){
            EigenvalueDecomposition AEign= A.eig();
            Matrix D_Plus = AEign.getD();
            for(i=0;i<D_Plus.getRowDimension();i++){
                double D_diagonalValue= D_Plus.get(i,i);
                D_diagonalValue=(D_diagonalValue==0)?0: 1.0/D_diagonalValue;
                D_Plus.set(i,i, D_diagonalValue);
            }
            Matrix A_plus=AEign.getV().times(D_Plus).times(AEign.getV().transpose());
            return A_plus.times(B).transpose();
        }else{
            return A.inverse().times(B).transpose();
        }
    }

    private static double linearRegressionAccuracy(Matrix w_postive, Matrix w_negative, List<double[]> datasets){
        int correctCount=0;
        for(double[] data: datasets){
            int y_i = (data[5]==0)?-1:1;
            Matrix x_i=new Matrix(5,1);
            x_i.set(0,0,1);
            x_i.set(1,0,data[0]);
            x_i.set(2,0,data[1]);
            x_i.set(3,0,data[2]);
            x_i.set(4,0,data[3]);

            double posEculidean = eculideanDistance(w_postive,x_i);
            double negEuclidean = eculideanDistance(w_negative,x_i);
            int y_predict = (posEculidean<negEuclidean)?1 : -1;

            if(y_i==y_predict){
                correctCount++;
            }
        }
        return 100*((double)correctCount)/569.0;

    }

    //calculate the euclidean distance
    private static double eculideanDistance(Matrix w, Matrix x){

        double numerator= w.times(x).get(0,0);
        numerator = (numerator<0)?(-1* numerator) : numerator;
        Matrix one= new Matrix(5,1,1);
        double denominator =  w.times(one).get(0,0);
        denominator =(denominator<0)?(-1* denominator) : denominator;
        return numerator/denominator;

//        Matrix difference = w.minus(x.transpose());
//        Matrix self = (Matrix) difference.clone();
//        Matrix one= new Matrix(5,1,1);
//        double distance = self.transpose().times(self).times(one).get(0,0);
//        return Math.sqrt(distance);
    }

}
