package ru.vsu.zhertvydedlina.aiservice.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import ru.vsu.zhertvydedlina.aiservice.calculation.entity.MaterialPrice;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ExcelCalculationService {

    private static final String SHEET_NAME = "Цены на металл";

    private final Map<String, MaterialPrice> materials = new HashMap<>();

    /**
     * Загружает цены из Excel.
     *
     * Можно вызывать при старте приложения
     * или перед каждым расчётом.
     */
    public void load(File excelFile) {

        materials.clear();

        try (FileInputStream inputStream =
                     new FileInputStream(excelFile);
             Workbook workbook =
                     WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheet(SHEET_NAME);

            if (sheet == null) {
                throw new IllegalArgumentException(
                        "В Excel отсутствует лист: " + SHEET_NAME
                );
            }

            /*
             * Строка 2 в Excel — заголовки.
             * Данные начинаются со строки 4.
             */
            for (int i = 3; i <= 29; i++) {

                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                String name =
                        getString(row.getCell(0));

                if (name == null ||
                        name.isBlank() ||
                        name.equals("-")) {
                    continue;
                }

                MaterialPrice price =
                        new MaterialPrice();

                price.setName(name);

                price.setPricePerTon(
                        getDecimal(row.getCell(1))
                );

                price.setSheetAreaM2(
                        getDecimal(row.getCell(2))
                );

                price.setSheetWeightTon(
                        getDecimal(row.getCell(4))
                );

                price.setSheetCuttingPrice(
                        getDecimal(row.getCell(5))
                );

                price.setSheetPrice(
                        getDecimal(row.getCell(6))
                );

                price.setPricePerM2(
                        getDecimal(row.getCell(7))
                );

                materials.put(
                        normalize(name),
                        price
                );
            }

            log.info(
                    "Загружено материалов из Excel: {}",
                    materials.size()
            );

        } catch (Exception e) {
            throw new RuntimeException(
                    "Не удалось загрузить цены из Excel",
                    e
            );
        }
    }

    public MaterialPrice findMaterial(String name) {

        if (name == null) {
            return null;
        }

        MaterialPrice exact =
                materials.get(normalize(name));

        if (exact != null) {
            return exact;
        }

        /*
         * Позже сюда можно добавить
         * интеллектуальное сопоставление.
         */
        return materials.entrySet()
                .stream()
                .filter(entry ->
                        normalize(name)
                                .contains(entry.getKey())
                                ||
                                entry.getKey()
                                        .contains(normalize(name))
                )
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    private String normalize(String value) {

        return value
                .toLowerCase()
                .replace("ё", "е")
                .replace(" ", "")
                .replace(",", ".")
                .trim();
    }

    private String getString(Cell cell) {

        if (cell == null) {
            return null;
        }

        return cell.toString().trim();
    }

    private BigDecimal getDecimal(Cell cell) {

        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            return BigDecimal.valueOf(
                    cell.getNumericCellValue()
            );
        }

        String value =
                cell.toString()
                        .replace(",", ".")
                        .trim();

        if (value.isBlank()) {
            return null;
        }

        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}