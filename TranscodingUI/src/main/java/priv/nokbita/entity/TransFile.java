package priv.nokbita.entity;

import javafx.beans.property.SimpleStringProperty;

import java.io.File;

public class TransFile {

    private String srcFileName;
    private File srcFile;
    private String srcFileCoding;
    private boolean srcFileBOM;

    private File destFile;
    private String destFileCoding;
    private boolean destFileBOM;

    private SimpleStringProperty operate;
    private SimpleStringProperty tip;

    public TransFile() {
            this.operate = new SimpleStringProperty();
            this.tip = new SimpleStringProperty();
    }


    public String getSrcFileName() {
        return srcFileName;
    }



    public void setSrcFileName(String srcFileName) {
        this.srcFileName = srcFileName;
    }

    public File getSrcFile() {
        return srcFile;
    }

    public void setSrcFile(File srcFile) {
        this.srcFile = srcFile;
    }

    public String getSrcFileCoding() {
        return srcFileCoding;
    }

    public void setSrcFileCoding(String srcFileCoding) {
        this.srcFileCoding = srcFileCoding;
    }

    public boolean isSrcFileBOM() {
        return srcFileBOM;
    }

    public void setSrcFileBOM(boolean srcFileBOM) {
        this.srcFileBOM = srcFileBOM;
    }

    public File getDestFile() {
        return destFile;
    }

    public void setDestFile(File destFile) {
        this.destFile = destFile;
    }

    public String getDestFileCoding() {
        return destFileCoding;
    }

    public void setDestFileCoding(String destFileCoding) {
        this.destFileCoding = destFileCoding;
    }

    public boolean isDestFileBOM() {
        return destFileBOM;
    }

    public void setDestFileBOM(boolean destFileBOM) {
        this.destFileBOM = destFileBOM;
    }

    public String getOperate() {
        return operate.get();
    }

    public SimpleStringProperty operateProperty() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate.set(operate);
    }

    public String getTip() {
        return tip.get();
    }

    public SimpleStringProperty tipProperty() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip.set(tip);
    }
}
