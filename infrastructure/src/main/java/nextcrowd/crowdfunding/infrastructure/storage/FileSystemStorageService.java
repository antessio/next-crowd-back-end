package nextcrowd.crowdfunding.infrastructure.storage;

import static nextcrowd.crowdfunding.infrastructure.storage.StorageUtils.contentTypeToExtension;
import static nextcrowd.crowdfunding.infrastructure.storage.StorageUtils.extensionToContentType;
import static nextcrowd.crowdfunding.infrastructure.storage.StorageUtils.getFileExtension;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import com.github.f4b6a3.uuid.UuidCreator;

public class FileSystemStorageService implements FileStorageService {

    private final Path storageDirectory;
    private final String baseUrl;

    public FileSystemStorageService(Path storageDirectory, String baseUrl) {
        this.storageDirectory = storageDirectory;
        this.baseUrl = baseUrl;
    }

    @Override
    public URI storeFile(byte[] file, String contentType) {
        // Implement the logic to store the file on the filesystem
        // For example, you can use java.nio.file.Files to write the file to a specific directory
        try {
            StorageUtils.StorageId storageId = new StorageUtils.StorageId(
                    StorageUtils.StorageLocation.FS,
                    contentType,
                    UuidCreator.getRandomBased().toString());
            String id =  storageId.toString();
            Path filePath = storageDirectory.resolve(id);
            Files.write(filePath, file);
            return URI.create(baseUrl + id);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }


    @Override
    public Optional<StorageResource> load(String id) {
        try {
            Path filePath = storageDirectory.resolve(id);
            if (Files.exists(filePath)) {
                byte[] content = Files.readAllBytes(filePath);
                String contentType = extensionToContentType(getFileExtension(id));
                return Optional.of(new StorageResource(content, contentType));
            } else {
                return Optional.empty();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image", e);
        }
    }


}
