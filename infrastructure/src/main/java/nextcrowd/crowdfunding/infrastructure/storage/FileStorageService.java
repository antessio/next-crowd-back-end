package nextcrowd.crowdfunding.infrastructure.storage;

import java.net.URI;
import java.util.Optional;

public interface FileStorageService {

    URI storeFile(byte[] file, String contentType);

    record StorageResource(byte[] content, String contentType) {
    }
    Optional<StorageResource> load(String id);

    Optional<StorageResource> loadFromUrl(String url);

    boolean isValidUrl(String url);
}
