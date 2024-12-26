# cqadb
Use SSM framework to build community question and answer (CQA)

##Construction of project environment
** * Operating system * *: window 10
* **IDE** ：Eclipse
* **JDK Version** : JDK1.8
** * Web container * *: The default container for SpringBoot integration - Tomcat
** * Database * *: MySQL 5.7
** * Dependency management tool * *: Maven is really convenient to manage jar packages
** * Version control * *: Git
** * Cache * *: Redis

##Database
All database files are in db_ Under the mysql directory, you can fill in data by yourself after importing the database

##Cache
1. All files are located in Redis_ Under the Windows directory
2. Unzip Redis30-windows.rar and start redis-server.exe
3. Client schedule file redis-desktop-manager-0.8.8.384.exe

##Internationalization
#If ($i18n=="zh_CN") Community Question Answers (CQA) # else Community Question Answers (CQA) # end

##Run
To directory D:  jzd_ Java  workspace  cqadb-master  doc Execute java - jar cqadb-master.jar