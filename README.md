<pre>
   ______                      __________
  / ____/________ _____  ___  / ____/  _/
 / / __/ ___/ __ `/ __ \/ _ \/ /    / /  
/ /_/ / /  / /_/ / /_/ /  __/ /____/ /   
\____/_/   \__,_/ .___/\___/\____/___/   
               /_/                       
</pre>
基于Jenkins的全阶段容器化CI/CD框架（适用于pipeline模式）


### 使用

##### 1、使用`docker in docker`来运行jenkins

详细：[https://github.com/nekoimi/jenkins-dind](https://github.com/nekoimi/jenkins-dind)


##### 2、设置全局共享库

![image](https://github.com/user-attachments/assets/fc4faa6d-96e8-455e-bd18-8d16ecba56db)

仓库地址最好使用自己的私有地址，可以将本项目clone到私有的git仓库

##### 3、特殊插件

除了jenkins默认推荐的插件以外，还需要安装一下插件：

- [minio插件](https://github.com/jenkinsci/minio-plugin) ： 上传打包产物到minio存储

- [企业微信机器人通知插件](https://github.com/jenkinsci/wxwork-notification-plugin) : 发送企业微信消息


##### 4、Jenkinsfile

```groovy
#!groovy

node {
    grapeCI()
}
```

jenkinsfile只需要写固定的入口启动代码，不需要其他代码，grapeCI对应的文件是：[vars/grapeCI.groovy](https://github.com/nekoimi/GrapeCI/blob/master/vars/grapeCI.groovy)，

通过该入口启动框架中的`stage`来完成流水线构建



