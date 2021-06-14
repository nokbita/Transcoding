package priv.nokbita.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import priv.nokbita.entity.TransFile;
import priv.nokbita.util.CodingCode;
import priv.nokbita.util.TransUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TransFileService {
    // 内部临时变量，程序员不应该使用该变量
    private static final ArrayList<File> staticFiles = new ArrayList<>();
    // 内部累加变量
    private static final ArrayList<TransFile> staticTransFiles = new ArrayList<>();
    private static final ArrayList<TransFile> staticTransFilesCopy = new ArrayList<>();
    // 内部变量
    private static TransFileService transFileService = null;


    private TransFileService(){};

    public static TransFileService getInstance() {
        if (transFileService == null) {
            synchronized (TransFileService.class) {
                if (transFileService == null) {
                    transFileService = new TransFileService();
                }
            }
        }
        return transFileService;
    }

    /**
     * 内部方法，递归遍历初始文件下所有文件和文件夹，将其路径存储到 static ArrayList<File> allFiles。
     * 注意：该方法不应该由程序员调用，该方法由getObservableList()调用
     * @param file 初始文件。
     */
    private void initializationStaticFiles(File file) {
        if (file.exists()) {
            staticFiles.add(file);
            if (file.isDirectory()) {
                File[] arr = file.listFiles();
                for (File value : Objects.requireNonNull(arr)) {
                    initializationStaticFiles(value);
                }
            }
        }
    }

    /**
     * 内部方法，生成对源文件的一些属性操作
     * 注意：该方法不应该由程序员调用，该方法由updateStaticTransFiles()调用
     * @param files 本类中的 static ArrayList<TransFile> staticTransFiles
     * @return ArrayList<TransFile>。
     */
    private ArrayList<TransFile> generateTransFiles(ArrayList<File> files) {
        ArrayList<TransFile> transFiles = new ArrayList<>();

        Coding coding = new Coding();

        for (int i = 0; i < files.size(); i++) {
            File srcFile = files.get(i);

            TransFile transFile = new TransFile();
            transFile.setSrcFileName(srcFile.getName());
            transFile.setSrcFile(srcFile);

            if (srcFile.isFile()) {

                // 空文件
                if (TransUtil.isEmpty(srcFile)) {
                    transFile.setOperate(CodingCode.COPY);
                    transFile.setTip("空文件");
                }

                // 文件扩展名不支持，复制
                if (!TransUtil.isEmpty(srcFile) && !TransUtil.isSupportedExtension(srcFile)) {
                    transFile.setOperate(CodingCode.COPY);
                    transFile.setTip("扩展名不支持");
                }

                // 文件无扩展名
                if (!TransUtil.isEmpty(srcFile) && !TransUtil.isWithExtension(srcFile)) {
                    transFile.setOperate(CodingCode.WAITING);
                    transFile.setTip("无扩展名");
                }

                // 通过以上排查，可确定该文件可以正常转码
                if (transFile.getOperate() == null) {
                    transFile.setOperate(CodingCode.TRANSCODING);
                    transFile.setTip("文件");
                }

                // 对可正常转码的文件，获取编码格式，继续细分判断
                if (transFile.getOperate().equals(CodingCode.TRANSCODING)) {
                    // 源文件编码格式
                    String srcCoding = coding.getCoding(srcFile);
                    transFile.setSrcFileCoding(srcCoding);

                    // 源文件是否带有BOM
                    boolean isSrcBOM = srcCoding.contains("BOM") || srcCoding.contains("LE") || srcCoding.contains("BE");
                    transFile.setSrcFileBOM(isSrcBOM);
                }
            } else {
                transFile.setOperate(CodingCode.MKDIRS);
                transFile.setTip("文件夹");
            }

            // 收集TransFile
            transFiles.add(transFile);
        }

        return transFiles;
    }

    public ObservableList<TransFile> getObservableList(File fileDirectory) {
        staticFiles.clear();
        initializationStaticFiles(fileDirectory);

        updateStaticTransFiles();

        return FXCollections.observableArrayList(staticTransFiles);
    }

    public ObservableList<TransFile> getObservableList(List<File> files) {
        staticFiles.clear();
        for (File f : files) {
            initializationStaticFiles(f);
        }

        updateStaticTransFiles();

        return FXCollections.observableArrayList(staticTransFiles);
    }

    // 内部方法，排重并更新staticTransFiles
    private void updateStaticTransFiles() {
        ArrayList<TransFile> newFiles = generateTransFiles(staticFiles);
        int size = newFiles.size();

        for (TransFile oldFile : staticTransFiles) {
            for (int i = 0; i < size; i++) {
                if (oldFile.getSrcFile().equals(newFiles.get(i).getSrcFile())) {
                    newFiles.remove(i);
                    size -= 1;
                    break;
                }
            }
        }
        staticTransFiles.addAll(newFiles);
    }

    public Task startTranscoding(String destFileCoding, File destDirectory) {

        Task task = new Task() {
            String srcRootDirectoryName = null;

            @Override
            protected Object call() throws Exception {
                for (int i = 0; i < staticTransFiles.size(); i++) {

                    try {
                        TransFile transFile = staticTransFiles.get(i);
                        File srcFile = transFile.getSrcFile();

                        // 目标文件
                        File destFile = null;
                        // 如果源文件是文件夹，则初始化源文件根目录名
                        if (srcFile.isDirectory() && i == 0) {
                            srcRootDirectoryName = srcFile.getName();
                            destFile = new File(destDirectory.getAbsolutePath() + "//"
                                    + srcRootDirectoryName);
                        }

                        if (srcRootDirectoryName != null && i > 0) {
                            // 用户选择的源文件是文件夹
                            destFile = new File(destDirectory.getAbsolutePath() + "//" + srcRootDirectoryName
                                    + srcFile.getAbsolutePath().split(srcRootDirectoryName)[1]);
                        }

                        if (srcRootDirectoryName == null) {
                            // 用户选择的源文件是文件
                            destFile = new File(destDirectory.getAbsolutePath() + "//"
                                    + srcFile.getName());
                        }
                        transFile.setDestFile(destFile);

                        if (transFile.getOperate().equals(CodingCode.TRANSCODING)) {
                            // 目标编码
                            transFile.setDestFileCoding(destFileCoding);

                            // 目标编码是否有BOM
                            boolean isDestBOM = destFileCoding.contains("BOM") || destFileCoding.contains("LE") || destFileCoding.contains("BE");
                            transFile.setDestFileBOM(isDestBOM);

                            // 原始编码与目标编码一致，预操作为复制
                            if (transFile.getSrcFileCoding().equals(destFileCoding)) {
                                transFile.setOperate(CodingCode.COPY);
                            }
                        }

                        if (transFile.getOperate().equals(CodingCode.WAITING)) {
                            transFile.setOperate(CodingCode.COPY);
                        }

                        if (i == 0 && !staticTransFilesCopy.isEmpty()) {
                            staticTransFilesCopy.clear();
                        }
                        staticTransFilesCopy.add(transFile);

                        // 开始转码
                        if (srcFile.isDirectory()) {
                            transFile.getDestFile().mkdirs();
                        } else {
                            TransUtil.convert(transFile);
                        }

                        updateProgress((i + 1), staticTransFiles.size());
                        updateMessage("进度：" + (i + 1) + " / " + staticTransFiles.size());
                        if (i + 1 == staticTransFiles.size()) {
                            updateMessage((i + 1) + " / " + staticTransFiles.size() + " 转换完成，点击打开 -->");
                            updateValue(true);
                        }

                        if (isCancelled()) {
                            break;
                        }
                    } catch (Exception e) {
                        // 捕获转码时产生的异常，方便排查错误
                        e.printStackTrace();
                    }
                }

                return null;
            }
        };
        return task;
    }

    public void clearStaticTransFiles() {
        staticTransFiles.clear();
    }

    public void clearStaticTransFilesCopy() {
        staticTransFilesCopy.clear();
    }

    public ArrayList<TransFile> getStaticTransFilesCopy() {
        return staticTransFilesCopy;
    }

}
