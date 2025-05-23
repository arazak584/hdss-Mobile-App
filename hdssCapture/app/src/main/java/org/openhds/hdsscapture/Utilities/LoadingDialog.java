package org.openhds.hdsscapture.Utilities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import org.openhds.hdsscapture.R;

public class LoadingDialog extends Dialog {

    private Context mContext;
    private ProgressBar progressBarLoading;
    private TextView txtLoadingMessage;
    private String message = "";


    public LoadingDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loading_dialog);

        progressBarLoading = (ProgressBar) findViewById(R.id.progressBarLoading);
        txtLoadingMessage = (TextView) findViewById(R.id.txtLoadingMessage);

        if (txtLoadingMessage != null){
            txtLoadingMessage.setText(message);
        }

        setCancelable(false);
    }

    public String getMessage(){
        return message;
    }

    public void setMessage(@StringRes int messageId){
        if (txtLoadingMessage != null){
            txtLoadingMessage.setText(messageId);
        }
    }

    public void setMessage(String message){
        this.message = message;

        if (txtLoadingMessage != null){
            txtLoadingMessage.setText(message);
        }
    }

}
