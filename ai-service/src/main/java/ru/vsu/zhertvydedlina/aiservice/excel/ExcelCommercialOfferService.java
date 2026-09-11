package ru.vsu.zhertvydedlina.aiservice.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.vsu.zhertvydedlina.aiservice.calculation.request.CalculationResult;
import ru.vsu.zhertvydedlina.aiservice.ai.dto.response.ExtractedParametersDto;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

@Slf4j
@Service
public class ExcelCommercialOfferService {

    @Value("${app.excel.template-path}")
    private String templatePath;

    public byte[] generate(
            ExtractedParametersDto params,
            CalculationResult result) {

        File template =
                new File(templatePath);

        if (!template.exists()) {
            throw new IllegalStateException(
                    "Excel-шаблон не найден: "
                            + template.getAbsolutePath()
            );
        }

        try (
                FileInputStream input =
                        new FileInputStream(template);

                Workbook workbook =
                        WorkbookFactory.create(input);

                ByteArrayOutputStream output =
                        new ByteArrayOutputStream()
        ) {

            Sheet calculation =
                    workbook.getSheet("Расчёт");

            Sheet kp =
                    workbook.getSheet("КП с ндс");

            if (calculation == null) {
                throw new IllegalStateException(
                        "В шаблоне отсутствует лист 'Расчёт'"
                );
            }

            if (kp == null) {
                throw new IllegalStateException(
                        "В шаблоне отсутствует лист 'КП с ндс'"
                );
            }

            /*
             * Первый блок расчёта:
             * строки 3-8.
             *
             * Здесь заполняем данные,
             * а не создаём новый Excel.
             */

            setString(
                    calculation,
                    "C3",
                    params.getMaterial()
            );

            /*
             * D3 — цена материала.
             * Здесь позже можно поставить
             * актуальную цену из PricingService.
             */

            /*
             * E3 — количество листов /
             * площадь для покраски.
             */

            setNumber(
                    calculation,
                    "E3",
                    params.getQuantity()
            );

            /*
             * Часы лазера.
             */
            if (params.getOperations() != null &&
                    params.getOperations().getLaser() != null) {

                // TODO:
                // поставить фактически рассчитанное
                // количество часов.
            }

            /*
             * ВАЖНО:
             * сейчас мы не заставляем POI
             * вычислять формулы как Excel.
             *
             * Мы записываем итоговые значения
             * самостоятельно.
             */

            setNumber(
                    calculation,
                    "L8",
                    result.getTotalWithVat()
            );

            /*
             * Для КП.
             */
            setString(
                    kp,
                    "B15",
                    params.getProductName()
            );

            setNumber(
                    kp,
                    "D15",
                    result.getTotalWithVat()
            );

            setNumber(
                    kp,
                    "E15",
                    result.getTotalWithVat()
            );

            workbook.write(output);

            log.info(
                    "КП сформировано, размер {} байт",
                    output.size()
            );

            return output.toByteArray();

        } catch (Exception e) {

            log.error(
                    "Ошибка формирования КП",
                    e
            );

            throw new RuntimeException(
                    "Не удалось сформировать Excel КП",
                    e
            );
        }
    }

    private void setString(
            Sheet sheet,
            String cellAddress,
            String value) {

        CellAddress address = new CellAddress(cellAddress);

        Row row = sheet.getRow(address.getRow());

        if (row == null) {
            row = sheet.createRow(address.getRow());
        }

        Cell cell = row.getCell(address.getColumn());

        if (cell == null) {
            cell = row.createCell(address.getColumn());
        }

        cell.setCellValue(value);
    }

    private void setNumber(
            Sheet sheet,
            String cellAddress,
            Number value) {
        // Реализуем ниже.
    }
}