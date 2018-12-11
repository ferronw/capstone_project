package processing.test.capstone_prototype_1;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SelectImageActivity extends AppCompatActivity {

    Button btn_imageSelect;
    Button btn_generate;
    ImageView image;

    String selectedPhotoFilePath;
    Uri passableUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_level);

        btn_imageSelect = (Button) findViewById(R.id.btn_imageselect);
        btn_generate = (Button) findViewById(R.id.btn_generate);
        image = findViewById(R.id.iv_selectedphoto);

        btn_imageSelect.setOnClickListener(v->{
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 1);
        });

        btn_generate.setOnClickListener(v->{
            if (passableUri != null) {
                Intent i = new Intent(SelectImageActivity.this, GenerateLevelActivity.class);
                i.putExtra("image_file_path", passableUri.toString());
                startActivity(i);
            }
        });
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();

                selectedPhotoFilePath = getRealPathFromURI(imageUri);
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
                File file = wrapper.getDir("Images",MODE_PRIVATE);
                file = new File(file, "levelToGenerate"+".jpg");
                try{
                    OutputStream stream = null;
                    stream = new FileOutputStream(file);
                    selectedImage.compress(Bitmap.CompressFormat.JPEG,100,stream);
                    stream.flush();
                    stream.close();
                }catch (IOException e) // Catch the exception
                {
                    e.printStackTrace();
                }

                Uri savedImageURI = Uri.parse(file.getAbsolutePath());
                passableUri = savedImageURI;
                image.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(SelectImageActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(SelectImageActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    public String getRealPathFromURI(Uri contentUri) {

        // can post image
        String [] proj={MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery( contentUri,
                proj, // Which columns to return
                null,       // WHERE clause; which rows to return (all rows)
                null,       // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }
}
