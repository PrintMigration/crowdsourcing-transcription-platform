package mainApplication;

import java.util.Arrays;
import java.util.List;

public class Constants {
	public static final int TRANSCRIBER_CHECKOUT_LIMIT = 1000; // Change back to 3 after testing
	public static final int EDITOR_CHECKOUT_LIMIT = 1000; // Change back to 10 after testing
	public static final int TEI_ENCODER_CHECKOUT_LIMIT = 1000; // Change back to 10 after testing
	
	public static final String LOCAL_IMAGE_DIRECTORY = "/home/student/images/";
	public static final String EXTERNAL_IMAGE_DIRECTORY = "/images/";
	public static final String FINAL_STORAGE_TYPE = ".pdf";
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static class DocumentStatus {
		public static final String NEEDS_TRANSCRIBING = "Needs Transcribing";
		public static final String TRANSCRIBING = "Transcribing";
		public static final String NEEDS_EDITING = "Needs Editing";
		public static final String EDITING = "Editing";
		public static final String NEEDS_TEI_ENCODING = "Needs TEI Encoding";
		public static final String TEI_ENCODING = "TEI Encoding";
		public static final String COMPLETED = "Completed";
		public static final List<String> statusOrder = Arrays.asList(new String[] {NEEDS_TRANSCRIBING, TRANSCRIBING, NEEDS_EDITING, EDITING, NEEDS_TEI_ENCODING, TEI_ENCODING, COMPLETED});
	}
	
	public static class NotificationType {
		public static final String TRANSCRIPTION_COMPLETE = "Transcription complete";
		public static final String TRANSCRIPTION_RETURNED = "Transcription returned";
		public static final String EDIT_COMPLETE = "Edit complete";
		public static final String EDIT_RETURNED = "Edit returned";
		public static final String EDIT_RETURNED_TO_TRANSCRIBER = "Edit returned to transcriber";
		public static final String ENCODING_COMPLETE = "Encoding complete";
		public static final String ENCODING_RETURNED = "Encoding returned";
		public static final String REQUEST_APPROVED = "Request approved";
		public static final String REQUEST_DENIED = "Request denied";
		public static final String ROLE_ASSIGNED = "Role assigned";
		public static final String USER_DEACTIVATED = "User deactivated";
		public static final String DOCUMENT_ADDED = "Document added";
		public static final String MULTIPLE_DOCUMENTS_ADDED = "Multiple documents added";
		public static final String MULTIPLE_DOCUMENTS_ERROR = "Multiple documents error";
		public static final String DOCUMENT_UPDATED = "Document updated";
		public static final String DOCUMENT_DELETED = "Document deleted";
		public static final String DOCUMENT_STATUS_CHANGED = "Document status changed";
	}
	
	public static class Role {
		public static final String TRANSCRIBER = "Transcriber";
		public static final String EDITOR = "Editor";
		public static final String ENCODER = "Encoder";
		public static final String METADATA_EXPERT = "Metadata Expert";
		public static final String ADMINISTRATOR = "Administrator";
		public static final List<String> roleHierarchy = Arrays.asList(new String[] {TRANSCRIBER, EDITOR, ENCODER, METADATA_EXPERT, ADMINISTRATOR});
	}
	
	public static class TextType {
		public static final String TRANSCRIPTION = "Transcription";
		public static final String EDIT = "Edit";
		public static final String TEI_ENCODING = "TEI encoding";
	}
}
