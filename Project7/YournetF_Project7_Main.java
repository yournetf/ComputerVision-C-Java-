import java.io.*;
import java.util.Arrays;
import java.util.StringTokenizer;

class Thinning{
    int numRows;
    int numCols;
    int minVal;
    int maxVal;
    int changeCount;
    int cycleCount;
    int[][] aryOne;
    int[][] aryTwo;

    public Thinning(int numRows, int numCols, int minVal, int maxVal){
        this.numRows = numRows;
        this.numCols = numCols;
        this.minVal = minVal;
        this.maxVal = maxVal;
        this.changeCount = 0;
        this.cycleCount = 0;
        this.aryOne = new int[numRows + 2][numCols + 2];
        this.aryTwo = new int[numRows + 2][numCols + 2];
    }

    public void loadImage(BufferedReader inFile, int[][] aryOne) throws IOException {
        for(int i = 1; i < aryOne.length - 1; i++){
            String currentLine = inFile.readLine();
            StringTokenizer currentLineTokenizer = new StringTokenizer(currentLine);
            for(int j = 1; j < aryOne[i].length - 1; j++){
                if(currentLineTokenizer.hasMoreTokens()){
                    aryOne[i][j] = Integer.parseInt(currentLineTokenizer.nextToken());
                }
            }
        }
    }

    public void prettyDotPrint(int[][] aryOne, BufferedWriter outFile1) throws IOException {
        for(int i = 0; i < aryOne.length; i++){
            for(int j = 0; j < aryOne[i].length; j++){
                if(aryOne[i][j] == 0){
                    outFile1.write(".");
                } else if (aryOne[i][j] == 1) {
                    outFile1.write("1");
                }
                outFile1.write(" ");
            }
            outFile1.write("\n");
        }
    }

    public void copyArys(int[][] aryOne, int[][] aryTwo){
        for(int i = 0; i < aryOne.length; i++){
            for(int j = 0; j < aryOne[i].length; j++){
                aryOne[i][j] = aryTwo[i][j];
            }
        }
    }

    public void thinning(int[][] aryOne, int[][] aryTwo, BufferedWriter logFile) throws IOException{
        logFile.write("Entering thinning() before thinning 4 sides, aryOne is below.\n");
        prettyDotPrint(aryOne, logFile);
        changeCount = 0;

        directionalThinning("north", aryOne, aryTwo, logFile);
        logFile.write("after northThinning(); aryTwo is below: ");
        prettyDotPrint(aryTwo, logFile);
        copyArys(aryOne, aryTwo);

        directionalThinning("south", aryOne, aryTwo, logFile);
        logFile.write("after southThinning(); aryTwo is below: ");
        prettyDotPrint(aryTwo, logFile);
        copyArys(aryOne, aryTwo);

        directionalThinning("west", aryOne, aryTwo, logFile);
        logFile.write("after westThinning(); aryTwo is below: ");
        prettyDotPrint(aryTwo, logFile);
        copyArys(aryOne, aryTwo);

        directionalThinning("east", aryOne, aryTwo, logFile);
        logFile.write("after eastThinning(); aryTwo is below: ");
        prettyDotPrint(aryTwo, logFile);
        copyArys(aryOne, aryTwo);

        logFile.write("Leaving thinning(); "
                            + "cycleCount = " + cycleCount
                            + "changeCount = " + changeCount
                     );
    }

    public void directionalThinning(String direction, int[][] aryOne, int[][] aryTwo, BufferedWriter logFile) throws IOException{
        int iOffset = 0;
        int jOffset = 0;
        switch (direction){
            case "north":
                iOffset = -1;
                jOffset = 0;
                break;
            case "south":
                iOffset = 1;
                jOffset = 0;
                break;
            case "west":
                iOffset = 0;
                jOffset = -1;
                break;
            case "east":
                iOffset = 0;
                jOffset = 1;
                break;
        }
        logFile.write("Entering " + direction + "Thinning(); cycleCount = " + cycleCount + " changeCount = " + changeCount + "\n");
        int i = 1;
        while(i < numRows + 2){
            int j = 1;
            while(j < (numCols + 2)){
                if(aryOne[i][j] > 0 && aryOne[i + iOffset][j + jOffset] == 0){
                    int nonZeroCount = countNonZeroNeighbors(aryOne, i, j);
                    boolean flag = checkConnector(aryOne, i, j);
                    logFile.write(  "In "
                            + direction + "Thinning();"
                            + "i = " + i
                            + " j = " + j
                            + " nonZeroCount = " + nonZeroCount
                            + " Flag = " + flag
                            + "\n");
                    if(nonZeroCount >= 4 && !flag){
                        aryTwo[i][j] = 0;
                        prettyDotPrint(aryTwo, logFile);
                        changeCount++;
                    } else {
                        aryTwo[i][j] = aryOne[i][j];
                    }
                }
                j++;
            }
            i++;
        }
        logFile.write("Leaving " + direction + "Thinning();"
                            + "cycleCount = " + cycleCount
                            + "changeCount = " + changeCount
                            + "\n");
    }

    public int countNonZeroNeighbors(int[][] ary, int i, int j){
        int count = 0;
        for(int r = i-1; r < i+2; r++){
            for(int c = j-1; c < j+2; c++){
                if(r==i && c==j) {
                    continue;
                }
                else if (ary[r][c] != 0) {
                    count++;
                }
            }
        }
        return count;
    }

    public boolean checkConnector(int[][] ary, int i , int j){
        if( (ary[i-1][j] == 0 && ary[i+1][j] == 0)
                || (ary[i][j-1] == 0 && ary[i][j+1] == 0)
                || (ary[i-1][j] == 0 && ary[i][j-1] == 0 && ary[i-1][j-1] == 1)
                || (ary[i-1][j] == 0 && ary[i][j+1] == 0 && ary[i+1][j+1] == 1)
                || (ary[i][j-1] == 0 && ary[i+1][j] == 0 && ary[i+1][j-1] == 1)
                || (ary[i+1][j] == 0 && ary[i][j+1] == 0 && ary[i+1][j+1] == 1)
        ){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public String toString() {
        return "Thinning{" +
                "numRows=" + numRows +
                ", numCols=" + numCols +
                ", minVal=" + minVal +
                ", maxVal=" + maxVal +
                ", changeCount=" + changeCount +
                ", cycleCount=" + cycleCount +
                '}';
    }
}

public class YournetF_Project7_Main {
    public static void main(String[] args) throws IOException {

        BufferedReader inFile = null;
        try{
            inFile = new BufferedReader(new FileReader(args[0]));
        } catch (FileNotFoundException e) {
            System.out.println("Unable to read file: " + args[0]);
        }

        BufferedWriter outFile1 = null;
        try{
            outFile1 = new BufferedWriter(new FileWriter(args[1]));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BufferedWriter logFile = null;
        try{
            logFile = new BufferedWriter(new FileWriter(args[2]));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Attempts to read the header of the inFile.
        String inFileHeader = null;
        try {
            assert inFile != null;
            inFileHeader = inFile.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Checks the header and assigns the proper values to the Thinning class.
        StringTokenizer inFileTokenizer = new StringTokenizer(inFileHeader);
        int numRows = Integer.parseInt(inFileTokenizer.nextToken());
        int numCols = Integer.parseInt(inFileTokenizer.nextToken());
        int minVal = Integer.parseInt(inFileTokenizer.nextToken());
        int maxVal = Integer.parseInt(inFileTokenizer.nextToken());

        //Attempts to write image header to the outFile1.txt
        try {
            outFile1.write("input image header\n");
            outFile1.write(numRows + " " + numCols + " " + minVal + " " + maxVal + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Creates an instance of thinning and uses constructor to initialize attributes.
        Thinning thinning = new Thinning(numRows, numCols, minVal, maxVal);

        //Loads image into from inFile into aryOne.
        thinning.loadImage(inFile, thinning.aryOne);
        thinning.copyArys(thinning.aryTwo, thinning.aryOne);
        try{
            outFile1.write( "In main(), before thinning, changeCount = " + thinning.changeCount
                                + "; cycleCount = " + thinning.cycleCount
                                + "\n");
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

        thinning.prettyDotPrint(thinning.aryOne, outFile1);

        thinning.changeCount = 1;
        while(thinning.changeCount > 0){
            thinning.thinning(thinning.aryOne, thinning.aryTwo, logFile);

            thinning.cycleCount++;

            try{
                outFile1.write("In main(), inside iteration; "
                        + "changeCount = " + thinning.changeCount
                        + "cycleCount = " + thinning.cycleCount
                        + "\n"
                );
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }

            thinning.prettyDotPrint(thinning.aryOne, outFile1);
        }

        outFile1.write("In main(), the final skeleton; "
                        + " changeCount = " + thinning.changeCount
                        + " cycleCount = " + thinning.cycleCount
                        + "\n"
                      );

        thinning.prettyDotPrint(thinning.aryOne, outFile1);

        //Attempts to close the files that were opened for reading/writing.
        try {
            inFile.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            outFile1.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            logFile.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
