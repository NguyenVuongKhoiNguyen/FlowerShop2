<template>
  <div class="container mt-4">
    <div v-if="errorMsg" class="alert alert-danger alert-dismissible fade show" role="alert">
      {{ errorMsg }}
    </div>
    <h2 class="mb-4">Giỏ hàng của tôi</h2>

    <!-- No carts -->
    <div v-if="carts.length === 0" class="alert alert-info">
        Không tim thấy giỏ hàng nào
    </div>

    <!-- Cart list -->
    <div v-for="cart in carts" :key="cart.id" class="card mb-4 shadow-sm">
      
      <!-- Cart header -->
      <div class="card-header d-flex justify-content-between align-items-center">
        <div>
          <strong>CartID: #{{ cart.id }}</strong>
        </div>
        <div>
          <span class="text-muted me-3">
            Ngày tạo: {{ formatDate(cart.createDate) }}
          </span>
          <span class="badge bg-success">
            Tổng tiền: {{ formatPrice(cart.total) }}
          </span>
        </div>
      </div>

      <!-- Cart body -->
      <div class="card-body">

        <!-- Items table -->
        <table class="table table-bordered table-hover text-center align-middle">
          <thead class="table-light">
            <tr>
              <th>#</th>
              <th>Hình</th>
              <th>Tên sản phẩm</th>
              <th>Đơn giá</th>
              <th>Số lượng</th>
              <th>Tổng phụ</th>
            </tr>
          </thead>

          <tbody>
            <tr v-for="(item, index) in cart.items" :key="item.productId">
              <td>{{ index + 1 }}</td>
              <td><img :src="getImageUrl(item.productImage)" class="img-fluid rounded border" style="width: 70px; height: 100px; object-fit: cover;"></td>
              <td>{{ item.productName }}</td>
              <td>{{ formatPrice(item.price) }}</td>
              <td>{{ item.quantity }}</td>
              <td>{{ formatPrice(item.subtotal) }}</td>
            </tr>
          </tbody>
        </table>
        <div class="d-flex justify-content-between">
            <div>
              <button class="btn btn-info me-3" @click="editCart(cart.id)">Sửa</button>
              <button class="btn btn-primary" @click="saveOrder(cart)" :disabled="savingId === cart.id">
                <span v-if="savingId === cart.id" class="spinner-border spinner-border-sm me-1" role="status"></span>
                {{ savingId === cart.id ? 'Đang xử lý...' : 'Tạo hoá đơn' }}
              </button>
            </div>
            <button class="btn btn-danger" @click="deleteCart(cart.id)">Xoá</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
    import Order from '../models/Order'
    import { onMounted, ref } from "vue";
    import { getCartsByUsername, getCartById, deleteCartById } from "../services/cartService";
    import { createOrder } from "../services/orderService";
    import { useAuthStore } from "../stores/auth";
    import { useCartStore } from "../stores/cart";
    import { useRouter } from "vue-router";
import { getAccountByUsername, getAllPaginatedAndFilteredAccounts } from '../services/accountService';
 
    const auth = useAuthStore();
    const cartStore = useCartStore();
    const router = useRouter();
    const carts = ref([]);
    const errorMsg = ref(null);
    const savingId = ref(null);

    const showError = (e, msg) => {
        errorMsg.value = e.response?.data || msg;
        setTimeout(() => errorMsg.value = null, 2000);
    };

    const getAccountCarts = async () => {
        try {
            const response = await getCartsByUsername(auth.user);
            carts.value = response.data;
        } catch (error) {
            showError(error, 'Lỗi khi tải giỏ hàng');
        }
    }

    const formatPrice = (value) => {
      return new Intl.NumberFormat('vi-VN', {
          style: 'currency',
          currency: 'VND'
      }).format(value)
    }

    const formatDate = (date) => {
        return new Date(date).toLocaleDateString();
    };

    const BASE_URL = import.meta.env.VITE_API_URL;
    const getImageUrl = (image) => {
        if (!image) return 'https://placehold.co/300x200';
        return `${BASE_URL}/images/${image}`;
    };

    const editCart = async (id) => {
        try {
            const response = await getCartById(id);
            cartStore.id = id;
            cartStore.items = response.data.items;
            router.push("/cart");
        } catch (error) {
            showError(error, 'Lỗi khi tải giỏ hàng');
        }
    }

    const deleteCart = async (id) => {
        try {
            return await deleteCartById(id);
        } catch (error) {
            showError(error, 'Lỗi khi xoá giỏ hàng');
        } finally {
            getAccountCarts();
        }
    }

    const saveOrder = async (cart) => {
      savingId.value = cart.id;
      try {
        const response = await getAccountByUsername(auth.user);
        const account = response.data;
        const orderResponse = new Order(auth.user, account.fullname, account.phone, account.address, 'PENDING', []);
        cart.items.forEach(item => orderResponse.addOrderDetail(item.productId, item.quantity));
        await createOrder(orderResponse);
        await deleteCart(cart.id);
      } catch (error) {
        showError(error, 'Lỗi khi tạo đơn hàng');
      } finally {
        savingId.value = null;
      }
    }

    onMounted( async () => {
        await getAccountCarts();
    });
</script>