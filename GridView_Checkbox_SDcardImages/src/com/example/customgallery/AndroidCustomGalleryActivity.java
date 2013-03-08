/**
 *  This sample reads the images from sd card you can read both thumbnails and actual images 
 *   
 *   Click on the image will take you to view it in Galley.
 *   
 *   Check box selection can be made and check it by "Select" button after selecting images  using checkbox
 * 
 */

package com.example.customgallery;

import java.io.File;
import java.net.URL;

import com.example.bouncelistview.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class AndroidCustomGalleryActivity extends Activity implements OnItemClickListener {
	private int count;
	static Bitmap[] thumbnails;
	private boolean[] thumbnailsselection;
	
	static String[] arrPath;
	
	private ImageAdapter imageAdapter;
	Cursor myCursor;
	static String thumbnailPath[];

	int j=0;
	
	Cursor imagecursor;
	int image_column_index;
	ProgressDialog dialog;
	GridView imagegrid;
	
	/** Called when the activity is first created. */
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		imagegrid  = (GridView) findViewById(R.id.PhoneImageGrid);
		imagegrid.setOnItemClickListener(this);
		
		Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

		if(isSDPresent)
		{
			dialog=ProgressDialog.show(this, "", "Images loading...");
			doback dob=new doback();
	        dob.execute();
		}
		else
		{
			Toast.makeText(getApplicationContext(),
					"Sorry,No SD Card Found On This Device", 
					Toast.LENGTH_LONG).show();
		}
		
				
	    
		
		final Button selectBtn = (Button) findViewById(R.id.selectBtn);
		selectBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
			   int len = thumbnailsselection.length;
			   int cnt = 0;
				String selectImages = "";
				for (int i =0; i<len; i++)
				{
					if (thumbnailsselection[i]){
						cnt++;
						selectImages = selectImages + arrPath[i] + "|";
					}
				}
				String[] split=selectImages.split("|");
				 Log.v("length",""+split.length+" "+cnt);
				if (cnt == 0)
				{
					Toast.makeText(getApplicationContext(),
							"Please select at least one image", 
							Toast.LENGTH_LONG).show();
				} 
				else {
					Toast.makeText(getApplicationContext(),
							"You've selected Total " + cnt +"----"+ selectImages + " image(s).",
							Toast.LENGTH_LONG).show();
					Log.d("SelectedImages", selectImages); 
					
					
					
				}
			}
		});
	}
	class doback extends AsyncTask<URL, Integer, Long>
	 {

	  protected Long doInBackground(URL... arg0) 
	  {
	   try
	   {
		    
        gettingImagesFromSDcard();
	   }
	   catch(Exception e)
	   {
	    
	   }
	   return null;
	  }
	  protected void onProgressUpdate(Integer... progress) 
	  {
	   
	  }
	  protected void onPostExecute(Long result) 
	  {
	   try
	   {
		
		imageAdapter = new ImageAdapter();
		imagegrid.setAdapter(imageAdapter);
	    imagecursor.close();
		dialog.dismiss();
	   }
	   catch(Exception e)
	   {
	    e.printStackTrace();
	    dialog.dismiss();
	   }
	  }
	 }
    @SuppressWarnings("deprecation")
	void  gettingImagesFromSDcard()
    {
    	/**
		 * To Read thumb images you can use below Code. 
		 */
    	 //Specify the columns to read
    	 String[] columns = { MediaStore.Images.Thumbnails.DATA, MediaStore.Images.Thumbnails._ID,MediaStore.Images.Thumbnails.KIND, MediaStore.Images.Thumbnails.IMAGE_ID };
		 String orderBy = MediaStore.Images.Thumbnails._ID;
         String selection1 = MediaStore.Images.Thumbnails.KIND + "="  + // Select only mini's
	                       MediaStore.Images.Thumbnails.MINI_KIND;
		
         imagecursor = managedQuery(
 				MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, columns, selection1,
 				null, orderBy);
 	     image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID);
		
		this.count =  imagecursor.getCount();
		this.thumbnails = new Bitmap[this.count];
		this.arrPath = new String[this.count];
		this.thumbnailPath = new String[this.count];
		this.thumbnailsselection = new boolean[this.count];
		
		for (int i = 0; i < this.count; i++) 
		{
			imagecursor.moveToPosition(i);
			
				
			int id = imagecursor.getInt(image_column_index);
			thumbnails[i] = MediaStore.Images.Thumbnails.getThumbnail(
					getApplicationContext().getContentResolver(), id,
					MediaStore.Images.Thumbnails.MINI_KIND, null);
			
			
			int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA);
			
		    thumbnailPath[i]= imagecursor.getString(imagecursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA));
			arrPath[i]= imagecursor.getString(dataColumnIndex);
	

		   
		}
		
		/**
		 * To Read Big images from Camera folder you can use below Code. 
		 */
		
		/*String[] projection = new String[]{
	            MediaStore.Images.Media._ID,
	             MediaStore.Images.Media.DATA, // add DATA column
	            MediaStore.Images.Media.DATE_TAKEN,
	            MediaStore.Images.Media.TITLE,

	    };

	    // Get the base URI for the People table in the Contacts content provider.
	    Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	    Log.i("URI", images.toString());

	            // Make the query.
	    Cursor cur = managedQuery(
	            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
	            projection, // Which columns to return
	            null,         // Which rows to return (all rows)
	            null,       // Selection arguments (none)
	            MediaStore.Images.Media._ID         // Ordering
	            );

	    Log.i("ListingImages"," query count="+cur.getCount());

	    if (cur.moveToFirst()) {
	        String bucket;
	        String date;
	        String name;

	        int dateColumn = cur.getColumnIndex(
	            MediaStore.Images.Media.DATE_TAKEN);

	        int nameColumn = cur.getColumnIndex(
	                MediaStore.Images.Media.TITLE);


	            // Get the field values
	            date = cur.getString(dateColumn);
	            name = cur.getString(nameColumn);
	          int columnIndex = cur.getColumnIndex(MediaStore.Images.Media.DATA);
	          String picPath = cur.getString(columnIndex);

	            
	            // Do something with the values.
	            Log.i("ListingImages", 
	                   "  name_taken=" + name+" picPath= "+picPath);

	    }*/

    }
	public class ImageAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public ImageAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return count;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(
						R.layout.galleryitem, null);
				holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);
				holder.checkbox = (CheckBox) convertView.findViewById(R.id.itemCheckBox);
				
				convertView.setTag(holder);
			}
			else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.checkbox.setId(position);
			holder.imageview.setId(position);
			
			holder.checkbox.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					CheckBox cb = (CheckBox)v;
					int id = cb.getId();
					if (thumbnailsselection[id]){
						cb.setChecked(false);
						thumbnailsselection[id] = false;
					} else {
						cb.setChecked(true);
						cb.setVisibility(View.VISIBLE);
						thumbnailsselection[id] = true;
					}
				}
			});
			holder.imageview.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int id = v.getId();
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.parse("file://" + arrPath[id]), "image/*");
					startActivity(intent);
				}
			});
			
			holder.imageview.setImageBitmap(thumbnails[position]);
			holder.checkbox.setChecked(thumbnailsselection[position]);
		
			holder.id = position;
			return convertView;
		}
	}
	class ViewHolder {
		ImageView imageview;
		CheckBox checkbox;
		int id;
	}
	
	public void onItemClick(AdapterView<?> arg0, View v, int pos, long arg3)
    {

	}
}