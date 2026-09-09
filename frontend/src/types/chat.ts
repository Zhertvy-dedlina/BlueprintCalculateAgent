export type MessageSender = 'user' | 'bot';

export interface ChatFile {
  id: string;
  name: string;
  size: number;
  type: string;
  url?: string;
  rawFile?: File;
}

export interface Message {
  id: string;
  sender: MessageSender;
  text: string;
  timestamp: string;
  files?: ChatFile[];
  status?: 'sending' | 'sent' | 'error';
}

export interface ChatSettings {
  apiUrl: string;
}

export interface QuickPrompt {
  id: string;
  title: string;
  prompt: string;
  category?: string;
}

export interface ChatSession {
  id: string;
  title: string;
  createdAt: string;
  updatedAt: string;
  createdAtMs: number;   // epoch ms – used for date-group sorting
  messages: Message[];
}
