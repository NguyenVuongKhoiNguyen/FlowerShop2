import { defineStore } from "pinia";
import api from "../services/axiosConfig";
import { jwtDecode } from "jwt-decode";

export const useAuthStore = defineStore("auth", {
    state: () => ({
        token: null,
        user: null,
        roles: []
    }),

    getters:  {
        isAdmin: (state) => state.roles.includes("ROLE_ADMIN"),
        isManager: (state) => state.roles.includes("ROLE_MANAGER"),
        isUser: (state) => state.roles.includes("ROLE_USER"),
        isAuthenticated: (state) => {
            if (!state.token) return false;
            try {
                const { exp } = jwtDecode(state.token);
                return exp * 1000 > Date.now(); // token not expired
            } catch {
                return false;
            }
        }
    },

    actions: {

        _setSession(token) {
            const decoded = jwtDecode(token);
            this.token = token;
            this.user = decoded.sub;
            this.roles = decoded.roles || [];
        },
        
        async login(username, password) {
            try {
                const res = await api.post("/auth/login", {
                    username,
                    password
                });
                this._setSession(res.data.token);
            } catch (error) {
                console.log(error)
            }
        },

        async loginWithGoogle(googleToken) {
            const res = await api.post("/auth/google", { token: googleToken });
            this._setSession(res.data.token);
            if (!this.roles.length) this.roles = ["ROLE_USER"] ;
            
        },

        logout() {
            this.token = null;
            this.user = null;
            this.roles = [];
        },

        initialize() {
            if (!this.token) return;

            //check expired
            try {
                const decoded = jwtDecode(this.token);
                //decoded.exp is in second so multiply it by 1000 times to convert it back to milisecond like Date.now()
                //decoded.exp is the expired time set in java
                //decoded.exp is a fix point in time not a count down
                const isExpired = (decoded.exp * 1000) < Date.now();
                if (isExpired) {
                    logout();
                } else {
                    this.user = decoded.sub;
                    this.roles = decoded.roles;
                }
            } catch (error) {
                logout();
            }

            //check session change
             window.addEventListener("storage", (event) => {
                if (event.key === "auth") {
                    if (!event.newValue) {
                        // another tab logged out
                        this.logout();
                    } else {
                        // another tab logged in with a different account
                        const { token, user, roles } = JSON.parse(event.newValue);
                        this.token = token;
                        this.user = user;
                        this.roles = roles;
                    }
                }
            });
        }
    },

    persist: {
        storage: localStorage,
        paths: ["token", "user", "roles"]
    }
});