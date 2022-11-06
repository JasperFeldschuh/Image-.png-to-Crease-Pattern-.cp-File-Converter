# Image-.png-to-Crease-Pattern-.cp-File-Converter
**General Use Cases:**  
Crease patterns are the most common and readilly available way to share the information required to fold other people's designs. Video tutorials and diagrams 
are often not available or locked behind a paywall, but the crease pattern of a model is almost always available for free. Another advantage
of crease patterns is that they reveal the underlyinig structures that went into creating the design. One of the best ways to improve as a designer is to modify
and experiment with the structures found in the crease patterns of other peoples's designs. This kind of experimentation can only be done with origami
drawing softwares such as Oriedita, which can't read image files, but almost universally can read .cp files. Currently, the only way to convert an image 
of a crease pattern into a .cp file is by manually drawing the crease pattern, which is tedious and time consuming. My program aims to automate this process,
converting photos into .cp files that can then be opened by origami drawing sofwares. While still in very early stages, my program has successfully 
converted images of simple box pleated crease patterns to .cp files.  

##  Instructions For Use:
**To run the program, you must first edit the string variable path (line 43) to the appropiate file path on your computer. You also need to change the path
on line 122 in the same way so that the .cp can file generated in a folder on you computer.**   

From there, the program will ask
for the name of the file, the gridsize, if there are partials, and the line thickness (this can be determined by zooming in on an image to the pixel level).
The program will then search for corner points on the image, and mark them black in a duplicate copy automatically created by adding "copy" to the name of
the original file. You need to open this duplicate image and check if it has the right number of corner points marked. If there are too many or too few
points found, the program will try again until all points are found. This process is repeated for edge and center points. Once all line intersections 
have been, you will be prompted to create a name, and a .cp file is generated and put in a preset location (that can be changed by editing line 122).  

##  Test Runs of The Program: 
https://drive.google.com/drive/folders/1QGt5s_2V9C5HFBG5EZi41BmbbeRLqsDX?usp=sharing

