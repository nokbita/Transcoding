module TranscodingUI {
    requires java.desktop;
    requires javafx.controls;
    exports priv.nokbita.app to javafx.graphics;
    opens priv.nokbita.entity to javafx.base;
}