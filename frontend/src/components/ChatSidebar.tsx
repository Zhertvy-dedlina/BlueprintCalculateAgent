import React, { useState, useMemo } from 'react';
import type { ChatSession } from '../types/chat';

interface ChatSidebarProps {
  sessions: ChatSession[];
  activeSessionId: string;
  onSelectSession: (id: string) => void;
  onCreateNewChat: () => void;
  onDeleteSession: (id: string, e: React.MouseEvent) => void;
  isOpen: boolean;
  onCloseMobile: () => void;
}

// ── date grouping helpers ──────────────────────────────────────────────────
function getDayLabel(ms: number): string {
  const now = new Date();
  const d   = new Date(ms);

  const diffDays = Math.floor(
    (Date.UTC(now.getFullYear(), now.getMonth(), now.getDate()) -
     Date.UTC(d.getFullYear(),  d.getMonth(),   d.getDate())) /
    86_400_000
  );

  if (diffDays === 0) return 'Сегодня';
  if (diffDays === 1) return 'Вчера';
  if (diffDays <= 7)  return 'Последние 7 дней';
  if (diffDays <= 30) return 'Последние 30 дней';
  return d.toLocaleDateString('ru-RU', { month: 'long', year: 'numeric' });
}

const GROUP_ORDER = ['Сегодня', 'Вчера', 'Последние 7 дней', 'Последние 30 дней'];

type GroupMap = Record<string, ChatSession[]>;

function groupSessions(sessions: ChatSession[]): [string, ChatSession[]][] {
  const map: GroupMap = {};

  for (const s of sessions) {
    const label = getDayLabel(s.createdAtMs ?? Date.now());
    if (!map[label]) map[label] = [];
    map[label].push(s);
  }

  const known   = GROUP_ORDER.filter((g) => map[g]);
  const unknown = Object.keys(map)
    .filter((g) => !GROUP_ORDER.includes(g))
    .sort((a, b) => {
      // Sort month-labels newest first by comparing the first session's ms
      const aMs = map[a][0]?.createdAtMs ?? 0;
      const bMs = map[b][0]?.createdAtMs ?? 0;
      return bMs - aMs;
    });

  return [...known, ...unknown].map((g) => [g, map[g]]);
}

// ── tiny "three-dot" icon ──────────────────────────────────────────────────
const DotsIcon = () => (
  <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
    <circle cx="5"  cy="12" r="2" />
    <circle cx="12" cy="12" r="2" />
    <circle cx="19" cy="12" r="2" />
  </svg>
);

const TrashIcon = () => (
  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M3 6h18" />
    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
  </svg>
);

const PencilIcon = () => (
  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
  </svg>
);

const SidebarToggleIcon = () => (
  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <rect x="3" y="3" width="18" height="18" rx="2" />
    <path d="M9 3v18" />
  </svg>
);

const SearchIcon = () => (
  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <circle cx="11" cy="11" r="8" />
    <line x1="21" y1="21" x2="16.65" y2="16.65" />
  </svg>
);

// ── SessionItem ────────────────────────────────────────────────────────────
interface SessionItemProps {
  session: ChatSession;
  isActive: boolean;
  onSelect: () => void;
  onDelete: (e: React.MouseEvent) => void;
}

const SessionItem: React.FC<SessionItemProps> = ({ session, isActive, onSelect, onDelete }) => {
  const [menuOpen, setMenuOpen] = useState(false);

  const handleMenuClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    setMenuOpen((v) => !v);
  };

  const handleDelete = (e: React.MouseEvent) => {
    setMenuOpen(false);
    onDelete(e);
  };

  return (
    <div
      className={`ow-session-item ${isActive ? 'ow-session-active' : ''}`}
      onClick={onSelect}
      title={session.title}
    >
      <span className="ow-session-title">{session.title}</span>

      {/* Actions appear on hover OR when menu is open */}
      <div className={`ow-session-actions ${menuOpen ? 'menu-open' : ''}`}>
        <button
          type="button"
          className="ow-icon-btn"
          onClick={handleMenuClick}
          title="Действия"
          aria-label="Действия"
        >
          <DotsIcon />
        </button>

        {menuOpen && (
          <>
            {/* Click-away overlay */}
            <div
              className="ow-menu-backdrop"
              onClick={(e) => { e.stopPropagation(); setMenuOpen(false); }}
            />
            <div className="ow-dropdown-menu">
              <button
                type="button"
                className="ow-dropdown-item danger"
                onClick={handleDelete}
              >
                <TrashIcon />
                <span>Удалить</span>
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  );
};

// ── Main sidebar ───────────────────────────────────────────────────────────
export const ChatSidebar: React.FC<ChatSidebarProps> = ({
  sessions,
  activeSessionId,
  onSelectSession,
  onCreateNewChat,
  onDeleteSession,
  isOpen,
  onCloseMobile,
}) => {
  const [search, setSearch] = useState('');

  const filtered = useMemo(() => {
    if (!search.trim()) return sessions;
    const q = search.toLowerCase();
    return sessions.filter((s) => s.title.toLowerCase().includes(q));
  }, [sessions, search]);

  const groups = useMemo(() => groupSessions(filtered), [filtered]);

  return (
    <>
      {/* Mobile backdrop */}
      {isOpen && (
        <div className="ow-backdrop" onClick={onCloseMobile} aria-hidden="true" />
      )}

      <aside className={`ow-sidebar ${isOpen ? 'ow-sidebar-open' : ''}`}>
        {/* ── Top bar ────────────────────────────────────────────── */}
        <div className="ow-topbar">
          {/* Collapse / logo button */}
          <button
            type="button"
            className="ow-icon-btn ow-topbar-btn"
            onClick={onCloseMobile}
            title="Свернуть панель"
            aria-label="Свернуть"
          >
            <SidebarToggleIcon />
          </button>

          <span className="ow-topbar-title">Blueprint Agent</span>

          {/* New chat (pencil) */}
          <button
            type="button"
            className="ow-icon-btn ow-topbar-btn"
            onClick={() => { onCreateNewChat(); onCloseMobile(); }}
            title="Новый чат"
            aria-label="Новый чат"
          >
            <PencilIcon />
          </button>
        </div>

        {/* ── Search ─────────────────────────────────────────────── */}
        <div className="ow-search-box">
          <SearchIcon />
          <input
            type="text"
            className="ow-search-input"
            placeholder="Поиск по чатам"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
          {search && (
            <button
              type="button"
              className="ow-search-clear"
              onClick={() => setSearch('')}
              aria-label="Очистить поиск"
            >
              ×
            </button>
          )}
        </div>

        {/* ── New Chat button (full-width, below search) ─────────── */}
        <div className="ow-new-chat-row">
          <button
            type="button"
            className="ow-new-chat-btn"
            onClick={() => { onCreateNewChat(); onCloseMobile(); }}
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
              <line x1="12" y1="5" x2="12" y2="19" />
              <line x1="5"  y1="12" x2="19" y2="12" />
            </svg>
            Новый чат
          </button>
        </div>

        {/* ── Session list ─────────────────────────────────────────── */}
        <div className="ow-sessions-scroll">
          {groups.length === 0 ? (
            <div className="ow-empty">
              {search ? 'Ничего не найдено' : 'Нет диалогов'}
            </div>
          ) : (
            groups.map(([label, items]) => (
              <div key={label} className="ow-group">
                <div className="ow-group-label">{label}</div>
                {items.map((s) => (
                  <SessionItem
                    key={s.id}
                    session={s}
                    isActive={s.id === activeSessionId}
                    onSelect={() => { onSelectSession(s.id); onCloseMobile(); }}
                    onDelete={(e) => onDeleteSession(s.id, e)}
                  />
                ))}
              </div>
            ))
          )}
        </div>

        {/* ── Bottom bar ───────────────────────────────────────────── */}
        <div className="ow-bottombar">
          <div className="ow-user-pill">
            <div className="ow-user-avatar">B</div>
            <div className="ow-user-info">
              <div className="ow-user-name">Blueprint Agent</div>
              <div className="ow-user-role">AI Assistant</div>
            </div>
          </div>
        </div>
      </aside>
    </>
  );
};
