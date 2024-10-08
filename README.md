# Transcoding

#### 介绍

1. 支持以下几种编码之间的相互转换：GB2312、GBK、GB18030、UTF-8、UTF-16LE、UTF-16BE、UTF-8BOM。
2. 支持编码自动识别。可自动识别文件的编码类型。
3. 支持批量转换。可一次转换多个文件的编码格式。
4. 支持整个项目直接转码。可将整个开发项目/工作空间一键转码。
5. 安全转码。遵循先复制后转码原则，不会损坏原始文件。
6. 支持在软件中预览文件。可在软件中预览将要转码的文件。

#### 安装教程

- 方式一：项目已打包成Windows平台的exe可执行文件 Transcoding_v0.7.1.exe ，常规安装即可。
- 方式二：项目提供免安装解压版 Transcoding_v0.7.1.7z（Windows平台），解压后双击 Transcoding.exe 即可运行。
- 方式三：项目提供Java字节码文件Jar包 Transcoding_v0.7.1.jar 。理论上，在任何装有Java运行时的操作系统上（Windows、Mac OS、Linux）都可以运行该文件。
- 安装源：https://github.com/nokbita/Transcoding/releases 。
- 使用教程：https://www.bilibili.com/video/BV18keGeaEzw/?spm_id_from=333.999.0.0 。

#### 其他

1. 支持的文件扩展名：txt，html，htm，asp，asa，aspx，asax，shtml，cpp，cxx，c，h，rc，pl，pm，cgi，php，java，jsp，js，vbs，vb，css，xml，csproj，xaml，cs，py，rb，erb，rhtml，text，markdown，md，sql.
2. 特色：
    - 支持拖拽导入；
    - 支持单击列表文件查看文件路径；
    - 支持双击列表文件"源文件"列打文件；
    - 支持查看最近一次转码日志；
    - 等等
3. 策略：本程序会将用户导入的文件或文件夹复制一份进行转码，并不会对原始文件产生破坏。
4. 注意：空文件、无扩展名、不支持的扩展名、不支持的文件编码默认复制，不进行转码。关于追加导入，虽然支持，但不建议这么做，因为有一些限制：如果追加导入同名文件，转码后，后导入的文件将覆盖前导入的文件；如果追加导入的是不同根目录下的文件夹，程序将不能正确转码。 **综上所述，该软件的最佳使用方法有两种：** 1导入文件夹，然后转码（不追加导入）：2导入文件，然后转码（可选的追加导入不同名文件）。

#### 运行展示
![主界面](https://user-images.githubusercontent.com/47719299/121901047-a7be1300-cd58-11eb-8206-6e44c3362ef7.png)


#### 技术实现
- 语言：Java
- GUI：JavaFx

- 读取文件前几位二进制编码识别文件编码类型。
- 根据Java内置字节流接口实现文件转码。

#### 更新预告

1. 无扩展名转码的支持；
2. 优化追加方案；
3. 优化软件性能；
4. ...


#### 更新日志：

v0.7.1，2021.01.19

    1. 修复若干bug

v0.7，2021.01.17

    1. 支持图形化操作界面；
    2. 增加若干辅助功能。

v0.5.1，2020.12.31

    1. （Console，该版本未公开）
    2. 新增29种文件扩展名的支持，目前共支持35种文件扩展名，分别是：txt;html;htm;asp;asa;aspx;asax;shtml;cpp;cxx;c;h;rc;pl;pm;cgi;php;java;jsp;js;vbs;vb;css;xml;csproj;xaml;cs;py;rb;erb;rhtml;text;markdown;md;sql；
    3. 修复对部分文件无法识别扩展名的错误；
    4. 修复对部分文件无法转码的错误；
    5. 优化算法。

v0.5，2020.12.18

    1. （Console）
    2. 软件问世。
    
