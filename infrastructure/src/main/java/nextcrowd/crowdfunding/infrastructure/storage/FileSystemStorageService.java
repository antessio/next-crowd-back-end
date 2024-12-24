package nextcrowd.crowdfunding.infrastructure.storage;

import static nextcrowd.crowdfunding.infrastructure.storage.StorageUtils.extensionToContentType;
import static nextcrowd.crowdfunding.infrastructure.storage.StorageUtils.getFileExtension;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import com.github.f4b6a3.uuid.UuidCreator;

import nextcrowd.crowdfunding.project.model.UploadedResource;
import nextcrowd.crowdfunding.project.model.UploadedResourceId;

public class FileSystemStorageService implements FileStorageService {

    public static final String PUBLIC_PATH = "/public/";
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
            String id = storageId.toString();
            Path filePath = storageDirectory.resolve(id);
            Files.write(filePath, file);
            return URI.create(getBaseUrl() + id);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    private String getBaseUrl() {
        if (baseUrl.endsWith("/")) {
            return baseUrl;
        }else{
            return baseUrl + "/";
        }
    }

    @Override
    public UploadedResource uploadFile(byte[] file, String contentType) {
        try {
            String id = UuidCreator.getRandomBased().toString();
            StorageUtils.StorageId storageId = new StorageUtils.StorageId(
                    StorageUtils.StorageLocation.FS,
                    contentType,
                    id);
            Path filePath = storageDirectory.resolve(storageId.toString());
            Files.write(filePath, file);
            String urlPath = PUBLIC_PATH + storageId;
            URI url = URI.create(getBaseUrl() + urlPath);
            // extract path from URI

            return UploadedResource.builder()
                                   .location(UploadedResource.Location.LOCAL)
                                   .id(new UploadedResourceId(storageId.getId()))
                                   .url(url.toString())
                                   .contentType(contentType)
                                   .path(urlPath)
                                   .build();
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }


    @Override
    public Optional<StorageResource> load(StorageUtils.StorageId id) {
        try {
            Path filePath = storageDirectory.resolve(id.toString());
            if (Files.exists(filePath)) {
                byte[] content = Files.readAllBytes(filePath);
                String contentType = extensionToContentType(getFileExtension(id.toString()));
                return Optional.of(new StorageResource(content, contentType));
            } else {
                return Optional.empty();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image", e);
        }
    }

    @Override
    public Optional<StorageResource> loadFromUrl(String url) {
        return Optional.of(url)
                       .map(u -> u.replace(getBaseUrl() + PUBLIC_PATH, ""))
                       // take only the last part of the url
                       //.map(u -> u.substring(u.lastIndexOf('/') + 1))
                       .map(StorageUtils.StorageId::parse)
                       .flatMap(this::load);
    }

    @Override
    public boolean isValidUrl(String url) {
        return Optional.of(url)
                       .map(u -> u.replace(getBaseUrl(), ""))
                       .map(u -> u.substring(u.lastIndexOf('/') + 1))
                       .map(StorageUtils.StorageId::parse)
                       .map(StorageUtils.StorageId::location)
                       .filter(StorageUtils.StorageLocation.FS::equals)
                       .isPresent();
    }


}
