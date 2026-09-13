import React, { useState } from 'react';
import type { ChatSettings } from '../types/chat';

interface SettingsModalProps {
  isOpen: boolean;
  onClose: () => void;
  settings: ChatSettings;
  onSaveSettings: (settings: ChatSettings) => void;
  onLogout: () => void;
}

export const SettingsModal: React.FC<SettingsModalProps> = ({
  isOpen,
  onClose,
  settings,
  onSaveSettings,
  onLogout,
}) => {
  const [apiUrl, setApiUrl] = useState(settings.apiUrl);

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSaveSettings({ apiUrl: apiUrl.trim() });
    onClose();
  };

  const handleLogout = () => {
    onClose();
    onLogout();
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
              URL эндпоинта AI-анализа
            </label>
            <input
              id="api-url-input"
              type="text"
              className="form-input"
              value={apiUrl}
              onChange={(e) => setApiUrl(e.target.value)}
              placeholder="http://localhost:8080/api/ai/analyze"
            />
            <p className="form-help">
              Адрес бэкенда для отправки сообщений и файлов (POST multipart/form-data).
              Статус подключения проверяется автоматически каждые 10 секунд.
              Если бэкенд недоступен, чат использует локальные ответы.
            </p>
          </div>

          <div className="modal-footer" style={{ justifyContent: 'space-between' }}>
            <button type="button" className="btn btn-danger" onClick={handleLogout}>
              Выйти
            </button>
            <div style={{ display: 'flex', gap: '8px' }}>
              <button type="button" className="btn btn-secondary" onClick={onClose}>
                Отмена
              </button>
              <button type="submit" className="btn btn-primary">
                Сохранить
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>
  );
};
