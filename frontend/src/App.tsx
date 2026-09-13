import React, { useState, useEffect, useRef } from 'react';
import type { Message, ChatFile, ChatSettings, ChatSession } from './types/chat';
import { ChatHeader } from './components/ChatHeader';
import { ChatSidebar } from './components/ChatSidebar';
import { ChatMessage } from './components/ChatMessage';
import { ChatInput } from './components/ChatInput';
import { QuickPrompts } from './components/QuickPrompts';
import { SettingsModal } from './components/SettingsModal';
import { AuthModal } from './components/AuthModal';
import { sendMessageToAgent, checkBackendHealth } from './services/chatService';
import { getToken, saveToken, clearToken, AuthError } from './services/authService';
import './App.css';

const INITIAL_SETTINGS: ChatSettings = {
  apiUrl: '/api/ai/analyze',
};

const getWelcomeMessage = (): Message => ({
  id: `welcome-${Date.now()}`,
  sender: 'bot',
  text: 'Здравствуйте! Я ассистент **Blueprint Calculate Agent**.\n\nЗагрузите чертёж детали, спецификацию или задайте вопрос по расчёту материалов и трудоёмкости.',
  timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
});

const createNewSession = (title = 'Новый чат'): ChatSession => {
  const now = new Date();
  const nowStr = now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  return {
    id: `session-${Date.now()}-${Math.random().toString(36).substring(2, 7)}`,
    title,
    createdAt: nowStr,
    updatedAt: nowStr,
    createdAtMs: now.getTime(),
    messages: [getWelcomeMessage()],
  };
};

function getApiBaseUrl(apiUrl: string): string {
  try {
    return new URL(apiUrl).origin;
  } catch {
    return window.location.origin;
  }
}

function App() {
  const [token, setToken] = useState<string | null>(() => getToken());

  const [sessions, setSessions] = useState<ChatSession[]>(() => {
    const savedSessions = localStorage.getItem('chat_sessions');
    if (savedSessions) {
      try {
        const parsed = JSON.parse(savedSessions);
        if (Array.isArray(parsed) && parsed.length > 0) {
          return parsed.map((s: ChatSession) => ({
            ...s,
            createdAtMs: s.createdAtMs ?? Date.now(),
          }));
        }
      } catch {
        // ignore parse error
      }
    }

    const oldMessages = localStorage.getItem('chat_messages');
    if (oldMessages) {
      try {
        const parsedOld = JSON.parse(oldMessages);
        if (Array.isArray(parsedOld) && parsedOld.length > 0) {
          const migratedSession: ChatSession = {
            id: `session-${Date.now()}`,
            title: 'Предыдущий диалог',
            createdAt: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
            updatedAt: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
            createdAtMs: Date.now(),
            messages: parsedOld,
          };
          return [migratedSession];
        }
      } catch {
        // ignore parse error
      }
    }

    return [createNewSession('Главный чат')];
  });

  const [activeSessionId, setActiveSessionId] = useState<string>(() => {
    const savedActiveId = localStorage.getItem('chat_active_session_id');
    return savedActiveId || '';
  });

  const [settings, setSettings] = useState<ChatSettings>(() => {
    const saved = localStorage.getItem('chat_settings');
    if (saved) {
      try {
        return JSON.parse(saved);
      } catch {
        // ignore parse error
      }
    }
    return INITIAL_SETTINGS;
  });

  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isSettingsOpen, setIsSettingsOpen] = useState(false);
  const [isOnline, setIsOnline] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const activeSession = sessions.find((s) => s.id === activeSessionId) || sessions[0];

  useEffect(() => {
    let cancelled = false;
    const ping = async () => {
      const result = await checkBackendHealth(settings.apiUrl);
      if (!cancelled) setIsOnline(result);
    };
    void ping();
    const interval = setInterval(ping, 10_000);
    return () => {
      cancelled = true;
      clearInterval(interval);
    };
  }, [settings.apiUrl]);

  useEffect(() => {
    localStorage.setItem('chat_sessions', JSON.stringify(sessions));
  }, [sessions]);

  useEffect(() => {
    if (activeSession?.id) {
      localStorage.setItem('chat_active_session_id', activeSession.id);
    }
  }, [activeSession?.id]);

  useEffect(() => {
    localStorage.setItem('chat_settings', JSON.stringify(settings));
  }, [settings]);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [activeSession?.messages, isLoading]);

  const handleAuth = (newToken: string) => {
    saveToken(newToken);
    setToken(newToken);
  };

  const handleLogout = () => {
    clearToken();
    setToken(null);
  };

  const handleCreateNewChat = () => {
    const newSession = createNewSession(`Чат ${sessions.length + 1}`);
    setSessions((prev) => [newSession, ...prev]);
    setActiveSessionId(newSession.id);
  };

  const handleSelectSession = (id: string) => {
    setActiveSessionId(id);
  };

  const handleDeleteSession = (id: string, e: React.MouseEvent) => {
    e.stopPropagation();

    if (sessions.length <= 1) {
      const fresh = createNewSession('Главный чат');
      setSessions([fresh]);
      setActiveSessionId(fresh.id);
      return;
    }

    const remaining = sessions.filter((s) => s.id !== id);
    setSessions(remaining);
    if (activeSessionId === id) {
      setActiveSessionId(remaining[0].id);
    }
  };

  const handleSendMessage = async (text: string, files: ChatFile[] = []) => {
    if (!activeSession || !token) return;

    const currentSessionId = activeSession.id;
    const now = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

    const userMessage: Message = {
      id: `user-${Date.now()}`,
      sender: 'user',
      text,
      timestamp: now,
      files,
      status: 'sent',
    };

    const isFirstUserMessage = !activeSession.messages.some((m) => m.sender === 'user');
    let updatedTitle = activeSession.title;
    if (isFirstUserMessage) {
      if (text.trim()) {
        updatedTitle = text.trim().slice(0, 28) + (text.trim().length > 28 ? '...' : '');
      } else if (files.length > 0) {
        updatedTitle = `Файл: ${files[0].name.slice(0, 20)}`;
      }
    }

    setSessions((prev) =>
      prev.map((s) => {
        if (s.id === currentSessionId) {
          return {
            ...s,
            title: updatedTitle,
            updatedAt: now,
            messages: [...s.messages, userMessage],
          };
        }
        return s;
      })
    );

    setIsLoading(true);

    try {
      const reply = await sendMessageToAgent(text, files, settings, token);
      const botMessage: Message = {
        id: `bot-${Date.now()}`,
        sender: 'bot',
        text: reply,
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        status: 'sent',
      };

      setSessions((prev) =>
        prev.map((s) => {
          if (s.id === currentSessionId) {
            return {
              ...s,
              updatedAt: botMessage.timestamp,
              messages: [...s.messages, botMessage],
            };
          }
          return s;
        })
      );
    } catch (err: unknown) {
      if (err instanceof AuthError) {
        clearToken();
        setToken(null);
        return;
      }

      const errorText = err instanceof Error ? err.message : 'Произошла непредвиденная ошибка';
      const errorMessage: Message = {
        id: `bot-err-${Date.now()}`,
        sender: 'bot',
        text: `⚠️ Не удалось получить ответ: ${errorText}`,
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        status: 'error',
      };

      setSessions((prev) =>
        prev.map((s) => {
          if (s.id === currentSessionId) {
            return {
              ...s,
              messages: [...s.messages, errorMessage],
            };
          }
          return s;
        })
      );
    } finally {
      setIsLoading(false);
      checkBackendHealth(settings.apiUrl).then((online) => {
        setIsOnline(online);
      });
    }
  };

  if (!token) {
    return (
      <AuthModal
        apiBaseUrl={getApiBaseUrl(settings.apiUrl)}
        onAuth={handleAuth}
      />
    );
  }

  return (
    <div className="chat-app-container">
      <ChatSidebar
        sessions={sessions}
        activeSessionId={activeSession?.id || ''}
        onSelectSession={handleSelectSession}
        onCreateNewChat={handleCreateNewChat}
        onDeleteSession={handleDeleteSession}
        isOpen={isSidebarOpen}
        onCloseMobile={() => setIsSidebarOpen(false)}
      />

      <div className="chat-app-layout">
        <ChatHeader
          onDeleteChat={() => {
            if (activeSession) {
              const fakeEvent = { stopPropagation: () => {} } as React.MouseEvent;
              handleDeleteSession(activeSession.id, fakeEvent);
            }
          }}
          onOpenSettings={() => setIsSettingsOpen(true)}
          activeChatTitle={activeSession?.title}
          isOnline={isOnline}
        />

        <main className="chat-main-area">
          <div className="chat-scroll-content">
            {activeSession && activeSession.messages.length <= 1 && (
              <QuickPrompts onSelectPrompt={(prompt) => handleSendMessage(prompt, [])} />
            )}

            <div className="chat-messages-list">
              {activeSession?.messages.map((msg) => (
                <ChatMessage key={msg.id} message={msg} />
              ))}

              {isLoading && (
                <div className="message-row bot-row">
                  <div className="message-avatar" aria-hidden="true">
                    <svg
                      width="18"
                      height="18"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      strokeWidth="2"
                    >
                      <path d="M12 2a2 2 0 0 1 2 2v2a2 2 0 0 1-2 2 2 2 0 0 1-2-2V4a2 2 0 0 1 2-2z" />
                      <rect x="3" y="8" width="18" height="12" rx="3" />
                      <circle cx="8" cy="14" r="1.5" />
                      <circle cx="16" cy="14" r="1.5" />
                      <path d="M10 18h4" />
                    </svg>
                  </div>
                  <div className="message-bubble bot-bubble typing-bubble">
                    <span className="typing-dot" />
                    <span className="typing-dot" />
                    <span className="typing-dot" />
                    <span className="typing-label">Анализ запроса...</span>
                  </div>
                </div>
              )}

              <div ref={messagesEndRef} />
            </div>
          </div>
        </main>

        <footer className="chat-footer-area">
          <ChatInput onSendMessage={handleSendMessage} isLoading={isLoading} />
        </footer>

        {isSettingsOpen && (
          <SettingsModal
            isOpen={isSettingsOpen}
            onClose={() => setIsSettingsOpen(false)}
            settings={settings}
            onSaveSettings={setSettings}
            onLogout={handleLogout}
          />
        )}
      </div>
    </div>
  );
}

export default App;
