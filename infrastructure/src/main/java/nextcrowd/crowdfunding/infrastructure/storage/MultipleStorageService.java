package nextcrowd.crowdfunding.infrastructure.storage;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import nextcrowd.crowdfunding.project.model.UploadedResource;

@Component
public class MultipleStorageService implements FileStorageService {

    private final Map<StorageUtils.StorageLocation, FileStorageService> storageServices;

    public MultipleStorageService(Map<StorageUtils.StorageLocation, FileStorageService> storageServices) {
        this.storageServices = storageServices;
    }

    @Override
    public URI storeFile(byte[] file, String contentType) {
        return Stream.of(StorageUtils.StorageLocation.FS, StorageUtils.StorageLocation.S3)
                     .filter(storageServices::containsKey)
                     .map(storageServices::get)
                     .findFirst()
                     .map(storageService -> storageService.storeFile(file, contentType))
                     .orElseThrow(() -> new IllegalStateException("No storage service available"));
    }

    @Override
    public UploadedResource uploadFile(byte[] file, String contentType) {
        return Stream.of(StorageUtils.StorageLocation.FS, StorageUtils.StorageLocation.S3)
                     .filter(storageServices::containsKey)
                     .map(storageServices::get)
                     .findFirst()
                     .map(storageService -> storageService.uploadFile(file, contentType))
                     .orElseThrow(() -> new IllegalStateException("No storage service available"));
    }

    @Override
    public Optional<StorageResource> load(StorageUtils.StorageId storageId) {
        FileStorageService storageService = Optional.ofNullable(storageServices.get(storageId.location()))
                                                    .orElseThrow(() -> new IllegalStateException("No storage service available"));
        return storageService.load(storageId)
                             .map(storageResource -> new StorageResource(storageResource.content(), storageResource.contentType()));
    }

    @Override
    public Optional<StorageResource> loadFromUrl(String url) {
        return Stream.of(StorageUtils.StorageLocation.FS, StorageUtils.StorageLocation.S3)
                     .map(storageServices::get)
                     .filter(storageService -> storageService.isValidUrl(url))
                     .findFirst()
                     .flatMap(storageService -> storageService.loadFromUrl(url));

    }

    @Override
    public boolean isValidUrl(String url) {
        return Stream.of(StorageUtils.StorageLocation.FS, StorageUtils.StorageLocation.S3)
                     .map(storageServices::get)
                     .anyMatch(storageService -> storageService.isValidUrl(url));
    }


}
