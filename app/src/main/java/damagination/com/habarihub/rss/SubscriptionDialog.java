package damagination.com.habarihub.rss;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import damagination.com.habarihub.R;


public class SubscriptionDialog extends DialogFragment {
    private String source;
    private String url;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.subscription_dialog, null);
        final EditText sourceName = (EditText) view.findViewById(R.id.search_feed);

        builder.setView(view)
                .setPositiveButton(R.string.search, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        source = sourceName.getText().toString();

                        Intent intent = new Intent(getActivity(), FeedSearch.class);
                        intent.putExtra(Intent.EXTRA_TEXT, source);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SubscriptionDialog.this.getDialog().cancel();
                    }
                });


        return builder.create();
    }
}
