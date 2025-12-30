package org.openhds.hdsscapture.odk.listener;

import org.openhds.hdsscapture.odk.task.OdkFormLoadResult;

public interface OdkFormLoadListener {

    void onOdkFormLoadSuccess(OdkFormLoadResult result);

    void onOdkFormLoadFailure(OdkFormLoadResult result);
}
