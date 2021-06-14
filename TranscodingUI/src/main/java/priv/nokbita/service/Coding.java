package priv.nokbita.service;

import priv.nokbita.util.CodingCode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Coding {

    /**
     * 获取文件的编码格式
     * 仅支持CodingCode中的UTF-8、UTF-8BOM、UTF-16LE、UTF-16BE、GB2312、GBK、GB18030
     * 注意：GB码统一返回GB18030（修正，统一返回GBK，而不是GB18030，原因是jre环境下会发生java.nio.charset.UnsupportedCharsetException: GB18030，原因未知）
     * @param file 文件，存在且非空、受支持的文件扩展名（文本类型）、受支持的编码类型
     * @return 文件编码格式
     */
    protected String getCoding(File file) {
        String utf8BOMorUTF16BOM = isUTF8BOMorUTF16BOM(file);
        if(utf8BOMorUTF16BOM != null) {
            return utf8BOMorUTF16BOM;
        }
        if (isUTF8(file)) {
            return CodingCode.UTF8;
        } else {
            return CodingCode.GBK;
        }
    }


    /**
     * 在maximumBytes范围内，将IO流读取的每一个0~255的整型数据存进ArrayList<Integer>中
     * @param file 文件，存在且非空
     * @param maximumBytes 读取字节的最大数量
     * @return 存有整型数据的ArrayList，如果文件为空，则返回空的ArrayList。
     */
    private ArrayList<Integer> getReadInts(File file, long maximumBytes) {
        ArrayList<Integer> integers = new ArrayList<>();
        try {

            InputStream is = new FileInputStream(file);
            int b;
            for (int i = 0; i < maximumBytes; i++) {
                if ((b = is.read()) != -1) {
                    integers.add(b);
                } else {
                    break;
                }
            }
            is.close();

        } catch (IOException e) {
//            e.printStackTrace();
            // private方法只在本类中调用，该异常无需处理
        }
        return integers;
    }


    /**
     * 将一个0~255的int型整数转化为8位二进制字符串
     * @param range0to255 一个0~255的int型整数
     * @return 一个8位二进制字符串
     */
    private String readIntTo8bitBinaryStr(int range0to255) {
        StringBuilder binary = new StringBuilder();
        String str = Integer.toBinaryString(range0to255);
        for (int i = 8; str.length() < i; i--) {
            binary.insert(0,"0");
        }
        return binary.append(str).toString();
    }


    /**
     * 对UTF-8BOM、UTF-16LE、UTF-16BE判断
     * @param file 文件，存在且非空
     * @return 返回对应编码格式的字符串，如果是不支持的文件编码（主要指不带BOM的），则返回null
     */
    protected String isUTF8BOMorUTF16BOM(File file) {
        // 只取前3个字节
        ArrayList<Integer> integers = getReadInts(file,3);

        if(integers.size() < 2) {
            return null;
        }

        int dec1 = integers.get(0);
        int dec2 = integers.get(1);
        if (dec1 == 0xff && dec2 == 0xfe) {
            return CodingCode.UTF16_LE;
        }
        if (dec1 == 0xfe && dec2 == 0xff) {
            return CodingCode.UTF16_BE;
        }
        if (integers.size() > 2) {
            int dec3 = integers.get(2);
            if (dec1 == 0xef && dec2 == 0xbb && dec3 == 0xbf) {
                return CodingCode.UTF8_BOM;
            }
        }
        return null;
    }

    /**
     * 判断文件编码格式是否是UTF-8，4byte之内
     * 该方法最多对文件的前1024*30个字节格式进行检查，
     * 也就是说，如果读取超过1024*30个字节仍旧没有检查出包含其他编码格式的字符，则认定该文件的编码格式是UTF-8.
     * @param file 文件，存在且非空
     * @return 是则返回true，否则返回false
     */
    private boolean isUTF8(File file) {

        long maxByteNum = file.length();
        if (maxByteNum >= 1024*30) {
            maxByteNum = 1024*30;  // 最多读取前1024*30个字节数据
        }

        int oneByteTimes = 0;
        int twoByteTimes = 0;
        int threeByteTimes = 0;
        int forthByteTimes = 0;

        ArrayList<Integer> integers = getReadInts(file, maxByteNum);
        int i = 0;
        int len = integers.size();
        while (i < len) {
            // 规定，只要已检查的字节中符合UTF-8的字符达到10个以上即认定为UTF-8
            if (twoByteTimes > 10 || threeByteTimes > 10 || forthByteTimes > 10) {
                return true;
            }

            String binary = readIntTo8bitBinaryStr(integers.get(i));
            if (binary.startsWith("0")) {
                oneByteTimes++;
                i++;
                continue;
            }

            if (len > 1 && binary.startsWith("110") && i+1 < len) {
                String binary2 = readIntTo8bitBinaryStr(integers.get(i+1));
                if (binary2.startsWith("10")) {
                    twoByteTimes++;
                    i += 2;
                    continue;
                }
            }

            if (len > 2 && binary.startsWith("1110") && i+2 < len) {
                String binary2 = readIntTo8bitBinaryStr(integers.get(i+1));
                String binary3 = readIntTo8bitBinaryStr(integers.get(i+2));
                if (binary2.startsWith("10") && binary3.startsWith("10")) {
                    threeByteTimes++;
                    i += 3;
                    continue;
                }
            }

            if (len > 3 && binary.startsWith("11110") && i+3 < len) {
                String binary2 = readIntTo8bitBinaryStr(integers.get(i+1));
                String binary3 = readIntTo8bitBinaryStr(integers.get(i+2));
                String binary4 = readIntTo8bitBinaryStr(integers.get(i+3));
                if (binary2.startsWith("10") && binary3.startsWith("10") && binary4.startsWith("10")) {
                    forthByteTimes++;
                    i += 4;
                    continue;
                }
            }

            break;
        }

        int count = len - (oneByteTimes + twoByteTimes*2 + threeByteTimes*3 + forthByteTimes*4);
        return count >= 0 && count <= 3;
    }

}
