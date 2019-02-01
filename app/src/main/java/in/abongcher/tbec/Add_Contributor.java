package in.abongcher.tbec;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;

public class Add_Contributor extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener{
    DataBaseHandler db;
    Contributor con;

    private ImageView imageView;
    private EditText nam;
    private Spinner gen;
    private EditText mob;
    private EditText amt;

    Bitmap bitmap;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__contributor);

        db = new DataBaseHandler(this);
        con = new Contributor();

        imageView = (ImageView) findViewById(R.id.customer_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });


        nam = (EditText) findViewById(R.id.edit_name);
        gen = (Spinner) findViewById(R.id.spinner_gender);
        mob = (EditText) findViewById(R.id.edit_mobile);
        amt = (EditText) findViewById(R.id.edit_fee);

        String[] items = new String[]{"Male", "Female", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Add_Contributor.this, android.R.layout.simple_spinner_dropdown_item, items);
        gen.setAdapter(adapter);
        gen.setOnItemSelectedListener(this);

        submit = (Button) findViewById(R.id.saveBtn);
        submit.setOnClickListener(this);
    }

    public void selectImage(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case 2:
                if(resultCode == RESULT_OK){
                    Uri choosenImage = data.getData();

                    if(choosenImage !=null){
                        bitmap = decodeUri(choosenImage, 400);
                        imageView.setImageBitmap(bitmap);
                    }
                }
                //this condition was added as trial version
                //you may delete it abraham
                else if(resultCode == RESULT_CANCELED){
                    Toast.makeText(Add_Contributor.this, "Select an image.", Toast.LENGTH_SHORT).show();
                }
            default:
        }
    }

    //COnvert and resize our image to 400dp for faster uploading our images to DB
    protected Bitmap decodeUri(Uri selectedImage, int REQUIRED_SIZE) {

        try {

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

            // The new size we want to scale to
            // final int REQUIRED_SIZE =  size;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    //Convert bitmap to bytes
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    private byte[] profileImage(Bitmap b){

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, bos);
        return bos.toByteArray();

    }

    //Insert data to the database
    private void addContact(){
        try {
            long success = db.isCustomerAdded(new Contributor(profileImage(bitmap), nam.getText().toString(), con.getGender(), mob.getText().toString(), Integer.valueOf(amt.getText().toString())));
            if (success != -1) {
                Toast.makeText(Add_Contributor.this, "Data integrated.", Toast.LENGTH_SHORT).show();
                nam.setText("");
                mob.setText("");
                amt.setText("");
            }
            else {
                throw new Exception();
            }
        }

        catch (Exception ex){
            if(ex instanceof NullPointerException) {
                Toast.makeText(Add_Contributor.this, "Empty image: Select Image.", Toast.LENGTH_LONG).show();
            }
            else if(ex instanceof NumberFormatException){
                Toast.makeText(Add_Contributor.this, "Blank amount: Input amount.", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(Add_Contributor.this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        Drawable drawable = getResources().getDrawable(R.drawable.image);
        imageView.setImageDrawable(drawable);
        nam.setHintTextColor(getResources().getColor(R.color.gray));
        nam.setHint("Name");
        mob.setHintTextColor(getResources().getColor(R.color.gray));
        mob.setHint("Mobile No");
        amt.setHintTextColor(getResources().getColor(R.color.gray));
        amt.setHint("Amount");
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        con = new Contributor();
        con.setGender(adapterView.getItemAtPosition(position).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    @Override
    public void onClick(View view) {
        addContact();
    }




}
