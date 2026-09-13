import React, { useState, useRef, useEffect } from 'react';

interface ChatHeaderProps {
  onDeleteChat: () => void;
  onOpenSettings: () => void;
  activeChatTitle?: string;
  isOnline: boolean;
}

export const ChatHeader: React.FC<ChatHeaderProps> = ({
  onDeleteChat,
  onOpenSettings,
  activeChatTitle,
  isOnline,
}) => {
  const [confirmOpen, setConfirmOpen] = useState(false);
  const popupRef = useRef<HTMLDivElement>(null);

  // Close popup when clicking outside
  useEffect(() => {
    if (!confirmOpen) return;
    const handler = (e: MouseEvent) => {
      if (popupRef.current && !popupRef.current.contains(e.target as Node)) {
        setConfirmOpen(false);
      }
    };
    document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, [confirmOpen]);

  const handleConfirmDelete = () => {
    setConfirmOpen(false);
    onDeleteChat();
  };

  return (
    <header className="chat-header">
      <div className="chat-header-info">
        <div className="chat-avatar">
          <svg
            width="22"
            height="22"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <path d="M12 2a2 2 0 0 1 2 2v2a2 2 0 0 1-2 2 2 2 0 0 1-2-2V4a2 2 0 0 1 2-2z" />
            <rect x="3" y="8" width="18" height="12" rx="3" />
            <circle cx="8" cy="14" r="1.5" />
            <circle cx="16" cy="14" r="1.5" />
            <path d="M10 18h4" />
          </svg>
        </div>

        <div className="chat-title-group">
          <div className="chat-title-row">
            <h1 className="chat-title">Blueprint Calculate Agent</h1>
            {activeChatTitle && (
              <span className="chat-current-subtitle" title={activeChatTitle}>
                / {activeChatTitle}
              </span>
            )}
          </div>
          <div className="chat-status-container">
            <span className={`status-dot ${isOnline ? 'online' : 'offline'}`} />
            <span className="status-text">
              {isOnline ? 'В сети' : 'Не в сети'}
            </span>
          </div>
        </div>
      </div>

      <div className="chat-header-actions">
        <button
          type="button"
          className="header-btn"
          onClick={onOpenSettings}
          title="Настройки API"
          aria-label="Настройки подключения"
        >
          <svg
            width="18"
            height="18"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <path d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z" />
            <circle cx="12" cy="12" r="3" />
          </svg>
          <span className="btn-label">Настройки</span>
        </button>

        {/* Delete chat button with inline confirm popup */}
        <div className="header-delete-wrapper" ref={popupRef}>
          <button
            type="button"
            className={`header-btn danger-hover ${confirmOpen ? 'active' : ''}`}
            onClick={() => setConfirmOpen((v) => !v)}
            title="Удалить текущий чат"
            aria-label="Удалить чат"
            aria-expanded={confirmOpen}
          >
            <svg
              width="18"
              height="18"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <path d="M3 6h18" />
              <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6" />
              <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2" />
            </svg>
            <span className="btn-label">Удалить</span>
          </button>

          {confirmOpen && (
            <div className="delete-confirm-popup" role="dialog" aria-label="Подтверждение удаления">
              <div className="delete-confirm-icon">
                <svg
                  width="20"
                  height="20"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="2"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                >
                  <path d="M3 6h18" />
                  <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6" />
                  <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2" />
                </svg>
              </div>
              <div className="delete-confirm-text">
                <p className="delete-confirm-title">Удалить чат?</p>
                <p className="delete-confirm-sub">
                  {activeChatTitle
                    ? `«${activeChatTitle}» будет удалён без возможности восстановления.`
                    : 'Этот чат будет удалён без возможности восстановления.'}
                </p>
              </div>
              <div className="delete-confirm-actions">
                <button
                  type="button"
                  className="delete-confirm-btn cancel"
                  onClick={() => setConfirmOpen(false)}
                >
                  Отмена
                </button>
                <button
                  type="button"
                  className="delete-confirm-btn confirm"
                  onClick={handleConfirmDelete}
                >
                  Удалить
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};
