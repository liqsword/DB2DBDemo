# Introduction 
这是一个结合nodeapp_db2db一起运行的独立的Java应用，也可部署运行于Azure webjob。主要功能是监听event hub的消息并同步到自己的mysql数据库里。这里以部署在Azure webjob为例，需要选完成nodeapp_db2db的部署。

# Getting Started
这里以手动部署为例，也可以配置VSTS实现CI/CD.
1.	下载应用到本地
```Bash
git clone https://github.com/radezheng/DB2DBDemo
cd DB2DBDemo
mvn install
```
2.	将下载依赖和打包, 确认生成的jar包与run.bat文件里的一致。此时需要将生成的 jar包和run.bat一起，打包成zip文件，如myjob.zip。run.bat的内容：
```Bash
set PATH=%PATH%;"D:\Program Files\Java\jdk1.8.0_73\bin"
java -cp DB2DB-1.0-SNAPSHOT-jar-with-dependencies.jar com.db2db.App
```

3.	登录Azure portal, 分别将myjob.zip创建为webapp A与B的web作业。确保其成功启动后，部署完成。此时两应用的数据应能实现同步。

# Contribute
详细架构请参见PPT描述