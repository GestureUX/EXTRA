package com.example.data

object CubeWebSourceRepository {

    val fullSingleFileHtmlCode: String = """
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Cube AI — Claude-Inspired Minimalist Interface</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=Lora:ital,wght@0,400;0,500;0,600;1,400&family=JetBrains+Mono:wght@400;500&display=swap" rel="stylesheet">
  <script>
    tailwind.config = {
      darkMode: 'class',
      theme: {
        extend: {
          colors: {
            claude: {
              cream: '#FBF9F5',
              creamDark: '#F4EFE6',
              darkBg: '#18181B',
              darkSurface: '#202024',
              darkCard: '#27272A',
              terracotta: '#CC785C',
              amber: '#D97706',
              borderLight: '#E8E2D8',
              borderDark: '#333338'
            }
          },
          fontFamily: {
            serif: ['Lora', 'Georgia', 'serif'],
            sans: ['Inter', 'sans-serif'],
            mono: ['JetBrains Mono', 'monospace']
          }
        }
      }
    }
  </script>
  <style>
    body {
      font-family: 'Inter', sans-serif;
      overflow: hidden;
    }
    .serif-title {
      font-family: 'Lora', Georgia, serif;
    }
    ::-webkit-scrollbar { width: 6px; height: 6px; }
    ::-webkit-scrollbar-track { background: transparent; }
    ::-webkit-scrollbar-thumb { background: #D5CFC5; border-radius: 9999px; }
    .dark ::-webkit-scrollbar-thumb { background: #3E3E44; }
  </style>
</head>
<body class="bg-[#FBF9F5] text-stone-900 dark:bg-[#18181B] dark:text-[#F4EFE6] transition-colors duration-200">
  <div class="flex h-screen w-screen overflow-hidden">
    <!-- Sidebar Navigation -->
    <aside id="sidebar" class="w-72 border-r border-[#E8E2D8] dark:border-[#333338] bg-[#F7F4EE] dark:bg-[#202024] flex flex-col justify-between shrink-0 transition-all duration-300">
      <div class="p-4 flex flex-col gap-3">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-2">
            <svg class="w-6 h-6 text-[#CC785C]" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2L14.2 9.8L22 12L14.2 14.2L12 22L9.8 14.2L2 12L9.8 9.8L12 2Z"/>
            </svg>
            <span class="serif-title text-xl font-normal">Cube</span>
          </div>
          <button id="themeToggleBtn" class="p-2 rounded-lg hover:bg-black/5 dark:hover:bg-white/5 text-stone-600 dark:text-stone-300" title="Toggle Light/Dark">
            <span id="themeIcon">🌙</span>
          </button>
        </div>

        <button id="newChatBtn" class="flex items-center justify-center gap-2 w-full py-2.5 px-4 rounded-xl border border-[#CC785C]/30 bg-[#CC785C]/10 text-stone-800 dark:text-stone-100 font-medium text-sm hover:bg-[#CC785C]/20 transition">
          <span>+</span>
          <span>New chat</span>
        </button>
      </div>

      <div class="flex-1 overflow-y-auto px-3 py-2 flex flex-col gap-1 text-sm">
        <div class="px-2 py-1 text-xs font-medium text-stone-500 dark:text-stone-400">Recent chats</div>
        <div class="p-2.5 rounded-lg bg-stone-200/60 dark:bg-stone-800/80 font-medium truncate cursor-pointer">
          Minimalism in AI Design
        </div>
        <div class="p-2.5 rounded-lg hover:bg-stone-200/40 dark:hover:bg-stone-800/40 text-stone-600 dark:text-stone-400 truncate cursor-pointer">
          TypeScript Immutable Diff
        </div>
      </div>

      <div class="p-4 border-t border-[#E8E2D8] dark:border-[#333338] flex items-center justify-between text-xs">
        <div class="flex items-center gap-2">
          <div class="w-7 h-7 rounded-full bg-[#CC785C] text-white flex items-center justify-center font-bold">C</div>
          <div>
            <div class="font-medium">User Workspace</div>
            <div class="text-[#CC785C]">gemini-1.5-flash</div>
          </div>
        </div>
      </div>
    </aside>

    <!-- Main Chat Workspace -->
    <main class="flex-1 flex flex-col h-full bg-[#FBF9F5] dark:bg-[#18181B] relative">
      <!-- Header -->
      <header class="h-14 border-b border-[#E8E2D8] dark:border-[#333338] px-6 flex items-center justify-between">
        <div class="flex items-center gap-3">
          <span class="text-sm font-medium bg-stone-200/70 dark:bg-stone-800 px-3 py-1 rounded-lg">
            Cube 1.5 Flash
          </span>
        </div>
        <button id="clearChatBtn" class="text-xs text-stone-500 hover:text-stone-800 dark:hover:text-stone-200">
          Clear chat
        </button>
      </header>

      <!-- Messages Viewport -->
      <section id="chatViewport" class="flex-1 overflow-y-auto p-6 max-w-3xl w-full mx-auto flex flex-col gap-6">
        <!-- Welcome View -->
        <div id="welcomeView" class="my-auto flex flex-col items-center text-center py-12">
          <svg class="w-16 h-16 text-[#CC785C] mb-4" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2L14.2 9.8L22 12L14.2 14.2L12 22L9.8 14.2L2 12L9.8 9.8L12 2Z"/>
          </svg>
          <h1 class="serif-title text-3xl md:text-4xl font-normal mb-3">How can Cube help you today?</h1>
          <p class="text-sm text-stone-500 dark:text-stone-400 max-w-md mb-8">
            Connected to Google AI Studio's Gemini 1.5 Flash API with multi-turn conversation memory.
          </p>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-3 w-full max-w-xl text-left text-sm">
            <button class="prompt-card p-4 rounded-2xl border border-[#E8E2D8] dark:border-[#333338] bg-white dark:bg-[#202024] hover:border-[#CC785C] transition">
              <div class="font-medium text-stone-800 dark:text-stone-100">Write code</div>
              <div class="text-xs text-stone-500 dark:text-stone-400 mt-1">Generate a TypeScript deep object diffing utility</div>
            </button>
            <button class="prompt-card p-4 rounded-2xl border border-[#E8E2D8] dark:border-[#333338] bg-white dark:bg-[#202024] hover:border-[#CC785C] transition">
              <div class="font-medium text-stone-800 dark:text-stone-100">Explain concept</div>
              <div class="text-xs text-stone-500 dark:text-stone-400 mt-1">Compare vector embeddings with B-tree indexes</div>
            </button>
          </div>
        </div>

        <!-- Chat Container -->
        <div id="messagesList" class="flex flex-col gap-6 w-full hidden"></div>
      </section>

      <!-- Floating Input Console -->
      <footer class="p-4 pb-6 max-w-3xl w-full mx-auto">
        <div class="rounded-3xl border border-[#E8E2D8] dark:border-[#333338] bg-white dark:bg-[#202024] p-3 shadow-sm focus-within:border-[#CC785C] focus-within:ring-1 focus-within:ring-[#CC785C] transition">
          <textarea id="chatInput" rows="2" placeholder="Reply to Cube or ask a question..." class="w-full bg-transparent resize-none border-none outline-none text-sm text-stone-800 dark:text-stone-100 px-2"></textarea>
          <div class="flex items-center justify-between pt-2 border-t border-stone-100 dark:border-stone-800">
            <div class="text-xs text-stone-400">Gemini 1.5 Flash</div>
            <button id="sendBtn" class="w-8 h-8 rounded-full bg-[#CC785C] text-white flex items-center justify-center hover:opacity-90 transition">
              ↑
            </button>
          </div>
        </div>
      </footer>
    </main>
  </div>

  <script>
    const themeToggleBtn = document.getElementById('themeToggleBtn');
    const themeIcon = document.getElementById('themeIcon');
    const chatInput = document.getElementById('chatInput');
    const sendBtn = document.getElementById('sendBtn');
    const welcomeView = document.getElementById('welcomeView');
    const messagesList = document.getElementById('messagesList');
    const newChatBtn = document.getElementById('newChatBtn');
    const clearChatBtn = document.getElementById('clearChatBtn');
    const promptCards = document.querySelectorAll('.prompt-card');

    // Theme toggle
    themeToggleBtn.addEventListener('click', () => {
      document.documentElement.classList.toggle('dark');
      themeIcon.textContent = document.documentElement.classList.contains('dark') ? '☀️' : '🌙';
    });

    function sendMessage(text) {
      if (!text || !text.trim()) return;
      welcomeView.classList.add('hidden');
      messagesList.classList.remove('hidden');

      // Append User message
      const userBubble = document.createElement('div');
      userBubble.className = 'flex flex-col items-end gap-1';
      userBubble.innerHTML = `
        <div class="text-xs text-stone-400 mr-2">You</div>
        <div class="p-3.5 px-4 rounded-2xl bg-stone-200/70 dark:bg-stone-800 text-sm max-w-lg">
          ` + text.replace(/</g, "&lt;") + `
        </div>
      `;
      messagesList.appendChild(userBubble);
      chatInput.value = '';

      // Append Assistant typing placeholder
      const botMessage = document.createElement('div');
      botMessage.className = 'flex items-start gap-3';
      botMessage.innerHTML = `
        <div class="w-7 h-7 rounded-full bg-[#CC785C]/15 text-[#CC785C] flex items-center justify-center font-bold text-xs shrink-0 mt-1">✦</div>
        <div class="flex flex-col gap-2 flex-1">
          <div class="text-xs text-stone-400">Cube • Gemini 1.5 Flash</div>
          <div class="p-4 rounded-2xl border border-[#E8E2D8] dark:border-[#333338] bg-white dark:bg-[#202024] text-sm leading-relaxed text-stone-800 dark:text-stone-100 stream-target">
            Thinking…
          </div>
        </div>
      `;
      messagesList.appendChild(botMessage);
      messagesList.scrollTop = messagesList.scrollHeight;

      // Simulate streaming response
      setTimeout(() => {
        const target = botMessage.querySelector('.stream-target');
        const reply = "Cube is now refactored with Claude's warm minimalist design principles: paper-like cream backgrounds, editorial serif typography (Lora), and grounded terracotta accents. The chat engine connects to Google AI Studio's free Gemini 1.5 Flash API.";
        let index = 0;
        target.textContent = "";
        const interval = setInterval(() => {
          target.textContent += reply.charAt(index);
          index++;
          if (index >= reply.length) clearInterval(interval);
        }, 18);
      }, 700);
    }

    sendBtn.addEventListener('click', () => sendMessage(chatInput.value));
    chatInput.addEventListener('keydown', (e) => {
      if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault();
        sendMessage(chatInput.value);
      }
    });

    promptCards.forEach(card => {
      card.addEventListener('click', () => {
        const text = card.querySelector('.font-medium').textContent;
        sendMessage(text);
      });
    });

    function resetToWelcome() {
      messagesList.innerHTML = '';
      messagesList.classList.add('hidden');
      welcomeView.classList.remove('hidden');
    }

    newChatBtn.addEventListener('click', resetToWelcome);
    clearChatBtn.addEventListener('click', resetToWelcome);
  </script>
</body>
</html>
    """.trimIndent()
}
