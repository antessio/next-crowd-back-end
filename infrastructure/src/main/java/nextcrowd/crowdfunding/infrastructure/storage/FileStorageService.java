package nextcrowd.crowdfunding.infrastructure.storage;

import java.net.URI;
import java.util.Optional;

import nextcrowd.crowdfunding.project.model.UploadedResource;

public interface FileStorageService {

    URI storeFile(byte[] file, String contentType);

    UploadedResource uploadFile(byte[] file, String contentType);

    record StorageResource(byte[] content, String contentType) {
    }
    Optional<StorageResource> load(StorageUtils.StorageId id);

    Optional<StorageResource> loadFromUrl(String url);

    boolean isValidUrl(String url);
}
