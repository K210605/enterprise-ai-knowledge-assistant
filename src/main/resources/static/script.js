// ==========================================
// DOM ELEMENTS
// ==========================================

const questionInput = document.getElementById("questionInput");
const sendBtn = document.getElementById("sendBtn");
const chatMessages = document.getElementById("chatMessages");
const newChatBtn = document.getElementById("newChatBtn");

const pdfInput = document.getElementById("pdfInput");
const uploadBtn = document.getElementById("uploadBtn");
const uploadStatus = document.getElementById("uploadStatus");

const chatHistory = document.getElementById("chatHistory");

let currentConversationId = null;


// ==========================================
// HTML ESCAPING
// ==========================================

function escapeHTML(text) {
    return String(text).replace(/[&<>"']/g, character => {
        const entities = {
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#039;"
        };

        return entities[character];
    });
}


// ==========================================
// MARKDOWN FORMATTING
// ==========================================

function formatMarkdown(text) {

    let html = escapeHTML(text || "");

    html = html.replace(/^### (.*)$/gm, "<h3>$1</h3>");
    html = html.replace(/^## (.*)$/gm, "<h2>$1</h2>");
    html = html.replace(/^# (.*)$/gm, "<h1>$1</h1>");

    html = html.replace(/\*\*(.*?)\*\*/g, "<strong>$1</strong>");
    html = html.replace(/\*(.*?)\*/g, "<em>$1</em>");
    html = html.replace(/`(.*?)`/g, "<code>$1</code>");

    html = html.replace(/^\s*[-*] (.*)$/gm, "<li>$1</li>");
    html = html.replace(/^\s*\d+\.\s(.*)$/gm, "<li>$1</li>");

    html = html.replace(
        /((?:<li>.*?<\/li>\s*)+)/gs,
        "<ul>$1</ul>"
    );

    html = html.replace(/\n/g, "<br>");

    return html;
}


// ==========================================
// DISPLAY MESSAGE
// ==========================================

function addMessage(message, sender) {

    const messageDiv = document.createElement("div");

    messageDiv.style.padding = "15px";
    messageDiv.style.marginBottom = "15px";
    messageDiv.style.borderRadius = "10px";
    messageDiv.style.maxWidth = "80%";
    messageDiv.style.whiteSpace = "normal";
    messageDiv.style.lineHeight = "1.6";

    if (sender === "user") {

        messageDiv.style.background = "#075985";
        messageDiv.style.marginLeft = "auto";
        messageDiv.textContent = message;

    } else {

        messageDiv.style.background = "#1e293b";
        messageDiv.style.marginRight = "auto";
        messageDiv.innerHTML = formatMarkdown(message);
    }

    chatMessages.appendChild(messageDiv);
    chatMessages.scrollTop = chatMessages.scrollHeight;

    return messageDiv;
}


// ==========================================
// WELCOME MESSAGE
// ==========================================

function showWelcomeMessage() {

    chatMessages.innerHTML = "";

    const welcome = document.createElement("div");

    welcome.className = "welcome-message";

    welcome.innerHTML = `
        <h1>Hello! 👋</h1>
        <p>
            Upload your PDF documents and ask me anything about them.
        </p>
    `;

    chatMessages.appendChild(welcome);
}


// ==========================================
// DISPLAY SOURCES
// ==========================================

function displaySources(sources) {

    if (!sources || sources.length === 0) {
        return;
    }

    const sourceContainer = document.createElement("div");

    sourceContainer.style.background = "#1e293b";
    sourceContainer.style.padding = "18px";
    sourceContainer.style.marginBottom = "15px";
    sourceContainer.style.borderRadius = "12px";
    sourceContainer.style.maxWidth = "85%";

    const heading = document.createElement("h3");

    heading.textContent = "📚 Retrieved Sources";
    heading.style.marginBottom = "15px";
    heading.style.color = "#38bdf8";

    sourceContainer.appendChild(heading);

    sources.forEach(source => {

        const sourceCard = document.createElement("div");

        sourceCard.style.borderTop = "1px solid #475569";
        sourceCard.style.padding = "14px 0";

        const fileName = document.createElement("p");

        fileName.textContent =
            "📄 " + (source.fileName || "Unknown document");

        fileName.style.fontWeight = "bold";

        const chunkIndex = document.createElement("p");

        chunkIndex.textContent =
            "Chunk Index: " + (source.chunkIndex ?? "N/A");

        chunkIndex.style.color = "#38bdf8";
        chunkIndex.style.fontSize = "13px";

        const content = source.content || "No content available.";

        const preview = document.createElement("p");

        preview.textContent =
            content.length > 220
                ? content.substring(0, 220) + "..."
                : content;

        preview.style.color = "#94a3b8";
        preview.style.fontSize = "13px";
        preview.style.lineHeight = "1.6";
        preview.style.marginTop = "10px";

        sourceCard.appendChild(fileName);
        sourceCard.appendChild(chunkIndex);
        sourceCard.appendChild(preview);

        if (content.length > 220) {

            const toggleButton = document.createElement("button");

            toggleButton.textContent = "View full content";
            toggleButton.style.background = "transparent";
            toggleButton.style.border = "none";
            toggleButton.style.color = "#38bdf8";
            toggleButton.style.cursor = "pointer";
            toggleButton.style.marginTop = "8px";

            let expanded = false;

            toggleButton.addEventListener("click", function() {

                expanded = !expanded;

                preview.textContent = expanded
                    ? content
                    : content.substring(0, 220) + "...";

                toggleButton.textContent = expanded
                    ? "Show less"
                    : "View full content";
            });

            sourceCard.appendChild(toggleButton);
        }

        sourceContainer.appendChild(sourceCard);
    });

    chatMessages.appendChild(sourceContainer);

    chatMessages.scrollTop = chatMessages.scrollHeight;
}


// ==========================================
// LOAD CHAT HISTORY
// ==========================================

async function loadChatHistory() {

    try {

        const response = await fetch("/api/conversations");

        if (!response.ok) {
            throw new Error("Unable to load chat history");
        }

        const conversations = await response.json();

        chatHistory.innerHTML = "";

        if (!conversations || conversations.length === 0) {

            const placeholder = document.createElement("p");

            placeholder.className = "history-placeholder";
            placeholder.textContent = "No previous conversations yet.";

            chatHistory.appendChild(placeholder);

            return;
        }

        conversations.forEach(conversation => {

            const chatItem = document.createElement("div");

            chatItem.className = "chat-history-item";

            if (Number(conversation.id) === Number(currentConversationId)) {
                chatItem.classList.add("active");
            }

            const title = document.createElement("span");

            title.className = "chat-history-title";
            title.textContent = conversation.title || "New Chat";
            title.title = conversation.title || "New Chat";

            title.addEventListener("click", function() {
                openConversation(conversation.id);
            });

            const deleteButton = document.createElement("button");

            deleteButton.className = "delete-chat-btn";
            deleteButton.textContent = "🗑";
            deleteButton.title = "Delete conversation";

            deleteButton.addEventListener("click", function(event) {

                event.stopPropagation();

                deleteConversation(conversation.id);
            });

            chatItem.appendChild(title);
            chatItem.appendChild(deleteButton);

            chatHistory.appendChild(chatItem);
        });

    } catch (error) {

        console.error("Chat history error:", error);
    }
}


// ==========================================
// OPEN PREVIOUS CONVERSATION
// ==========================================

async function openConversation(conversationId) {

    try {

        const response = await fetch(
            `/api/conversations/${conversationId}/messages`
        );

        if (!response.ok) {
            throw new Error("Unable to load conversation");
        }

        const messages = await response.json();

        currentConversationId = conversationId;

        chatMessages.innerHTML = "";

        if (!messages || messages.length === 0) {

            showWelcomeMessage();

        } else {

            messages.forEach(message => {

                addMessage(
                    message.content,
                    message.role
                );

                if (message.role === "assistant") {

                    displaySources(message.sources || []);
                }
            });
        }

        await loadChatHistory();

    } catch (error) {

        alert("Unable to open conversation.");

        console.error(error);
    }
}


// ==========================================
// DELETE CONVERSATION
// ==========================================

async function deleteConversation(conversationId) {

    const confirmDelete = confirm(
        "Are you sure you want to delete this conversation?"
    );

    if (!confirmDelete) {
        return;
    }

    try {

        const response = await fetch(
            `/api/conversations/${conversationId}`,
            {
                method: "DELETE"
            }
        );

        if (!response.ok) {
            throw new Error("Unable to delete conversation");
        }

        if (Number(currentConversationId) === Number(conversationId)) {

            currentConversationId = null;

            showWelcomeMessage();
        }

        await loadChatHistory();

    } catch (error) {

        alert("Failed to delete conversation.");

        console.error(error);
    }
}


// ==========================================
// SEND QUESTION TO RAG BACKEND
// ==========================================

async function sendQuestion() {

    const question = questionInput.value.trim();

    if (!question) {
        return;
    }

    addMessage(question, "user");

    questionInput.value = "";

    sendBtn.disabled = true;

    const loadingMessage = addMessage("Thinking...", "ai");

    try {

        const requestBody = {
            question: question
        };

        if (currentConversationId !== null) {

            requestBody.conversationId = currentConversationId;
        }

        const response = await fetch("/api/search", {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify(requestBody)
        });

        if (!response.ok) {
            throw new Error("Server error: " + response.status);
        }

        const data = await response.json();

        loadingMessage.remove();

        currentConversationId = data.conversationId;

        addMessage(data.answer, "ai");

        displaySources(data.sources);

        await loadChatHistory();

    } catch (error) {

        loadingMessage.textContent =
            "Something went wrong. Please try again.";

        console.error(error);

    } finally {

        sendBtn.disabled = false;

        questionInput.focus();
    }
}


// ==========================================
// PDF UPLOAD
// ==========================================

async function uploadPDF() {

    const file = pdfInput.files[0];

    if (!file) {

        uploadStatus.textContent = "Please select a PDF first.";

        return;
    }

    if (!file.name.toLowerCase().endsWith(".pdf")) {

        uploadStatus.textContent = "Only PDF files are allowed.";

        return;
    }

    const formData = new FormData();

    formData.append("file", file);

    uploadBtn.disabled = true;

    uploadStatus.textContent =
        "Uploading and indexing PDF... Please wait.";

    try {

        const response = await fetch("/api/pdf/upload", {

            method: "POST",

            body: formData
        });

        if (!response.ok) {

            const errorMessage = await response.text();

            throw new Error(errorMessage);
        }

        const data = await response.json();

        uploadStatus.textContent =
            "PDF uploaded and indexed successfully: " + file.name;

        addMessage(
            "Your PDF has been uploaded and indexed successfully!\n\n" +
            "**Document ID:** " + data.documentId + "\n\n" +
            "**Total Chunks:** " + data.totalChunks + "\n\n" +
            "**New Vectors Stored:** " + data.newVectorsStored,
            "ai"
        );

        pdfInput.value = "";

    } catch (error) {

        uploadStatus.textContent =
            "PDF upload failed: " + error.message;

        console.error(error);

    } finally {

        uploadBtn.disabled = false;
    }
}


// ==========================================
// NEW CHAT
// ==========================================

function startNewChat() {

    currentConversationId = null;

    showWelcomeMessage();

    questionInput.value = "";

    loadChatHistory();
}


// ==========================================
// EVENT LISTENERS
// ==========================================

sendBtn.addEventListener("click", sendQuestion);

uploadBtn.addEventListener("click", uploadPDF);

newChatBtn.addEventListener("click", startNewChat);

questionInput.addEventListener("keydown", function(event) {

    if (event.key === "Enter") {
        sendQuestion();
    }
});


// ==========================================
// INITIALIZE APPLICATION
// ==========================================

document.addEventListener("DOMContentLoaded", function() {

    showWelcomeMessage();

    loadChatHistory();
});