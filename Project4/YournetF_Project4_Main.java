import java.io.*;
import java.util.StringTokenizer;

class Morphology{
    public int numImgRows;
    public int numImgCols;
    public int imgMin;
    public int imgMax;
    public int numStructRows;
    public int numStructCols;
    public int structMin;
    public int structMax;
    public static int rowOrigin;
    public static int colOrigin;
    public int rowFrameSize;
    public int colFrameSize;
    public int extraRows;
    public int extraCols;
    public int rowSize;
    public int colSize;
    public int[][] zeroFramedAry;
    public int[][] morphAry;
    public int[][] tempAry;
    public int [][] structAry;

    public Morphology(int numImgRows, int numImgCols, int imgMin, int imgMax, int numStructRows, int numStructCols, int structMin, int structMax, int rowOrigin, int colOrigin){
        this.numImgRows = numImgRows;
        this.numImgCols = numImgCols;
        this.imgMin = imgMin;
        this.imgMax = imgMax;
        this.numStructRows = numStructRows;
        this.numStructCols = numStructCols;
        this.structMin = structMin;
        this.structMax = structMax;
        this.rowOrigin = rowOrigin;
        this.colOrigin = colOrigin;

        this.rowFrameSize = numStructRows / 2;
        this.colFrameSize = numStructCols / 2;

        this.extraRows = rowFrameSize * 2;
        this.extraCols = colFrameSize * 2;

        this.rowSize = numImgRows + extraRows;
        this.colSize = numImgCols + extraCols;

        this.zeroFramedAry = new int[rowSize][colSize];
        this.morphAry = new int[rowSize][colSize];
        this.tempAry = new int[rowSize][colSize];

        this.structAry = new int[numStructRows][numStructCols];

        zero2DAry(zeroFramedAry, rowSize, colSize);
        zero2DAry(morphAry, rowSize, colSize);
        zero2DAry(tempAry, rowSize, colSize);
        zero2DAry(structAry, numStructRows, numStructCols);
    }

    public void zero2DAry(int[][]Ary, int nRows, int nCols){
        for (int i = 0; i < nRows; i++){
            for (int j = 0; j < nCols; j++){
                Ary[i][j] = 0;
            }
        }
    }

    public void loadImg(BufferedReader inFileReader, int[][] zeroFramedAry) throws IOException {
        for(int i = rowOrigin; i < zeroFramedAry.length-1; i++){
            String currentLine = inFileReader.readLine();
            StringTokenizer currentLineTokenizer = new StringTokenizer(currentLine);
            for(int j = colOrigin; j < zeroFramedAry[i].length-1; j++){
                zeroFramedAry[i][j] = Integer.parseInt(currentLineTokenizer.nextToken());
            }
        }
    }

    public void binaryPrettyPrint(int[][] inAry, BufferedWriter prettyPrintFile ) throws IOException {
        prettyPrintFile.write("// " + inAry.length + " " + inAry[0].length + " 0" + " 1" + "\n");
        for(int i = 0; i < inAry.length;i++){
            for(int j = 0; j < inAry[i].length; j++){
                if(inAry[i][j] == 0) prettyPrintFile.write(". ");
                else prettyPrintFile.write("1 ");
            }
            prettyPrintFile.write("\n");
        }
    }

    public void loadStruct(BufferedReader structFile, int[][] structAry) throws IOException {
        for (int i = 0; i < structAry.length; i++){
            StringTokenizer structTokenizer = new StringTokenizer(structFile.readLine());
            for (int j = 0; j < structAry[i].length; j++){
                structAry[i][j] = Integer.parseInt(structTokenizer.nextToken());
            }
        }
    }

    public void process1(BufferedWriter prettyPrintFile) throws IOException {
        String filename = "1st Run Results/dilationOutFile.txt";
        BufferedWriter outfile = new BufferedWriter(new FileWriter(filename));

        zero2DAry(morphAry, rowSize, colSize);
        computeDilation(zeroFramedAry, morphAry);
        aryToFile(morphAry, outfile);
        binaryPrettyPrint(morphAry, prettyPrintFile);

        outfile.close();
    }

    public void computeDilation(int[][] inAry, int[][] outAry){
        int i = rowFrameSize;

        while(i < rowSize){
            int j = colFrameSize;
            while (j < colSize){
                if(inAry[i][j] > 0){
                    onePixelDilation(i, j, inAry, outAry);
                }
                j++;
            }
            i++;
        }
    }

    public void onePixelDilation(int i, int j, int[][] inAry, int[][] outAry){
        int iOffset = i - rowOrigin;
        int jOffset = j - colOrigin;

        int rIndex = 0;

        while (rIndex < numStructRows){
            int cIndex = 0;
            while(cIndex < numStructCols){
                if(structAry[rIndex][cIndex] > 0){
                    outAry[iOffset + rIndex][jOffset + cIndex] = 1;
                }
                cIndex++;
            }
            rIndex++;
        }
    }

    public void aryToFile(int[][] inAry, BufferedWriter outFile) throws IOException {
        outFile.write("// " + numImgRows + " " + numImgCols + " " + imgMin + " " + imgMax + "\n");
        for(int i = 0; i < inAry.length; i++){
            for (int j = 0; j < inAry[i].length; j++){
                outFile.write(inAry[i][j] + " ");
            }
            outFile.write("\n");
        }
    }

    public void process2(BufferedWriter prettyPrintFile) throws IOException {
        String filename = "1st Run Results/erosionOutFile.txt";
        BufferedWriter outfile = new BufferedWriter(new FileWriter(filename));

        zero2DAry(morphAry, rowSize, colSize);
        computeErosion(zeroFramedAry, morphAry);
        aryToFile(morphAry, outfile);
        binaryPrettyPrint(morphAry, prettyPrintFile);

        outfile.close();
    }

    public void computeErosion(int[][] inAry, int[][] outAry){
        int i = rowFrameSize;
        while (i < rowSize){
            int j = colFrameSize;
            while(j < colSize){
                if(inAry[i][j] > 0){
                    onePixelErosion(i, j, inAry, outAry);
                }
                j++;
            }
            i++;
        }
    }

    public void onePixelErosion(int i, int j, int[][] inAry, int[][] outAry){
        int iOffset = i - rowOrigin;
        int jOffset = j - colOrigin;

        boolean matchFlag = true;

        int rIndex = 0;

        while(matchFlag && (rIndex < numStructRows)){
            int cIndex = 0;

            while (matchFlag && (cIndex < numStructCols)){
                if((structAry[rIndex][cIndex] > 0) && (inAry[iOffset+rIndex][jOffset+cIndex] <= 0)){
                    matchFlag = false;
                }
                cIndex++;
            }
            rIndex++;
        }

        if (matchFlag){
            outAry[i][j] = 1;
        }
        else {
           outAry[i][j] = 0;
        }
    }

    public void process3(BufferedWriter prettyPrintFile) throws IOException {
        String filename = "1st Run Results/openingOutFile.txt";
        BufferedWriter outfile = new BufferedWriter(new FileWriter(filename));

        zero2DAry(morphAry, rowSize, colSize);
        computeOpening(zeroFramedAry, morphAry, tempAry);
        aryToFile(morphAry, outfile);
        binaryPrettyPrint(morphAry, prettyPrintFile);

        outfile.close();
    }

    public void computeOpening(int[][] zeroFramedAry, int[][] morphAry, int[][] tempAry){
        computeErosion(zeroFramedAry, tempAry);
        computeDilation(tempAry, morphAry);
    }

    public void process4(BufferedWriter prettyPrintFile) throws IOException {
        String filename = "1st Run Results/closingOutFile.txt";
        BufferedWriter outFile = new BufferedWriter(new FileWriter(filename));

        zero2DAry(morphAry, rowSize, colSize);
        computeClosing(zeroFramedAry, morphAry, tempAry);
        aryToFile(morphAry, outFile);
        binaryPrettyPrint(morphAry, prettyPrintFile);

        outFile.close();
    }

    public void process5(BufferedWriter prettyPrintFile) throws IOException {
        String filename = "1st Run Results/dilationOutFile.txt";
        BufferedWriter outFile = new BufferedWriter(new FileWriter(filename));

        zero2DAry(morphAry, rowSize, colSize);
        computeDilation(zeroFramedAry, morphAry);
        aryToFile(morphAry, outFile);
        binaryPrettyPrint(morphAry, prettyPrintFile);

        outFile.close();

        filename = "1st Run Results/erosionOutFile.txt";
        outFile = new BufferedWriter(new FileWriter(filename));

        zero2DAry(morphAry, rowSize, colSize);
        computeErosion(zeroFramedAry, morphAry);
        aryToFile(morphAry, outFile);
        binaryPrettyPrint(morphAry, prettyPrintFile);

        outFile.close();

        filename = "1st Run Results/openingOutFile.txt";
        outFile = new BufferedWriter(new FileWriter(filename));

        zero2DAry(morphAry, rowSize, colSize);
        computeOpening(zeroFramedAry, morphAry, tempAry);
        aryToFile(morphAry, outFile);
        binaryPrettyPrint(morphAry, prettyPrintFile);

        outFile.close();

        filename = "1st Run Results/closingOutFile.txt";
        outFile = new BufferedWriter(new FileWriter(filename));

        zero2DAry(morphAry, rowSize, colSize);
        computeClosing(zeroFramedAry, morphAry, tempAry);
        aryToFile(morphAry, outFile);
        binaryPrettyPrint(morphAry, prettyPrintFile);

        outFile.close();
    }



    public void computeClosing(int[][] zeroFramedAry, int[][] morphAry, int[][] tempAry){
        computeDilation(zeroFramedAry, tempAry);
        computeErosion(tempAry, morphAry);
    }

}

public class YournetF_Project4_Main {
    public static void main(String[] args) throws IOException {

        //Checks to see if the inFile can be read.
        BufferedReader inFileReader = null;
        try{
            inFileReader = new BufferedReader(new FileReader(args[0]));
        } catch (FileNotFoundException e) {
            System.out.println("Unable to open file '" + args[0] + "'");
        }

        //Checks to see if the structFile can be read.
        BufferedReader structFileReader = null;
        try{
            structFileReader = new BufferedReader(new FileReader(args[1]));
        } catch (FileNotFoundException e) {
            System.out.println("Unable to open file '" + args[1] + "'");
        }

        //Checks to see if the prettyPrintFile can be opened.
        BufferedWriter prettyPrintFile = null;
        try{
            prettyPrintFile = new BufferedWriter(new FileWriter(args[3]));
        } catch (FileNotFoundException e) {
            System.out.println("Unable to open file '" + args[3] + "'");
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

        //Attempts to read the header of the structFile.
        String structHeader = null;
        try {
            assert structFileReader != null;
            structHeader = structFileReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        StringTokenizer structTokenizer = new StringTokenizer(structHeader);
        int numStructRows = Integer.parseInt(structTokenizer.nextToken());
        int numStructCols = Integer.parseInt(structTokenizer.nextToken());
        int structMin = Integer.parseInt(structTokenizer.nextToken());
        int structMax = Integer.parseInt(structTokenizer.nextToken());

        String structOriginsLine = null;
        try{
            assert structFileReader != null;
            structOriginsLine = structFileReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        structTokenizer = new StringTokenizer(structOriginsLine);
        int rowOrigin = Integer.parseInt(structTokenizer.nextToken());
        int colOrigin = Integer.parseInt(structTokenizer.nextToken());

        //Creates an instance of morphology and initializes with the proper constructor values.
        Morphology morphology = new Morphology(numImgRows, numImgCols, imgMin, imgMax, numStructRows, numStructCols, structMin, structMax, rowOrigin, colOrigin);

        morphology.zero2DAry(morphology.zeroFramedAry, morphology.rowSize, morphology.colSize);

        morphology.loadImg(inFileReader, morphology.zeroFramedAry);
        assert prettyPrintFile != null;
        morphology.binaryPrettyPrint(morphology.zeroFramedAry, prettyPrintFile);

        morphology.zero2DAry(morphology.structAry, morphology.numStructRows, morphology.numStructCols);
        morphology.loadStruct(structFileReader, morphology.structAry);
        morphology.binaryPrettyPrint(morphology.structAry, prettyPrintFile);

        int choice = Integer.parseInt(args[2]);

        switch (choice){
            case 1:
                morphology.process1(prettyPrintFile);
            case 2:
                morphology.process2(prettyPrintFile);
            case 3:
                morphology.process3(prettyPrintFile);
            case 4:
                morphology.process4(prettyPrintFile);
            case 5:
                morphology.process5(prettyPrintFile);
        }

        inFileReader.close();
        structFileReader.close();
        prettyPrintFile.close();

    }
}
