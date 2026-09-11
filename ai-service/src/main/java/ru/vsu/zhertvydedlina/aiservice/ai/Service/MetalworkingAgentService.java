package ru.vsu.zhertvydedlina.aiservice.ai.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.vsu.zhertvydedlina.aiservice.calculation.request.CalculationResult;
import ru.vsu.zhertvydedlina.aiservice.ai.dto.response.AgentResponse;
import ru.vsu.zhertvydedlina.aiservice.ai.dto.response.ExtractedParametersDto;
import ru.vsu.zhertvydedlina.aiservice.ai.dto.response.ValidationResult;
import ru.vsu.zhertvydedlina.aiservice.converter.DxfConverterService;
import ru.vsu.zhertvydedlina.aiservice.excel.ExcelCommercialOfferService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetalworkingAgentService {

    private final AiService aiService;
    private final GigaChatFileService fileService;
    private final DxfConverterService dxfConverterService;

    private final ParameterValidationService validationService;
    private final CalculationEngine calculationEngine;
    private final ExcelCommercialOfferService excelCommercialOfferService;

    public AgentResponse processRequest(
            String userText,
            List<MultipartFile> files) {

        List<String> gigaChatFileIds =
                new ArrayList<>();

        List<File> tempFiles =
                new ArrayList<>();

        try {

            /*
             * 1. Обрабатываем файлы
             */
            if (files != null) {

                for (MultipartFile file : files) {

                    if (file == null ||
                            file.isEmpty()) {
                        continue;
                    }

                    File fileToUpload =
                            prepareFile(file);

                    tempFiles.add(fileToUpload);

                    String fileId =
                            fileService.uploadFromFile(
                                    fileToUpload
                            );

                    gigaChatFileIds.add(fileId);
                }
            }

            /*
             * 2. ИИ извлекает параметры
             */
            ExtractedParametersDto params =
                    aiService.extractParameters(
                            userText,
                            gigaChatFileIds
                    );

            /*
             * 3. Backend проверяет полноту
             */
            ValidationResult validation =
                    validationService.validate(params);

            if (!validation.isValid()) {

                params.setDataSufficient(false);
                params.setClarifyingQuestion(
                        validation.getQuestion()
                );

                return AgentResponse.needClarification(
                        validation.getQuestion(),
                        params
                );
            }

            params.setDataSufficient(true);

            /*
             * 4. Backend считает стоимость
             */
            CalculationResult result =
                    calculationEngine.calculate(
                            params
                    );

            /*
             * 5. Формируем Excel КП
             */
            byte[] excel =
                    excelCommercialOfferService.generate(
                            params,
                            result
                    );

            return AgentResponse.success(
                    excel,
                    params
            );

        } catch (Exception e) {

            log.error(
                    "Ошибка обработки заявки",
                    e
            );

            return AgentResponse.error(
                    "Ошибка при расчёте: "
                            + e.getMessage()
            );

        } finally {

            for (File file : tempFiles) {
                dxfConverterService
                        .deleteFileIfExists(file);
            }
        }
    }

    private File prepareFile(
            MultipartFile multipartFile)
            throws Exception {

        String filename =
                multipartFile
                        .getOriginalFilename();

        if (filename == null) {
            throw new IllegalArgumentException(
                    "У файла отсутствует имя."
            );
        }

        String lower =
                filename.toLowerCase();

        if (lower.endsWith(".dxf")) {

            log.info(
                    "Конвертация DXF → PDF"
            );

            return dxfConverterService
                    .convertDxfToPdf(
                            multipartFile
                    );
        }

        if (lower.endsWith(".pdf") ||
                lower.endsWith(".png") ||
                lower.endsWith(".jpg") ||
                lower.endsWith(".jpeg")) {

            String extension =
                    lower.substring(
                            lower.lastIndexOf(".")
                    );

            Path temp =
                    Files.createTempFile(
                            "metalworking-",
                            extension
                    );

            multipartFile.transferTo(
                    temp.toFile()
            );

            return temp.toFile();
        }

        if (lower.endsWith(".dwg")) {

            throw new IllegalArgumentException(
                    "Формат DWG пока не поддерживается."
            );
        }

        if (lower.endsWith(".dks")) {

            /*
             * Здесь позже будет:
             *
             * DKS → PDF/PNG → GigaChat
             *
             * Нельзя просто отправить DKS
             * в GigaChat.
             */

            throw new IllegalArgumentException(
                    "Формат DKS пока требует отдельного конвертера."
            );
        }

        throw new IllegalArgumentException(
                "Неподдерживаемый формат файла: "
                        + filename
        );
    }
}