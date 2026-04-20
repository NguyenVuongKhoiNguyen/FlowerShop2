<template>
    <div class="container mt-5" style="max-width: 400px;">
        <h2 class="mb-4 text-center">Đăng nhập</h2>

        <form @submit.prevent="handleLogin">
            <div class="mb-3">
                <label class="form-label">Tên đăng nhập</label>
                <input 
                    v-model="username"
                    type="text" 
                    class="form-control"
                    required
                />
            </div>

            <div class="mb-3">
                <label class="form-label">Mật khẩu</label>
                <input 
                    v-model="password"
                    type="password" 
                    class="form-control"
                    required
                />
            </div>

            <div v-if="error" class="alert alert-danger">
                {{ error }}
            </div>

            <button 
                type="submit" 
                class="btn btn-primary w-100"
                :disabled="loading"
            >
                {{ loading ? "Đang đăng nhập..." : "Đăng nhập" }}
            </button>

            <div class="mt-4">
                <div id="googleSignInDiv"></div>
            </div>
        </form>
    </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "../stores/auth";

const username = ref("");
const password = ref("");
const error = ref("");
const loading = ref(false);

const router = useRouter();
const auth = useAuthStore();

const handleLogin = async () => {
    error.value = "";
    loading.value = true;

    try {
        await auth.login(username.value, password.value);
        //redirect after login
        router.push("/");
    } catch (err) {
        //basic error handling
        if (err.response?.status === 401) {
            error.value = "Invalid username or password";
        } else {
            error.value = "Something went wrong";
        }
    } finally {
        loading.value = false;
    }
};

//google login
const handleGoogleLogin = async (response) => {
    try {
        const googleToken = response.credential;
        await auth.loginWithGoogle(googleToken);
        router.push("/");
    } catch (err) {
        error.value = "Google login failed";
    }
};


onMounted(() => {
    const initGoogle = () => {
        if (!window.google) {
            setTimeout(initGoogle, 100);
            return;
        }

        window.google.accounts.id.initialize({
            client_id: import.meta.env.VITE_GOOGLE_CLIENT_ID,
            callback: handleGoogleLogin
        });

        window.google.accounts.id.renderButton(
            document.getElementById("googleSignInDiv"),
            {
                theme: "outline",
                size: "large",
                width: 375
            }
        );
    };
    initGoogle();
});
</script>