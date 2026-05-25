<template>
  <div class="d-flex">
    
    <!-- Sidebar -->
    <div class="bg-dark text-white p-3" style="width: 250px; min-height: 100vh;">
      <ul class="nav flex-column">
        <li class="nav-item" v-if="auth.isAuthenticated && auth.isAdmin">
          <router-link class="nav-link text-white" to="/dashboard/account">
            👤 Tài khoản
          </router-link>
        </li>
        <li class="nav-item" v-if="auth.isAuthenticated && auth.isAdmin">
          <router-link class="nav-link text-white" to="/dashboard/product">
            📦 Sản phẩm
          </router-link>
        </li>
        <li class="nav-item" v-if="auth.isAuthenticated && auth.isAdmin">
          <router-link class="nav-link text-white" to="/dashboard/category">
            🗂 Loại sản phẩm
          </router-link>
        </li>
        <li class="nav-item" v-if="auth.isAuthenticated && (auth.isAdmin || auth.isManager)">
          <router-link class="nav-link text-white" to="/dashboard/order">
            🧾 Đơn hàng
          </router-link>
        </li>
      </ul>
    </div>

    <!-- Main Content -->
    <div class="flex-grow-1 p-4">
      <router-view />
    </div>

  </div>
</template>

<script setup>
  import { useAuthStore } from "../stores/auth";
  const auth = useAuthStore();
</script>