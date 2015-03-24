package ua.com.glady.uacc.tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.MailTo;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Checkable;
import android.widget.EditText;

import ua.com.glady.uacc.R;

import static ua.com.glady.uacc.tools.ToolsStr.isInteger;

/**
 * This class contains various function to handle strings.
 * By design it must contains only static functions
 *
 * Created by Slava on 19.03.2015.
 */
public class ToolsView {

    /**
     * Verifies if certain view element (radioButton, checkBox) checked or not
     * @param view parent view
     * @param id checkable view id
     * @return id if view with given id is checkable and it checked
     */
    public static boolean isChecked(View view, int id){
        boolean result = false;
        View component = view.findViewById(id);
        if (component instanceof Checkable)
            result = ((Checkable) component).isChecked();
        return result;
    }

    /**
     * Returns integer number from editText
     * @param view - parent view
     * @param editId - EditText id
     * @param defaultValue - what should be returned id edit doesn't contains valid integer
     * @return editText text converted to integer
     */
    public static int getInt(View view, int editId, int defaultValue){
        View component = view.findViewById(editId);
        if (! (component instanceof EditText))
            throw new IllegalArgumentException();
        String s = ((EditText) component).getText().toString();
        if (isInteger(s))
            return Integer.parseInt(s);
        else
            return defaultValue;
    }

    public static Intent newEmailIntent(Context context, String address, String subject, String body, String cc) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { address });
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_CC, cc);
        intent.setType("message/rfc822");
        return intent;
    }

    /**
     * Shows popup html viewer
     * @param context activity context
     * @param title title of the popup
     * @param html content of the web view
     */
    public static void showPopupWebView(final Context context, String title, String html){
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);

        WebView wv = new WebView(context);
        wv.loadData(html, "text/html; charset=UTF-8", null);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.startsWith("mailto:")){
                    MailTo mt = MailTo.parse(url);
                    Intent i = newEmailIntent(context, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
                    context.startActivity(i);
                    view.reload();
                    return true;
                }
                else{
                    view.loadUrl(url);
                }
                return true;
            }
        });

        alert.setView(wv);
        alert.setNegativeButton(R.string.Close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

}
