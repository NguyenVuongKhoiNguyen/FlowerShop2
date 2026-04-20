import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "../stores/auth"

const routes = [
    // Client (non account)
    { path: "/", component: () => import("../views/Home.vue") },
    { path: "/product", component: () => import("../views/Product.vue") },
    { path: "/cart", component: () => import("../views/Cart.vue") },
    { path: "/product/:id", component: () => import("../views/ProductDetails.vue") },
    
    // Account pages(Account)
    { path: "/login", component: () => import("../views/Login.vue") },
    { path: "/account-info", component: () => import("../views/AccountInfo.vue"), meta: { requiresAuth: true } },
    { path: "/account-cart", component: () => import("../views/AccountCart.vue"), meta: { requiresAuth: true } },
    { path: "/account-order", component: () => import("../views/AccountOrder.vue"), meta: { requiresAuth: true } },

    // Dashboard (Admin - account, product, category, order) (Manager - order)
    {
        path: "/dashboard",
        component: () => import("../components/Dashboard.vue"),
        meta: { requiresAuth: true },
        children: [
            { path: "account", component: () => import("../views/DashboardAccount.vue") },
            { path: "product", component: () => import("../views/DashboardProduct.vue") },
            { path: "category", component: () => import("../views/DashboardCategory.vue") },
            { path: "order", component: () => import("../views/DashboardOrder.vue") }
        ]
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

router.beforeEach((to, from) => {
    const auth = useAuthStore();

    const requiresAuth = to.meta.requiresAuth;
    const requiredRoles = to.meta.roles;


    // 1. Not logged in
    if (requiresAuth && !auth.token) {
        auth.logout();
        return '/login';
    }

    // 2. Logged in but lacks role
    if (requiredRoles && requiredRoles.length > 0) {
        const hasRole = auth.roles.some(role =>
            requiredRoles.includes(role)
        );

        if (!hasRole) {
            return '/'; // or 403 page
        }
    }

    // 3. Prevent going back to login/register
    if ((to.path === '/login') && auth.token) {
        return '/';
    }

    return true;
});

export default router