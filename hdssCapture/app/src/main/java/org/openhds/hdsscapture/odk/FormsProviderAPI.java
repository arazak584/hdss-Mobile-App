package org.openhds.hdsscapture.odk;

import android.net.Uri;
import android.provider.BaseColumns;

public class FormsProviderAPI {

    public static final String AUTHORITY = "org.odk.collect.android.provider.odk.forms";

    // This class cannot be instantiated
    private FormsProviderAPI() {}

    /**
     * Notes table
     */
    public static final class FormsColumns implements BaseColumns {
        // This class cannot be instantiated
        private FormsColumns() {}

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/forms");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.odk.form";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.odk.form";

        // Other columns...
    }
}
