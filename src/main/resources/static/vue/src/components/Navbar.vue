<template>
    <div class="sticky-top">
        <nav class="navbar navbar-expand-lg navbar-dark bg-dark px-3">
            <router-link class="navbar-brand fw-bold" to="/">🌸 Flower Shop</router-link>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="mainNav">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item">
                        <router-link class="nav-link" to="/">Trang chủ</router-link>
                    </li>
                    <li class="nav-item">
                        <router-link class="nav-link" to="/product">Sản phẩm</router-link>
                    </li>
                    <li class="nav-item">
                        <router-link class="nav-link" to="/cart">🛒 Giỏ hàng</router-link>
                    </li>
                </ul>
                <ul class="navbar-nav ms-auto mb-2 mb-lg-0 align-items-center">
                    <li class="nav-item" v-if="!isLogin">
                        <router-link class="nav-link" to="/login">Đăng nhập</router-link>
                    </li>
                    <!-- <li class="nav-item" v-if="!isLogin">
                        <router-link class="btn btn-outline-light btn-sm ms-2"  to="/register">Đăng ký</router-link>
                    </li> -->
                    <li class="nav-item dropdown" v-if="isLogin && isUser">
                        <a class="nav-link dropdown-toggle d-flex align-items-center gap-2" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                            <img :src="getImageUrl(photo)" style="width:32px; height:32px; object-fit:cover; border-radius:50%;" />
                            {{ fullname || "No User" }}
                        </a>
                        <ul class="dropdown-menu dropdown-menu-end">
                            <li><router-link class="dropdown-item" to="/account-info">Thông tin tài khoản</router-link></li>
                            <li><router-link class="dropdown-item" to="/account-cart">Giỏ hàng đã lưu</router-link></li>
                            <li><router-link class="dropdown-item" to="/account-order">Đơn hàng</router-link></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item text-danger" @click="logout">Đăng xuất</a></li>
                        </ul>
                    </li>
                    <li class="nav-item" v-if="isLogin && (isAdmin || isManager)">
                        <router-link class="btn btn-outline-light btn-sm ms-2"  to="/dashboard/order">Quản lý</router-link>
                    </li>
                </ul>
            </div>
    
        </nav>
        <div v-if="cart.successfullyAdded" class="container mt-3 alert alert-success">
            Lưu thành công
        </div>

    </div>
</template>

<script setup>
    import { useCartStore } from '../stores/cart';
    import { useAuthStore } from "../stores/auth";
    import { useRouter } from "vue-router";
    import { computed, onMounted, ref, watch } from 'vue';
    import { getAccountByUsername } from '../api/accountService';

    const cart = useCartStore();
    const auth = useAuthStore();
    const router = useRouter();

    const isLogin = computed(() => !!auth.token);
    const isAdmin = computed(() => auth.isAdmin());
    const isManager = computed(() => auth.isManager());
    const isUser = computed(() => auth.isUser());

    const fullname = ref("No User");
    const photo = ref(null);

    const BASE_URL = import.meta.env.VITE_API_URL;
    const getImageUrl = (img) => img ? `${BASE_URL}/images/${img}` : '${BASE_URL}/images/avatar.jpg';
    
    const fillFullname = async (username) => {
        try {
            const request = await getAccountByUsername(username);
            const account = request.data;
            fullname.value = account.fullname;
            photo.value = account.photo;
        } catch (error) {
            console.log(error);
            fullname.value = "No User";
        }
    };
    
    // logout
    const logout = () => {
        auth.logout();
        router.push("/");
    };

    watch(() => auth.user, (newUser) => {
        if (newUser) {
            fillFullname(newUser);
        } else {
            fullname.value = "No User";
        }
    });

    onMounted(() => {
        if (auth.user) {
            fillFullname(auth.user);
        }
    });
</script>