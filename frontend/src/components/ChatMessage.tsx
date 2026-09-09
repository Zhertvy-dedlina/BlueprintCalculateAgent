import React from 'react';
import type { Message, ChatFile } from '../types/chat';

interface ChatMessageProps {
  message: Message;
}

export const ChatMessage: React.FC<ChatMessageProps> = ({ message }) => {
  const isUser = message.sender === 'user';

  return (
    <div className={`message-row ${isUser ? 'user-row' : 'bot-row'}`}>
      {!isUser && (
        <div className="message-avatar" aria-hidden="true">
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
            <path d="M12 2a2 2 0 0 1 2 2v2a2 2 0 0 1-2 2 2 2 0 0 1-2-2V4a2 2 0 0 1 2-2z" />
            <rect x="3" y="8" width="18" height="12" rx="3" />
            <circle cx="8" cy="14" r="1.5" />
            <circle cx="16" cy="14" r="1.5" />
            <path d="M10 18h4" />
          </svg>
        </div>
      )}

      <div className={`message-bubble ${isUser ? 'user-bubble' : 'bot-bubble'}`}>
        {/* Attached files preview */}
        {message.files && message.files.length > 0 && (
          <div className="attached-files-container">
            {message.files.map((file) => (
              <FileCard key={file.id} file={file} />
            ))}
          </div>
        )}

        {/* Text content */}
        <div className="message-text">
          <FormattedMessageContent text={message.text} />
        </div>

        {/* Message meta (time, status) */}
        <div className="message-meta">
          <span className="message-time">{message.timestamp}</span>
          {isUser && (
            <span className="message-status">
              {message.status === 'sending' && '⏳'}
              {message.status === 'sent' && '✓'}
              {message.status === 'error' && '⚠️'}
            </span>
          )}
        </div>
      </div>
    </div>
  );
};

const FileCard: React.FC<{ file: ChatFile }> = ({ file }) => {
  const isImage = file.type.startsWith('image/') || /\.(png|jpe?g|webp|gif|svg)$/i.test(file.name);
  const formattedSize = (file.size / 1024).toFixed(1) + ' КБ';

  return (
    <div className="file-chip">
      {isImage && file.url ? (
        <img src={file.url} alt={file.name} className="file-chip-thumb" />
      ) : (
        <div className="file-chip-icon">
          <svg
            width="16"
            height="16"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
            <polyline points="14 2 14 8 20 8" />
            <line x1="16" y1="13" x2="8" y2="13" />
            <line x1="16" y1="17" x2="8" y2="17" />
            <polyline points="10 9 9 9 8 9" />
          </svg>
        </div>
      )}
      <div className="file-chip-info">
        <span className="file-chip-name" title={file.name}>
          {file.name}
        </span>
        <span className="file-chip-size">{formattedSize}</span>
      </div>
    </div>
  );
};

// Formats basic markdown elements: tables, bold, italic, code blocks, lists
const FormattedMessageContent: React.FC<{ text: string }> = ({ text }) => {
  // Check if text contains markdown table
  const lines = text.split('\n');
  const elements: React.ReactNode[] = [];

  let tableBuffer: string[] = [];
  let inTable = false;

  const flushTable = () => {
    if (tableBuffer.length > 0) {
      elements.push(renderTable(tableBuffer, `table-${elements.length}`));
      tableBuffer = [];
      inTable = false;
    }
  };

  for (let i = 0; i < lines.length; i++) {
    const line = lines[i];
    const isTableRow = line.trim().startsWith('|') && line.trim().endsWith('|');

    if (isTableRow) {
      inTable = true;
      tableBuffer.push(line);
    } else {
      if (inTable) {
        flushTable();
      }

      // Empty line
      if (!line.trim()) {
        elements.push(<div key={`space-${i}`} className="message-line-break" />);
      } else {
        elements.push(
          <div key={`line-${i}`} className="message-line">
            {formatInline(line)}
          </div>
        );
      }
    }
  }

  if (inTable) {
    flushTable();
  }

  return <>{elements}</>;
};

function renderTable(tableLines: string[], key: string): React.ReactNode {
  // Filter out delimiter rows like |---|---|
  const validRows = tableLines.filter((l) => !/^\|[\s-:]+\|/.test(l.trim()));
  if (validRows.length === 0) return null;

  const headerCells = validRows[0]
    .split('|')
    .slice(1, -1)
    .map((c) => c.trim());

  const bodyRows = validRows.slice(1).map((row) =>
    row
      .split('|')
      .slice(1, -1)
      .map((c) => c.trim())
  );

  return (
    <div key={key} className="chat-table-wrapper">
      <table className="chat-table">
        <thead>
          <tr>
            {headerCells.map((cell, idx) => (
              <th key={`th-${idx}`}>{formatInline(cell)}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {bodyRows.map((row, rIdx) => (
            <tr key={`tr-${rIdx}`}>
              {row.map((cell, cIdx) => (
                <td key={`td-${rIdx}-${cIdx}`}>{formatInline(cell)}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function formatInline(str: string): React.ReactNode {
  // Replace **bold**, `code`, *italic*
  const parts: React.ReactNode[] = [];
  const regex = /(\*\*.*?\*\*|`.*?`|\*.*?\*)/g;

  let lastIdx = 0;
  let match: RegExpExecArray | null;

  while ((match = regex.exec(str)) !== null) {
    if (match.index > lastIdx) {
      parts.push(str.substring(lastIdx, match.index));
    }

    const token = match[0];
    if (token.startsWith('**') && token.endsWith('**')) {
      parts.push(<strong key={match.index}>{token.slice(2, -2)}</strong>);
    } else if (token.startsWith('`') && token.endsWith('`')) {
      parts.push(<code key={match.index}>{token.slice(1, -1)}</code>);
    } else if (token.startsWith('*') && token.endsWith('*')) {
      parts.push(<em key={match.index}>{token.slice(1, -1)}</em>);
    }

    lastIdx = match.index + token.length;
  }

  if (lastIdx < str.length) {
    parts.push(str.substring(lastIdx));
  }

  return parts.length > 0 ? parts : str;
}
