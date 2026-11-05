package com.example.data

object CubeWebSourceRepository {

    val fullSingleFileHtmlCode: String = """
<!DOCTYPE html>
<html lang="en" class="dark">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Cube AI — Futuristic Neural Interface</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=JetBrains+Mono:wght@400;500&family=Space+Grotesk:wght@500;600;700&display=swap" rel="stylesheet">
  <script>
    tailwind.config = {
      darkMode: 'class',
      theme: {
        extend: {
          colors: {
            cube: {
              bg: '#0B0F17',
              surface: '#111826',
              card: '#151E2E',
              border: '#1E293B',
              cyan: '#00F0FF',
              cyanDim: '#00B8C4'
            }
          },
          fontFamily: {
            display: ['Space Grotesk', 'sans-serif'],
            sans: ['Inter', 'sans-serif'],
            mono: ['JetBrains Mono', 'monospace']
          },
          boxShadow: {
            'cyan-glow': '0 0 25px -4px rgba(0, 240, 255, 0.28)',
            'cyan-sm': '0 0 12px -2px rgba(0, 240, 255, 0.22)'
          }
        }
      }
    }
  </script>
  <style>
    body {
      background-color: #0B0F17;
      color: #F8FAFC;
      font-family: 'Inter', sans-serif;
      overflow: hidden;
    }
    .bg-grid-pattern {
      background-image: radial-gradient(rgba(0, 240, 255, 0.07) 1px, transparent 1px);
      background-size: 28px 28px;
    }
    .input-glow:focus-within {
      border-color: rgba(0, 240, 255, 0.65);
      box-shadow: 0 0 28px -4px rgba(0, 240, 255, 0.25);
    }
    ::-webkit-scrollbar { width: 5px; height: 5px; }
    ::-webkit-scrollbar-track { background: transparent; }
    ::-webkit-scrollbar-thumb { background: #1E293B; border-radius: 9999px; }
    ::-webkit-scrollbar-thumb:hover { background: #00F0FF; }
    @keyframes pulseCursor {
      0%, 100% { opacity: 1; }
      50% { opacity: 0.2; }
    }
    .streaming-cursor {
      display: inline-block;
      width: 8px;
      height: 15px;
      background: #00F0FF;
      vertical-align: middle;
      margin-left: 4px;
      animation: pulseCursor 0.8s infinite;
    }
  </style>
</head>
<body class="h-screen w-screen flex select-none bg-cube-bg text-slate-100">

  <!-- COLLAPSIBLE SIDEBAR -->
  <aside id="sidebar" class="w-72 bg-cube-surface/95 border-r border-cube-border flex flex-col justify-between transition-all duration-300 z-30">
    <!-- Top: Brand Logo & New Chat -->
    <div class="p-4 space-y-4">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-3">
          <div class="w-9 h-9 rounded-xl bg-cyan-500/10 border border-cube-cyan/50 flex items-center justify-center shadow-cyan-sm">
            <svg class="w-5 h-5 text-cube-cyan" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
              <polyline points="3.27 6.96 12 12.01 20.73 6.96"/>
              <line x1="12" y1="22.08" x2="12" y2="12"/>
            </svg>
          </div>
          <div>
            <span class="font-display font-bold text-lg tracking-wider text-white">CUBE</span>
            <span class="ml-1.5 text-[10px] font-mono uppercase px-1.5 py-0.5 rounded bg-cyan-500/15 text-cube-cyan border border-cube-cyan/30">OS 2.4</span>
          </div>
        </div>
        <button id="collapseSidebarBtn" class="p-2 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800/60 transition">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 19l-7-7 7-7m8 14l-7-7 7-7"/></svg>
        </button>
      </div>

      <button id="newChatBtn" class="w-full py-2.5 px-4 rounded-xl bg-gradient-to-r from-cyan-500/20 to-sky-500/10 hover:from-cyan-500/30 hover:to-sky-500/20 border border-cube-cyan/50 text-white font-display font-medium text-sm flex items-center justify-center gap-2 shadow-cyan-sm transition-all active:scale-[0.98]">
        <svg class="w-4 h-4 text-cube-cyan" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.2" d="M12 4v16m8-8H4"/></svg>
        <span>New Chat</span>
      </button>
    </div>

    <!-- Middle: Recent Chat History List -->
    <div class="flex-1 overflow-y-auto px-3 space-y-1" id="historyList">
      <div class="px-2 py-1.5 text-[11px] font-mono uppercase tracking-wider text-slate-400">Recent Sessions</div>
      <!-- Populated dynamically via JS -->
    </div>

    <!-- Bottom: User Profile, Settings & Theme Mode Indicator -->
    <div class="p-3 border-t border-cube-border space-y-2 bg-slate-950/40">
      <div class="flex items-center justify-between px-2.5 py-2 rounded-xl bg-cube-card/70 border border-slate-800">
        <div class="flex items-center gap-2">
          <span class="w-2 h-2 rounded-full bg-cube-cyan animate-pulse"></span>
          <span class="text-xs font-mono text-slate-300" id="themeModeText">Stealth #0B0F17</span>
        </div>
        <button id="toggleThemeBtn" class="text-[11px] font-mono text-cube-cyan hover:underline">Switch</button>
      </div>

      <div class="flex items-center justify-between p-2 rounded-xl hover:bg-slate-800/50 transition cursor-pointer">
        <div class="flex items-center gap-3">
          <div class="w-8 h-8 rounded-lg bg-gradient-to-br from-cyan-400 to-blue-600 flex items-center justify-center text-slate-950 font-bold text-xs">AM</div>
          <div>
            <div class="text-xs font-semibold text-white">Alex Mercer</div>
            <div class="text-[10px] font-mono text-cube-cyan">CUBE PRO • ACTIVE</div>
          </div>
        </div>
        <button class="p-1.5 text-slate-400 hover:text-white rounded-lg hover:bg-slate-700/40 transition" title="Settings">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"/><circle cx="12" cy="12" r="3"/></svg>
        </button>
      </div>
    </div>
  </aside>

  <!-- MAIN CHAT INTERFACE -->
  <main class="flex-1 flex flex-col h-full relative bg-grid-pattern">
    <!-- Top Header -->
    <header class="h-16 border-b border-cube-border bg-cube-bg/80 backdrop-blur-md px-5 flex items-center justify-between z-20">
      <div class="flex items-center gap-3">
        <button id="openSidebarBtn" class="hidden p-2 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800/60 transition">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"/></svg>
        </button>
        <span class="font-display font-bold text-base tracking-wide text-white">Cube AI</span>
        <!-- Model Selector Dropdown -->
        <select id="modelSelect" class="bg-cube-card border border-cube-border hover:border-cube-cyan/50 text-xs font-mono text-slate-200 rounded-xl px-3 py-1.5 focus:outline-none focus:border-cube-cyan transition">
          <option value="Cube 1.0 Pro">Cube 1.0 Pro • Deep Reasoning</option>
          <option value="Cube Fast">Cube Fast • Ultra-Low Latency</option>
          <option value="Cube Quantum">Cube Quantum • Code & Math</option>
        </select>
      </div>

      <button id="clearChatBtn" class="px-3 py-1.5 rounded-xl border border-slate-800 hover:border-rose-500/50 bg-slate-900/60 hover:bg-rose-500/10 text-xs font-mono text-slate-300 hover:text-rose-300 flex items-center gap-1.5 transition">
        <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg>
        <span>Clear Chat</span>
      </button>
    </header>

    <!-- Scrollable Viewport -->
    <div id="chatScrollArea" class="flex-1 overflow-y-auto px-4 md:px-12 py-8 pb-44 space-y-6">
      <!-- Welcome Empty State -->
      <section id="welcomeView" class="max-w-3xl mx-auto mt-10 text-center space-y-8">
        <div class="relative inline-flex items-center justify-center">
          <div class="absolute -inset-4 rounded-full bg-cube-cyan/15 blur-xl"></div>
          <div class="relative w-20 h-20 rounded-2xl bg-cube-surface border border-cube-cyan/50 shadow-cyan-glow flex items-center justify-center">
            <svg class="w-10 h-10 text-cube-cyan" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
              <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
              <polyline points="3.27 6.96 12 12.01 20.73 6.96"/>
              <line x1="12" y1="22.08" x2="12" y2="12"/>
            </svg>
          </div>
        </div>

        <div class="space-y-2">
          <h1 class="font-display font-bold text-3xl md:text-4xl text-white tracking-tight">What can Cube help you with today?</h1>
          <p class="text-sm text-slate-400 max-w-lg mx-auto">High-contrast neural workspace engineered for code synthesis, real-time data analysis, and rapid ideation.</p>
        </div>

        <!-- 4 Quick-Start Suggestion Cards -->
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-3.5 text-left">
          <button onclick="triggerQuickPrompt('Write a clean, high-performance WebGL shader and interactive particle cube in JavaScript.')" class="group p-4 rounded-2xl bg-cube-surface/90 hover:bg-cube-card border border-cube-border hover:border-cube-cyan/50 transition-all hover:-translate-y-0.5 shadow-sm">
            <div class="flex items-center justify-between mb-2">
              <span class="text-xs font-mono uppercase text-cube-cyan">Write Code</span>
              <span class="text-slate-500 group-hover:text-cube-cyan transition">→</span>
            </div>
            <div class="font-display font-semibold text-sm text-white mb-1">Synthesize WebGL Shader</div>
            <p class="text-xs text-slate-400">Build an interactive 60fps isometric 3D particle cube in vanilla JS.</p>
          </button>

          <button onclick="triggerQuickPrompt('Analyze Q4 SaaS retention metrics and identify the top 3 churn bottlenecks with SQL queries.')" class="group p-4 rounded-2xl bg-cube-surface/90 hover:bg-cube-card border border-cube-border hover:border-cube-cyan/50 transition-all hover:-translate-y-0.5 shadow-sm">
            <div class="flex items-center justify-between mb-2">
              <span class="text-xs font-mono uppercase text-sky-400">Analyze Data</span>
              <span class="text-slate-500 group-hover:text-cube-cyan transition">→</span>
            </div>
            <div class="font-display font-semibold text-sm text-white mb-1">Cohort Telemetry Breakdown</div>
            <p class="text-xs text-slate-400">Uncover retention anomalies and generate diagnostic SQL pipelines.</p>
          </button>

          <button onclick="triggerQuickPrompt('Brainstorm 5 disruptive AI-native developer tools with low-latency edge inference.')" class="group p-4 rounded-2xl bg-cube-surface/90 hover:bg-cube-card border border-cube-border hover:border-cube-cyan/50 transition-all hover:-translate-y-0.5 shadow-sm">
            <div class="flex items-center justify-between mb-2">
              <span class="text-xs font-mono uppercase text-purple-400">Brainstorm Ideas</span>
              <span class="text-slate-500 group-hover:text-cube-cyan transition">→</span>
            </div>
            <div class="font-display font-semibold text-sm text-white mb-1">Edge AI Product Concepts</div>
            <p class="text-xs text-slate-400">Explore next-gen developer workflows powered by local neural models.</p>
          </button>

          <button onclick="triggerQuickPrompt('Design a zero-trust microservices architecture using Rust, gRPC, and distributed Raft consensus.')" class="group p-4 rounded-2xl bg-cube-surface/90 hover:bg-cube-card border border-cube-border hover:border-cube-cyan/50 transition-all hover:-translate-y-0.5 shadow-sm">
            <div class="flex items-center justify-between mb-2">
              <span class="text-xs font-mono uppercase text-emerald-400">System Architecture</span>
              <span class="text-slate-500 group-hover:text-cube-cyan transition">→</span>
            </div>
            <div class="font-display font-semibold text-sm text-white mb-1">Distributed Rust Mesh</div>
            <p class="text-xs text-slate-400">Blueprint fault-tolerant state machines with sub-millisecond RPCs.</p>
          </button>
        </div>
      </section>

      <!-- Active Messages Container -->
      <div id="messagesContainer" class="max-w-3xl mx-auto space-y-6 hidden"></div>
    </div>

    <!-- Floating Grok-Style Bottom Input Bar -->
    <div class="absolute bottom-0 inset-x-0 bg-gradient-to-t from-cube-bg via-cube-bg/95 to-transparent pt-6 pb-5 px-4 md:px-12">
      <div class="max-w-3xl mx-auto">
        <div class="rounded-2xl bg-cube-surface/95 border border-cube-border input-glow transition-all p-3 shadow-2xl">
          <textarea id="chatInput" rows="1" placeholder="Ask Cube anything, attach files, or search the web..." class="w-full bg-transparent text-sm text-white placeholder-slate-500 focus:outline-none resize-none max-h-36 px-1 py-1"></textarea>
          <div class="flex items-center justify-between pt-2 mt-1 border-t border-slate-800/80">
            <div class="flex items-center gap-1.5">
              <!-- Attachment Button -->
              <button id="attachBtn" class="p-2 rounded-xl text-slate-400 hover:text-white hover:bg-slate-800/70 transition" title="Attach file">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.172 7l-6.586 6.586a2 2 0 102.828 2.828l6.414-6.586a4 4 0 00-5.656-5.656l-6.415 6.585a6 6 0 108.486 8.486L20.5 13"/></svg>
              </button>
              <!-- Web Search Toggle -->
              <button id="webSearchToggle" class="px-2.5 py-1.5 rounded-xl border border-slate-800 text-slate-400 hover:text-white text-xs font-mono flex items-center gap-1.5 transition">
                <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><circle cx="12" cy="12" r="10" stroke-width="2"/><path stroke-width="2" d="M2 12h20M12 2a15.3 15.3 0 014 10 15.3 15.3 0 01-4 10 15.3 15.3 0 01-4-10 15.3 15.3 0 014-10z"/></svg>
                <span>Web Search</span>
              </button>
              <!-- Voice Input Button -->
              <button id="voiceBtn" class="p-2 rounded-xl text-slate-400 hover:text-cube-cyan hover:bg-slate-800/70 transition" title="Voice Input">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11a7 7 0 01-7 7m0 0a7 7 0 01-7-7m7 7v4m0 0H8m4 0h4m-4-8a3 3 0 01-3-3V5a3 3 0 116 0v6a3 3 0 01-3 3z"/></svg>
              </button>
            </div>

            <!-- Prominent Send Button -->
            <button id="sendBtn" class="px-4 py-2 rounded-xl bg-cube-cyan hover:bg-cyan-300 text-slate-950 font-display font-bold text-xs tracking-wide flex items-center gap-1.5 shadow-cyan-sm transition active:scale-95">
              <span>Send</span>
              <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M5 12h14M12 5l7 7-7 7"/></svg>
            </button>
          </div>
        </div>
      </div>
    </div>
  </main>

  <script>
    const chatInput = document.getElementById('chatInput');
    const sendBtn = document.getElementById('sendBtn');
    const welcomeView = document.getElementById('welcomeView');
    const messagesContainer = document.getElementById('messagesContainer');
    const newChatBtn = document.getElementById('newChatBtn');
    const clearChatBtn = document.getElementById('clearChatBtn');
    const webSearchToggle = document.getElementById('webSearchToggle');
    let webSearchActive = false;

    webSearchToggle.addEventListener('click', () => {
      webSearchActive = !webSearchActive;
      webSearchToggle.className = webSearchActive
        ? 'px-2.5 py-1.5 rounded-xl border border-cube-cyan/60 bg-cyan-500/15 text-cube-cyan text-xs font-mono flex items-center gap-1.5 transition'
        : 'px-2.5 py-1.5 rounded-xl border border-slate-800 text-slate-400 hover:text-white text-xs font-mono flex items-center gap-1.5 transition';
    });

    function triggerQuickPrompt(text) {
      chatInput.value = text;
      handleSendMessage();
    }

    function handleSendMessage() {
      const content = chatInput.value.trim();
      if (!content) return;
      welcomeView.classList.add('hidden');
      messagesContainer.classList.remove('hidden');

      appendUserMessage(content);
      chatInput.value = '';
      setTimeout(() => simulateCubeStreamingResponse(content), 850);
    }

    sendBtn.addEventListener('click', handleSendMessage);
    chatInput.addEventListener('keydown', (e) => {
      if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault();
        handleSendMessage();
      }
    });

    newChatBtn.addEventListener('click', resetToWelcome);
    clearChatBtn.addEventListener('click', resetToWelcome);

    function resetToWelcome() {
      messagesContainer.innerHTML = '';
      messagesContainer.classList.add('hidden');
      welcomeView.classList.remove('hidden');
    }
  </script>
</body>
</html>
""".trimIndent()
}
