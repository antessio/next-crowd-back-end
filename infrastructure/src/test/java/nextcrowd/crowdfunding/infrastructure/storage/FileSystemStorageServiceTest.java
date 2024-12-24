package nextcrowd.crowdfunding.infrastructure.storage;


import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FileSystemStorageServiceTest {

    private FileSystemStorageService fileSystemStorageService;
    private final static String BASE_URL = "http://localhost:8080";
    private final static String STORAGE_DIRECTORY = "/tmp/.nextcrowd";

    @BeforeAll
    static void beforeAll() throws IOException {
        Files.createDirectories(Path.of(STORAGE_DIRECTORY));
        Files.createDirectories(Path.of(STORAGE_DIRECTORY + "/image"));
        Files.createDirectories(Path.of(STORAGE_DIRECTORY + "/video"));

    }

    @AfterAll
    static void afterAll() throws IOException {
        Files.walk(Path.of(STORAGE_DIRECTORY))
             .sorted((a, b) -> b.getNameCount() - a.getNameCount())
             .forEach(path -> {
                 try {
                     Files.delete(path);
                 } catch (IOException e) {
                     throw new RuntimeException(e);
                 }
             });

    }

    @BeforeEach
    void setUp() {
        fileSystemStorageService = new FileSystemStorageService(Path.of(STORAGE_DIRECTORY), BASE_URL);
    }

    @Test
    void storeFile() throws IOException {
        byte[] fileContent = "test content".getBytes();
        String contentType = "image/png";

        URI fileUri = fileSystemStorageService.storeFile(fileContent, contentType);

        assertNotNull(fileUri);
        assertTrue(fileUri.toString().startsWith(BASE_URL));

        Path storedFilePath = Path.of(STORAGE_DIRECTORY, fileUri.toString().substring(BASE_URL.length()));
        assertTrue(Files.exists(storedFilePath));
        assertArrayEquals(fileContent, Files.readAllBytes(storedFilePath));
    }

    @Test
    void storeFileInvalidContentType() {
        byte[] fileContent = "test content".getBytes();
        String contentType = "application/pdf";

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> fileSystemStorageService.storeFile(fileContent, contentType))
                .matches(e -> e.getMessage().contains("Unsupported content type"));
    }

    @Test
    void load() {
        byte[] fileContent = "test content".getBytes();
        String contentType = "image/png";
        URI fileUri = fileSystemStorageService.storeFile(fileContent, contentType);
        String id = fileUri.toString().replace(BASE_URL, "");
        FileSystemStorageService.StorageResource storageResource = fileSystemStorageService.load(StorageUtils.StorageId.parse(id))
                                                                                           .orElseThrow();

        assertArrayEquals(fileContent, storageResource.content());
        assertEquals(storageResource.contentType(), contentType);
    }

}