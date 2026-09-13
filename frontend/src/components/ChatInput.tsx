import React, { useState, useRef, useEffect } from 'react';
import type { ChatFile } from '../types/chat';

interface ChatInputProps {
  onSendMessage: (text: string, files: ChatFile[]) => void;
  isLoading: boolean;
}

export const ChatInput: React.FC<ChatInputProps> = ({ onSendMessage, isLoading }) => {
  const [text, setText] = useState('');
  const [attachedFiles, setAttachedFiles] = useState<ChatFile[]>([]);
  const [isDragOver, setIsDragOver] = useState(false);
  const textareaRef = useRef<HTMLTextAreaElement>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  // Auto-resize textarea height based on content
  useEffect(() => {
    if (textareaRef.current) {
      textareaRef.current.style.height = 'auto';
      textareaRef.current.style.height = `${Math.min(textareaRef.current.scrollHeight, 140)}px`;
    }
  }, [text]);

  const handleSend = () => {
    if ((!text.trim() && attachedFiles.length === 0) || isLoading) {
      return;
    }

    onSendMessage(text.trim(), attachedFiles);
    setText('');
    setAttachedFiles([]);

    if (textareaRef.current) {
      textareaRef.current.style.height = 'auto';
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  const processFileList = (fileList: FileList | null) => {
    if (!fileList || fileList.length === 0) return;

    const newFiles: ChatFile[] = [];
    for (let i = 0; i < fileList.length; i++) {
      const file = fileList[i];
      const isImg = file.type.startsWith('image/');
      const chatFile: ChatFile = {
        id: `${Date.now()}-${Math.random().toString(36).substring(2, 9)}`,
        name: file.name,
        size: file.size,
        type: file.type || 'application/octet-stream',
        rawFile: file,
        url: isImg ? URL.createObjectURL(file) : undefined,
      };
      newFiles.push(chatFile);
    }

    setAttachedFiles((prev) => [...prev, ...newFiles]);
  };

  const handleFileInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    processFileList(e.target.files);
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  const handleRemoveFile = (id: string) => {
    setAttachedFiles((prev) => prev.filter((f) => f.id !== id));
  };

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragOver(true);
  };

  const handleDragLeave = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragOver(false);
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragOver(false);
    processFileList(e.dataTransfer.files);
  };

  return (
    <div
      className={`chat-input-wrapper ${isDragOver ? 'drag-over' : ''}`}
      onDragOver={handleDragOver}
      onDragLeave={handleDragLeave}
      onDrop={handleDrop}
    >
      {/* Attached files preview chips before sending */}
      {attachedFiles.length > 0 && (
        <div className="input-attachments-bar">
          {attachedFiles.map((file) => (
            <div key={file.id} className="input-attachment-chip">
              <span className="attachment-name" title={file.name}>
                {file.name}
              </span>
              <span className="attachment-size">({(file.size / 1024).toFixed(0)} КБ)</span>
              <button
                type="button"
                className="attachment-remove-btn"
                onClick={() => handleRemoveFile(file.id)}
                title="Удалить файл"
                aria-label="Удалить вложение"
              >
                ×
              </button>
            </div>
          ))}
        </div>
      )}

      <div className="chat-input-row">
        {/* Hidden file input */}
        <input
          type="file"
          ref={fileInputRef}
          onChange={handleFileInputChange}
          multiple
          accept=".pdf,.png,.jpg,.jpeg,.webp,.dwg,.dxf,.svg,.xlsx,.xls,.csv"
          className="visually-hidden"
          id="file-upload-input"
        />

        {/* Attachment button */}
        <button
          type="button"
          className="input-action-btn attach-btn"
          onClick={() => fileInputRef.current?.click()}
          title="Прикрепить чертёж или документ"
          aria-label="Прикрепить файл"
          disabled={isLoading}
        >
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
            <path d="M21.44 11.05l-9.19 9.19a6 6 0 0 1-8.49-8.49l9.19-9.19a4 4 0 0 1 5.66 5.66l-9.2 9.19a2 2 0 0 1-2.83-2.83l8.49-8.48" />
          </svg>
        </button>

        {/* Text area */}
        <textarea
          ref={textareaRef}
          value={text}
          onChange={(e) => setText(e.target.value)}
          onKeyDown={handleKeyDown}
          placeholder="Спросите о чертеже, расчёте или прикрепите файл... (Enter для отправки)"
          rows={1}
          className="chat-textarea"
          disabled={isLoading}
        />

        {/* Send button */}
        <button
          type="button"
          className="input-action-btn send-btn"
          onClick={handleSend}
          disabled={(!text.trim() && attachedFiles.length === 0) || isLoading}
          title="Отправить сообщение"
          aria-label="Отправить сообщение"
        >
          {isLoading ? (
            <span className="btn-spinner" />
          ) : (
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
              <line x1="22" y1="2" x2="11" y2="13" />
              <polygon points="22 2 15 22 11 13 2 9 22 2" />
            </svg>
          )}
        </button>
      </div>

      <div className="input-hint">
        Нажмите <b>Enter</b> для отправки, <b>Shift + Enter</b> для новой строки. Перетащите файлы для загрузки.
      </div>
    </div>
  );
};
