# Top-10-Downloader-Android

Image of app in source repo.

This application simulates Apple’s app store where it will display the top downloaded applications by category. It displays the top 10 and top 25 most downloaded free and paid applications as well as the top 10 and 25 most downloaded songs. The application will download the data using an RSS feed provided by Apple which will be converted from an XML format. 

The idea behind this application so to solve a fundamental problem when building phone applications which to execute and run the application without interfering with the rest of the phone’s usability. Most of the time if applications don’t address this issue, if the application requires long performing tasks, they will either have their processes and activities blocked if a user wants to do other tasks and will cease to execute, or they will block other application’s processes and activities on the phone which can freeze of the phone. 

Because downloading data from the internet can have many uncontrolled dependencies such as internet connection quality and data size, it is best to assume the worst case of these two dependencies which would be considered a long performing task that can lead to usability issues. 

If we assume that long performing tasks need execute, then it is of best practice to have these tasks perform on a background thread not on the main UI thread which is shared by all other applications. This is exactly what the application will do. It will run asynchronously which will run downloading tasks in the background so that it cannot interfere with any other tasks that happen to run on the main UI thread. 

Although this application only downloads XML files which does not require a lot of processing time, it will demonstrates the fundamental solution to running long performing tasks which can very well be applied to any other applications fetching heavier data such as video streaming, music downloads, audiobooks etc. 

![alt text](https://github.com/eelizan1/Top-10-Downloader-Android/blob/master/Top10Downloader.png)

