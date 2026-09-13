import type { ChatFile, ChatSettings } from '../types/chat';

/** Ping the backend health endpoint or server to check availability. */
export async function checkBackendHealth(apiUrl: string): Promise<boolean> {
  const trimmed = apiUrl.trim();
  if (!trimmed) return false;

  try {
    let origin = '';
    const base = trimmed.replace(/\/[^/]+\/?$/, '');
    try {
      origin = new URL(trimmed, window.location.origin).origin;
    } catch {
      origin = window.location.origin;
    }

    const candidateUrls = [
      `${base}/health`,
      `${origin}/actuator/health`,
      `${origin}/health`,
      trimmed,
    ];

    for (const url of candidateUrls) {
      try {
        const res = await fetch(url, {
          method: 'GET',
          signal: AbortSignal.timeout(2000),
        });
        if (res.status > 0 && res.status < 500) {
          return true;
        }
      } catch {
        // Continue trying next endpoint or fallback
      }
    }

    // Fallback: no-cors ping directly to the endpoint.
    // If the server port is listening, it succeeds with an opaque response.
    // If the server is offline/down, it throws a TypeError (connection refused).
    try {
      await fetch(trimmed, {
        method: 'GET',
        mode: 'no-cors',
        signal: AbortSignal.timeout(2500),
      });
      return true;
    } catch {
      return false;
    }
  } catch {
    return false;
  }
}


export async function sendMessageToAgent(
  userText: string,
  files: ChatFile[] = [],
  settings: ChatSettings
): Promise<string> {
  // Always attempt a real request if apiUrl is set
  if (settings.apiUrl.trim()) {
    try {
      const formData = new FormData();
      formData.append('message', userText);
      for (const file of files) {
        if (file.rawFile) {
          formData.append('files', file.rawFile, file.name);
        }
      }

      const response = await fetch(settings.apiUrl, {
        method: 'POST',
        body: files.length > 0 ? formData : JSON.stringify({ message: userText }),
        headers: files.length > 0 ? {} : { 'Content-Type': 'application/json' },
      });

      if (!response.ok) {
        throw new Error(`Ошибка сервера: ${response.status} ${response.statusText}`);
      }

      const data = await response.json();
      return data.reply || data.message || data.text || JSON.stringify(data);
    } catch (err: unknown) {
      const errorMessage = err instanceof Error ? err.message : String(err);
      console.warn('API бэкенда недоступен, используется локальный ответ:', errorMessage);
      return `${getMockAgentResponse(userText, files)}\n\n*(Примечание: Ответ сгенерирован локально, так как бэкенд недоступен: ${errorMessage})*`;
    }
  }

  // No API URL configured — use mock with a small delay
  await new Promise((resolve) => setTimeout(resolve, 800 + Math.random() * 700));
  return getMockAgentResponse(userText, files);
}

function getMockAgentResponse(prompt: string, files: ChatFile[]): string {
  const lower = prompt.toLowerCase();

  if (files.length > 0) {
    const fileNames = files.map((f) => `«${f.name}» (${(f.size / 1024).toFixed(1)} КБ)`).join(', ');
    return `📎 Получены файлы для анализа:\n${fileNames}\n\n` +
      `🔍 Анализирую чертёж и спецификацию...\n` +
      `• Распознавание геометрических примитивов: выполнено (100%)\n` +
      `• Определение размерных цепочек и допусков: завершено\n` +
      `• Материал заготовки: Сталь конструкционная ГОСТ 1050-88\n\n` +
      `📊 **Предварительный расчёт норм расхода:**\n` +
      `1. Масса заготовки: ~14.2 кг\n` +
      `2. Расчетное время механической обработки: 42 мин\n` +
      `3. Рекомендуемый технологический маршрут: Токарная ЧПУ -> Фрезерная -> Термообработка\n\n` +
      `Что конкретно требуется рассчитать подробнее: трудоёмкость, стоимость партии или карту раскроя?`;
  }

  if (lower.includes('привет') || lower.includes('здравствуй') || lower.includes('hello')) {
    return `Здравствуйте! Я Blueprint Calculate Agent — интеллектуальный ассистент по анализу чертежей и инженерно-экономическим расчётам.\n\nВы можете:\n1. Загрузить чертёж (.pdf, .png, .dwg, .dxf) для автоматического извлечения размеров и допусков.\n2. Запросить расчёт расхода материалов, массы или трудоёмкости.\n3. Сформировать коммерческую спецификацию или карту технологического процесса.\n\nЧем я могу помочь вам прямо сейчас?`;
  }

  if (lower.includes('спецификац') || lower.includes('рассчитай') || lower.includes('расчет') || lower.includes('расчёт')) {
    return `📊 **Пример расчёта спецификации деталей:**\n\n` +
      `| Позиция | Наименование детали | Материал | Кол-во | Масса шт. (кг) | Общая масса (кг) |\n` +
      `|---|---|---|---|---|---|\n` +
      `| 1 | Вал приводной | Сталь 40Х | 2 | 3.45 | 6.90 |\n` +
      `| 2 | Фланец упорный | Сталь 20 | 4 | 1.12 | 4.48 |\n` +
      `| 3 | Втулка бронзовая | БрОЦС 5-5-5 | 4 | 0.48 | 1.92 |\n\n` +
      `Итоговая масса заготовок: **13.30 кг**\n` +
      `Ориентировочная стоимость сырья: ~4 850 ₽\n\n` +
      `Прикрепите чертёж конкретного изделия для точного покомпонентного расчёта!`;
  }

  if (lower.includes('формат') || lower.includes('файл') || lower.includes('загруз')) {
    return `Поддерживаемые форматы документов и чертежей:\n\n` +
      `• **Чертежи и векторная графика:** PDF, DWG, DXF, SVG\n` +
      `• **Изображения и сканы:** PNG, JPG, JPEG, WEBP, TIFF\n` +
      `• **Спецификации и таблицы:** XLS, XLSX, CSV\n\n` +
      `Вы можете воспользоваться кнопкой скрепки или перетащить файл прямо в окно чата.`;
  }

  if (lower.includes('кто ты') || lower.includes('что умеешь')) {
    return `Я специализированный AI-ассистент инженерного расчёта. Моя задача — сократить время инженера-технолога и сметчика при работе с чертежами и конструкторской документацией.\n\n` +
      `Ключевые возможности:\n` +
      `• Распознавание геометрии, размеров, шероховатостей и допусков\n` +
      `• Подбор режимов резания и оборудования\n` +
      `• Калькуляция себестоимости деталей и партий\n` +
      `• Автоматическая генерация спецификаций по ГОСТ/ISO`;
  }

  return `Запрос принят в обработку: «${prompt}».\n\n` +
    `Для формирования детального отчёта вы можете прикрепить файл чертежа изделия либо уточнить марку материала, объём партии деталей и требования к термообработке.`;
}
