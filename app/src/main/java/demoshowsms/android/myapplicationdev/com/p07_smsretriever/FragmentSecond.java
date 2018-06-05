package demoshowsms.android.myapplicationdev.com.p07_smsretriever;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSecond extends Fragment {
    TextView textView,tvFrag2;
    EditText etWord;
    Button btnRetrieve, btnEmail;

    public FragmentSecond() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        tvFrag2 = (TextView)view.findViewById(R.id. tvFrag2);
        etWord = (EditText)view.findViewById(R.id. etWord2);
        btnRetrieve = (Button)view.findViewById(R.id. btnRetrieveFrag2);
        btnEmail = (Button)view.findViewById(R.id. btnSend2);

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvFrag2.getText().toString().equals("")){
                    Toast.makeText(getActivity().getBaseContext(), "Please retrieve a record", Toast.LENGTH_LONG).show();
                }else{
                    String message = "Search word:\n" + etWord.getText().toString() + "\n" + tvFrag2.getText().toString() + "\n";

                    Intent email = new Intent(Intent.ACTION_SEND);

                    email.putExtra(Intent.EXTRA_EMAIL,
                            new String[]{"jason_lim@rp.edu.sg"});
                    email.putExtra(Intent.EXTRA_SUBJECT,
                            "Hi faci,\n" +
                                    "I am yanyi" +
                                    "\n" +
                                    "This is the word fragment");
                    email.putExtra(Intent.EXTRA_TEXT,
                            message);
                    email.setType("message/rfc822");
                    startActivity(Intent.createChooser(email,
                            "Choose an Email client :"));
                }
            }
        });

        btnRetrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = PermissionChecker.checkSelfPermission
                        (getActivity().getBaseContext(), Manifest.permission.READ_SMS);

                if (permissionCheck != PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_SMS}, 0);
                    // stops the action from proceeding further as permission not
                    //  granted yet
                    return;
                }

                if(etWord.getText().toString().equals("")){
                    tvFrag2.setText("");
                }else{
                    String words = etWord.getText().toString();
                    String[] splitStr = words.split("\\s+");

                    Uri uri = Uri.parse("content://sms");
                    // The columns we want
                    //  date is when the message took place
                    //  address is the number of the other party
                    //  body is the message content
                    //  type 1 is received, type 2 sent
                    String[] reqCols = new String[]{"date", "address", "body", "type"};

                    // Get Content Resolver object from which to
                    //  query the content provider
                    ContentResolver cr = getActivity().getContentResolver();
                    // Fetch SMS Message from Built-in Content Provider
                    String filter = "body LIKE ?";
                    String [] filterArgs = new String[splitStr.length];

                    for(int i=0; i < splitStr.length; i++){
                        filterArgs[i] = "%"+splitStr[i]+"%";
                        Log.d("arg", filterArgs[i]);
                        if(i == (splitStr.length-1)){
                            filter += "";
                        }else{
                            filter += " OR body LIKE ?";
                        }
                    }

                    Cursor cursor = cr.query(uri, reqCols, filter, filterArgs, null);

                    String smsBody = "";
                    if (cursor.moveToFirst()) {
                        do {
                            long dateInMillis = cursor.getLong(0);
                            String date = (String) DateFormat
                                    .format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                            String address = cursor.getString(1);
                            String body = cursor.getString(2);
                            String type = cursor.getString(3);
                            if (type.equalsIgnoreCase("1")) {
                                type = "Sent:";
                                smsBody += type + " " + address + "\n at " + date
                                        + "\n\"" + body + "\"\n\n";
                            }
                        } while (cursor.moveToNext());
                    }
                    tvFrag2.setText(smsBody);
                }
            }
        });
        return view;
    }

}
