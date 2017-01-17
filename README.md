# PieSender

## Synopsis
PieSender is an android file manager that allows easily transferring files or directories via wifi:
- Android Device --> Personal Computer
- Personal Computer --> Android Device
- Android Device --> Android Device

The only requirement is for both devices to share the same connection.

## Interest and Reason to be
Android devices do not allow sending files via wifi but through wifi-direct, which is not provided by a wide range of devices. I wanted to make it easy to use wifi for most of android devices to transfer files, considering: 
- most of them can create a wifi hotspot (allowing Android to Android transfers with no need of an intermediate router)  
- all of them can connect to a wifi network (for Android --> Computer and Computer --> Android transfers)  

There already exist some other solutions which I had the chance to try out, such as [Dukto](https://play.google.com/store/apps/details?id=it.msec.dukto&hl=it), but I though I could achieve a better user experience, add a few extra functionalities, and in the meanwhile have fun and gain more insight into the android development world.

## Main Functionalities
- [X] Browse directories from your file system (having informations such as name and size for file system entries)
- [X] Automatic network discovery (A simple network broadcast based protocol to automatically discover all avaiable devices)
- [X] Send a single file
- [X] Send multiple files (in parallel using threads)
- [X] Receive multiple files (in parallel)
- [X] Search top bar for filtering current directory 
- [ ] Web server (By building it respecting the same API contract from my django file server project [cappuccino-server](https://github.com/MattiaPrimavera/cappuccino-server) I can simply make re-use of the client from that project [cappuccino-web](https://github.com/MattiaPrimavera/cappuccino-web) and statically serve that to any browser that can reach the Android device through the network): this would make possible to transfer files from and to a computer with no need of having the Desktop version of the app
- [ ] Extra module for [cappuccino-server](https://github.com/MattiaPrimavera/cappuccino-server) if self-hosted on a RaspBerryPi
- [ ] Tag directories to synchronize
- [ ] Search top bar to look recursively into sub-directories
- [ ] Search top bar REGEX file filtering

## Architectural hints and design patterns
- [Model-View-Presenter Pattern](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93presenter)
- [View Holder Pattern - Efficient view caching ](https://xjaphx.wordpress.com/2011/06/16/viewholder-pattern-caching-view-efficiently/)
- [Recycler View - next generation ListView](https://developer.android.com/reference/android/support/v7/widget/RecyclerView.html)
- File transfer demands handled with a queue (to limit parallel transfers number)
- All transfer threads inform the model of their progress, while transfers view refresh within controlled rate (storing last refresh request timestamp one can ignore further requests within a delay)
- [Services](https://developer.android.com/guide/components/services.html) handle background operations
- Progress bar effect through view overlay (exploting transparence and background color)
- Swipeable interface composed of 3 views: FileManager, Downloads, Uploads


## Some helpful sources to thank
- [Android Databinding: Goodbye Presenter, hello ViewModel!](http://tech.vg.no/2015/07/17/android-databinding-goodbye-presenter-hello-viewmodel/)
- [Android Code That Scales, With MVP](http://engineering.remind.com/android-code-that-scales/)
- [Using an ArrayAdapter with ListView](https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView)
- [AVLoadingIndicatorView](https://github.com/81813780/AVLoadingIndicatorView)  
- [Using lists and grids in Android with RecylerView - Tutorial](http://www.vogella.com/tutorials/AndroidRecyclerView/article.html)
- [Using the RecycleView](https://guides.codepath.com/android/using-the-recyclerview)
- [RecycleView animations](https://www.sitepoint.com/mastering-complex-lists-with-the-android-recyclerview/)
- [Bound Services](http://www.truiton.com/2014/11/bound-service-example-android/)
- [MVP for Android: how to organize the presentation layer](http://antonioleiva.com/mvp-android/)
- [ViewHolder pattern](https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView#improving-performance-with-the-viewholder-pattern)
- [Simple `ListFragment` with `ArrayAdapter`](http://www.tutorialsbuzz.com/2014/05/android-listfragment-using-arrayadapter.html)  
- [Creating a File Browser in Android](http://forum.codecall.net/topic/79689-creating-a-file-browser-in-android/)
- [File Sender and Receiver + File name](http://www.adp-gmbh.ch/blog/2004/november/15.html)
- [Bytes -> Long  Conversion](http://stackoverflow.com/questions/1026761/how-to-convert-a-byte-array-to-its-numeric-value-java)
- [Long -> Bytes Conversion](http://stackoverflow.com/questions/4485128/how-do-i-convert-long-to-byte-and-back-in-java)
- [Network Discovery](http://michieldemey.be/blog/network-discovery-using-udp-broadcast/)
- [Async Task Loaders](https://developer.android.com/reference/android/content/AsyncTaskLoader.html)
- [Check connection available](http://stackoverflow.com/questions/5474089/how-to-check-currently-internet-connection-is-available-or-not-in-android)  
- [Folder](http://www.flaticon.com/free-icon/folder_181524#term=folders&page=1&position=43)
- [File Type Material pack](http://www.flaticon.com/packs/files-3)
