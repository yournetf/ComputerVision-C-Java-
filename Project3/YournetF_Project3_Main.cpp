#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
using namespace std;

class Enhancement{
    public:
        int numRows;
        int numCols;
        int minVal;
        int maxVal;
        int maskRows;
        int maskCols;
        int maskMin;
        int maskMax;
        int newMin;
        int newMax;
        int thrVal;
        int** mirrorFmAry;
        int** avgAry;
        int** medianAry;
        int** GaussAry;
        int** thrAry;
        int** mask2DAry;
        int* mask1DAry;
        int maskWeight;
        int* neighbor1DAry;

    int loadMask(ifstream& maskFile, int** mask2DAry,ofstream& logFile){
        logFile << "Entering loadMask()";
        logFile << "\n";
        logFile << "Loading array into mask2DAry... ";
        logFile << "\n";
        int weight = 0;
        for(int i = 0; i < maskRows; i++){
            for(int j = 0; j < maskCols; j++){
                maskFile >> mask2DAry[i][j];
                weight += mask2DAry[i][j];
                logFile << mask2DAry[i][j] << " ";
            }
            logFile << "\n";
        }
        logFile << "Loaded.";
        logFile << "\n";
        logFile << "Leaving loadMask() and returning maskWeight: " << weight;
        logFile << "\n";
        return weight;
    }

    void loadMask2Dto1D(int** mask2DAry, int* mask1DAry, ofstream& logFile){
        logFile << "Entering loadMask2Dto1D()";
        logFile << "\n";
        logFile << "Loading mask2DAry into mask1DAry...";
        logFile << "\n";
        for(int i = 0; i < maskRows; i++){
            for(int j = 0; j < maskCols; j++){
                mask1DAry[i+j] = mask2DAry[i][j];
                logFile << mask1DAry[i+j] << " ";
            }
        }
        logFile << "\n";
        logFile << "Loaded.";
        logFile << "\n";
        logFile << "Leaving loadMask2Dto1D()";
        logFile << "\n";
    }

    void loadImage(ifstream& inFile, int** mirrorFmAry, ofstream& logFile){
        logFile << "Entering loadImage()";
        logFile << "\n";
        logFile << "Loading image into mirrorFmAry...";
        logFile << "\n";
        logFile << "Image: ";
        logFile << "\n";
        for(int i = 0; i < numRows; i++){
            for (int j = 0; j < numCols; j++)
            {
                inFile >> mirrorFmAry[1 + i][1 + j];
                logFile << mirrorFmAry[1 + i][1 + j] << " ";
            }
            logFile << "\n";
        }
        logFile << "has been loaded as mirrorFmArray: ";
        logFile << "\n";
        for(int i = 0; i < (numRows + 2); i++){
            for (int j = 0; j < (numCols + 2); j++)
            {
                logFile << mirrorFmAry[i][j] << " ";
            }
            logFile << "\n";
        }
        logFile << "Leaving loadMask2Dto1D()";
        logFile << "\n";
    }

    void prettyPrint(int** imgAry, ofstream& logFile){
            logFile << "Entering PrettyPrint()" << endl;
            logFile << "Rows: " <<numRows << "\n" << 
                    "Columns: " <<numCols << "\n" <<
                    "Minimum Value: " << minVal << "\n" <<
                    "Maximum Value: " << maxVal << "\n" << endl;
            
            stringstream maxValStream;
            maxValStream << maxVal;
            string maxValString = maxValStream.str();

            int Width = maxValString.length();

            int i = 0;
            while (i < numRows + 2){
                int j = 0;
                while (j < numCols + 2){
                    logFile << imgAry[i][j];

                    stringstream imgAryStream;
                    imgAryStream << imgAry[i][j];
                    string imgAryString = imgAryStream.str();

                    int WW = imgAryString.length();
                    
                    while (WW <= Width){
                        logFile << " ";
                        WW++;
                    }
                    j++;
                }
                logFile << "\n";
                i++;
            }
            logFile << "leaving PrettyPrint()" << endl;
        }

        void computeAvg(int** mirrorFmAry, int** avgAry, ofstream& logFile){
            logFile << "** Entering computeMean() **";
            logFile << "\n";
            newMin = 9999;
            newMax = 0;

            int i = 1;
            
            while (i <= numRows){
                int j = 1;

                while (j <= numCols){
                    loadNeighbor2Dto1D(mirrorFmAry, i, j, neighbor1DAry);
                    
                    logFile << "** Below is the conversion of mirrorFmAry[" << i << "][" << j << "] to 1D array **";
                    logFile << "\n";

                    print1DArray(neighbor1DAry, logFile);

                    sort(neighbor1DAry);

                    logFile << "** Below is the sorted neighbor array of [" << i << "][" << j << "]";
                    logFile << "\n";

                    print1DArray(neighbor1DAry, logFile);
                    logFile << "\n";

                    int sum = 0;
                    for (int i = 0; i < 9; i++){
                        sum += neighbor1DAry[i];
                    }
                    
                    if (sum == 0){
                        avgAry[i][j] = 0;
                    }
                    else {
                        avgAry[i][j] = sum/9;
                    }

                    if(newMin > avgAry[i][j]){
                        newMin = avgAry[i][j];
                    }
                    if(newMax < avgAry[i][j]){
                        newMax = avgAry[i][j];
                    }
                    j++;
                }
            i++;
            }
            logFile << "** Below is the avgAry **";
            logFile << "\n";
            for (int i = 0; i < numRows + 2; i++)
            {
                for (int j = 0; j < numCols + 2; j++)
                {
                    logFile << avgAry[i][j] << "  ";
                }
                logFile << "\n";
            }
            
            logFile << "** Leaving computeMean() method";
        }

        void loadNeighbor2Dto1D(int** mirrorFmAry, int i, int j, int* neighbor1DAry){
            int neighborIndex = 0;
            for (int r = (i - 1); r <= (i + 1); r++){
                for (int c = (j - 1); c <= (j + 1); c++)
                {
                    neighbor1DAry[neighborIndex++] = mirrorFmAry[r][c];
                }
            }    
        }

        void print1DArray(int* Ary, ofstream& logFile){
            logFile << "Entering print1DArray()";
            logFile << "\n";
            for (int i = 0; i < 9; i++){
                logFile << Ary[i] << " ";
            }
            logFile << "Exiting print1DArray()";
            logFile << "\n";
        }

        void sort(int* neighbor1DAry){
            for (int i = 0; i < 9; i++){
                int min = i;
                for (int j = i + 1; j < 9; j++){
                    if (neighbor1DAry[j] < neighbor1DAry[min]){
                        min = j;
                    }
                }
                if (min != i) {
                    swap(neighbor1DAry[min], neighbor1DAry[i]);
                }
            }
        }

        void binThreshold(int** inAry, int** outAry, int thrVal, ofstream& logFile){
            logFile << "Entering binaryThreshold()";
            logFile << "\n";

            int i = 0;
            int j = 0;

            while (i < numRows + 2){
                j = 0;
                while (j < numCols + 2){
                    if(inAry[i][j] >= thrVal){
                        outAry[i][j] = 1;
                    }
                    else{
                        outAry[i][j] = 0;
                    }
                    j++;
                }
                i++;
            }
            logFile << "Exiting binaryThreshold()";
            logFile << "\n";
        }

        void computeMedian(int** mirrorFmAry, int** medianAry, ofstream& logFile){
            logFile << "** Entering computeMedian() method **";
            newMin = 9999;
            newMax = 0;

            int i = 1;
            
            while (i <= numRows){
                int j = 1;
                while (j <= numCols){
                    loadNeighbor2Dto1D(mirrorFmAry, i, j, neighbor1DAry);

                    logFile << "** Below is conversion of mirrorFmAry[" << i << "][" << j << "] to 1D array";
                    print1DArray(neighbor1DAry, logFile);

                    sort(neighbor1DAry);

                    logFile << "** Below is the sorted neighbord array of [" << i << "][" << j << "]";
                    logFile << "\n";

                    print1DArray(neighbor1DAry, logFile);

                    medianAry[i][j] = neighbor1DAry[4];

                    if(newMin > medianAry[i][j]){
                        newMin = medianAry[i][j];
                    }
                    if(newMax < medianAry[i][j]){
                        newMax = medianAry[i][j];
                    }
                    j++;
                }
                i++;
            }
            logFile << "** Leaving computeMedian method";
            logFile << "\n";
        }

        void prettyPrint2DMask(int** imgAry, ofstream& logFile){
            logFile << "Entering PrettyPrint2DMask()" << endl;
            logFile << "Rows: " << maskRows << "\n" << 
                    "Columns: " << maskCols << "\n" <<
                    "Minimum Value: " << maskMin << "\n" <<
                    "Maximum Value: " << maskMax << "\n" << endl;
            
            stringstream maxValStream;
            maxValStream << maskMax;
            string maxValString = maxValStream.str();

            int Width = maxValString.length();

            int i = 0;
            while (i < maskRows){
                int j = 0;
                while (j < maskCols){
                    logFile << imgAry[i][j];

                    stringstream imgAryStream;
                    imgAryStream << imgAry[i][j];
                    string imgAryString = imgAryStream.str();

                    int WW = imgAryString.length();
                    
                    while (WW <= Width){
                        logFile << " ";
                        WW++;
                    }
                    j++;
                }
                logFile << "\n";
                i++;
            }
            logFile << "leaving PrettyPrint2DMask()" << endl;
        }

        void computeGauss(int** mirrorFmAry, int** GaussAry, ofstream& logFile){
            logFile << "** Entering computeGauss() method";
            logFile << "\n";

            newMin = 9999;
            newMax = 0;

            int i = 1;
            
            while (i <= numRows){
                int j = 1;
                while (j <= numCols){
                    loadNeighbor2Dto1D(mirrorFmAry, i, j, neighbor1DAry);

                    logFile << "** Below is conversion of mirrorFmAry[" << i << "][" << j << "] to 1D Array";
                    logFile << "\n";

                    print1DArray(neighbor1DAry, logFile);

                    logFile << "** Below is the mask1DAry";
                    logFile << "\n";

                    print1DArray(mask1DAry, logFile);

                    GaussAry[i][j] = convolution(neighbor1DAry, mask1DAry, logFile);

                    if(newMin > GaussAry[i][j]){
                        newMin = GaussAry[i][j];
                    }
                    if (newMax < GaussAry[i][j])
                    {
                        newMax = GaussAry[i][j];
                    }
                    j++;
                }
                i++;
            }
            logFile << "Leaving computeGauss() method";
            logFile << "\n";
        }

        int convolution(int* neighbor1DAry, int*mask1DAry, ofstream& logFile){
            logFile << "Entering convolution() method";
            logFile << "\n";

            int result = 0;
            int i = 0;

            while (i < 9){
                result += neighbor1DAry[i] * mask1DAry[i];
                i++;
            }

            result = result / maskWeight;

            logFile << "Leaving convolution() method, convolution maskWeight = "<< maskWeight << " result/maskWeight = " << result;
            logFile << "\n";

            return result;
        }
    
};

int main(int argc, char** argv){
    //Checks to see if the number of passed arguments is correct.
    if(argc != 8){
        cout << "Your command line need to include 7 parameters exactly." << endl;
        exit(1);
    }

    //Initializes references to files that are to read/written, and checks if they can be opened.
    ifstream inFile(argv[1]);
    if(!inFile.is_open()){
        cout << "Unable to open: " << argv[1];
        exit(1);
    }

    ifstream maskFile(argv[2]);
    if(!maskFile.is_open()){
        cout << "Unable to open: " << argv[2];
        exit(1);
    }

    ofstream AvgFile(argv[4]);
    if(!AvgFile.is_open()){
        cout << "Unable to open: " << argv[4];
        exit(1);
    }

    ofstream MedianFile(argv[5]);
    if(!MedianFile.is_open()){
        cout << "Unable to open: " << argv[5];
        exit(1);
    }

    ofstream GaussFile(argv[6]);
    if(!GaussFile.is_open()){
        cout << "Unable to open: " << argv[6];
        exit(1);
    }

    ofstream logFile(argv[7]);
    if(!logFile.is_open()){
        cout << "Unable to open: " << argv[7];
        exit(1);
    }

    //Assigns thrVal to the integer conversion of the second argument passed.
    int thrVal = atoi(argv[3]);

    //Creates and instance of enhancement class.
    Enhancement* enhancement = new Enhancement;

    //Assigns the image header within the img text file to the attribute of the enhancement instance.
    inFile >> enhancement->numRows;
    inFile >> enhancement->numCols;
    inFile >> enhancement->minVal;
    inFile >> enhancement->maxVal;

    //Assigns the mask header within the mask text file to the attributes of the enhancement instance.
    maskFile >> enhancement->maskRows;
    maskFile >> enhancement->maskCols;
    maskFile >> enhancement->maskMin;
    maskFile >> enhancement->maskMax;

    //Initializes the pointer for each 2D array of size [numRows + 2][numCols + 2]
    enhancement->mirrorFmAry = new int*[(enhancement->numRows) + 2];
    enhancement->avgAry = new int*[(enhancement->numRows) + 2];
    enhancement->medianAry = new int*[(enhancement->numRows) + 2];
    enhancement->GaussAry = new int*[(enhancement->numRows) + 2];
    enhancement->thrAry = new int*[(enhancement->numRows) + 2];

    //Allocates memory for each 2D array above.
    for(int i = 0; i < (enhancement->numRows) + 2; i++){
        enhancement->mirrorFmAry[i] = new int[enhancement->numCols];
        enhancement->avgAry[i] = new int[enhancement->numCols];
        enhancement->medianAry[i] = new int[enhancement->numCols];
        enhancement->GaussAry[i] = new int[enhancement->numCols];
        enhancement->thrAry[i] = new int[enhancement->numCols];
    }

    //Initializes every values of the 2D arrays above to 0.
    for (int i = 0; i < (enhancement->numRows) + 2; i++){
        for (int j = 0; j < (enhancement->numCols) + 2; j++){
            enhancement->mirrorFmAry[i][j] = 0;
            enhancement->avgAry[i][j] = 0;
            enhancement->medianAry[i][j] = 0;
            enhancement->GaussAry[i][j] = 0;
            enhancement->thrAry[i][j] = 0;
        }
    }

    //Initializes pointer and dynamically allocates mask2DAry.
    enhancement->mask2DAry = new int*[enhancement->maskRows];
    for(int i = 0; i < enhancement->maskRows; i++){
        enhancement->mask2DAry[i] = new int[enhancement->maskCols];
    }

    //Allocates mask1DAry to an int array of size 9.
    enhancement->mask1DAry = new int[9]; 

    //Allocates mask1DAry to an int array of size 9.
    enhancement->neighbor1DAry = new int[9];

    //Calls the loadMask(...) function and assigns the maskWeight attribute to the result.
    enhancement->maskWeight = enhancement->loadMask(maskFile, enhancement->mask2DAry, logFile);
    
    //Calls the loadMask2Dto1D(...) function to convert the mask2DAry into a mask1DAry.
    enhancement->loadMask2Dto1D(enhancement->mask2DAry, enhancement->mask1DAry, logFile);

    //Calls the loadImage(...) function to load the image into the mirrorFmAry.
    enhancement->loadImage(inFile, enhancement->mirrorFmAry, logFile);

    AvgFile << "** Below is the mirror framed input image. **";
    AvgFile << "\n";

    //Calls the prettyPrint method so that the mirror framed imaged can be printed into avgFile. 
    enhancement->prettyPrint(enhancement->mirrorFmAry, AvgFile);
    AvgFile << "\n";

    //Calls computeAvg, so that the avgAry can represent the actual averages in relation to each ealements neighbors.
    enhancement->computeAvg(enhancement->mirrorFmAry, enhancement->avgAry, logFile);

    AvgFile << "** Below is the 3x3 averaging of the input image. **";
    AvgFile << "\n";

    //Calls prettyPrint so the new avgAry image can be displayed into AvgFile.
    enhancement->prettyPrint(enhancement->avgAry, AvgFile);
    AvgFile << "\n";

    //Calls binaryThreshold method to convert the avgAry into a binaryThr image.
    enhancement->binThreshold(enhancement->avgAry, enhancement->thrAry, thrVal, logFile);

    AvgFile << "** Below is the result of the binary threshold of averaging method **";
    AvgFile << "\n";

    //Calls prettyPrint to print the binaryThr image into the AvgFile.
    enhancement->prettyPrint(enhancement->thrAry, AvgFile);

    MedianFile << "** Below is the mirror framed input image **";
    MedianFile << "\n";

    //Calls prettyPrint to print the original mirror framed input image into MedianFile.
    enhancement->prettyPrint(enhancement->mirrorFmAry, MedianFile);
    
    //Calls computeMedian, so that medianAry can represent the actual medians in relation to each elements neighbors.
    enhancement->computeMedian(enhancement->mirrorFmAry, enhancement->medianAry, logFile);

    MedianFile << "** Below is the 3x3 median filter of the input image. **";
    MedianFile << "\n";

    //Calls prettyPrint so the new medianAry image can be displayed to the MedianFile.
    enhancement->prettyPrint(enhancement->medianAry, MedianFile);

    //Calls binThreshold method to convert the medianAry into binaryThr image.
    enhancement->binThreshold(enhancement->medianAry, enhancement->thrAry, thrVal, logFile);

    MedianFile << "** Below is the result of the binary threshold of median filtered image. **";
    MedianFile << "\n";

    //Calls prettyPrint to print the binaryThr image into the MedianFile.
    enhancement->prettyPrint(enhancement->thrAry, MedianFile);

    GaussFile << "** Below is the mirror framed input image. **";
    GaussFile << "\n";

    //Calls prettyPrint to print the orginal framed input into GaussFile.
    enhancement->prettyPrint(enhancement->mirrorFmAry, GaussFile);

    GaussFile << "** Below is the mask for the Gaussian filter **";
    GaussFile << "\n";

    //Calls prettyPrint to print the original mask for the Gaussian filter. 
    enhancement->prettyPrint2DMask(enhancement->mask2DAry, GaussFile);

    //Calls computeGauss, so that the GaussAry can represent the actual Gaussian values in relation to each elements neighbors.
    enhancement->computeGauss(enhancement->mirrorFmAry, enhancement->GaussAry, logFile);

    GaussFile << "** Below is the 3x3 Gaussian filter of the input image. **";
    GaussFile << "\n";

    //Calls prettyPrint to print the newly filtered GaussAry into the GaussFile. 
    enhancement->prettyPrint(enhancement->GaussAry, GaussFile);

    //Calls binaryThreshild method to convert the GaussAry into the binaryThr image.
    enhancement->binThreshold(enhancement->GaussAry, enhancement->thrAry, thrVal, logFile);

    GaussFile << "** Below is the result of the binary threshold of Gaussian filtered image. **";
    GaussFile << "\n";

    //Calls prettyPrint to print the binaryThr image into the GaussFile.
    enhancement->prettyPrint(enhancement->thrAry, GaussFile);


    inFile.close();
    maskFile.close();
    AvgFile.close();
    MedianFile.close();
    GaussFile.close();
    logFile.close();
}