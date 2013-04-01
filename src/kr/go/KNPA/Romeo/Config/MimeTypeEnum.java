package kr.go.KNPA.Romeo.Config;

/**
 * Mime Type 목록
 * @author 최영우
 * @since 2013.4.1
 */
public enum MimeTypeEnum {

	pdf("application/pdf"),			
	xls("application/excel"),			
	ppt("application/powerpoint"),			
	zip("application/x-zip"),			
	mpeg("audio/mpeg"),			
	mp3("audio/mpeg"),			
	bmp("image/bmp"),
	gif("image/gif"),
	jpeg("image/jpeg"),
	jpg("image/jpeg"),
	png("image/png"),
	txt("text/plain"),
	text("text/plain"),
	xml("text/xml"),
	mpg("video/mpeg"),
	avi("video/x-msvideo"),
	doc("application/msword"),
	docx("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
	xlsx("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
	word("application/msword"),
	json("application/json");
	
	private String mimeType; 
    private MimeTypeEnum(String mime) {
    	this.mimeType = mime;
    }
}