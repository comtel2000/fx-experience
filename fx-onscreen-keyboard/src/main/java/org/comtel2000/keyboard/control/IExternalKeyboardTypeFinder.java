package org.comtel2000.keyboard.control;

import java.util.List;

/**
 * Created by Guglielmo Moretti - CEIA SpA
 * Date: 16/10/2017.
 */
public interface IExternalKeyboardTypeFinder {

    class KeyboardTypeBean {
        private IKeyboardType type;
        private String root;
        private String file;

        public KeyboardTypeBean(IKeyboardType type, String root, String file) {
            this.type = type;
            this.root = root;
            this.file = file;
        }

        public IKeyboardType getType() {
            return type;
        }

        public String getRoot() {
            return root;
        }

        public String getFile() {
            return file;
        }
    }

    List<KeyboardTypeBean> getExternalKeyboardTypes();

}
