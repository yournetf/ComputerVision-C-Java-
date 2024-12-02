This repository is a collection of projects completed for my Computer Vision class at Queens College.

***Disclaimer***
Each directory has its own "specs" PDF that will list the command line arguments necessary for execution.

Below are the descriptions and visualizations for each project.
-----------------------------------------------------------------------------
Project 1 (C++):
  
  Accepts a grey-scale image as input and then creates a bi-modal histogram based on the information gathered. Using a deepest concavity algorithm, determines the value that is 
  most likely to be the boundary between an object pixel and background pixel. The original image is then converted into a binary image that only displays pixels over this     
  selected threshold value, therefore isolating the object and removing excess noise.
  
(https://www.youtube.com/watch?v=cJ3befJoD4w&ab_channel=FrankYournet)
_____________________________________________________________________________
Project 2 (Java):
  Accepts a histogram representing the the values of a grey-scale image as input. Then, uses this histogram to create Gaussian Curves and display the displacement between the calculated Gaussian Curves and the actual histogram data.

(https://www.youtube.com/watch?v=ICW9jbsZeCA&ab_channel=FrankYournet)
_____________________________________________________________________________
Project 3 (C++):
  Accepts a grey-scale image as input and visualizes the effects of median, average, and Gaussian filtering for noise reduction(Image enhancement). As the files are scrolled through in the video, you should notice the image getting cleaner.

(https://www.youtube.com/watch?v=d2qvpvd_27o&ab_channel=FrankYournet)
_____________________________________________________________________________
Project 4 (Java):
  Accepts a binary image as input, along with a structuring elements. Structuring elements are used to implement dilation, erosion, opening, and closing algorithms. These algorithms are used to clean the noise (salt and peppers) within an image. As the video scrolls through files, you should notice changes in the object pixels but not in the overall structure. The first transition shown is from the "opening" algorithm and is typically the one used for noise supression, but each one has its own beneficial use case.

(https://www.youtube.com/watch?v=1E6GOD2foOM&t=30s&ab_channel=FrankYournet)

_____________________________________________________________________________

