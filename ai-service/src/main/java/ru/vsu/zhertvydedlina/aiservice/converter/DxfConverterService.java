package ru.vsu.zhertvydedlina.aiservice.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@Service
public class DxfConverterService {

    // Директория для временных файлов (можно вынести в application.yml)
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + "/cad_conversions/";

    public DxfConverterService() {
        // Создаем папку при старте, если её нет
        new File(TEMP_DIR).mkdirs();
    }

    /**
     * Конвертирует DXF в PDF с помощью Inkscape CLI.
     * @param file Исходный файл (MultipartFile)
     * @return Сконвертированный File в формате PDF
     */
    public File convertDxfToPdf(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".dxf")) {
            throw new IllegalArgumentException("Поддерживается только формат .dxf");
        }

        String uniqueId = UUID.randomUUID().toString();
        Path inputPath = Path.of(TEMP_DIR + uniqueId + ".dxf");
        Path outputPath = Path.of(TEMP_DIR + uniqueId + ".pdf");

        try {
            // Сохраняем загруженный файл во временную директорию
            file.transferTo(inputPath.toFile());

            // Формируем команду для Inkscape
            // --export-filename указывает выходной файл, тип определяется по расширению (.pdf)
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "inkscape",
                    inputPath.toAbsolutePath().toString(),
                    "--export-filename=" + outputPath.toAbsolutePath().toString(),
                    "--export-type=pdf"
            );

            log.info("Запуск конвертации DXF в PDF: {}", inputPath.getFileName());

            // Запускаем процесс и ждем завершения
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                // Читаем ошибку из потока, если конвертация не удалась
                String errorOutput = new String(process.getErrorStream().readAllBytes());
                log.error("Ошибка Inkscape (код {}): {}", exitCode, errorOutput);
                throw new RuntimeException("Не удалось сконвертировать DXF файл. Ошибка Inkscape.");
            }

            if (!Files.exists(outputPath)) {
                throw new RuntimeException("Файл PDF не был создан после конвертации.");
            }

            log.info("Успешная конвертация в: {}", outputPath.getFileName());
            return outputPath.toFile();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(
                    "Конвертация была прервана", e
            );
        } catch (IOException e) {
            throw new RuntimeException(
                    "Ошибка при запуске конвертера", e
            );
        } finally {
            // Очищаем исходный DXF файл
            deleteFileIfExists(inputPath.toFile());
            // удаление должно происходить ПОСЛЕ успешной загрузки в GigaChat (см. Шаг 3).
        }
    }

    public void deleteFileIfExists(File file) {
        if (file != null && file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                log.warn("Не удалось удалить временный файл: {}", file.getAbsolutePath());
            }
        }
    }
}