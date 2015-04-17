package com.iqbal.deblurringapp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.Ragnarok.BitmapFilter;

public class MainActivity extends ActionBarActivity {
	
	/**
	 * 
	 * List of Variables and Widgets we will use in our Activity
	 * 
	 */
	
	private static int REQUEST_CAMERA = 1;
	private static int RESULT_LOAD_IMAGE = 2;
	private static String KEY_NEXT_IMAGE_NUMBER = "nextNumber";

	private boolean isImageSelected = false;
	private boolean isEffectSelected = false;

	private TextView textViewEffect;
	private Button buttonLoadImage;
	private Button btnChooseEffect;
	private Button btnApplyEffect;
	private Button buttonSave;
	private Button buttonCancel;
	private ImageView imageView;
	private LinearLayout linearLayoutSave;

	private String effectChosen;
	private String pathToSavePhoto = "";
	

	private Bitmap bm;
	private Bitmap bm2;
	private Bitmap bmOutImg;

	private int idEffectChosen;
	private int width;
	private int height;

	// effectsList : The list of effects we can apply
	final CharSequence[] effectsList = { "Deblur", "Relief", "Average Blur",
			"Oil Painting", "Neon", "Pixelate", "Old TV", "Invert Color",
			"Block", "Aged photo", "GrayScale", "Light", "Lomo", "HDR",
			"Gaussian Blur", "Soft Glow", "Sketch", "Motion Blur", "Gotham",
			"Darken" };
	
	/**
	 * 
	 * onCreate Method
	 * 
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// hide the action bar
		getSupportActionBar().hide();
		
		// get the path of the DIRECTORY_PICTURES
		pathToSavePhoto = ""+ Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

		// get the width and the height of the user_screen,
		// we use it to change the image size and adapt it to the screen size
		DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
		width = displayMetrics.widthPixels;
		height = displayMetrics.heightPixels;

		// This part of code will be executed only the first time the user run the app,
		// we will associate this number to taken images, or images after applying effect
		// it will start with "1" and increment it each time we use it
		if (loadPrefs(KEY_NEXT_IMAGE_NUMBER) == null)
			SavePreferences(KEY_NEXT_IMAGE_NUMBER, "1");

		// Image View
		// to show the chosen image and the image after applying effect
		imageView = (ImageView) findViewById(R.id.imageView);

		// Linear Layout Save
		// contain the two button save and canvel
		// we use it to make visible/invisible those buttons 
		linearLayoutSave = (LinearLayout) findViewById(R.id.saveLayout);
		
		//Text View 
		// to show the user the name of the effect he chose (kind of confirmation and communication)
		textViewEffect = (TextView) findViewById(R.id.textViewEffect);

		// buttonLoadImage 
		// Chose image from the gallery or take a new one
		buttonLoadImage = (Button) findViewById(R.id.btnSelectPhoto);
		buttonLoadImage.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				
				// we will show it again when the user apply the effect
				linearLayoutSave.setVisibility(View.GONE);
				
				// call loadImage
				loadImage();
			}
		});
		
		// btnChooseEffect
		// Chose effect to apply
		btnChooseEffect = (Button) findViewById(R.id.btnChooseEffect);
		btnChooseEffect.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				// we will show it again when the user apply the effect
				linearLayoutSave.setVisibility(View.GONE);
				
				// call alertListEffects
				choseEffect();
			}
		});

		// btnApplyEffect
		// apply the effect on the image
		btnApplyEffect = (Button) findViewById(R.id.btnApplyEffect);
		btnApplyEffect.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// check if the use chose the image and the effect before apply
				if (isImageSelected && isEffectSelected){
						// call applyEffect  
						applyEffect();
				}
				else{
					//show the message "Please, Chose ..."
					Toast.makeText(getApplicationContext(),"Please, Chose the Image and the Effect",Toast.LENGTH_LONG).show();
				}
					
			}
		});

		// buttonSave: Save the result to gallery
		buttonSave = (Button) findViewById(R.id.btnSave);
		buttonSave.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// call saveMethode
				saveMethode();
			}
		});
		// buttonCancel: the use don't want to save the image
		buttonCancel = (Button) findViewById(R.id.btnCancel);
		buttonCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// hide the linearLayout save, we don't need it any pore
				// we will show it again when the user apply the effect
				linearLayoutSave.setVisibility(View.GONE);
			}
		});

	}

	/**
	 * 
	 * List of methods we are using in our Activity
	 * 
	 */
	
	
	/*
	 * Load Image
	 * */
	private void loadImage() {
		// the three possibility, the user can choose one of theme.
		final CharSequence[] items = { "Take Photo", "Choose from Gallery", "Cancel" };

		// the alert dialog to show
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle("Add Photo!");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				// The onClick method will be executed when the use click an item from the CharSequence[] "items"
				// check which one is the clicked item 
				if (items[item].equals("Take Photo")) {
					/*
					 *  item = Take Photo
					 */
					// we declare an intent to open a new activity
					// in this case we will open MediaStore.ACTION_IMAGE_CAPTURE 
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					// the image name, we use here the KEY_NEXT_IMAGE_NUMBER,
					// like this we will have in our gallery takenPhoto1.jpg takenPhoto2.jpg ... 
					String takenPhoto = "takenPhoto"+ loadPrefs(KEY_NEXT_IMAGE_NUMBER) + ".jpg";
					// for each image we have a file, using the pathToSavePhoto/takenPhotoName
					File f = new File(pathToSavePhoto + File.separator,takenPhoto);
					// we pass the file as an arg to our next activity,
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
					// start the activity for result, with the arg REQUEST_CAMERA
					// this REQUEST_CAMERA is an id that we will use.
					startActivityForResult(intent, REQUEST_CAMERA);
				} else if (items[item].equals("Choose from Gallery")) {
					/*
					 *  item = Choose from Gallery
					 */
					// we declare an intent to open a new activity
					// in this case we will open MediaStore.Images.Media.EXTERNAL_CONTENT_URI
					// the action this time is ACTION_PICK
					Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					intent.setType("image/*");
					// start the activity for result, with the arg RESULT_LOAD_IMAGE
					// this RESULT_LOAD_IMAGE is an id that we will use.
					startActivityForResult(Intent.createChooser(intent, "Select File"),RESULT_LOAD_IMAGE);
				} else if (items[item].equals("Cancel")) {
					/*
					 *  item = Cancel
					 */
					// in this case, we dismiss the dialog without any treatment
					dialog.dismiss();
				}
			}
		});
		// for any alertDiakog, once you finish the treatment, you should show the builder
		builder.show();
	}
	
	/*
	 * Each time we use the startActivityForResult (see the load image method)
	 * We should implement this method
	 * this method will be executed after that:
	 *   ==> The user chose the image from the gallery
	 *   ==> The user take a photo using the deblurring app
	 */
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			// if we started the startActivityForResult with REQUEST_CAMERA
			// that's mean the user did chose the item = Take Photo
			// and because the resultCode == RESULT_OK that's mean the user did take a photo an save it 
			// here we will get this photo
			if (requestCode == REQUEST_CAMERA) {
				// we have to use the same file path, and the same image name 
				File f = new File(pathToSavePhoto + File.separator);
				String takenPhoto = "takenPhoto"+ loadPrefs(KEY_NEXT_IMAGE_NUMBER) + ".jpg";
				// we use a loop to check all file in the given path
				for (File temp : f.listFiles()) {
					// we compare the name of each file to our image name
					if (temp.getName().equals(takenPhoto)) {
						// once we find a file with the same name
						// we stock it in File "f" to use it after
						f = temp;
						// and we break the loop, because we don't need to search any more
						break;
					}
				}
				// end for
				try {
					// get the bitmap options
					BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
					// put our image (the f file) in our input bitmap
					bm = BitmapFactory.decodeFile(f.getAbsolutePath(),btmapOptions);
					// change the bitmap size to adapt it to our device screen
					bm = Bitmap.createScaledBitmap(bm, width - 70,height - 300, true);
					// show the input bitmap in our imageView
					imageView.setImageBitmap(bm);
					// change the value of isImageSelected from false to true 
					isImageSelected = true;

					// this treatment for saving the image after compressing the bm
					f.delete();
					OutputStream fOut = null;
					String takenPhoto2 = "takenPhoto"+ loadPrefs(KEY_NEXT_IMAGE_NUMBER) + ".jpg";

					File file = new File(pathToSavePhoto, takenPhoto2);
					try {
						fOut = new FileOutputStream(file);
						bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
						fOut.flush();
						fOut.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (requestCode == RESULT_LOAD_IMAGE) {
				// if we started the startActivityForResult with RESULT_LOAD_IMAGE
				// that's mean the user did chose the item = Choose from Gallery
				// and because the resultCode == RESULT_OK that's mean the user did chose a photo 
				// here we will get this photo
				// to do this we will use the arg data of startActivityForResult
				// remember that: protected void onActivityResult(int requestCode, int resultCode, Intent data)
				Uri selectedImageUri = data.getData();
				// we got the path using our method getPath
				String tempPath = getPath(selectedImageUri, MainActivity.this);
				
				// we do the same treatment (check this part for REQUEST_CAMERA)
				BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
				bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
				bm = Bitmap.createScaledBitmap(bm, width - 70, height - 300,true);
				imageView.setImageBitmap(bm);
				isImageSelected = true;
			}
		}
	}
	
	/*
	 * Choose Effect
	 */
	public void choseEffect() {

		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle("Select your effect: ");
		builder.setIcon(android.R.drawable.ic_menu_slideshow);
		builder.setItems(effectsList, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				// once the user chose the effect
				// we put the name in our string effectChosen
				effectChosen = (String) effectsList[item];
				textViewEffect.setText("Chosen effect: "+effectChosen);
				// we put the id in our idEffectChosen
				idEffectChosen = item+1;
				// we change isEffectSelected from false to true.
				isEffectSelected = true;
				// after that we dismiss the dialog
				dialog.dismiss();

			}
		}).show();
	}
	
	
	/*
	 * Apply Effect
	 */
	protected void applyEffect() {
		
		// Note: bm is our in image, bmOutImg is our out image
		
		// show the two options: save/cancel
		linearLayoutSave.setVisibility(View.VISIBLE);

		// to use the bmOutImg again, we recycle it
		if (bmOutImg != null) {
			bmOutImg.recycle();
			bmOutImg = null;
		}

		/*
		 * Now we are ready to apply the effect
		 * 
		 * The Darken effect is implemented in the MainActivity
		 * 
		 * The others are implemented in the cn.Ragnarok package, but with the same method,
		 *  
		 *  ===>  We need only to understand one effect.
		 * */
		
		// check if the effect is the darken one, or one from the others
		if (effectChosen.equals("Darken")) {
			
			// two int array (int[]) in/out
			int[] mPhotoIntArray;
			int[] mCannyOutArray;

			// we use the width and the heigth of our bm
			mPhotoIntArray = new int[bm.getWidth() * bm.getHeight()];
			// Copy pixel data from the Bitmap into the 'intArray' array
			bm.getPixels(mPhotoIntArray, 0, bm.getWidth(), 0, 0, bm.getWidth(),bm.getHeight());

			// we use the width and the heigth of our bm
			mCannyOutArray = new int[bm.getWidth() * bm.getHeight()];
			/*
			 * Call the darkenImage methode 
			 * 
			 * We declared this method as a native one, check bottom,
			 * there no treatment java, this method will call the C++ method in the Darken.cpp file
			 * */
			darkenImage(bm.getHeight(), bm.getWidth(), mPhotoIntArray,mCannyOutArray);
			// create our output
			bmOutImg = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(),Config.ARGB_8888);
			
			// adapt the mCannyOutArray to put it in our output
			bmOutImg.setPixels(mCannyOutArray, 0, bm.getWidth(), 0, 0,bm.getWidth(), bm.getHeight());
		} else {
			// if the user choose an other effect (other than darken)
			// we call the apply method
			apply(idEffectChosen);
		}

		// After applying the effect to our bm
		// we are sure that bmOutImg contain the result of the chosen effect
		// we set the image to our imageView 
		imageView.setImageBitmap(bmOutImg);

	}


	private void saveMethode() {
		/*
		 * we create the photo name using all those components
		 *  => the effectChosen name
		 *  => the string "photo"
		 *  => the KEY_NEXT_IMAGE_NUMBER (the i)
		 *  => the ".png" extension 
		 */
		int i = Integer.parseInt(loadPrefs(KEY_NEXT_IMAGE_NUMBER));
		String photoName = effectChosen+ "Photo" + i + ".png";
		
		/*
		 * we create the file name using all those components
		 *  => the pathToSavePhoto
		 *  => the File.separator
		 *  => the photoName
		 *  
		 *  >>>>> Result is:  pathToSavePhoto/photoName
		 */
		String outFileName = pathToSavePhoto + File.separator + photoName;
		
		// we call the OutputBitmapToFile to save the bmOutImg at outFileName
		// this method return true/false ::  imageSaved/canNotSaveImage
		if (OutputBitmapToFile(bmOutImg, outFileName)) {
			//if true (image saved)
			// we increment the i ( the KEY_NEXT_IMAGE_NUMBER) for next use
			i++;
			// than we save it
			SavePreferences(KEY_NEXT_IMAGE_NUMBER, "" + i);
			// we show the user the alert dialog to confirm saving
			linearLayoutSave.setVisibility(View.GONE);
			new AlertDialog.Builder(MainActivity.this)
					.setTitle("Message")
					.setMessage("Image has been saved.\nFile: /Pictures")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setPositiveButton("OK",null).show();
		} else {
			//if false (can not save image)
			// we show the user the alert dialog to confirm that
			new AlertDialog.Builder(MainActivity.this)
					.setTitle("Saving Image ...")
					.setMessage("Erreur, Image has not been saved.")
					.setIcon(android.R.drawable.ic_menu_save)
					.setCancelable(false)
					.setPositiveButton("OK", null)
					.show();
		}

	}

	/*
	 *  The OutputBitmapToFile
	 */
	public boolean OutputBitmapToFile(Bitmap InBm, String Filename) {
		// we use the ByteArrayOutputStream bytes
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		// we compress the given bitmap into bytes
		InBm.compress(Bitmap.CompressFormat.PNG, 100, bytes);

		// we instance a file using the  Filename
		File f = new File(Filename);
		try {
			// we create the file
			f.createNewFile();
			// write the bytes in file
			FileOutputStream fo = new FileOutputStream(f);
			fo.write(bytes.toByteArray());
			fo.close();
			// avery things ok : return true; 
			return true;
		} catch (Exception e) {
			// Something wrong : return false; 
			return false;
		}
	}

	/*
	 * getPath
	 */
	public String getPath(Uri uri, Activity activity) {
		String[] projection = { MediaColumns.DATA };
		@SuppressWarnings("deprecation")
		Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
	
	/**
	 * This is the declaration of the darkenImage method
	 * 
	 * the most important part is: native
	 * that's mean this method will call a native code (c++ in our example) Darken.cpp
	 * that's why we don't have any java treatment, all treatment in native side  
	 *
	 */
	public native boolean darkenImage(int width, int height, int[] mPhotoIntArray, int[] mCannyOutArray);

	// You have also to declare you library, check libs/armeabi-v7a
	// remark that we are using the name of our app
	static {
		System.loadLibrary("DeblurringAPP");
	}

	/*
	 * Apply Effect
	 */
	private void apply(int effectNumber) {
		
		// we use the effect id to know which effect the user chose
		
		/*
		 * like the darken effect is implemented in our mainActivity.java using:
		 * ===> public native boolean darkenImage
		 * ===> System.loadLibrary("DeblurringAPP");
		 * 
		 * >>>> You will find for each effect his class java with the native method, and the loadLibreary
		 */
		switch (effectNumber) {
		case BitmapFilter.AVERAGE_BLUR_STYLE:
			bmOutImg = BitmapFilter.changeStyle(bm,BitmapFilter.AVERAGE_BLUR_STYLE, 5); 
			break;
		case BitmapFilter.GAUSSIAN_BLUR_STYLE:
			bmOutImg = BitmapFilter.changeStyle(bm, BitmapFilter.GAUSSIAN_BLUR_STYLE, 1.2);
			break;
		case BitmapFilter.SOFT_GLOW_STYLE:
			bmOutImg = BitmapFilter.changeStyle(bm, BitmapFilter.SOFT_GLOW_STYLE, 0.6);
			break;
		case BitmapFilter.LIGHT_STYLE:
			int width = bm.getWidth();
			int height = bm.getHeight();
			bmOutImg = BitmapFilter.changeStyle(bm, BitmapFilter.LIGHT_STYLE,width / 3, height / 2, width / 2);
			break;
		case BitmapFilter.LOMO_STYLE:
			bmOutImg = BitmapFilter.changeStyle(bm, BitmapFilter.LOMO_STYLE,(bm.getWidth() / 2.0) * 95 / 100.0);
			break;
		case BitmapFilter.NEON_STYLE:
			bmOutImg = BitmapFilter.changeStyle(bm, BitmapFilter.NEON_STYLE,200, 100, 50);
			break;
		case BitmapFilter.PIXELATE_STYLE:
			bmOutImg = BitmapFilter.changeStyle(bm,BitmapFilter.PIXELATE_STYLE, 10);
			break;
		case BitmapFilter.MOTION_BLUR_STYLE:
			bmOutImg = BitmapFilter.changeStyle(bm,BitmapFilter.MOTION_BLUR_STYLE, 10, 1);
			break;
		case BitmapFilter.OIL_STYLE:
			bmOutImg = BitmapFilter.changeStyle(bm, BitmapFilter.OIL_STYLE, 5);
			break;
		default:
			bmOutImg = BitmapFilter.changeStyle(bm, effectNumber);
			break;
		}
	}
	
	// to save a given value, (we use it to save NEXT_IMAGE_NUMBER)
	public void SavePreferences(String key, String value) {
		SharedPreferences sh_Pref = getSharedPreferences("MyData",Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = sh_Pref.edit();
		edit.putString(key, value);
		edit.commit();
	}
	
	// to lode a value of a given key (we use it to load NEXT_IMAGE_NUMBER)
	public String loadPrefs(String KEY) {
		SharedPreferences sh_Pref = getSharedPreferences("MyData",Context.MODE_PRIVATE);
		return sh_Pref.getString(KEY, null);
	}


	// this method will be execute when the user jit the back button to exit the app
	// we will ask him a confirmation: "Are you sure you want to exit?" 
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			new AlertDialog.Builder(MainActivity.this)
					.setTitle("Confirm logout ...")
					.setMessage("Are you sure you want to exit?")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setPositiveButton("YES",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int which) {
									finish();
								}
							})
					.setNegativeButton("NO",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int which) {
									dialog.cancel();
								}
							}).show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}