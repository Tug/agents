set LOCAL=.\bin
set JDOM_PATH=E:\jdom-1.0\jdom.jar
set REPAST_PATH="C:\Program Files\Repast 3\Repast J\repast.jar"
set LOCALPATH=%LOCAL%;%JDOM_PATH%;%REPAST_PATH% 
java -cp %LOCALPATH% epfl.lia.logist.LogistPlatform config\default.xml %1
