/*
 * Copyright 2024 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.omnione.did.tas.v1.agent.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.property.TasProperty;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for loading files and extracting JSON values.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileLoaderService {
    private final Map<String, String> fileContentsMap = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TasProperty tasProperty;
    private final Environment environment;

    /**
     * Loads the contents of all files in the configured sample path.
     *
     * @throws IOException if an I/O error occurs while reading the files.
     */
    @PostConstruct
    public void loadFiles() throws IOException {
        List<String> activeProfiles = Arrays.asList(environment.getActiveProfiles());
        if (!activeProfiles.contains("sample")) {
            Path startPath = Paths.get(tasProperty.getSamplePath());

            Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (Files.isRegularFile(file)) {
                        String content = Files.readString(file);
                        fileContentsMap.put(file.getFileName().toString(), content);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    /**
     * Retrieves the contents of a file by name.
     *
     * @param fileName the name of the file to retrieve.
     * @return the contents of the file, or null if the file is not found.
     */
    public String getFileContent(String fileName) {
        return fileContentsMap.get(fileName);
    }
}
