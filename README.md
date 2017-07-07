# languagestudy

###    MP3 to Wav(libmad) :
    
        http://groups.google.com/group/prajnashi
        
        https://github.com/prajnashi/libmad
        
        http://www.badlogicgames.com/wordpress/?p=231

###  libmpg123 ：
  
        http://www.mpg123.de/features.shtml
        
        `https://github.com/tulskiy/camomile`                [Used for decode MP3 to PCM]
        
        https://github.com/libgdx/libgdx
    

###  libmp3lame
  
        `https://github.com/intervigilium/Lame4Android`      [Used for package PCM to Wav file, add write bytes]
        
        https://github.com/intervigilium/libwave
        
        https://github.com/intervigilium/liblame
    

###    Speed Change :
        `http://soundtouch.surina.net`           [Used for change Tempo, Wav to Wav]


###    JNI build(git has a prebuilt share libray , ndk is not nessary) :
        run in Android studio terminal : LanguageListen/app/src/main/jni$ ndk-build

###    Permission :
        SDCard Read 
    
###    Test Env:
        1. Mi 4C, Android 7
        2. Emulator x86 , Android 5
    
###    变速方法(Change Tempo)：
       1. 使用mpg123将mp3， 解码为WAV文件，新的文件名为 文件内容SHA1.wav
       2. 使用soundTouch，改变Tempo，为了减少频繁变动, Tempo只能取6个值（可以调整为连续） ，文件名称 SHA1_[Tempo].wav
       3. 播放改变Tempo之后的文件
       
###    功能说明 ,只支持mp3文件( Feature, Only support MP3) ：
    
        1. 手动创建播放列表 (Create PlayList by manaul , not scan)
        2. 文件选择可以按照整个目录选取 ( select all files in directory)
        3. 保存每个播放列表的进度（PlayList history : json, support by google gson） , 保存路径  ： package name /files/PlayControl.json
        4. 支持变速播放 , 缓存目录 package name /cache ， 解码文件名称为： 文件内容SHA1.wav， 变速文件名称 SHA1_[tempo].wav
            退出播放界面，将自动删除Cache文件
    
   
   
