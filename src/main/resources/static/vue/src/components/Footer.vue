<template>
    <footer class="bg-dark text-white text-center py-3 mt-5 sticky-bottom">
		<small>&copy; 2024 Flower Shop. All rights reserved.</small>
	</footer>

    <!-- Chatbox Toast -->
    <div class="position-fixed bottom-0 end-0 p-3" style="z-index: 1100;">

        <!-- Chat Toast -->
        <div v-if="open" class="toast show mb-2" role="alert" style="width: 320px;">
            <div class="toast-header bg-primary text-white">
                <strong class="me-auto">Hỗ trợ</strong>
                <button type="button" class="btn-close btn-close-white" @click="open = false"></button>
            </div>
            <div class="toast-body d-flex flex-column" style="height: 320px;">
                <!-- Messages -->
                <div class="flex-grow-1 overflow-auto mb-2" ref="messageBox">
                    <div v-for="(msg, index) in messages" :key="index"
                        :class="msg.sender === username ? 'd-flex justify-content-end align-items-end gap-2 mb-2' : 'd-flex align-items-end gap-2 mb-2'">

                        <img v-if="msg.sender !== username" :src="getAvatarUrl(msg.photo)" style="width:28px;height:28px;object-fit:cover;border-radius:50%;flex-shrink:0;" />

                        <div :class="msg.sender === username
                            ? 'bg-primary text-white rounded p-2 small'
                            : 'bg-light rounded p-2 small'">
                            <b>{{ msg.sender }}:</b> {{ msg.content }}
                        </div>

                        <img v-if="msg.sender === username" :src="getAvatarUrl(msg.photo)" style="width:28px;height:28px;object-fit:cover;border-radius:50%;flex-shrink:0;" />
                    </div>
                </div>
            
                <!-- Input -->
                <div class="d-flex gap-2 mt-2">
                    <input 
                        v-model="input"
                        type="text" 
                        class="form-control form-control-sm" 
                        placeholder="Nhập tin nhắn..."
                        @keyup.enter="sendMessage"
                    />
                    <button class="btn btn-primary btn-sm" @click="sendMessage">Gửi</button>
                </div>
            </div>
        </div>

        <!-- Toggle Button -->
        <button class="btn btn-primary rounded-circle shadow d-flex align-items-center justify-content-center ms-auto" style="width:52px; height:52px;" @click="open = !open; scrollToBottom()">
            💬
        </button>

    </div>
</template>

<script setup>
    import { ref, onMounted, computed, watch, nextTick } from 'vue';
    import { useAuthStore } from '../stores/auth'
    import SockJS from 'sockjs-client';
    import { Client } from '@stomp/stompjs'

    const authStore = useAuthStore();
    const open = ref(false);
    const input = ref('');
    const username = computed(() => authStore.user ?? "Guess");
    const BASE_URL = import.meta.env.VITE_API_URL;
    const getAvatarUrl = (photo) => {
        if (!photo) return `${BASE_URL}/images/avatar.jpg`;
        return `${BASE_URL}/images/${photo}`;
    };
    
    const messages = ref([]);
    const stompClient = ref(null);
    const messageBox = ref(null);

    const scrollToBottom = () => {
        nextTick(() => {
            if (messageBox.value) messageBox.value.scrollTop = messageBox.value.scrollHeight;
        });
    };

    watch(messages, scrollToBottom, { deep: true });

    const connect = () => {
        stompClient.value = new Client({
            webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
            reconnectDelay: 5000,

            onConnect: () => {
                console.log("Connected")

                stompClient.value.subscribe('/topic/messages', (msg) => { //msg from sptingboot
                    console.log(JSON.parse(msg.body));
                    messages.value.push(JSON.parse(msg.body))
                    open.value = true;
                })
            },

            onStompError: (frame) => {
                console.error('Broker error:', frame.headers['message'])
            },

            onWebSocketClose: () => {
                console.warn('Disconnected')
            }
        })

        stompClient.value.activate()
    }

    const sendMessage = () => {
    if (!input.value.trim()) return;

        // check connection
        if (!stompClient.value || !stompClient.value.connected) {
            console.error("Not connected to WebSocket")
            return;
        }

        const message = {
            sender: username.value,
            content: input.value
        };

        // correct API
        stompClient.value.publish({
            destination: '/app/chat',
            body: JSON.stringify(message)
        });

        input.value = '';
    };

    onMounted(() => {
        connect();
    });
</script>

