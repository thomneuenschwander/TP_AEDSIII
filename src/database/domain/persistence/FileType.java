package database.domain.persistence;

public enum FileType {
    BIN(".bin"),
    DB(".db"),
    TXT(".txt"),
    JPEG(".jpeg"),
    PNG(".png"),
    MP3(".mp3"),
    MP4(".mp4"),
    PDF(".pdf"),
    DOCX(".docx"),
    XLSX(".xlsx"),
    OTHER("");

    private final String extension;

    FileType(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public static FileType fromExtension(String extension) {
        for (FileType fileType : values()) {
            if (fileType.getExtension().equalsIgnoreCase(extension)) {
                return fileType;
            }
        }
        return OTHER;
    }
}
