import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

public class Utility {

//    private static double calculateConditionalEntropy(ArrayList<String[]> dataset, int featureRow,double splitThreshHold){
//        double surviveRateGivenFeatureTrueCount=0;
//        double featureTrueCount=0;
//        double surviveRateGivenFeatureFalseCount=0;
//        double featureFlaseCount=0;
//        double size= dataset.size();
//        for (String[] data : dataset) {
//            if (splitThreshHold >= Double.parseDouble(data[featureRow])) {
//                if (Integer.parseInt(data[1]) == 1) {
//                    surviveRateGivenFeatureTrueCount++;
//                }
//                featureTrueCount++;
//            } else {
//                if(Integer.parseInt(data[1]) == 1){
//                    surviveRateGivenFeatureFalseCount++;
//                }
//                featureFlaseCount++;
//            }
//
//        }
//        double deadGivenFeatureTrue =(featureTrueCount-surviveRateGivenFeatureTrueCount)/featureTrueCount;
//        double trueFeatureCal=(surviveRateGivenFeatureTrueCount
//                *(Math.log(surviveRateGivenFeatureTrueCount)/Math.log(2)))+(deadGivenFeatureTrue*(Math.log(deadGivenFeatureTrue)/Math.log(2)));
//        double trueFeature = -(featureTrueCount/size)*trueFeatureCal;
//
//        double deadGivenFeatureFalse = (featureFlaseCount -surviveRateGivenFeatureFalseCount)/featureFlaseCount;
//        double falseFeatureCal = (surviveRateGivenFeatureFalseCount*
//                (Math.log(surviveRateGivenFeatureFalseCount)/Math.log(2)))+(deadGivenFeatureFalse*(Math.log(deadGivenFeatureFalse)/Math.log(2)));
//        double falseFeature = -(featureFlaseCount/size)*falseFeatureCal;
//
//        return trueFeature +falseFeature;
//    }
//
//    public static int calculateMaxInfoGain(ArrayList<String[]> dataset, double threshHold){
//        int bestRow=0;
//        double infoGain, bestInfoGain=0;
//        for(int i=0;i<12;i++){
//            infoGain=calculateInfoGain(dataset,i,threshHold);
//            if(infoGain>bestInfoGain){
//                bestRow=i;
//                bestInfoGain=infoGain;
//            }
//        }
//        return bestRow;
//
//    }
//
//    //return the info gain of a particular feature col
//    private static double calculateInfoGain(ArrayList<String[]> dataset, int featureCol, double threshHold){
//        return calculateEntropy(dataset)-calculateConditionalEntropy(dataset,featureCol,threshHold);
//    }
//
    private static double calculateEntropy(ArrayList<String[]> dataset){
        double surviveRate =countDataSurvivalProbability(dataset);
        double deadRate =1-surviveRate;
        return (-surviveRate* (Math.log(surviveRate)/Math.log(2))-deadRate*(Math.log(deadRate)/Math.log(2)));
    }

    private static double countDataSurvivalProbability(ArrayList<String[]> dataset) {
        double surivalCount = 0;
        int size = dataset.size();
        for (String[] data : dataset){
            if (Integer.parseInt(data[1]) == 1) {
                surivalCount++;
            }
        }
        return surivalCount/size;
    }

    private static double getLessThanProbabilityGivenThreshHold(ArrayList<String[]> dataset, int featureCol, double threshHold){
        double Count=0;
        for(String[] data: dataset){
            double value = Double.parseDouble(data[featureCol]);
            if(value<threshHold){
                Count++;
            }
        }
        return Count/dataset.size();
    }

    private static double calculateConditionalEntropyContinous(ArrayList<String[]> dataset, int featureCol, double threshHold){
        double lessThanProbabilityGivenThreshHold =getLessThanProbabilityGivenThreshHold(dataset,featureCol,threshHold);
        double greaterEqualProbabilityGivenThreshHold =1- lessThanProbabilityGivenThreshHold;
        return lessThanProbabilityGivenThreshHold * calculateEntropyLessThan(dataset, featureCol,threshHold)
                + greaterEqualProbabilityGivenThreshHold*  calculateEntropyGreaterEqual(dataset, featureCol,threshHold);
    }

    private static double calculateEntropyLessThan(ArrayList<String[]>dataset, int featureCol, double threshHold){
        double size = dataset.size();
        double lessThanCount =0;
        double surviveLessThanCount=0;
        for(String[] data: dataset){
            double value = Double.parseDouble(data[featureCol]);
            if(value<threshHold){
                lessThanCount++;
                if(Integer.parseInt(data[1])==1) {
                    surviveLessThanCount++;
                }
            }
        }
        double probabilityOfSurviveLessThan = surviveLessThanCount/lessThanCount;
        return -(lessThanCount/size)*(probabilityOfSurviveLessThan*(Math.log(probabilityOfSurviveLessThan)/Math.log(2))
                + (1-probabilityOfSurviveLessThan)*(Math.log(1-probabilityOfSurviveLessThan)/Math.log(2)));

    }

    private static double calculateEntropyGreaterEqual(ArrayList<String[]>dataset, int featureCol, double threshHold) {
        double size = dataset.size();
        double greaterEqualCount =0;
        double surviveGreaterEqualCount=0;
        for(String[] data: dataset){
            double value = Double.parseDouble(data[featureCol]);
            if(value>=threshHold){
                greaterEqualCount++;
                if(Integer.parseInt(data[1])==1) {
                    surviveGreaterEqualCount++;
                }
            }
        }
        double probabilityOfSurviveGreaterEqual = surviveGreaterEqualCount/greaterEqualCount;
        return -(greaterEqualCount/size)*(probabilityOfSurviveGreaterEqual*(Math.log(probabilityOfSurviveGreaterEqual)/Math.log(2))
                + (1-probabilityOfSurviveGreaterEqual)*(Math.log(1-probabilityOfSurviveGreaterEqual)/Math.log(2)));
    }
    private static double calculateInfoGainContinous(ArrayList<String[]> dataset, int featureCol, double threshHold){
        return calculateEntropy(dataset) - calculateConditionalEntropyContinous(dataset,featureCol,threshHold);
    }

    private static double[] findThreshHold(ArrayList<String []> dataset, int featureCol){
        double threshhold=0;
        double maxInfoGain=0;
        ArrayList<Double> sortFeatCol = sortFeatureCol(dataset,featureCol);
        int size=sortFeatCol.size();
        for(int i=0; i<size-1;i++){
            double possSplitValue =  sortFeatCol.get(i)+((sortFeatCol.get(i+1)-sortFeatCol.get(i))/2);
            double currInfoGain = calculateInfoGainContinous(dataset,featureCol,possSplitValue);
            if(currInfoGain>maxInfoGain){
                threshhold=possSplitValue;
                maxInfoGain=currInfoGain;
            }
        }
        return new double[]{threshhold,maxInfoGain};
    }

    private static ArrayList<Double> sortFeatureCol(ArrayList<String[] > dataset, int featureCol){
        ArrayList<Double> sortedCol =new ArrayList<>();
        HashSet<Double> duplicatCheck = new HashSet<>();
        for(String[] data: dataset){
            double value =Double.parseDouble(data[featureCol]);
            if(data[featureCol]!=null && !duplicatCheck.contains(value)) {
                sortedCol.add(value);
            }
            duplicatCheck.add(value);
        }
        Collections.sort(sortedCol);
        return  sortedCol;
    }

    //result[0]= threshhold and result[1]=best feature
    public static double[] findThreshHoldandFeature(ArrayList<String[]> dataset, LinkedList<Integer> features){
        double[] result={-1,-1,-1};
        double bestIG = -1;
        for(int i: features){
            double[] currBest = findThreshHold(dataset,i);
            if(currBest[1]>bestIG){
                result[0]=currBest[0];
                result[1]=i;
                bestIG=currBest[1];
                result[2]=bestIG;
            }
        }
        return result;
    }
}
