import { defineStore } from "pinia";
import api from "../api/axiosConfig";
import { jwtDecode } from "jwt-decode";

export const useAuthStore = defineStore("auth", {
    state: () => ({
        token: null,
        user: null,
        roles: []
    }),

    actions: {
        async login(username, password) {
            const res = await api.post("/auth/login", {
                username,
                password
            });

            this.token = res.data.token;

            // decode token
            const decoded = jwtDecode(this.token);

            this.user = decoded.sub;
            this.roles = decoded.roles || [];
        },

        async loginWithGoogle(googleToken) {
            const res = await api.post("/auth/google", { token: googleToken });
            this.token = res.data.token;

            const decoded = jwtDecode(this.token);
            this.user = decoded.sub;
            this.roles = ["ROLE_USER"];
        },

        logout() {
            this.token = null;
            this.user = null;
            this.roles = [];
        },

        initialize() {
            if (this.token) {
                const decoded = jwtDecode(this.token);
                this.user = decoded.sub;
                this.roles = decoded.roles || [];
            }
        },

        isAdmin() {
            return this.roles.includes("ROLE_ADMIN");
        },

        isManager() {
            return this.roles.includes("ROLE_MANAGER");
        },

        isUser() {
            return this.roles.includes("ROLE_USER");
        }
    },

    persist: {
        storage: localStorage,
        paths: ["token", "user", "roles"]
    }
});