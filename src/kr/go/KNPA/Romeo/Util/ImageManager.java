package kr.go.KNPA.Romeo.Util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.util.Log;

public class ImageManager {
	
	// 전체 Media Scanning하지 않고 특정 파일만 스캐닝 하기 :: http://www.androidpub.com/1953144
	public static byte[] bitmapToByteArray( Bitmap $bitmap ) {  
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;  
        $bitmap.compress( CompressFormat.JPEG, 50, stream) ;  
        byte[] byteArray = stream.toByteArray() ;  
        return byteArray ;  
    }  
	
	public static Bitmap byteArrayToBitmap( byte[] $byteArray ) {  
	    Bitmap bitmap = BitmapFactory.decodeByteArray( $byteArray, 0, $byteArray.length ) ;  
	    return bitmap ;  
	} 
	
	public static Bitmap bitmapFromURI (Context context, Uri uri) {
		Bitmap b = null;
		try {
			b =  Images.Media.getBitmap(context.getContentResolver(), uri);
//			String str = Encrypter.sharedEncrypter().encrypteString(new String(Encrypter.bitmapToByteArray(b)));
//			byte[] bt = Encrypter.sharedEncrypter().decrypteString(str).getBytes();
//			b = Encrypter.byteArrayToBitmap(bt);
			b = Bitmap.createScaledBitmap(b, 100, 100, true);
		} catch (FileNotFoundException e) {
			Log.d("ImageManager", "FileNotFoundException");
		} catch (IOException e) {
			Log.d("ImageManager", "IOException");
		}
		
		return b;
	}
	
	public static void saveBitmapFromURIToPath(Context context, Uri picURI, String path) {
		saveBitmapToPath(context, bitmapFromURI(context, picURI), path);
	}
	
	public static void saveBitmapFromURIToPath(Context context, Uri picURI,String path, Bitmap.CompressFormat format, int quality) {
		saveBitmapToPath(context, bitmapFromURI(context, picURI), path, format, quality, true);
	}
	
	public static void saveBitmapToPath(Context context, Bitmap bitmap, String path) {
		saveBitmapToPath(context, bitmap, path, Bitmap.CompressFormat.JPEG, 80, true);
	}
	
	public static void saveBitmapToPath(Context context, Bitmap bitmap, String path, Bitmap.CompressFormat format, int quality, boolean internal) {
		
		
		BufferedOutputStream out = null; 
		
		try {
			
			if(internal) {
				String[] _paths = path.split("/");
				String fileName = _paths[_paths.length-1];//null;
				out = new BufferedOutputStream(context.openFileOutput(fileName, 0));
			} else {
				File picFile = new File(path);
				picFile.createNewFile();
				FileOutputStream fos = new FileOutputStream(picFile);
				out = new BufferedOutputStream(fos);
			}
	        
	        bitmap.compress(format, quality, out);
	        out.flush();
        } catch (Exception e) {
        } finally {
        	try {
        		out.close();
        	} catch (IOException e) {
        	}
        }
	}
	
	public static Bitmap loadBitmapFromPath(String path, boolean internal) {
		if(internal) {
			path = "data/data/kr.go.KNPA.Romeo/files/"+path;
		}
		
		File file = new File(path);
		if (file.exists() == false) {
			Log.w("ImageManager", "during load bitmap from path, File Not Exists");
			return null;
		}
		   
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		   
		return bitmap;
	}
	
	public static void deleteBitmapFromPath(String path, boolean internal) {
		try{
			String parentPath = "";
			String fileName = null;
			
			String[] _paths = path.split("/");
			if(_paths.length > 1) {
				for(int i=0; i<(_paths.length-1);i++) {
					parentPath += _paths[i];
					parentPath += "/";
				}
			}
			fileName = _paths[_paths.length-1];
			
			if( internal) {
				parentPath = "data/data/kr.go.KNPA.Romeo/files/" + parentPath;
			}
			
			File file = new File(parentPath);
			File[] flist = file.listFiles();
			
			for(int i = 0 ; i < flist.length ; i++) {
				String fname = flist[i].getName();
				if(fname.equals(fileName)) {
					flist[i].delete();
				}
			}
		} catch(Exception e) {
			Log.wtf("ImageManager", "파일 삭제 실패");
		}


	}
}
