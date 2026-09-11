package ru.vsu.zhertvydedlina.aiservice.ai.Controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.vsu.zhertvydedlina.aiservice.ai.Service.AiService;
import ru.vsu.zhertvydedlina.aiservice.ai.Service.MetalworkingAgentService;
import ru.vsu.zhertvydedlina.aiservice.ai.dto.response.AgentResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;
    private final MetalworkingAgentService agentService;
    private static final Logger log = LoggerFactory.getLogger(AiController.class);

    @GetMapping("/test")
    public String test() {
        return aiService.ask(
                "Ответь одним предложением: что такое металл?"
        );
    }

    /**
     * Основной эндпоинт для анализа запроса и расчета стоимости.
     * Принимает текст и опционально список файлов (чертежи, эскизы).
     */
    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> analyzeRequest(
            @RequestPart("text") String text,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        log.info("Получен запрос на анализ. Текст: {}, Файлов: {}", text, files != null ? files.size() : 0);

        try {
            AgentResponse response = agentService.processRequest(text, files);

            // Если расчет успешен и есть файл, отдаем его как скачиваемый документ
            if ("SUCCESS".equals(response.getStatus()) && response.getExcelFileBytes() != null) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"kommercheskoe_predlozhenie.xlsx\"")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(response.getExcelFileBytes());
            }

            // Если нужны уточнения или произошла ошибка, возвращаем JSON
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // Например, если загрузили неподдерживаемый .dks
            log.warn("Ошибка валидации запроса: {}", e.getMessage());
            return ResponseEntity.badRequest().body(AgentResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(AgentResponse.error("Внутренняя ошибка сервера при расчете"));
        }
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "AI Service is running";
    }
}