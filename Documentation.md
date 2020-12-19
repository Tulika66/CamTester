## CamTester: Documentation


### Java Class files 



- MainAcivity.java 
This activity will send data to the CropActivity.class for processing images and MainActivityGallery for gallery view of extracted and processed images.
The onCreate method of the activity , upon launch of the application initialises the fields and buttons , loads any previously saved instance of the app (if any). Each component awaits events.After parsing the mandatory meta-data (failure of which does not allow the user to proceed further), depending on the action by the user, one of the buttons are fired `.Btntake` and btnSelect through the intent sends data to the `CropActivity.java` , meanwhile galleryview  activates the `MainActivityGallery.java` upon `onClick` event.





- CropActivity.java
This activity receives data from `MainActivity`, processes them and stores them in the device’s storage.
If the permissions asked during the installation of the app are granted, and depending upon the code via the intent, the state of the boolean variable(mFromAlbum) is set and the function `selectphoto()` is fired. Using the state of the variable, `selectphoto()` will either pick an image from device’s storage or launches camera.Upon  onResultActivity, Images are extracted and cropped , dimensions are fixed through the `calculateSampleSize()` , and are written via the `saveImage()` function.





- MainActivityGallery.java 
This activity is activated from the onClick event of the galleryview button of `MainActivity` . The permissions for reading Media Storage are checked and the files are loaded , Adapter is loaded and images are set to preview in a grid view.




### Layout Files 

- activity_main.xml  
   This layout Sets the views for the homepage of the app
With textureTag (input for material metadata ), `btn_select` and `btn_take` (for selecting images from the media.storage and using camera ) , `iv_view` the **ImageView** which displays the cropped image received , `view_gallery` button for displaying the local collection of a users materials.





- activity_crop.xml

   This layout Sets the cropping image with , with `btn_ok` and `btn_cancel` and `CropImageView` displaying the current status of the image under cropping consideration.


- activity_main_gallery.xml

   This layout contains the `GridView` of all the cached local images via the app in the device’s storage in a grid like fashion.

- activity_view_image.xml 

  This layout sets the imageview preview 


- grid_item.xml

  This layout pertains to each single imageview displayed in the gallery view of the app. 






### Android Manifest file

This file contains the essential information about the app to the Android build tools, the Android operating system.The components of the app, which include all activities, and content providers. Each component defines basic properties such as the name of its Java class, and intent filters that describe how the component can be started.

 

### Build.grade 
This file contains all the dependencies required and the related configuration details. 









