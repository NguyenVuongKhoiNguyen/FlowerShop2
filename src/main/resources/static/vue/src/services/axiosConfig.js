import axios from "axios";
import router from "../router/index";
import { useAuthStore } from "../stores/auth";

const api = axios.create({
    baseURL: "/api",
    timeout: 15000,
    headers: { "Content-Type": "application/json; charset=UTF-8" }
});

api.interceptors.request.use(config => {

    const auth = useAuthStore();

    if (auth.token) {
        config.headers.Authorization = `Bearer ${auth.token}`;
    }
    //for multipart form data
    if (config.data instanceof FormData) {
        delete config.headers["Content-Type"];
    }
    return config;
}, error => Promise.reject(error));

api.interceptors.response.use(
    res => res,
    err => {
        if (err.response?.status === 401) {
            const auth = useAuthStore();
            auth.logout();
            router.push("/login");
        }
        return Promise.reject(err);
    }
);

export default api;