

# Kaggle Plankton Classification 
Name : Jing Yu
Date:  23/10/2015
jingyu 

## Problem 

We could use classification method to identify the pictures from one plankton at a time. The target is to build an algorithm to automate the image identification process.

## Data Source 
Splitted the test file （20%） and train file (80%) . Data source:  
https://www.kaggle.com/c/datasciencebowl/data

## Method 
The general parseImage referred to the tutorial: 
https://www.kaggle.com/c/datasciencebowl/details/tutorial

Use the sift to extract different features on differnet and applied Random Forest classifier 

#### Parse image 

* Including  thresholding the images,  segmenting images, extracing region properties. I use the region properties, using sift to create features based on the intrinsic properties of the classes. 
        
* Create feaures vectors
   I loop through each of the directories in tain file and iterate every pictures in that set. For each image, I rescale it to 25 x 25 pixels and then add the rescaled pixel values to a feature vector, X. The last feature we include will be our width-to-length ratio. I create the class label in the vector y, which will have the true class label for each row of the feature vector, X.
    
```python
# Read in the images and create the features (in readingImage methods)
    # Only read in the images
            if fileName[-4:] != ".jpg":
              continue
            
            # Read in the images and create the features
            nameFileImage = "{0}{1}{2}".format(fileNameDir[0], os.sep, fileName)            
            image = imread(nameFileImage, as_grey=True)
            files.append(nameFileImage)
            axisratio = getMinorMajorRatio(image)
            image = resize(image, (maxPixel, maxPixel))
```

 * Result:   
    Accuracy of all classes: 43.34%
    
    Accuracy of the test: 45.12%
    
*In a nutshell   
    Adding the rescale image feature could help classification. Because the it provides the original information about the image. And increasing the size of the rescale image could improve the result because it provides more information about the original image.
    
#### Random Forest Classification
    *Big Picture 
A random forest model is an ensemble model of n_estimators number of decision trees. During the training process, each decision tree is grown automatically by making a series of conditional splits on the data. At each split in the decision tree, a random sample of max_features number of features is chosen and used to make a conditional decision on which of the two nodes that the data will be grouped in.

    * Method
I train data consisting of the feature vector X and the class label vector y, then calculate some class metrics for the performance of our model, by class and overall. 

```python
    print "Training"
    # n_estimators is the number of decision trees
    # max_features also known as m_try is set to the default value of the square root of the number of features
    clf = RF(n_estimators=100, n_jobs=3); 
    scores = cross_validation.cross_val_score(clf, X, y, cv=5, n_jobs=1);
    print "Accuracy of all classes"
    print np.mean(scores)
```

RF(n_estimators=100, n_jobs=3) to  identify different number of trees to random generate with square root of the number of features.

The avg accuracy on the 100/200/300  number of trees for 10 times is 
(with calling different paramters to RF) is:  
44.23%,   45.12%,    46.10% .

So as more the random forest trees, the accuracy is increasing. 
But still need to estimate the most suitable trade-off in this case.

    *In a nutshell 
Uisng random forest can enhance the accruacy of the training. But the result will fluctuate, we can get the average of them to estimate. We can spend much more time to tria more trees to achieve more accuracy. 
      
#### SIFT Scale-invariant feature transform 

Reference: 
http://opencv-python-tutroals.readthedocs.org/en/latest/py_tutorials/py_feature2d/py_sift_intro/py_sift_intro.
Using opencv 

    * Big Picture 
Scale-invariant feature transform (or SIFT) is an algorithm in computer vision to detect and describe local features in images. SIFT can robustly identify objects even among clutter and under partial occlusion, because the SIFT feature descriptor is invariant to uniform scaling, orientation, and partially invariant to affine distortion and illumination changes.

    * KeyPoints
Key locations are defined as maxima and minima of the result of difference of Gaussians function applied in scale space to a series of smoothed and resampled images. 

    * Keypoint Descriptor
 I used  16x16 neighbourhood around the keypoint is taken. It is devided into 16 sub-blocks of 4x4 size. For each sub-block, 8 bin orientation histogram is created. So a total of 128 bin values are available(So  the descriptor in the program is 128) It is represented as a vector to form keypoint descriptor. In addition to this, several measures are taken to achieve robustness against illumination changes, rotation etc.
    
    *Keypoint Matching
 In some cases, the second closest-match may be very near to the first. It may happen due to noise or some other reasons. So, thr atio of closest-distance to second-closest distance is taken. If it is greater than 0.8, they are rejected. It eliminaters around 90% of false matches while discards only 5% correct matches, as per the paper.

    * SIFT in OpenCV
So now let’s see SIFT functionalities available in OpenCV. Let’s start with keypoint detection and draw them. First we have to construct a SIFT object

```python
# get feature sift count
def getSiftCount(nameFileImage):
    img = cv2.imread(nameFileImage)
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    sift = cv2.SIFT()
    k, des = sift.detectAndCompute(gray, None)
    if des is None:
        return 0
    else:
        return len(k)
```

    * Result with sift fucntions:   
Accuracy of all classes: 47.12%

    * Conclusion      
Using the sift alorithm (in opencv) can increase the accuracy of the training and testing. The result is better than the avg result from random tree result (from my experiment). 

## Error analysis
    
1. For image itself. 
    The noise of the images, it may contain disturbed info which can  not match the model. We need to filter them. These have not been done in the train yet. 
    
2. More accurate featues need to be used
The featues are just general attribute of parts of pictures, they may not describe the whole pictures completely. 
    
3. Corner Cases: 
   (1) Sharp different pictures amy belong to one type
   (2) Same type may contains sharp different images 
   (3) Different types may belong to different types. 
  
## Future work

1. We can to dig out which number of random forest tree is the most suitale in this training.
2. Can extract more suitable features from images and optimize the sift result. 
3. We can try more machine learning method like lib-svm. 
4. Should add filter layer to filter out the noise from the orginal images.


