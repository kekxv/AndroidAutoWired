# 引入方式


以下配置均在项目`build.gradle`下

方式一

>
> 1. [生成 GitHub Token，教程点击本链接](https://docs.github.com/en/free-pro-team@latest/github/authenticating-to-github/creating-a-personal-access-token)
> 2. 配置 `gradle` ，增加仓库`https://maven.pkg.github.com/kekxv/JavaRepo/` ，github 的仓库需要授权（公开的也要），自己配置一下 `GITHUB_USER`、`GITHUB_PERSONAL_ACCESS_TOKEN`，建议在家目录的`gradle.properties`进行配置。
> ```
> //配置gradle
> android {
>     repositories {
>         maven {
>             name = "GitHubPackages"
>             url = uri("https://maven.pkg.github.com/kekxv/JavaRepo/")
>             credentials {
>                 username = System.getenv('ACCESS_USER') ?: project.properties['GITHUB_USER']
>                 password = System.getenv('ACCESS_PERSONAL_ACCESS_TOKEN') ?: project.properties['GITHUB_PERSONAL_ACCESS_TOKEN']
>             }
>         }
>     }
> }
> ```
> 3. 增加 `dependencies` `implementation 'com.kekxv:autowired:0.2.6'` (`0.2.6`为版本号，可更改为最新版本)
>

方式二

> 
> 直接下载前往 [仓库](https://github.com/kekxv/JavaRepo/packages) 下载`autowired-0.2.6.aar`导入到项目。
> 根据情况，可能需要将`autowired-0.2.6.aar` 拷贝到`libs` 目录并 `implementation fileTree(dir: "libs", include: ["*.jar"])` 更改为`implementation fileTree(dir: "libs", include: ["*.jar","*.aar"])`
>
