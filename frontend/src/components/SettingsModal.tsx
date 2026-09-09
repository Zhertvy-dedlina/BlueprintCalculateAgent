import React, { useState } from 'react';
import type { ChatSettings } from '../types/chat';

interface SettingsModalProps {
  isOpen: boolean;
  onClose: () => void;
  settings: ChatSettings;
  onSaveSettings: (settings: ChatSettings) => void;
}

export const SettingsModal: React.FC<SettingsModalProps> = ({
  isOpen,
  onClose,
  settings,
  onSaveSettings,
}) => {
  const [apiUrl, setApiUrl] = useState(settings.apiUrl);

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSaveSettings({ apiUrl: apiUrl.trim() });
    onClose();
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-window" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3 className="modal-title">Настройки подключения к бэкенду</h3>
          <button
            type="button"
            className="modal-close-btn"
            onClick={onClose}
            aria-label="Закрыть"
          >
            ×
          </button>
        </div>

        <form onSubmit={handleSubmit} className="modal-body">
          <div className="form-group">
            <label className="form-label" htmlFor="api-url-input">
              URL эндпоинта чата (API)
            </label>
            <input
              id="api-url-input"
              type="text"
              className="form-input"
              value={apiUrl}
              onChange={(e) => setApiUrl(e.target.value)}
              placeholder="http://localhost:8080/api/chat"
            />
            <p className="form-help">
              Адрес бэкенда для отправки сообщений и файлов (POST запрос).
              Статус подключения проверяется автоматически каждые 30 секунд через эндпоинт <code>/health</code>.
              Если бэкенд недоступен, чат автоматически использует локальные ответы.
            </p>
          </div>

          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={onClose}>
              Отмена
            </button>
            <button type="submit" className="btn btn-primary">
              Сохранить
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
