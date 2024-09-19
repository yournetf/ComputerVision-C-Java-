import java.io.*;
import java.util.StringTokenizer;

import static java.lang.Math.abs;
import static java.lang.Math.exp;

public class YournetF_Project2_Main {

    public static class BiMeans {

        //Attributes.
        int numRows;
        int numCols;
        int minVal;
        int maxVal;
        int BiGaussThrVal;
        int histHeight;
        int maxHeight;
        int[] histAry;
        int[] GaussAry;
        int[] bestFitGaussAry;
        char[][]GaussGraph;
        char[][]gapGraph;

        //Methods.
        public BiMeans(int numRows, int numCols, int minVal, int maxVal) {
            this.numRows = numRows;
            this.numCols = numCols;
            this.minVal = minVal;
            this.maxVal = maxVal;
            this.histAry = new int[maxVal + 1];
            this.GaussAry = new int[maxVal + 1];
            this.bestFitGaussAry = new int[maxVal + 1];
            this.GaussGraph = new char[maxVal + 1][this.histHeight + 1];
            this.gapGraph = gapGraph;
        }

        public int loadHistAry(BufferedReader inFileReader) throws IOException {
            this.histAry = new int[maxVal + 1];
            int max = 0;
            for (int i = 0; i <= maxVal; i++) {
                String line = inFileReader.readLine();
                StringTokenizer tokenizer = new StringTokenizer(line);
                tokenizer.nextToken();
                this.histAry[i] = Integer.parseInt(tokenizer.nextToken());
                if (this.histAry[max] < this.histAry[i]) max = i;
            }
            this.GaussGraph = new char[maxVal + 1][this.histAry[max] + 1];
            this.gapGraph = new char[maxVal + 1][this.histAry[max] + 1];
            return this.histAry[max];
        }

        public void printHist(BufferedWriter histFileWriter) throws IOException {
            histFileWriter.write("** Below is the input histogram ** \n");
            printImageHeader(histFileWriter);
            for (int i = 0; i < this.histAry.length; i++) {
                histFileWriter.write(i + " " + this.histAry[i]);
                histFileWriter.write("\n");
            }
        }

        public void displayHist(BufferedWriter histFileWriter) throws IOException {
            histFileWriter.write("** Below is the graphic display of the input histogram ** \n");
            printImageHeader(histFileWriter);
            for (int i = 0; i < this.histAry.length; i++) {
                histFileWriter.write(i + "(" + this.histAry[i] + "): ");
                for (int j = 0; j < this.histAry[i]; j++) {
                    histFileWriter.write("+");
                }
                histFileWriter.write("\n");
            }
        }

        public int biGaussian(int[]histAry, int[] GaussAry, int maxHeight, int minVal, int maxVal, char[][]GaussGraph, BufferedWriter logFileWriter) throws IOException {
            logFileWriter.write("Entering biGaussian method");
            double sum1;
            double sum2;
            double total;
            double minSumDiff;
            int offset = (int)(maxVal - minVal)/10;
            int dividePt = offset;
            int bestThr = dividePt;
            minSumDiff = 99999.0;

            while(dividePt < (maxVal - offset)){
                setZero(GaussAry);

                sum1 = fitGauss(0, dividePt, histAry, GaussAry, this.maxHeight, GaussGraph, logFileWriter);
                sum2 = fitGauss(dividePt, maxVal, histAry, GaussAry, this.maxHeight, GaussGraph, logFileWriter);
                total = sum1 + sum2;

                if(total < minSumDiff) {
                    minSumDiff = total;
                    bestThr = dividePt;
                    copyArys(GaussAry, this.bestFitGaussAry);
                }

                logFileWriter.write("In biGaussian(): dividePt: " + dividePt + " sum: " + sum1 + " sum2: " + sum2 + " total: " + total + " minSumDiff: " + minSumDiff + " bestThr: " + bestThr + "\n");

                dividePt++;
            }
            logFileWriter.write("Exiting biGaussian method, minSumDiff: " + minSumDiff + " bestThr: " + bestThr + "\n");
            return bestThr;
        }

        public double fitGauss(int leftIndex, int rightIndex, int[]histAry, int[]GaussAry, int maxHeight, char[][]GaussGrap, BufferedWriter logFileWriter) throws IOException {
            logFileWriter.write("Entering fitGauss() method" + "\n");
            double mean;
            double var;
            double sum = 0.0;
            double Gval;

            mean = computeMean(leftIndex, rightIndex, histAry, logFileWriter);
            var = computeVar(leftIndex, rightIndex, mean, histAry, logFileWriter);
            int index = leftIndex;

            while (index <= rightIndex) {
                Gval = modifiedGauss(index, mean, var, maxHeight);
                sum += abs(Gval - (double)histAry[index]);
                GaussAry[index] = (int)Gval;
                index++;
            }
            logFileWriter.write("leaving fitGauss method, sum: " + sum + "\n");
            return sum;
        }

        public double computeMean(int leftIndex, int rightIndex, int[]histAry, BufferedWriter logFileWriter) throws IOException {
            logFileWriter.write("Entering computeMean() method" + "\n");
            this.maxHeight = 0;
            int sum = 0;
            int numPixels = 0;
            int index = leftIndex;

            while(index < rightIndex) {
                sum += (histAry[index] * index);
                numPixels += histAry[index];

                if(histAry[index] > this.maxHeight) {
                    this.maxHeight = histAry[index];
                }
                index++;
            }
            double result = (double)sum/(double)numPixels;
            logFileWriter.write("Leaving computeMean method maxHeight: " + maxHeight + " result: " + result + "\n");
            return result;
        }

        public double computeVar(int leftIndex, int rightIndex, double mean, int[]histAry, BufferedWriter logFileWriter) throws IOException {
            logFileWriter.write("Entering computeVar() method" + "\n");
            double sum = 0.0;
            int numPixels = 0;

            int index = leftIndex;
            while(index <= rightIndex) {
                sum += (double)histAry[index] * Math.pow(((double)index-mean), 2);
                numPixels += histAry[index];

                index++;
            }
            double result = sum/(double)numPixels;
            logFileWriter.write("Leaving computeVar() method returning result: " + result);
            return result;
        }

        public double modifiedGauss(int x, double mean, double var, int maxHeight){
            double a = (double)x;
            double firstTerm = a - mean;
            double firstTermSquared = firstTerm * firstTerm;
            double negatedFirstTermSquared = -firstTermSquared;
            double secondTerm = 2 * var;
            double parentheses = negatedFirstTermSquared/secondTerm;
            double exponential = exp(parentheses);
            double gVal = (double)maxHeight * exponential;
            return gVal;
        }

        public void copyArys(int[] ary1, int[] ary2){
            for(int i = 0; i < ary1.length; i++) {
                ary2[i] = ary1[i];
            }
        }

        public void setZero(int[] ary){
            for(int i = 0; i < ary.length; i++) {
                ary[i] = 0;
            }
        }

        public void printBestFitGauss(int[] bestFitGaussAry, BufferedWriter GaussFileWriter) throws IOException {
            GaussFileWriter.write("** Below is the best fitted Gaussians **");
            GaussFileWriter.write("\n");
            printImageHeader(GaussFileWriter);
            for (int i = 0; i < bestFitGaussAry.length; i++) {
                GaussFileWriter.write(i + ": " + bestFitGaussAry[i] + "\n");
            }
        }

        public void printImageHeader(BufferedWriter fileWriter) throws IOException {
            fileWriter.write(this.numRows + " " + this.numCols + " " + this.minVal + " " + this.maxVal + "\t//Image header");
            fileWriter.write("\n");
        }

        public void plotGaussGraph(int[] bestFitGaussAry, char[][] GaussGraph, BufferedWriter logFileWriter) throws IOException {
            logFileWriter.write("Entering plotGaussGraph() method" + "\n");

            setBlanks(GaussGraph);

            int index = 0;

            while (index <= this.maxVal) {
                if (bestFitGaussAry[index] > 0) {
                    int i = 0;
                    while (i < bestFitGaussAry[index]) {
                        GaussGraph[index][i] = '*';
                        i++;
                    }
                }
                index++;
            }
            logFileWriter.write("Leaving plotGaussGraph() method" + "\n");
        }


        public void displayGaussGraph(char[][]GaussGraph, BufferedWriter fileWriter) throws IOException {
            fileWriter.write("** Below is the graphic display of the BestFitGaussAry **");
            fileWriter.write("\n");
            printImageHeader(fileWriter);
            for(int i = 0; i < GaussGraph.length; i++) {
                fileWriter.write(i + ": ");
                for (int j = 0; j < GaussGraph[i].length; j++) {
                    fileWriter.write(GaussGraph[i][j]);
                }
                fileWriter.write("\n");
            }
        }

        public void plotGapGraph(int[] histAry, int[] bestFitGaussAry, BufferedWriter logFileWriter) throws IOException {
            logFileWriter.write("Entering plotGapGraph() method");
            logFileWriter.write("\n");
            setBlanks(this.gapGraph);
            int index = 0;
            int end1;
            int end2;

            while(index <= this.maxVal){
                if(bestFitGaussAry[index] <= this.histAry[index]){
                    end1 = this.bestFitGaussAry[index];
                    end2 = this.histAry[index];
                }
                else {
                    end1 = this.histAry[index];
                    end2 = this.bestFitGaussAry[index];
                }
                int i = end1;
                while(i <= end2) {
                    this.gapGraph[index][i] = '@';
                    i++;
                }
                index++;
            }
            logFileWriter.write("leaving plotGaussGraph()");
        }

        public void dispGapGraph(char[][] gapGraph, BufferedWriter GaussFileWriter) throws IOException {
            GaussFileWriter.write("** Below dispays the gaps between the histogram and the best fitted Gaussians **");
            GaussFileWriter.write("\n");
            printImageHeader(GaussFileWriter);
            for(int i = 0; i < this.gapGraph.length; i++) {
                for (int j = 0; j < this.gapGraph[i].length; j++) {
                    GaussFileWriter.write(this.gapGraph[i][j]);
                }
                GaussFileWriter.write("\n");
            }
        }

        public void setBlanks(char[][] graph){
            for(int i = 0; i < graph.length; i++) {
                for(int j = 0; j < graph[i].length; j++) {
                    graph[i][j] = ' ';
                }
            }
        }
    }



    public static void main(String[] args) throws IOException {

        //Checks to see if the number of arguments passed is correct.
        if (args.length != 4) {
            System.out.println("You must have 4 arguments");
            System.exit(1);
        }

        //Initializes Read File and checks to see if it can be opened.

        BufferedReader inFileReader = null;
        try {
            inFileReader = new BufferedReader(new FileReader(args[0]));
        } catch (Exception e) {
            System.out.println("Unable to read file: " + args[0]);
            System.out.println(e);
        }

        //Initializes histFile and checks to see if it can be opened.
        BufferedWriter histFileWriter = null;
        try {
            histFileWriter = new BufferedWriter(new FileWriter(args[1]));
        } catch (Exception e) {
            System.out.println("Unable to write file: " + args[1]);
            System.out.println(e);
        }

        //Initializes GaussFile and checks to see if it can be opened.
        BufferedWriter GaussFileWriter = null;
        try {
            GaussFileWriter = new BufferedWriter(new FileWriter(args[2]));
        } catch (Exception e) {
            System.out.println("Unable to write file: " + args[2]);
            System.out.println(e);
        }

        //Initializes logFile and checks to see if it can be opened.
        BufferedWriter logFileWriter = null;
        try {
            logFileWriter = new BufferedWriter(new FileWriter(args[3]));
        } catch (Exception e) {
            System.out.println("Unable to write file: " + args[3]);
            System.out.println(e);
        }

        //Double checks to ensure the file reader cannot be null, then reads the first line.
        assert inFileReader != null;
        String line = inFileReader.readLine();

        /*Takes the first line and parses it for the first 4 values, and assigns them respectively.*/
        StringTokenizer tokenizer = new StringTokenizer(line);
        int numRows = Integer.parseInt(tokenizer.nextToken());
        int numCols = Integer.parseInt(tokenizer.nextToken());
        int minVal = Integer.parseInt(tokenizer.nextToken());
        int maxVal = Integer.parseInt(tokenizer.nextToken());

        //Declares an instance of BiMeans.
        BiMeans biMeans = new BiMeans(numRows, numCols, minVal, maxVal);

        //Loads the histAry and assigns its height to the histHeight attribute.
        biMeans.loadHistAry(inFileReader);

        assert histFileWriter != null;
        biMeans.printHist(histFileWriter);
        biMeans.displayHist(histFileWriter);

        assert logFileWriter != null;
        biMeans.BiGaussThrVal = biMeans.biGaussian(biMeans.histAry, biMeans.GaussAry, biMeans.maxHeight, biMeans.minVal, biMeans.maxVal, biMeans.GaussGraph, logFileWriter );

        assert GaussFileWriter != null;
        GaussFileWriter.write("** The selected threshold value is: " + biMeans.BiGaussThrVal + " **" + "\n");
        biMeans.printBestFitGauss(biMeans.bestFitGaussAry, GaussFileWriter);

        biMeans.plotGaussGraph(biMeans.bestFitGaussAry, biMeans.GaussGraph, logFileWriter);

        biMeans.displayGaussGraph(biMeans.GaussGraph, GaussFileWriter);

        biMeans.plotGapGraph(biMeans.histAry, biMeans.bestFitGaussAry, logFileWriter);

        biMeans.dispGapGraph(biMeans.gapGraph, GaussFileWriter);

        inFileReader.close();
        histFileWriter.close();
        logFileWriter.close();
        GaussFileWriter.close();
    }
}
