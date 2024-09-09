#include <iostream>
#include <fstream>
using namespace std;

class image{
    private:
        int numRows;
        int numCols;
        int minVal;
        int maxVal;
        int* histAry;
        int** imgAry;
        int thrVal;
    public:

        void loadImage(int** imgAry, ifstream& inFile){
            for(int i = 0; i < numRows; i++){
                for (int j = 0; j < numCols; j++)
                {
                    inFile >> imgAry[i][j];
                }
            }
        }

        void printIMGARY(){
            for(int i = 0; i < numRows; i++){
                for (int j = 0; j < numCols; j++)
                {
                    cout << imgAry[i][j];
                }
                cout << endl;
            }
        }

        void prettyPrint(int** imgAry, ofstream& logFile){
            logFile << "Enter PrettyPrint()" << endl;
            logFile << "Rows: " <<numRows << "\n" << 
                    "Columns: " <<numCols << "\n" <<
                    "Mininmum Value: " << minVal << "\n" <<
                    "Maximum Value: " << maxVal << "\n" << endl;
        }

        void computeHist(int** imgAry, int* histAry, ofstream& logFile){
            
        }

        void printHist(int* histAry, ofstream& histCountFile, ofstream& logFile){

        }

        void dispHist(int* histAry, ofstream& histCountFile, ofstream& logFile){

        }

        void binaryThreshold(int** imgAry, ofstream& binThrFilem, int thrVal, ofstream& logFile){

        }

        int getNumRows() const {
            return numRows;
        }

        void setNumRows(int numRows) {
            this->numRows = numRows;
        }

        int getNumCols() const {
            return numCols;
        }

        void setNumCols(int numCols) {
            this->numCols = numCols;
        }

        int getMinVal() const {
            return minVal;
        }

        void setMinVal(int minVal) {
            this->minVal = minVal;
        }

        int getMaxVal() const {
            return maxVal;
        }

        void setMaxVal(int maxVal) {
            this->maxVal = maxVal;
        }

        int* getHistAry() const {
            return histAry;
        }

        void setHistAry(int* histAry) {
            this->histAry = histAry;
        }

        int** getImgAry() const {
            return imgAry;
        }

        void setImgAry(int** imgAry) {
            this->imgAry = imgAry;
        }

        int getThrVal() const {
            return thrVal;
        }

        void setThrVal(int thrVal) {
            this->thrVal = thrVal;
        }

        
        
        
};//end of image class







int main(int argc, char** argv){
    if(argc != 7){
        cout << "Your command line needs to include exactly 6 parameters" << endl;
        cout << argc << endl;
        exit(1);
    }//end of if argc
    
    //Creates the variables, refering to the arguments passed.
    ifstream inFile(argv[1]);
    ofstream histCountFile(argv[3]);
    ofstream histGraphFile(argv[4]);
    ofstream binThrFile(argv[5]);
    ofstream logFile(argv[6]);

    //Checks to see if all files can be opened, if not, an error message is displayed.
    if(!inFile.is_open()){
        cout << "Unable to open the input file" << endl;
        exit(1);
    }
    if(!histCountFile.is_open()){
        cout << "Unable to open the \'histCountFile\' output file" << endl;
        exit(1);
    }
    if(!histGraphFile.is_open()){
        cout << "Unable to open the \'histGraphFile\' output file" << endl;
        exit(1);
    }
    if(!binThrFile.is_open()){
        cout << "Unable to open the \'binThrFile\' output file" << endl;
        exit(1);
    }
    if(!logFile.is_open()){
        cout << "Unable to open the \'logFile\' output file" << endl;
        exit(1);
    }
    
    //Creates an image instance
    image* img = new image;

    //Initializes the attributes of the input file and attributes them with their respective value. Then loads into the current image instance.
    int numRows, numCols, minVal, maxVal;
    inFile >> numRows;
    inFile >> numCols;
    inFile >> minVal;
    inFile >> maxVal;
    
    img->setNumRows(numRows);
    img->setNumCols(numCols);
    img->setMinVal(minVal);
    img->setMaxVal(maxVal);

    //Creates an imgAry and histAry and then applies them to the image instance
    int** imgAry = new int*[numRows];
    for(int i = 0; i < numRows; ++i) {
        imgAry[i] = new int[numCols];
    }
    int* histAry = new int[maxVal+1];
    for(int i = 0; i < (maxVal + 1);i++){
        histAry[i] = 0;
    }

    //Assigns the empty 2D array and 1D array to the image instance's respective attributes.
    img->setImgAry(imgAry);
    img->setHistAry(histAry);

    //.
    img->loadImage(imgAry, inFile);
    img->prettyPrint(imgAry, logFile);
    img->computeHist(imgAry, histAry, logFile);
    img->printHist(histAry, histCountFile, logFile);
    img->dispHist(histAry, histGraphFile, logFile);

    



    inFile.close();
    histCountFile.close();
    histGraphFile.close();
    binThrFile.close();
    logFile.close();

}//end of main


