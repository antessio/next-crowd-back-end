package nextcrowd.crowdfunding.infrastructure.storage;

public final class StorageUtils {

    private StorageUtils() {
    }

    public enum StorageLocation {
        FS,
        S3
    }

    public record StorageId(StorageLocation location, String contentType, String id) {

        private static final String ID_REGEX = ":";

        public static StorageId parse(String id) {
            String[] parts = id.split(ID_REGEX);
            String storageLocationPart = parts[0];
            String fileNamePart = parts[1];
            String extension = getFileExtension(fileNamePart);
            String uuid = fileNamePart.substring(0, fileNamePart.lastIndexOf('.'));
            return new StorageId(StorageLocation.valueOf(storageLocationPart.toUpperCase()), extensionToContentType(extension), uuid);
        }

        public FileType getFileType() {
            if (contentType.startsWith("image")) {
                return FileType.IMAGE;
            } else if (contentType.startsWith("video")) {
                return FileType.VIDEO;
            } else {
                throw new IllegalArgumentException("Unsupported content type: " + contentType);
            }
        }

        @Override
        public String toString() {
            return getFileType().name().toLowerCase() + "/" + location.name().toLowerCase() + ID_REGEX + id + contentTypeToExtension(contentType);
        }

    }

    public enum FileType {
        IMAGE,
        VIDEO
    }

    public static String contentTypeToExtension(String contentType) {
        switch (contentType) {
            case "image/jpeg":
                return ".jpg";
            case "image/png":
                return ".png";
            case "image/gif":
                return ".gif";
            case "image/heif":
                return ".heif";
            case "image/webp":
                return ".webp";
            case "video/mp4":
                return ".mp4";
            case "video/webm":
                return ".webm";
            case "video/ogg":
                return ".ogv";

            default:
                throw new IllegalArgumentException("Unsupported content type: " + contentType);

        }
    }

    // TODO add video contents
    // TODO add isVideo / isImage method in order to build the URL

    public static String extensionToContentType(String extension) {
        switch (extension) {
            case ".jpg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            case ".gif":
                return "image/gif";
            case ".heif":
                return "image/heif";
            case ".webp":
                return "image/webp";
            case ".mp4":
                return "video/mp4";
            case ".webm":
                return "video/webm";
            case ".ogv":
                return "video/ogg";
            default:
                throw new IllegalArgumentException("Unsupported extension: " + extension);
        }
    }

    public static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex >= 0) {
            return fileName.substring(dotIndex);
        } else {
            throw new IllegalArgumentException("File name does not have an extension: " + fileName);
        }
    }

}
