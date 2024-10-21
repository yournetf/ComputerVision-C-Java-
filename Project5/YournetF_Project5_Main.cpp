#include <iostream>
#include <fstream>
#include <string>
#include <sstream>
using namespace std;

class DistanceSkeleton{
    public:
        int numRows;
        int numCols;
        int minVal;
        int maxVal;
        int newMinVal;
        int newMaxVal;
        int** ZFAry;
        int** skeletonAry;
        int distanceChoice;

        void setZero(int** Ary){
            for (int i = 0; i < (numRows + 2); i++){
                for (int j = 0; j < (numCols + 2); j++){
                    Ary[i][j] = 0;
                }
            }
        } 

        void loadImage(ifstream& inFile, int** ZFAry){
            for(int i = 1; i < (numRows + 1); i++){
                for (int j = 1; j < (numCols + 1); j++){
                    inFile >> ZFAry[i][j];
                }
            }
        }

        void prettyPrint(int** ZFAry, ofstream& prettyPrintFile){
            for (int i = 0; i < (numRows + 2); i++){
                for (int j = 0; j < (numCols + 2); j++){
                    prettyPrintFile << ZFAry[i][j] << " ";
                }
                prettyPrintFile << "\n";
            }
            prettyPrintFile << "\n";
        }

        void distanceTransform(int** ZFAry, int distanceChoice, ofstream& prettyPrintFile, ofstream& logFile){
            logFile << "Entering DistanceTransform() method";
            logFile << "\n";

            distancePass1(ZFAry, distanceChoice, logFile);
            prettyPrintFile << "1st pass distance transform with choice = " << distanceChoice << "\n";
            prettyPrint(ZFAry, prettyPrintFile);

            distancePass2(ZFAry, distanceChoice, logFile);
            prettyPrintFile << "2nd pass distance transform with choice = " << distanceChoice << "\n";
            prettyPrint(ZFAry, prettyPrintFile);

            logFile << "Leaving DistanceTransform() method";
            logFile << "\n";
        }

        void distancePass1(int** ZFAry, int distanceChoice, ofstream& logFile){
            for(int i = 0; i < (numRows + 2); i++){
                for (int j = 0; j < (numCols + 2); j++){
                    if(ZFAry[i][j] > 0){
                        switch (distanceChoice)
                        {
                        case 8:
                            ZFAry[i][j] = (min(min(ZFAry[i-1][j-1], ZFAry[i-1][j]), min(ZFAry[i-1][j+1], ZFAry[i][j-1])) + 1);
                            break;
                        case 4:
                            ZFAry[i][j] = (min(min(ZFAry[i-1][j-1]+2, ZFAry[i-1][j]+1), min(ZFAry[i-1][j+1]+2, ZFAry[i][j-1]+1)));
                            break;
                        
                        default:
                            break;
                        }
                    }
                }
            }
        }
        
        void distancePass2(int** ZFAry, int distanceChoice, ofstream& logFile){
            for(int i = (numRows + 1); i  > 0; i--){
                for (int j = (numCols + 1); j > 0; j--){
                    if(ZFAry[i][j] > 0){
                        switch (distanceChoice)
                        {
                        case 8:
                            ZFAry[i][j] = min(min(min(ZFAry[i][j], ZFAry[i][j + 1] + 1), ZFAry[i + 1][j - 1] + 1), min(ZFAry[i + 1][j] + 1, ZFAry[i + 1][j + 1] + 1));
                            break;
                        case 4:
                            ZFAry[i][j] = min(min(min(ZFAry[i][j], ZFAry[i][j + 1] + 1), ZFAry[i + 1][j - 1] + 2), min(ZFAry[i + 1][j] + 1, ZFAry[i + 1][j + 1] + 2));
                            break;
                        
                        default:
                            break;
                        }
                    }
                }
            }
        }

        void compression(int** ZFAry, int distanceChoice, ofstream& skeletonFile, ofstream& prettyPrintFile, ofstream& logFile){
            logFile << "Entering compression() method.";
            logFile << "\n";

            computeLocalMaxima(ZFAry, skeletonAry, distanceChoice, logFile);
            prettyPrintFile << "Local maxima, skeletonAry with choice = " << distanceChoice;
            prettyPrintFile << "\n";
            prettyPrint(skeletonAry, prettyPrintFile);

            extractSkeleton(skeletonAry, skeletonFile, logFile);
            logFile << "In compression() Below is skeleton Array with choice = " << distanceChoice;
            logFile << "\n";
            prettyPrint(skeletonAry, logFile);

            logFile << "Leaving compression() method";
            logFile << "\n";
        }

        void computeLocalMaxima(int** ZFAry, int** skeletonAry, int distanceChoice, ofstream& logFile){
            for(int i = 1; i < (numRows + 1); i++){
                for (int j = 1; j < (numCols + 1); j++){
                    if(isLocalMaxima(ZFAry, i, j)){
                        skeletonAry[i][j] = ZFAry[i][j];
                    }
                    else{
                        skeletonAry[i][j] = 0;
                    }
                }
            }
        }

        bool isLocalMaxima(int** ZFAry, int i, int j) {
            int currentVal = ZFAry[i][j];

            return (currentVal >= ZFAry[i-1][j-1] &&  
                    currentVal >= ZFAry[i-1][j]   &&  
                    currentVal >= ZFAry[i-1][j+1] &&  
                    currentVal >= ZFAry[i][j-1]   &&  
                    currentVal >= ZFAry[i][j+1]   &&  
                    currentVal >= ZFAry[i+1][j-1] &&  
                    currentVal >= ZFAry[i+1][j]   &&  
                    currentVal >= ZFAry[i+1][j+1]);   
        }

        void extractSkeleton(int** skeletonAry, ofstream& skeletonFile, ofstream& logFile){
            skeletonFile << numRows << " " << numCols << " " << minVal << " " << maxVal;
            skeletonFile << "\n";
            for(int i = 0; i < (numRows + 2); i++){
                for(int j = 0; j < (numCols + 2); j++){
                    if(skeletonAry[i][j] > 0) {
                        skeletonFile << i << " ";
                        skeletonFile << j << " ";
                        skeletonFile << skeletonAry[i][j];
                        skeletonFile << "\n";
                    }
                }
            }
        }

        void loadSkeleton(ifstream& skeletonFile, int** ZFAry, ofstream& logFIle){
            int i;
            int j;
            int val;
            
            string line;
            getline(skeletonFile, line);
            while(getline(skeletonFile, line)){
                istringstream iss(line);
                iss >> i;
                iss >> j;
                iss >> val;

                ZFAry[i][j] = val;
            }
        }

        void deCompression(int** ZFAry, int distanceChoice, ofstream& prettyPrintFile, ofstream& logFile){
            logFile << "Entering deCompression() method";
            logFile << "\n";

            expansionPass1(ZFAry, distanceChoice, logFile);
            prettyPrintFile << "1st pass expansion with choice = " << distanceChoice;
            prettyPrintFile << "\n";
            prettyPrint(ZFAry, prettyPrintFile);

            expansionPass2(ZFAry, distanceChoice, logFile);
            prettyPrintFile << "2nd pass expansion with choice = " << distanceChoice;
            prettyPrintFile << "\n";
            prettyPrint(ZFAry, prettyPrintFile);
        }

        void expansionPass1(int** ZFAry, int distanceChoice, ofstream& logFile){
            for(int i = 1; i < (numRows + 1); i++){
                for(int j = 1; j < (numCols + 1); j++){
                    if(ZFAry[i][j] == 0){
                        int maxVal = ZFAry[i][j];
                        switch (distanceChoice)
                        {
                        case 8:
                            for (int row = i - 1; row <= i + 1; row++) {
                                for (int col = j - 1; col <= j + 1; col++) {
                                    maxVal = max(maxVal, ZFAry[row][col]-1);
                                }
                            }
                            ZFAry[i][j] = maxVal;
                            break;
                        case 4:
                            ZFAry[i][j] = max(max(max(max(max(ZFAry[i][j], ZFAry[i][j - 1]-1), 
                            ZFAry[i][j + 1]-1), 
                            ZFAry[i - 1][j - 1]-2), 
                            ZFAry[i - 1][j]-1), 
                            max(ZFAry[i - 1][j + 1]-2, 
                            max(ZFAry[i + 1][j - 1]-2, 
                            max(ZFAry[i + 1][j]-1, 
                            ZFAry[i + 1][j + 1]-2))));
                            break;
                        
                        default:
                            break;
                        }
                    }
                }
            }
        }

        void expansionPass2(int** ZFAry, int distanceChoice, ofstream& logFile){
            for(int i = (numRows); i > 0 ; i--){
                for(int j = (numCols); j > 0 ; j--){
                    int maxVal = ZFAry[i][j];
                    switch (distanceChoice)
                    {
                    case 8:
                        for (int row = i - 1; row <= i + 1; row++) {
                            for (int col = j - 1; col <= j + 1; col++) {
                                maxVal = max(maxVal, ZFAry[row][col]-1);
                            }
                        }
                        ZFAry[i][j] = maxVal;
                    break;
                    case 4:
                        ZFAry[i][j] = max(max(max(max(max(ZFAry[i][j], ZFAry[i][j - 1]-1), 
                        ZFAry[i][j + 1]-1), 
                        ZFAry[i - 1][j - 1]-2), 
                        ZFAry[i - 1][j]-1), 
                        max(ZFAry[i - 1][j + 1]-2, 
                        max(ZFAry[i + 1][j - 1]-2, 
                        max(ZFAry[i + 1][j]-1, 
                        ZFAry[i + 1][j + 1]-2))));
                    break;
                    default:
                        break;
                    }
                }
            }
        }

        void binThreshold(int** ZFAry, ofstream& deCompressedFile){
            for(int i = 0; i < (numRows + 2); i++){
                for(int j = 0; j < (numCols + 2); j++){
                    if(ZFAry[i][j] >= 1){
                        deCompressedFile << "1 "; 
                    }
                    else{
                        deCompressedFile << "0 ";
                    }
                }
                deCompressedFile << "\n";
            }
        }

        


};

int main(int argc, char** argv){
    
    //Creates an instance of each file passed as an argument and makes sure they can be opened.
    ifstream inFile(argv[1]);
    if(!inFile.is_open()){
        cout << "Unable to open: " << argv[1];
        exit(1);
    }

    ofstream prettyPrintFile(argv[3]);
    if(!prettyPrintFile.is_open()){
        cout << "Unable to open: " << argv[3];
        exit(1);
    }

    ofstream skeletonFile(argv[4]);
    if(!skeletonFile.is_open()){
        cout << "Unable to open: " << argv[4];
        exit(1);
    }
    
    ofstream deCompressedFile(argv[5]);
    if(!deCompressedFile.is_open()){
        cout << "Unable to open: " << argv[5];
        exit(1);
    }

    ofstream logFile(argv[6]);
    if(!logFile.is_open()){
        cout << "Unable to open: " << argv[6];
        exit(1);
    }

    //Creates an instance of the distance skeleton class.
    DistanceSkeleton* distanceSkeleton = new DistanceSkeleton;
    
    //Extracts the numRows, numCols, minVal, and maxVal values from the input file.
    inFile >> distanceSkeleton->numRows;
    inFile >> distanceSkeleton->numCols;
    inFile >> distanceSkeleton->minVal;
    inFile >> distanceSkeleton->maxVal;

    //Dynamically allocates ZFAry and skeletonAry to have 2 extra rows and columns.
    distanceSkeleton->ZFAry = new int*[distanceSkeleton->numRows + 2];
    distanceSkeleton->skeletonAry = new int*[distanceSkeleton->numRows + 2];
    for(int i = 0; i < distanceSkeleton->numRows + 2; i++){
        distanceSkeleton->ZFAry[i] = new int[distanceSkeleton->numCols + 2];
        distanceSkeleton->skeletonAry[i] = new int[distanceSkeleton->numCols + 2];
    }

    //Sets distanceChoice to the second argument passed.
    distanceSkeleton->distanceChoice = atoi(argv[2]);

    //Sets al values of ZFAry and skeletonAry to zero.
    distanceSkeleton->setZero(distanceSkeleton->ZFAry);
    distanceSkeleton->setZero(distanceSkeleton->skeletonAry);

    //Loads the image from the inFile into the ZFAry
    distanceSkeleton->loadImage(inFile, distanceSkeleton->ZFAry);

    distanceSkeleton->prettyPrint(distanceSkeleton->ZFAry, prettyPrintFile);

    distanceSkeleton->distanceTransform(distanceSkeleton->ZFAry, distanceSkeleton->distanceChoice, prettyPrintFile, logFile);

    distanceSkeleton->compression(distanceSkeleton->ZFAry, distanceSkeleton->distanceChoice, skeletonFile, prettyPrintFile, logFile);

    skeletonFile.close();

    ifstream skeletonInputFile(argv[4]);
    if(!skeletonInputFile.is_open()){
        cout << "Unable to open: " << argv[4] << " for reading";
        exit(1);
    }

    distanceSkeleton->setZero(distanceSkeleton->ZFAry);

    distanceSkeleton->loadSkeleton(skeletonInputFile, distanceSkeleton->ZFAry, logFile);
    
    prettyPrintFile << "Below is the loaded skeleton with choice = " << distanceSkeleton->distanceChoice;
    prettyPrintFile << "\n";
    distanceSkeleton->prettyPrint(distanceSkeleton->ZFAry, prettyPrintFile);

    distanceSkeleton->deCompression(distanceSkeleton->ZFAry, distanceSkeleton->distanceChoice, prettyPrintFile, logFile);

    deCompressedFile << distanceSkeleton->numRows << " " << distanceSkeleton->numCols << " " << distanceSkeleton->minVal << " " << distanceSkeleton->maxVal;
    deCompressedFile << "\n";

    distanceSkeleton->binThreshold(distanceSkeleton->ZFAry, deCompressedFile);

    skeletonInputFile.close();
    inFile.close();
    prettyPrintFile.close();
    deCompressedFile.close();
    logFile.close();

}