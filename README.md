# UpdateFun更新库

UpdateFun是一个[fir.im](http://fir.im/)的Android更新下载模块，在[fir.im]上上传自己的APP后接入该库即可实现检查更新下载

<img src="showUI/1.png" width="256"/>
<img src="showUI/2.png" width="256"/>


##用法

###Step 1

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

###Step 2

在Application（或者主界面activity中）的onCreate()中加上以下语句进行初始化：

```
UpdateKey.API_TOKEN = "写上你fir.im账号的API_TOKEN";
UpdateKey.RELEASE_ID = "写上APP的RELEASE_ID";
UpdateFunGO.init(this);
```

###Step 3

在主界面Activity的onResume()中加上以下语句：

```
 @Override
    protected void onResume() {
        super.onResume();
        UpdateFunGO.onResume(this);
    }
```

大功告成，好好享用吧

## LICENSE

[GPL v3](LICENSE)