#include <iostream>
#include <fstream>
#include <string>
using namespace std;

class Person{
    private:
        string name;
        int age;
    public:
        Person(string name, int age){
            this->name = name;
            this->age = age;
        }
        void printPerson(ofstream & ofile){
            ofile << name << " is " << age << " years old. \n";
        }//end print
};//end class Person

int main(int argc, char** argv){
    if(argc != 3){
        cout << "Your command line needs to include two parameters: input file and output file \n";
        exit(1);
    }//end if argc

    ifstream inFile(argv[1]);
    if(!inFile.is_open()){
        cout << "Unable to open the input file" << endl;
        exit(1);
    }

    ofstream outFile(argv[2]);
    if(!outFile.is_open()){
        cout << "Unable to open output file" << endl;
        exit(1);
    }

    int numOfPeople;
    inFile >> numOfPeople;
    outFile << "*** There are " << numOfPeople << " people ***" << "\n";
    Person** people = new Person*[numOfPeople];

    string Tname;
    int Tage;
    int index = 0;
    while(index < numOfPeople){
        inFile >> Tname;
        inFile >> Tage;
        Person* p=new Person(Tname, Tage);
        people[index++] = p;
    }//end while

    for(int index = 0; index < numOfPeople; index++){
        people[index]->printPerson(outFile);
    }//end for

    for(int index = 0; index < numOfPeople; index++){
        delete people[index];
    }

    delete[] people;

    inFile.close();
    outFile.close();
    exit(0);
}//end main