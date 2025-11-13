// Chat Application JavaScript
let stompClient = null;
let currentSessionId = null;
let messageCount = 0;

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    setupEventListeners();
    checkServerStatus();
});

function setupEventListeners() {
    document.getElementById('startServer').addEventListener('click', startServer);
    document.getElementById('stopServer').addEventListener('click', stopServer);
    document.getElementById('newSession').addEventListener('click', createNewSession);
    document.getElementById('sendMessage').addEventListener('click', sendMessage);
    document.getElementById('providerSelect').addEventListener('change', onProviderChange);
    document.getElementById('modelSelect').addEventListener('change', onModelChange);

    // Enter key to send message
    document.getElementById('messageInput').addEventListener('keypress', function(e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    });
}

async function startServer() {
    setButtonLoading('startServer', true);
    try {
        const response = await fetch('/api/server/start', { method: 'POST' });
        const data = await response.json();
        
        if (data.success) {
            updateServerStatus(true);
            connectWebSocket();
            loadProviders();
            createNewSession();
            addSystemMessage('Server started successfully');
        } else {
            addErrorMessage('Failed to start server');
        }
    } catch (error) {
        addErrorMessage('Error starting server: ' + error.message);
    } finally {
        setButtonLoading('startServer', false);
    }
}

async function stopServer() {
    setButtonLoading('stopServer', true);
    try {
        const response = await fetch('/api/server/stop', { method: 'POST' });
        const data = await response.json();
        
        if (data.success) {
            updateServerStatus(false);
            disconnectWebSocket();
            currentSessionId = null;
            addSystemMessage('Server stopped');
        } else {
            addErrorMessage('Failed to stop server');
        }
    } catch (error) {
        addErrorMessage('Error stopping server: ' + error.message);
    } finally {
        setButtonLoading('stopServer', false);
    }
}

async function checkServerStatus() {
    try {
        const response = await fetch('/api/server/status');
        const data = await response.json();
        updateServerStatus(data.running);
        
        if (data.running) {
            connectWebSocket();
            loadProviders();
            createNewSession();
        }
    } catch (error) {
        console.error('Error checking server status:', error);
    }
}

function updateServerStatus(isRunning) {
    const statusSpan = document.querySelector('#serverStatus span');
    const startBtn = document.getElementById('startServer');
    const stopBtn = document.getElementById('stopServer');
    const newSessionBtn = document.getElementById('newSession');
    const messageInput = document.getElementById('messageInput');
    const sendBtn = document.getElementById('sendMessage');
    
    if (isRunning) {
        statusSpan.textContent = 'Online';
        statusSpan.className = 'online';
        startBtn.disabled = true;
        stopBtn.disabled = false;
        newSessionBtn.disabled = false;
        messageInput.disabled = false;
        sendBtn.disabled = false;
    } else {
        statusSpan.textContent = 'Offline';
        statusSpan.className = 'offline';
        startBtn.disabled = false;
        stopBtn.disabled = true;
        newSessionBtn.disabled = true;
        messageInput.disabled = true;
        sendBtn.disabled = true;
    }
}

function connectWebSocket() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.debug = null; // Disable debug output
    
    stompClient.connect({}, function(frame) {
        console.log('WebSocket connected');
        
        stompClient.subscribe('/topic/messages', function(message) {
            const response = JSON.parse(message.body);
            handleChatResponse(response);
        });
    }, function(error) {
        console.error('WebSocket error:', error);
        addErrorMessage('WebSocket connection failed');
    });
}

function disconnectWebSocket() {
    if (stompClient !== null) {
        stompClient.disconnect();
        stompClient = null;
    }
}

async function createNewSession() {
    try {
        const response = await fetch('/api/session/new', { method: 'POST' });
        const data = await response.json();
        
        if (data.success) {
            currentSessionId = data.sessionId;
            messageCount = 0;
            updateSessionInfo();
            clearChatMessages();
            addSystemMessage('New session created: ' + currentSessionId);
        } else {
            addErrorMessage('Failed to create session: ' + data.error);
        }
    } catch (error) {
        addErrorMessage('Error creating session: ' + error.message);
    }
}

async function loadProviders() {
    try {
        const response = await fetch('/api/providers');
        const data = await response.json();

        if (data.success && data.providers) {
            const select = document.getElementById('providerSelect');
            select.innerHTML = '<option value="">Select a provider...</option>';

            data.providers.forEach(provider => {
                const option = document.createElement('option');
                option.value = provider.id;
                option.textContent = provider.name;
                select.appendChild(option);
            });

            // Restore saved provider or auto-select if only one
            const savedProvider = localStorage.getItem('lastProvider');
            if (savedProvider && data.providers.some(p => p.id === savedProvider)) {
                select.value = savedProvider;
                onProviderChange();
            } else if (data.providers.length === 1) {
                select.value = data.providers[0].id;
                onProviderChange();
            }
        }
    } catch (error) {
        console.error('Error loading providers:', error);
    }
}

async function onProviderChange() {
    const providerId = document.getElementById('providerSelect').value;
    const modelSelect = document.getElementById('modelSelect');

    // Save provider selection
    if (providerId) {
        localStorage.setItem('lastProvider', providerId);
    }

    if (!providerId) {
        modelSelect.innerHTML = '<option value="">Select provider first</option>';
        return;
    }

    try {
        const response = await fetch(`/api/models/${providerId}`);
        const data = await response.json();

        if (data.success && data.models) {
            modelSelect.innerHTML = '<option value="">Select a model...</option>';

            data.models.forEach(model => {
                const option = document.createElement('option');
                option.value = model.id;
                option.textContent = model.name;
                modelSelect.appendChild(option);
            });

            // Restore saved model or auto-select first
            const savedModel = localStorage.getItem('lastModel');
            if (savedModel && data.models.some(m => m.id === savedModel)) {
                modelSelect.value = savedModel;
            } else if (data.models.length > 0) {
                modelSelect.value = data.models[0].id;
            }
        }
    } catch (error) {
        console.error('Error loading models:', error);
    }
}

function onModelChange() {
    const modelId = document.getElementById('modelSelect').value;
    if (modelId) {
        localStorage.setItem('lastModel', modelId);
    }
}

async function sendMessage() {
    const messageInput = document.getElementById('messageInput');
    const content = messageInput.value.trim();

    if (!content || !currentSessionId) {
        return;
    }

    const providerId = document.getElementById('providerSelect').value;
    const modelId = document.getElementById('modelSelect').value;

    // Check if it's a command or chat message
    const isCommand = content.startsWith('/');

    if (!isCommand && (!providerId || !modelId)) {
        addErrorMessage('Please select a provider and model');
        return;
    }

    // Add user message to chat
    addUserMessage(content);

    // Clear input immediately for better UX
    messageInput.value = '';
    messageCount++;
    updateSessionInfo();

    const message = {
        content: content,
        providerId: providerId,
        modelId: modelId,
        type: isCommand ? 'COMMAND' : 'CHAT'
    };

    // Try WebSocket first, fallback to REST API
    if (stompClient && stompClient.connected) {
        stompClient.send('/app/chat', {}, JSON.stringify(message));
    } else {
        // Fallback to REST API
        try {
            const response = await fetch('/api/chat/send', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(message)
            });

            const data = await response.json();
            handleChatResponse(data);
        } catch (error) {
            addErrorMessage('Failed to send message: ' + error.message);
        }
    }
}

function handleChatResponse(response) {
    switch (response.type) {
        case 'ASSISTANT':
            addAssistantMessage(response.content);
            break;
        case 'ERROR':
            addErrorMessage(response.content);
            break;
        case 'SYSTEM':
            addSystemMessage(response.content);
            break;
    }
    
    if (response.sessionId && response.sessionId !== currentSessionId) {
        currentSessionId = response.sessionId;
        updateSessionInfo();
    }
}

function addUserMessage(content) {
    addMessage(content, 'user');
}

function addAssistantMessage(content) {
    addMessage(content, 'assistant');
}

function addErrorMessage(content) {
    addMessage(content, 'error');
}

function addSystemMessage(content) {
    addMessage(content, 'system');
}

function addMessage(content, type) {
    const messagesDiv = document.getElementById('chatMessages');
    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${type}`;
    
    // Convert markdown-like formatting
    content = formatMessage(content);
    messageDiv.innerHTML = content;
    
    messagesDiv.appendChild(messageDiv);
    messagesDiv.scrollTop = messagesDiv.scrollHeight;
}

function formatMessage(content) {
    // Escape HTML first
    content = content.replace(/&/g, '&amp;')
                     .replace(/</g, '&lt;')
                     .replace(/>/g, '&gt;');

    // Convert tool execution headers (bold with special formatting)
    content = content.replace(/\*\*Tool Execution:\*\* `([^`]+)`/g,
        '<div class="tool-execution"><strong>🔧 Tool Execution:</strong> <code>$1</code></div>');

    // Convert other bold text
    content = content.replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>');

    // Convert code blocks with better formatting
    content = content.replace(/```([\s\S]*?)```/g, function(match, code) {
        // Check if it's a shell command output
        if (code.includes('$ ') || code.includes('Status:')) {
            return '<pre class="code-output"><code>' + code.trim() + '</code></pre>';
        }
        return '<pre><code>' + code.trim() + '</code></pre>';
    });

    // Convert inline code
    content = content.replace(/`([^`]+)`/g, '<code>$1</code>');

    // Convert lists
    content = content.replace(/^- (.+)$/gm, '<li>$1</li>');
    content = content.replace(/(<li>.*<\/li>\n?)+/g, '<ul>$&</ul>');

    // Convert line breaks (but not within pre tags)
    content = content.split('<pre').map((part, i) => {
        if (i === 0) {
            return part.replace(/\n/g, '<br>');
        }
        const preParts = part.split('</pre>');
        if (preParts.length > 1) {
            return '<pre' + preParts[0] + '</pre>' + preParts[1].replace(/\n/g, '<br>');
        }
        return '<pre' + part;
    }).join('');

    return content;
}

function clearChatMessages() {
    const messagesDiv = document.getElementById('chatMessages');
    messagesDiv.innerHTML = '<div class="message system">New session started. Ready for input.</div>';
}

function updateSessionInfo() {
    document.getElementById('sessionId').textContent = currentSessionId || 'None';
    document.getElementById('messageCount').textContent = messageCount;
}

function setButtonLoading(buttonId, isLoading) {
    const button = document.getElementById(buttonId);
    if (isLoading) {
        button.disabled = true;
        button.innerHTML = '<span class="loading"></span> ' + button.textContent;
    } else {
        button.disabled = false;
        button.innerHTML = button.textContent.replace('<span class="loading"></span> ', '');
    }
}

// Load SockJS and STOMP libraries
function loadScript(src, callback) {
    const script = document.createElement('script');
    script.src = src;
    script.onload = callback;
    document.head.appendChild(script);
}

// Load required libraries
loadScript('https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js', function() {
    loadScript('https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js', function() {
        console.log('WebSocket libraries loaded');
    });
});