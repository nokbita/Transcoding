package priv.nokbita.util;

import javafx.stage.FileChooser;
import priv.nokbita.entity.TransFile;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

public class TransUtil {

    public static Collection<FileChooser.ExtensionFilter> getExtensionFilter() {
        Collection<FileChooser.ExtensionFilter> collection = new ArrayList<>();

        final String text = "*.txt;";
        final String html = "*.html;*.htm;*.asp;*.asa;*.aspx;*.asax;*.shtml;";
        final String cAndCPlus = "*.cpp;*.cxx;*.c;*.h;*.rc;";
        final String perl = "*.pl;*.pm;*.cgi;";
        final String php = "*.php;";
        final String java = "*.java;";
        final String jsp = "*.jsp;";
        final String javascript = "*.js;";
        final String vbscript = "*.vbs;*.vb;";
        final String css = "*.css;";
        final String xml = "*.xml;*.csproj;*.xaml;";
        final String cHashKey = "*.cs;";
        final String python = "*.py;";
        final String ruby = "*.rb;";
        final String eruby = "*.erb;*.rhtml;";
        final String markdown = "*.text;*.markdown;*.md;";
        final String sql = "*.sql;";
        final String allFile = text + html + cAndCPlus + perl + php + java + jsp + javascript + vbscript + css + xml + cHashKey
                + python + ruby + eruby + markdown + sql;
        final String other = "*.*";

        collection.add(new FileChooser.ExtensionFilter("ALL Files",allFile.split(";")));
        collection.add(new FileChooser.ExtensionFilter("TXT Files",text.split(";")));
        collection.add(new FileChooser.ExtensionFilter("HTML Files",html.split(";")));
        collection.add(new FileChooser.ExtensionFilter("C/C++ Files",cAndCPlus.split(";")));
        collection.add(new FileChooser.ExtensionFilter("PERL Files",perl.split(";")));
        collection.add(new FileChooser.ExtensionFilter("PHP Files",php.split(";")));
        collection.add(new FileChooser.ExtensionFilter("JAVA Files",java.split(";")));
        collection.add(new FileChooser.ExtensionFilter("JSP Files",jsp.split(";")));
        collection.add(new FileChooser.ExtensionFilter("JAVASCRIPT Files",javascript.split(";")));
        collection.add(new FileChooser.ExtensionFilter("VBSCRIPT Files",vbscript.split(";")));
        collection.add(new FileChooser.ExtensionFilter("CSS Files",css.split(";")));
        collection.add(new FileChooser.ExtensionFilter("XML Files",xml.split(";")));
        collection.add(new FileChooser.ExtensionFilter("C# Files",cHashKey.split(";")));
        collection.add(new FileChooser.ExtensionFilter("PYTHON Files",python.split(";")));
        collection.add(new FileChooser.ExtensionFilter("RUBY Files",ruby.split(";")));
        collection.add(new FileChooser.ExtensionFilter("ERUBY Files",eruby.split(";")));
        collection.add(new FileChooser.ExtensionFilter("MARKDOWN Files",markdown.split(";")));
        collection.add(new FileChooser.ExtensionFilter("SQL Files",sql.split(";")));
        collection.add(new FileChooser.ExtensionFilter("Other",other.split(";")));

        return collection;
    }

    public static String[] getSupportedCoding() {
        final String GB2312 = "GB2312";
        final String GBK = "GBK";
        final String GB18030 = "GB18030";
        final String UTF8 = "UTF-8";
        final String UTF16_LE = "UTF-16LE";
        final String UTF16_BE = "UTF-16BE";
        final String UTF8_BOM = "UTF-8BOM";

        return new String[]{GB2312, GBK, GB18030, UTF8, UTF16_LE, UTF16_BE, UTF8_BOM};
    }

    public static File getSystemDeskTopDirectory() {
        return FileSystemView.getFileSystemView().getHomeDirectory();
    }

    public static File getDefaultDestDirectory() {
        File systemDeskTopFile = getSystemDeskTopDirectory();
        return new File(systemDeskTopFile.getAbsolutePath() + "\\Converted_Transcoding");
    }

    /**
     * 非空检查
     * @param file 文件或文件夹
     * @return 如果文件或文件夹是空的，则返回true，否则返回false
     */
    public static boolean isEmpty(File file) {
        // 空文件夹
        if (file.isDirectory() && Objects.requireNonNull(file.listFiles()).length == 0) {
            return true;
        }

        // 空文件
        return file.isFile() && file.length() == 0;
    }

    /**
     * 检查文件是否带有扩展名
     * @param file 文件
     * @return 如果文件有扩展名则返回true，否则返回false
     */
    public static boolean isWithExtension(File file) {
        return file.getName().contains(".");
    }

    /**
     * 受支持的文件扩展名
     * 此方法（包括）有共有两个重载方法
     * 目前支持: txt;html;htm;asp;asa;aspx;asax;shtml;cpp;cxx;c;h;rc;pl;pm;cgi;php;java;jsp;js;vbs;vb;css;xml;csproj;xaml;cs;py;rb;erb;rhtml;text;markdown;md;sql;
     * @param file 文件
     * @return 如果文件扩展名受支持则返回true，否则返回false。
     */
    public static boolean isSupportedExtension(File file) {
        if (!isWithExtension(file)) {
            return false;
        }

        String[] extensions = getSupportedExtension();

        String[] fileWholeNames = file.getName().split("\\.");
        String fileExtension = null;

        if (fileWholeNames.length > 1) {
            fileExtension = fileWholeNames[fileWholeNames.length-1];
        } else {
            fileExtension = fileWholeNames[0];
        }

        for (String s : extensions) {
            if (fileExtension.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 受支持的文件扩展名
     * 此方法（包括）有共有两个重载方法
     * 目前支持: txt;html;htm;asp;asa;aspx;asax;shtml;cpp;cxx;c;h;rc;pl;pm;cgi;php;java;jsp;js;vbs;vb;css;xml;csproj;xaml;cs;py;rb;erb;rhtml;text;markdown;md;sql;
     * @param extension 文件扩展名
     * @return 如果文件扩展名受支持则返回true，否则返回false。
     */
    public static boolean isSupportedExtension(String extension) {
        String[] extensions = getSupportedExtension();

        for (String s : extensions) {
            if (extension.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取支持的文件扩展名
     * @return 字符串数组
     */
    public static String[] getSupportedExtension() {
        final String text = "txt;";
        final String html = "html;htm;asp;asa;aspx;asax;shtml;";
        final String cAndCPlus = "cpp;cxx;c;h;rc;";
        final String perl = "pl;pm;cgi;";
        final String php = "php;";
        final String java = "java;";
        final String jsp = "jsp;";
        final String javascript = "js;";
        final String vbscript = "vbs;vb;";
        final String css = "css;";
        final String xml = "xml;csproj;xaml;";
        final String cHashKey = "cs;";
        final String python = "py;";
        final String ruby = "rb;";
        final String eruby = "erb;rhtml;";
        final String markdown = "text;markdown;md;";
        final String sql = "sql;";
        String all = text + html + cAndCPlus + perl + php + java + jsp + javascript + vbscript + css + xml + cHashKey
                + python + ruby + eruby + markdown + sql;
        String[] extensions = all.split(";");
        return extensions;
    }

    /**
     * 转码
     * @param tf TransFile
     * @throws IOException IO异常（文件不存在）
     */
    public static void convert(TransFile tf) throws IOException {

        if (tf.getOperate().equals(CodingCode.COPY)) {
            FileChannel src = new FileInputStream(tf.getSrcFile()).getChannel();
            FileChannel dest = new FileOutputStream(tf.getDestFile()).getChannel();
            dest.transferFrom(src,0,src.size());
            src.close();
            dest.close();
            return;
        }

        File srcFile = tf.getSrcFile();
        File destFile = tf.getDestFile();

        FileInputStream fis = new FileInputStream(srcFile);
        FileOutputStream fos = new FileOutputStream(destFile);
        BufferedReader br = null;
        BufferedWriter bw = null;

        // 原始编码与目标编码均没有BOM
        if (!tf.isSrcFileBOM() && !tf.isDestFileBOM()) {
            br = new BufferedReader(new InputStreamReader(fis, Charset.forName(tf.getSrcFileCoding())));
            bw = new BufferedWriter(new OutputStreamWriter(fos, Charset.forName(tf.getDestFileCoding())));
        }

        // 原始编码与目标编码均带有BOM
        if (tf.isSrcFileBOM() && tf.isDestFileBOM()) {

            if (tf.getSrcFileCoding().equals(CodingCode.UTF8_BOM)) {
                br = new BufferedReader(new InputStreamReader(fis, Charset.forName(CodingCode.UTF8)));
            } else {
                br = new BufferedReader(new InputStreamReader(fis, Charset.forName(tf.getSrcFileCoding())));
            }

            if (tf.getDestFileCoding().equals(CodingCode.UTF8_BOM)) {
                bw = new BufferedWriter(new OutputStreamWriter(fos, Charset.forName(CodingCode.UTF8)));
            } else {
                bw = new BufferedWriter(new OutputStreamWriter(fos, Charset.forName(tf.getDestFileCoding())));
            }
        }

        // 原始编码不带BOM，目标编码带有BOM
        if (!tf.isSrcFileBOM() && tf.isDestFileBOM()) {

            switch (tf.getDestFileCoding()) {
                case CodingCode.UTF8_BOM:
                    fos.write(0xef);
                    fos.write(0xbb);
                    fos.write(0xbf);
                    break;
                case CodingCode.UTF16_LE:
                    fos.write(0xff);
                    fos.write(0xfe);
                    break;
                case CodingCode.UTF16_BE:
                    fos.write(0xfe);
                    fos.write(0xff);
                    break;
            }

            br = new BufferedReader(new InputStreamReader(fis, Charset.forName(tf.getSrcFileCoding())));
            if (tf.getDestFileCoding().equals(CodingCode.UTF8_BOM)) {
                bw = new BufferedWriter(new OutputStreamWriter(fos, Charset.forName(CodingCode.UTF8)));
            } else {
                bw = new BufferedWriter(new OutputStreamWriter(fos, Charset.forName(tf.getDestFileCoding())));
            }
        }

        // 原始编码带BOM，目标编码不带BOM
        if (tf.isSrcFileBOM() && !tf.isDestFileBOM()) {

            switch (tf.getSrcFileCoding()) {
                case CodingCode.UTF8_BOM:
                    fis.skip(3);
                    break;
                case CodingCode.UTF16_LE:
                case CodingCode.UTF16_BE:
                    fis.skip(2);
                    break;
            }

            if (tf.getSrcFileCoding().equals(CodingCode.UTF8_BOM)) {
                br = new BufferedReader(new InputStreamReader(fis, Charset.forName(CodingCode.UTF8)));
            } else {
                br = new BufferedReader(new InputStreamReader(fis, Charset.forName(tf.getSrcFileCoding())));
            }
            bw = new BufferedWriter(new OutputStreamWriter(fos, Charset.forName(tf.getDestFileCoding())));
        }

        int c;
        while ((c = br.read()) != -1) {
            bw.write(c);
        }

        br.close();
        bw.close();
        fis.close();
        fos.close();
    }

    public static String log(ArrayList<TransFile> staticTransFiles) {

        if (staticTransFiles == null || staticTransFiles.isEmpty()) {
            return "无记录";
        }

        int file = 0;
        int directory = 0;
        int copy = 0;
        int empty = 0;
        int withoutExtensionCopy = 0;
        int withoutExtensionTrans = 0;
        int nonSupportedExtension = 0;
        int sameCoding = 0;
        int transcoding = 0;
        StringBuilder filePath = new StringBuilder();

        for (TransFile tf : staticTransFiles) {
            if (tf.getSrcFile().isDirectory()) {
                directory++;
            }
            if (tf.getSrcFile().isFile()) {
                file++;

                if (tf.getOperate().equals(CodingCode.COPY)) {
                    copy++;
                }
                if (TransUtil.isEmpty(tf.getSrcFile())) {
                    empty++;
                }
                if (!TransUtil.isEmpty(tf.getSrcFile()) && tf.getOperate().equals(CodingCode.COPY) && !TransUtil.isWithExtension(tf.getSrcFile())) {
                    withoutExtensionCopy++;
                } else if (!TransUtil.isEmpty(tf.getSrcFile()) && !TransUtil.isWithExtension(tf.getSrcFile())) {
                    withoutExtensionTrans++;
                }
                if (!TransUtil.isEmpty(tf.getSrcFile()) && TransUtil.isWithExtension(tf.getSrcFile()) && !TransUtil.isSupportedExtension(tf.getSrcFile())) {
                    nonSupportedExtension++;
                }
                if (!TransUtil.isEmpty(tf.getSrcFile()) && tf.getSrcFileCoding() != null && tf.getSrcFileCoding().equals(tf.getDestFileCoding())) {
                    sameCoding++;
                }

                if (tf.getOperate().equals(CodingCode.TRANSCODING)) {
                    transcoding++;
                }
            }
            filePath.append("| 源文件：");
            filePath.append(tf.getSrcFile().getAbsolutePath());
            filePath.append("\n");
            filePath.append("| 目标文件：");
            filePath.append(tf.getDestFile().getAbsolutePath());
            filePath.append("\n");
        }
        filePath.delete(filePath.length()-2, filePath.length());

        String srcPath;
        String destPath;
        TransFile transFile = staticTransFiles.get(0);
        if (transFile.getSrcFile().isFile()) {
            srcPath = "请查看“详细”";
            destPath = "请查看“详细”";
        } else {
            srcPath = transFile.getSrcFile().getAbsolutePath();
            destPath = transFile.getDestFile().getAbsolutePath();
        }

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String nowTime = simpleDateFormat.format(date);
        String print = "|-----------------------------------------------" + "\n" +
                "| 日志：" + "\n" +
                "| " + "\n" +
                "| 文件总计：" + (directory+file) + "（文件：" + file + "，文件夹：" + directory + "）" + "\n" +
                "| 复制：" + copy + "（空文件：" + empty + "，原始编码与目标编码相同：" + sameCoding +"，无扩展名：" + withoutExtensionCopy + "，扩展名不支持：" + nonSupportedExtension + "）" + "\n" +
                "| 转码：" + transcoding + "（无扩展名：" + withoutExtensionTrans + "）" + "\n" +
                "| 原始位置：" + srcPath + "\n" +
                "| 目标位置：" + destPath + "\n" +
                "| 详细：" + "\n" +
                filePath.toString() + "\n" +
                "| " + "\n" +
                "| 完成" + "\n" +
                "| " + nowTime + "\n" +
                "|-----------------------------------------------";
        return print;
    }
}
