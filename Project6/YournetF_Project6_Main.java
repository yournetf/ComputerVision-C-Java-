import java.io.*;
import java.util.StringTokenizer;

class Property{
    public int label;
    public int numPixels;
    public int minR;
    public int minC;
    public int maxR;
    public int maxC;
}

class ccLabel{
    public int numRows;
    public int numCols;
    public int minVal;
    public int maxVal;
    public int newLabel;
    public int trueNumCC;
    public int newMin;
    public int newMax;
    public int[][] zeroFramedAry;
    public int[] nonZeroNeighborAry = new int[5];
    public int[] EQTable;
    public int[] property;

    public void zero2D(int[][] array){
        for(int i = 0; i < array.length; i++){
            for(int j = 0; j < array[i].length; j++){
                array[i][j] = 0;
            }
        }
    }

    public void loadImage(BufferedReader inFile, int[][] zeroFramedAry) throws IOException {
        for (int i = 1; i < zeroFramedAry.length - 1; i++) {
            String currentLine = inFile.readLine();
            StringTokenizer currentLineTokenizer = new StringTokenizer(currentLine);
            for (int j = 1; j < zeroFramedAry[i].length - 1; j++) {
                if (currentLineTokenizer.hasMoreTokens()) {
                    zeroFramedAry[i][j] = Integer.parseInt(currentLineTokenizer.nextToken());
                }
            }
        }
    }

    public void prettyDotPrint(int[][] zeroFramedAry, BufferedWriter prettyPrintFile) throws IOException {
        int cellWidth = Integer.toString(maxVal).length() + 2; // Fixed width for each cell, based on maxVal

        for (int i = 0; i < zeroFramedAry.length; i++) {
            for (int j = 0; j < zeroFramedAry[i].length; j++) {
                if (zeroFramedAry[i][j] != 0) {
                    prettyPrintFile.write(String.format("%" + cellWidth + "d", zeroFramedAry[i][j])); // Print value with padding
                } else {
                    prettyPrintFile.write(String.format("%" + cellWidth + "s", ".")); // Print dot with padding
                }
            }
            prettyPrintFile.write("\n"); // New line at the end of each row
        }
    }


    public void connected4(int[][] zeroFramedAry, int newLabel, int[] EQTable, BufferedWriter prettyPrintFile, BufferedWriter logFile) throws IOException {
        logFile.write("entering connected4 method" + "\n");
        connected4Pass1(zeroFramedAry, newLabel, EQTable, logFile);
        logFile.write("After connected4 pass1, newLabel = " + newLabel + "\n");
        prettyDotPrint(zeroFramedAry, prettyPrintFile);
        printEQTable(newLabel, prettyPrintFile);

        connected4Pass2(zeroFramedAry, newLabel, EQTable, logFile);
        logFile.write("After connected4 pass2, newLabel = " + newLabel + "\n");
        prettyDotPrint(zeroFramedAry, prettyPrintFile);
        printEQTable(newLabel, prettyPrintFile);

//        trueNumCC = manageEQTable(EQTable, newLabel);
//        printEQTable(newLabel, prettyPrintFile);
//        newMin = 0;
//        newMax = trueNumCC;
//        property = new int[trueNumCC + 1];
//        logFile.write("In connected4, after manage EQAry, trueNumCC = " + trueNumCC);

//        Property[] properties = new Property[(numRows*numCols/4)];
//        connected4Pass3(zeroFramedAry, EQTable, properties, trueNumCC, logFile);

        prettyDotPrint(zeroFramedAry, prettyPrintFile);

        printEQTable(newLabel, prettyPrintFile);

        logFile.write("Leaving connected4 method");
    }

    public void connected4Pass1(int[][] zeroFramedAry, int newLabel, int[] EQTable, BufferedWriter logFile) throws IOException {
        logFile.write("Entering connected4Pass1()");
        for (int i = 1; i < zeroFramedAry.length - 1; i++) {
            for (int j = 1; j < zeroFramedAry[i].length - 1; j++) {
                if (zeroFramedAry[i][j] > 0) {
                    // Case 1
                    if (zeroFramedAry[i - 1][j] == 0 && zeroFramedAry[i][j - 1] == 0) {
                        newLabel++;
                        zeroFramedAry[i][j] = newLabel;
                    }
                    // Case 2
                    else if (zeroFramedAry[i - 1][j] != 0 && zeroFramedAry[i - 1][j] == zeroFramedAry[i][j - 1]) {
                        zeroFramedAry[i][j] = zeroFramedAry[i - 1][j];
                    }
                    // Case 3: Conflict case, update EQTable
                    else if (zeroFramedAry[i - 1][j] != zeroFramedAry[i][j - 1] &&
                            (zeroFramedAry[i - 1][j] != 0 || zeroFramedAry[i][j - 1] != 0)) {
                        if (zeroFramedAry[i - 1][j] == 0) {
                            zeroFramedAry[i][j] = zeroFramedAry[i][j - 1];
                        } else if (zeroFramedAry[i][j - 1] == 0) {
                            zeroFramedAry[i][j] = zeroFramedAry[i - 1][j];
                        } else {
                            int minLabel = Math.min(zeroFramedAry[i - 1][j], zeroFramedAry[i][j - 1]);
                            int maxLabel = Math.max(zeroFramedAry[i - 1][j], zeroFramedAry[i][j - 1]);
                            zeroFramedAry[i][j] = minLabel;
                            EQTable[maxLabel] = minLabel;
                            System.out.println("Updated EQTable: " + maxLabel + " -> " + minLabel);
                        }
                        System.out.println(newLabel);
                    }
                }
            }
        }
        logFile.write("Leaving connected4Pass1()");
    }


    public void connected4Pass2(int[][] zeroFramedAry, int newLabel, int[] EQTable, BufferedWriter logFile) throws IOException {
        logFile.write("Entering connected4Pass2()");

        for (int i = zeroFramedAry.length - 1; i > 0; i--) {
            for (int j = zeroFramedAry[i].length - 1; j > 0; j--) {
                if (zeroFramedAry[i][j] > 0) {
                    int p = zeroFramedAry[i][j];
                    int e = zeroFramedAry[i][j + 1];
                    int g = zeroFramedAry[i + 1][j];


                    if ((p != 0 || e != 0 || g != 0) && p != e && p != g && e != g) {
                        int min = Integer.MAX_VALUE;


                        if (p != 0) min = p;
                        if (e != 0 && e < min) min = e;
                        if (g != 0 && g < min) min = g;


                        zeroFramedAry[i][j] = min;


                        if (e != 0 && e != min) {
                            EQTable[Math.max(p, e)] = min;
                        }
                        if (g != 0 && g != min) {
                            EQTable[Math.max(p, g)] = min;
                        }
                    }
                }
            }
        }
        logFile.write("Leaving connected4Pass2()");
    }

    public void connected4Pass3(int[][] zeroFramedAry, int[] EQTable, Property[] CCproperty, int trueNumCC, BufferedWriter logFile) throws IOException {
        logFile.write("Entering connectPass3 method\n");

        for (int i = 1; i <= trueNumCC; i++) {
            CCproperty[i] = new Property();
            CCproperty[i].label = i;
            CCproperty[i].numPixels = 0;
            CCproperty[i].minR = numRows;
            CCproperty[i].maxR = 0;
            CCproperty[i].minC = numCols;
            CCproperty[i].maxC = 0;
        }


        for (int r = 1; r < zeroFramedAry.length - 1; r++) {
            for (int c = 1; c < zeroFramedAry[r].length - 1; c++) {

                if (zeroFramedAry[r][c] > 0) {

                    int k = EQTable[zeroFramedAry[r][c]];
                    zeroFramedAry[r][c] = k;


                    CCproperty[k].numPixels++;
                    if (r < CCproperty[k].minR) {
                        CCproperty[k].minR = r;
                    }
                    if (r > CCproperty[k].maxR) {
                        CCproperty[k].maxR = r;
                    }
                    if (c < CCproperty[k].minC) {
                        CCproperty[k].minC = c;
                    }
                    if (c > CCproperty[k].maxC) {
                        CCproperty[k].maxC = c;
                    }
                }
            }
        }

        logFile.write("Leaving connectPass3 method\n");
    }

    public void printImg(int[][] zeroFramedAry, BufferedWriter labelFile) throws IOException {
        for(int i = 0; i < zeroFramedAry.length; i++){
            for(int j = 0; j < zeroFramedAry[i].length; j++){
                labelFile.write(Integer.toString(zeroFramedAry[i][j]) + " ");
            }
            labelFile.write("\n");
        }
    }


    public void printEQTable(int newLabel, BufferedWriter prettyPrintFile) throws IOException {
        prettyPrintFile.write("Equivalence Table (up to newLabel " + newLabel + "):\n");
        for (int i = 1; i <= EQTable.length-1; i++) {
            if (EQTable[i] != 0) {
                prettyPrintFile.write(EQTable[i] + " ");
            }
        }
        prettyPrintFile.write("\n");
    }



}

public class YournetF_Project6_Main {
    public static void main(String[] args) throws IOException {

        //Checks to see if the inFile can be read.
        BufferedReader inFileReader = null;
        try{
            inFileReader = new BufferedReader(new FileReader(args[0]));
        } catch (FileNotFoundException e) {
            System.out.println("Unable to open file '" + args[0] + "'");
        }

        //Checks to see if the prettyPrintFile can be opened.
        BufferedWriter prettyPrintFile = null;
        try{
            prettyPrintFile = new BufferedWriter(new FileWriter(args[2]));
        } catch (FileNotFoundException e) {
            System.out.println("Unable to open file '" + args[2] + "'");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Checks to see if the labelFile can be opened.
        BufferedWriter labelFile = null;
        try{
            labelFile = new BufferedWriter(new FileWriter(args[3]));
        } catch (FileNotFoundException e) {
            System.out.println("Unable to open file '" + args[3] + "'");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Checks to see if the propertyFile can be opened.
        BufferedWriter propertyFile = null;
        try{
            propertyFile = new BufferedWriter(new FileWriter(args[4]));
        } catch (FileNotFoundException e) {
            System.out.println("Unable to open file '" + args[4] + "'");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Checks to see if the logFile can be opened.
        BufferedWriter logFile = null;
        try{
            logFile = new BufferedWriter(new FileWriter(args[5]));
        } catch (FileNotFoundException e) {
            System.out.println("Unable to open file '" + args[5] + "'");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        //Attempts to read the header of the inFile.
        String inFileHeader = null;
        try {
            assert inFileReader != null;
            inFileHeader = inFileReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Checks the header and assigns the proper values to the Morphology class.
        StringTokenizer inFileTokenizer = new StringTokenizer(inFileHeader);
        int numImgRows = Integer.parseInt(inFileTokenizer.nextToken());
        int numImgCols = Integer.parseInt(inFileTokenizer.nextToken());
        int imgMin = Integer.parseInt(inFileTokenizer.nextToken());
        int imgMax = Integer.parseInt(inFileTokenizer.nextToken());

        int connectedness = Integer.parseInt(args[1]);

        ccLabel ccInstance = new ccLabel();

        ccInstance.numRows = numImgRows;
        ccInstance.numCols = numImgCols;
        ccInstance.minVal = imgMin;
        ccInstance.maxVal = imgMax;

        ccInstance.EQTable = new int[(ccInstance.numRows * numImgCols)/4];

        ccInstance.zeroFramedAry = new int[ccInstance.numRows + 2][ccInstance.numCols + 2];

        ccInstance.newLabel = 0;

        ccInstance.zero2D(ccInstance.zeroFramedAry);

        ccInstance.loadImage(inFileReader, ccInstance.zeroFramedAry);

        if(connectedness == 4){
            ccInstance.connected4(ccInstance.zeroFramedAry, ccInstance.newLabel, ccInstance.EQTable, prettyPrintFile, logFile);
        }
        else if(connectedness == 8){
            //call connected8()
        }

        assert labelFile != null;
        labelFile.write(numImgRows + " " +  numImgCols + " " +  ccInstance.newMin + " " + ccInstance.newMax);
        labelFile.write("\n");
        ccInstance.printImg(ccInstance.zeroFramedAry, labelFile);

        prettyPrintFile.close();
        inFileReader.close();
        logFile.close();
        propertyFile.close();
        labelFile.close();




    }
}
