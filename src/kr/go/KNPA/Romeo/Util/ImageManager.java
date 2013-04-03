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

/**
 *  이미지 파일 관리\n
 *  uri에서 이미지 가져오거나, 로컬 파일 시스템에서 이미지파일을 삭제하거나 로드\n
 *  또는 파일을 byte array로 또는 그 반대로 변환한다
 */
public class ImageManager {
	

	/**
	 * 비트맵 파일을 byte array로 바꾼다
	 * 전체 Media Scanning하지 않고 특정 파일만 스캐닝함
	 * @see http://www.androidpub.com/1953144
	 * @param $bitmap
	 * @return byteArray
	 */
	public static byte[] bitmapToByteArray( Bitmap $bitmap ) {  
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;  
        $bitmap.compress( CompressFormat.JPEG, 50, stream) ;  
        byte[] byteArray = stream.toByteArray() ;  
        return byteArray ;  
    }  
	
	/**
	 * byte array를 bitmap 파일로 변환
	 * @param $byteArray
	 * @return bitmap
	 */
	public static Bitmap byteArrayToBitmap( byte[] $byteArray ) {  
	    Bitmap bitmap = BitmapFactory.decodeByteArray( $byteArray, 0, $byteArray.length ) ;  
	    return bitmap ;  
	} 
	
	/**
	 * uri로부터 비트맵 파일 가져오기
	 * @param context
	 * @param uri
	 * @return bitmap
	 */
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
	
	/**
	 * uri에서 bitmap을 가져와서 path로 저장
	 * @param context
	 * @param picURI
	 * @param path
	 */
	public static void saveBitmapFromURIToPath(Context context, Uri picURI, String path) {
		saveBitmapToPath(context, bitmapFromURI(context, picURI), path);
	}
	
	/**
	 * uri에서 bitmap을 가져와서 path로 저장하되 포맷과 퀄리티를 추가로 설정
	 * @param context
	 * @param picURI
	 * @param path
	 * @param format
	 * @param quality
	 */
	public static void saveBitmapFromURIToPath(Context context, Uri picURI,String path, Bitmap.CompressFormat format, int quality) {
		saveBitmapToPath(context, bitmapFromURI(context, picURI), path, format, quality, true);
	}
	
	/**
	 * bitmap을 path에 저장. 포맷은 JPEG이고, 퀄리티는 80, internal = true
	 * @param context
	 * @param bitmap
	 * @param path
	 */
	public static void saveBitmapToPath(Context context, Bitmap bitmap, String path) {
		saveBitmapToPath(context, bitmap, path, Bitmap.CompressFormat.JPEG, 80, true);
	}
	
	/**
	 * bitmap을 path에 저장.
	 * @param context 메인 컨텍스트
	 * @param bitmap 비트맵 파일
	 * @param path 경로
	 * @param format 저장할 포맷
	 * @param quality 압축 품질(0-100)
	 * @param internal true이면 내부 버퍼에 저장, 아니면 내부 파일에 저장
	 */
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
	
	/**
	 * path에서 비트맵 로드 
	 * @param path
	 * @param internal
	 * @return
	 */
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
	
	/**
	 * path에서 비트맵 파일 삭제
	 * @param path 파일 이름을 포함한 경로
	 * @param internal
	 */
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
