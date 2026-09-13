import React from 'react';
import type { QuickPrompt } from '../types/chat';

interface QuickPromptsProps {
  onSelectPrompt: (prompt: string) => void;
}

const DEFAULT_PROMPTS: QuickPrompt[] = [
  {
    id: 'spec',
    title: 'Рассчитать спецификацию',
    prompt: 'Рассчитай пример спецификации деталей и расход материалов',
    category: 'Расчёт',
  },
  {
    id: 'formats',
    title: 'Поддерживаемые форматы',
    prompt: 'Какие форматы файлов и чертежей ты умеешь анализировать?',
    category: 'Файлы',
  },
  {
    id: 'capabilities',
    title: 'Возможности агента',
    prompt: 'Что ты умеешь и как автоматизируешь работу инженера?',
    category: 'Справка',
  },
  {
    id: 'tolerance',
    title: 'Анализ размеров и допусков',
    prompt: 'Как ты определяешь допуски, посадки и шероховатости на чертеже?',
    category: 'Техпроцесс',
  },
];

export const QuickPrompts: React.FC<QuickPromptsProps> = ({ onSelectPrompt }) => {
  return (
    <div className="quick-prompts-container">
      <div className="quick-prompts-hero">
        <div className="quick-prompts-icon">
          <svg
            width="32"
            height="32"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="1.8"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <polygon points="12 2 2 7 12 12 22 7 12 2" />
            <polyline points="2 17 12 22 22 17" />
            <polyline points="2 12 12 17 22 12" />
          </svg>
        </div>
        <h2 className="quick-prompts-title">Добро пожаловать в Blueprint Agent</h2>
        <p className="quick-prompts-subtitle">
          Загрузите чертёж или выберите типовой запрос для быстрого старта:
        </p>
      </div>

      <div className="quick-prompts-grid">
        {DEFAULT_PROMPTS.map((item) => (
          <button
            key={item.id}
            type="button"
            className="quick-prompt-card"
            onClick={() => onSelectPrompt(item.prompt)}
          >
            <span className="quick-prompt-badge">{item.category}</span>
            <span className="quick-prompt-card-title">{item.title}</span>
            <span className="quick-prompt-arrow">→</span>
          </button>
        ))}
      </div>
    </div>
  );
};
