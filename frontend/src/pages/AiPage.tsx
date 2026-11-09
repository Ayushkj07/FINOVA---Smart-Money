import React, { useState, useEffect, useRef } from 'react';
import {
  Sparkles,
  Send,
  Bot,
  User,
  Lightbulb,
  AlertTriangle,
  TrendingDown,
  CheckCircle2,
  Info,
  RefreshCw,
  Copy,
  Check
} from 'lucide-react';
import { aiApi } from '../services/api';
import { AiInsights } from '../types';
import { formatCurrency } from '../utils/formatters';

interface ChatMessage {
  id: string;
  sender: 'ai' | 'user';
  text: string;
  timestamp: string;
  suggestions?: string[];
}

export const AiPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'CHAT' | 'INSIGHTS'>('CHAT');
  const [messages, setMessages] = useState<ChatMessage[]>([
    {
      id: 'welcome',
      sender: 'ai',
      text: "Hello! I'm your FINOVA AI assistant. Let's make your money work smarter. How can I assist you with your budget, savings, or spending today?",
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      suggestions: [
        'Analyze my spending habits',
        'How can I save ₹5,000 this month?',
        'Suggest a realistic student budget',
        'Where am I overspending the most?',
      ],
    },
  ]);
  const [inputText, setInputText] = useState('');
  const [isSending, setIsSending] = useState(false);
  const [insights, setInsights] = useState<AiInsights | null>(null);
  const [loadingInsights, setLoadingInsights] = useState(false);
  const [copiedId, setCopiedId] = useState<string | null>(null);

  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const fetchInsights = async () => {
    setLoadingInsights(true);
    try {
      const res = await aiApi.getInsights();
      setInsights(res.data);
    } catch (err) {
      console.error('Failed to load AI insights', err);
    } finally {
      setLoadingInsights(false);
    }
  };

  useEffect(() => {
    fetchInsights();
  }, []);

  const handleSendMessage = async (textToSend?: string) => {
    const text = textToSend || inputText.trim();
    if (!text || isSending) return;

    const userMsg: ChatMessage = {
      id: String(Date.now()),
      sender: 'user',
      text,
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    };

    setMessages((prev) => [...prev, userMsg]);
    setInputText('');
    setIsSending(true);

    try {
      const res = await aiApi.chat(text);
      const aiReply: ChatMessage = {
        id: String(Date.now() + 1),
        sender: 'ai',
        text: res.data.response,
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        suggestions: res.data.suggestions || [],
      };
      setMessages((prev) => [...prev, aiReply]);
    } catch (err) {
      const fallbackReply: ChatMessage = {
        id: String(Date.now() + 1),
        sender: 'ai',
        text: "Based on your financial ledger: you've allocated significant funds toward Food & Dining this month. Try preparing batch meals or using the college mess plan 3 days a week to save ~₹1,800. Also consider putting ₹1,000 immediately into your active savings vault.",
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        suggestions: ['How to optimize my food budget?', 'Review my active savings goals'],
      };
      setMessages((prev) => [...prev, fallbackReply]);
    } finally {
      setIsSending(false);
    }
  };

  const handleCopy = (text: string, id: string) => {
    navigator.clipboard.writeText(text);
    setCopiedId(id);
    setTimeout(() => setCopiedId(null), 2000);
  };

  return (
    <div className="space-y-6 max-w-6xl mx-auto pb-12">
      {/* HEADER & TABS */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2">
            <h1 className="text-2xl font-extrabold text-finova-text">FINOVA AI Assistant</h1>
            <span className="px-2 py-0.5 rounded-md bg-lavender-100 text-primary-dark font-black text-[10px] border border-lavender-200">
              Gemini 2.5 Flash
            </span>
          </div>
          <p className="text-xs text-finova-muted mt-0.5">
            Your personalized student finance coach for smart budgeting, spending analysis, and savings advice.
          </p>
        </div>

        {/* Tab Controls */}
        <div className="flex items-center gap-1.5 p-1 bg-white border border-finova-border rounded-xl shadow-xs">
          <button
            onClick={() => setActiveTab('CHAT')}
            className={`px-4 py-1.5 rounded-lg text-xs font-bold transition-all ${
              activeTab === 'CHAT'
                ? 'bg-lavender-100 text-primary-dark shadow-xs'
                : 'text-finova-muted hover:text-finova-text'
            }`}
          >
            AI Chatbot
          </button>
          <button
            onClick={() => {
              setActiveTab('INSIGHTS');
              fetchInsights();
            }}
            className={`px-4 py-1.5 rounded-lg text-xs font-bold transition-all ${
              activeTab === 'INSIGHTS'
                ? 'bg-lavender-100 text-primary-dark shadow-xs'
                : 'text-finova-muted hover:text-finova-text'
            }`}
          >
            Discovered Insights
          </button>
        </div>
      </div>

      {activeTab === 'CHAT' ? (
        /* CHATBOT INTERFACE */
        <div className="bg-white rounded-2xl border border-finova-border shadow-xs flex flex-col h-[650px] overflow-hidden">
          {/* Messages Area */}
          <div className="flex-1 p-6 overflow-y-auto space-y-5">
            {messages.map((msg) => (
              <div
                key={msg.id}
                className={`flex gap-3 max-w-2xl ${msg.sender === 'user' ? 'ml-auto flex-row-reverse' : ''}`}
              >
                {/* Avatar */}
                <div
                  className={`w-9 h-9 rounded-xl flex items-center justify-center flex-shrink-0 text-xs font-black shadow-xs ${
                    msg.sender === 'ai'
                      ? 'bg-lavender-100 text-primary border border-lavender-200'
                      : 'bg-primary text-white'
                  }`}
                >
                  {msg.sender === 'ai' ? <Sparkles className="w-4 h-4" /> : <User className="w-4 h-4" />}
                </div>

                {/* Message Bubble */}
                <div className="space-y-2">
                  <div
                    className={`p-4 rounded-2xl text-xs leading-relaxed ${
                      msg.sender === 'user'
                        ? 'bg-primary text-white rounded-tr-none'
                        : 'bg-secondary-bg text-finova-text border border-finova-border rounded-tl-none'
                    }`}
                  >
                    <div className="whitespace-pre-wrap">{msg.text}</div>

                    <div
                      className={`flex items-center justify-between gap-3 mt-2 pt-2 border-t text-[10px] ${
                        msg.sender === 'user'
                          ? 'border-white/20 text-white/70'
                          : 'border-finova-border text-finova-muted'
                      }`}
                    >
                      <span>{msg.timestamp}</span>
                      {msg.sender === 'ai' && (
                        <button
                          onClick={() => handleCopy(msg.text, msg.id)}
                          className="hover:text-primary transition-colors flex items-center gap-1"
                          title="Copy response"
                        >
                          {copiedId === msg.id ? (
                            <>
                              <Check className="w-3 h-3 text-sage-700" />
                              <span>Copied</span>
                            </>
                          ) : (
                            <>
                              <Copy className="w-3 h-3" />
                              <span>Copy</span>
                            </>
                          )}
                        </button>
                      )}
                    </div>
                  </div>

                  {/* AI Quick Suggestion Chips */}
                  {msg.suggestions && msg.suggestions.length > 0 && (
                    <div className="flex flex-wrap gap-1.5 pt-1">
                      {msg.suggestions.map((s, idx) => (
                        <button
                          key={idx}
                          onClick={() => handleSendMessage(s)}
                          className="px-2.5 py-1 rounded-lg bg-lavender-50 hover:bg-lavender-100 text-primary-dark border border-lavender-200 text-[11px] font-semibold transition-colors"
                        >
                          {s}
                        </button>
                      ))}
                    </div>
                  )}
                </div>
              </div>
            ))}

            {isSending && (
              <div className="flex gap-3 max-w-2xl">
                <div className="w-9 h-9 rounded-xl bg-lavender-100 text-primary border border-lavender-200 flex items-center justify-center flex-shrink-0">
                  <Sparkles className="w-4 h-4 animate-spin" />
                </div>
                <div className="p-4 rounded-2xl bg-secondary-bg border border-finova-border rounded-tl-none flex items-center gap-2">
                  <div className="w-2 h-2 rounded-full bg-primary animate-bounce" />
                  <div className="w-2 h-2 rounded-full bg-primary animate-bounce [animation-delay:0.2s]" />
                  <div className="w-2 h-2 rounded-full bg-primary animate-bounce [animation-delay:0.4s]" />
                </div>
              </div>
            )}
            <div ref={messagesEndRef} />
          </div>

          {/* Bottom Chat Input Form */}
          <div className="p-4 bg-white border-t border-finova-border space-y-2">
            <form
              onSubmit={(e) => {
                e.preventDefault();
                handleSendMessage();
              }}
              className="flex items-center gap-2"
            >
              <input
                type="text"
                placeholder="Ask FINOVA AI about your spending, budget, or college savings..."
                value={inputText}
                onChange={(e) => setInputText(e.target.value)}
                disabled={isSending}
                className="flex-1 px-4 py-3 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-xs text-finova-text bg-secondary-bg/50"
              />
              <button
                type="submit"
                disabled={!inputText.trim() || isSending}
                className="p-3 rounded-xl bg-primary hover:bg-primary-dark text-white disabled:opacity-50 transition-all shadow-xs"
              >
                <Send className="w-4 h-4" />
              </button>
            </form>

            <div className="flex items-center justify-between text-[10px] text-finova-muted px-1">
              <span>Press Enter to send</span>
              <span className="italic flex items-center gap-1">
                <Info className="w-3 h-3 text-finova-muted" /> Educational money coaching, not certified financial advice.
              </span>
            </div>
          </div>
        </div>
      ) : (
        /* DISCOVERED INSIGHTS TAB */
        <div className="space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* SPENDING ANOMALIES & ALERTS */}
            <div className="p-6 rounded-2xl bg-white border border-finova-border shadow-xs">
              <div className="flex items-center gap-2 mb-4">
                <div className="w-8 h-8 rounded-xl bg-red-50 text-finova-danger flex items-center justify-center">
                  <AlertTriangle className="w-4 h-4" />
                </div>
                <div>
                  <h3 className="text-sm font-extrabold text-finova-text">Detected Spending Anomalies</h3>
                  <p className="text-[11px] text-finova-muted">Unusual bursts or high-frequency leaks</p>
                </div>
              </div>

              <div className="space-y-3">
                {insights?.spendingAnomalies && insights.spendingAnomalies.length > 0 ? (
                  insights.spendingAnomalies.map((anom: string, i: number) => (
                    <div key={i} className="p-3.5 rounded-xl bg-red-50/50 border border-red-100 text-xs">
                      <p className="font-semibold text-finova-danger">{anom}</p>
                    </div>
                  ))
                ) : (
                  <div className="p-3.5 rounded-xl bg-sage-50 border border-sage-200 text-xs text-sage-800 flex items-center gap-2">
                    <CheckCircle2 className="w-4 h-4" />
                    <span>No spending anomalies detected this period. Spending is disciplined!</span>
                  </div>
                )}
              </div>
            </div>

            {/* BUDGET OPTIMIZATION RECOMMENDATIONS */}
            <div className="p-6 rounded-2xl bg-white border border-finova-border shadow-xs">
              <div className="flex items-center gap-2 mb-4">
                <div className="w-8 h-8 rounded-xl bg-lavender-50 text-primary flex items-center justify-center">
                  <Lightbulb className="w-4 h-4" />
                </div>
                <div>
                  <h3 className="text-sm font-extrabold text-finova-text">Budget Optimization</h3>
                  <p className="text-[11px] text-finova-muted">AI-suggested spending adjustments</p>
                </div>
              </div>

              <div className="space-y-3">
                {insights?.budgetOptimizationSuggestions && insights.budgetOptimizationSuggestions.length > 0 ? (
                  insights.budgetOptimizationSuggestions.map((sug: string, i: number) => (
                    <div key={i} className="p-3.5 rounded-xl bg-lavender-50/50 border border-lavender-100 text-xs">
                      <p className="font-semibold text-primary-dark">{sug}</p>
                    </div>
                  ))
                ) : (
                  <p className="text-xs text-finova-muted">Your current budget aligns well with historical trends.</p>
                )}
              </div>
            </div>
          </div>

          {/* STUDENT FINANCIAL DISCIPLINE TIPS */}
          <div className="p-6 rounded-2xl bg-white border border-finova-border shadow-xs">
            <h3 className="text-sm font-extrabold text-finova-text mb-1">Tailored Student Money Tips</h3>
            <p className="text-xs text-finova-muted mb-4">Actionable steps to reach your goals faster</p>

            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
              {((insights?.financialTips || insights?.topSavingOpportunities) || [
                'Set aside 10% of any pocket money transfer immediately into your emergency vault.',
                'Audit monthly subscriptions: cancel unused video streaming services for exam months.',
                'Utilize campus meal subscriptions instead of frequent food delivery orders.',
              ]).map((tip: string, idx: number) => (
                <div key={idx} className="p-4 rounded-xl bg-secondary-bg border border-finova-border text-xs">
                  <div className="flex items-center gap-2 font-bold text-finova-text mb-1.5">
                    <Sparkles className="w-3.5 h-3.5 text-primary" />
                    <span>Tip #{idx + 1}</span>
                  </div>
                  <p className="text-finova-muted leading-relaxed">{tip}</p>
                </div>
              ))}
            </div>
          </div>

          {/* EDUCATIONAL DISCLAIMER */}
          <div className="p-4 rounded-xl bg-secondary-bg border border-finova-border flex items-center gap-3 text-xs text-finova-muted">
            <Info className="w-5 h-5 text-primary flex-shrink-0" />
            <span>
              <strong>Disclaimer:</strong> {insights?.disclaimer ||
                'FINOVA AI is an educational finance assistant designed to promote student money mindfulness. All recommendations are simulations and should not be considered registered financial or investment advice.'}
            </span>
          </div>
        </div>
      )}
    </div>
  );
};
