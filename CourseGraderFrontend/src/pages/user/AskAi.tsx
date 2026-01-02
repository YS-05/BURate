import { useState, useRef, useEffect } from "react";
import ReactMarkdown from "react-markdown"; // <--- NEW: Renders bolding/lists
import { askAIAdvisor } from "../../api/axios";
import Spinner from "../../components/Spinner";

type Message = {
  role: "user" | "assistant";
  content: string;
};

const AskAi = () => {
  const [input, setInput] = useState("");
  const [messages, setMessages] = useState<Message[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Reference to the bottom of the chat list
  const messagesEndRef = useRef<HTMLDivElement>(null);

  // AUTO-SCROLL: Whenever 'messages' or 'loading' changes, scroll to bottom
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, loading]);

  const handleSend = async () => {
    if (!input.trim() || loading) return;

    const userMessage: Message = { role: "user", content: input };

    setMessages((prev) => [...prev, userMessage]);
    setInput("");
    setLoading(true);
    setError(null);

    try {
      const response = await askAIAdvisor({ message: userMessage.content });
      setMessages((prev) => [
        ...prev,
        { role: "assistant", content: response },
      ]);
    } catch (err) {
      console.error(err);
      setError("AI Advisor is currently unavailable. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container my-5">
      <h1 className="fw-bold mb-2">AI Advisor (Beta)</h1>
      <p className="text-muted">
        Consider marking courses as completed for better recommendations/advice!
      </p>
      <div
        className="border rounded-2 d-flex flex-column shadow-sm" // Added shadow
        style={{ height: "600px", backgroundColor: "#f8f9fa" }} // Taller & Light Gray BG
      >
        {/* Messages Area */}
        <div className="flex-grow-1 p-4 overflow-auto">
          {messages.length === 0 && !loading && (
            <div className="text-center text-muted mt-5">
              <h4>
                <i className="bi bi-chat-text me-2 bu-red"></i>Hello!
              </h4>
              <p>
                I can help with course planning, hub requirements, and much
                more!.
              </p>
              <p className="small">
                Try asking: <em>"What courses satisfy Scientific Inquiry?"</em>
              </p>
            </div>
          )}

          {messages.map((msg, idx) => (
            <div
              key={idx}
              className={`mb-3 d-flex ${
                msg.role === "user"
                  ? "justify-content-end"
                  : "justify-content-start"
              }`}
            >
              <div
                className={`p-3 rounded-3 ${
                  msg.role === "user"
                    ? "bg-bu-red text-white" // BU Red
                    : "bg-white border text-dark shadow-sm" // AI Bubble
                }`}
                style={{
                  maxWidth: "80%",
                  wordWrap: "break-word",
                }}
              >
                {/* MARKDOWN RENDERER: Makes bolding and lists look real */}
                {msg.role === "assistant" ? (
                  <ReactMarkdown>{msg.content}</ReactMarkdown>
                ) : (
                  msg.content
                )}
              </div>
            </div>
          ))}

          {loading && <Spinner />}

          {/* Invisible div to scroll to */}
          <div ref={messagesEndRef} />
        </div>

        {/* Input Area */}
        <div className="border-top p-3 bg-white">
          <div className="input-group">
            <input
              className="form-control form-control-lg"
              placeholder="Ask a question..."
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && handleSend()}
              disabled={loading}
            />
            <button
              className="btn btn-bu-red px-4"
              onClick={handleSend}
              disabled={loading || !input.trim()}
            >
              Send
            </button>
          </div>
          {error && <div className="text-danger small mt-2">{error}</div>}
        </div>
      </div>
    </div>
  );
};

export default AskAi;
