package nextcrowd.crowdfunding.infrastructure.storage;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

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
    public Optional<StorageResource> loadImage(String id) {
        StorageUtils.StorageId storageId = StorageUtils.StorageId.parse(id);
        FileStorageService storageService = Optional.ofNullable(storageServices.get(storageId.location()))
                                                    .orElseThrow(() -> new IllegalStateException("No storage service available"));
        return storageService.loadImage(storageId.toString())
                             .map(storageResource -> new StorageResource(storageResource.content(), storageResource.contentType()));
    }




}
