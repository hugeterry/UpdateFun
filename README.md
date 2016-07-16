# UpdateFun更新模块

UpdateFun是一个[fir.im](http://fir.im/)的android更新下载模块，输入[fir.im](http://fir.im/)提供的API_TOKEN以及RELEASE_ID后即可实现检查更新下载


<img src="showUI/1.png" width="600" height="400"/>
<img src="showUI/2.png" width="600" height="400"/>


#用法

##Step 1

在gradle文件中加入下面的依赖:

```
dependencies {
    compile 'cn.hugeterry.updatefun:updatefun:1.2.1'
}
```

如果你使用Maven，那么加入下面的依赖：

```
<dependency>
  <groupId>cn.hugeterry.updatefun</groupId>
  <artifactId>updatefun</artifactId>
  <version>1.2.1</version>
  <type>pom</type>
</dependency>
```

##Step 2

在Application的onCreate中加上以下语句进行初始化：

```
//此处填上在http://fir.im/注册账号后获得的API_TOKEN以及APP的RELEASE_ID
UpdateKey.API_TOKEN = "";
UpdateKey.RELEASE_ID = "578a11e7748aac01b7000039";
new UpdateFunGO(this);
```

# LICENSE

[GPL v3](LICENSE)

