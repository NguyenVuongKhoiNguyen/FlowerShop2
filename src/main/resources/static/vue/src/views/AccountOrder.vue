<template>
  <div class="container mt-4">
    <div v-if="errorMsg" class="alert alert-danger alert-dismissible fade show" role="alert">
      {{ errorMsg }}
    </div>
    <h3 class="mb-3">Đơn hàng của tôi</h3>

    <!-- Date filter -->
    <div class="row g-2 mb-4">
      <div class="col-6 col-md-3">
        <input v-model="filter.fromDate" type="date" class="form-control" placeholder="Từ ngày" />
      </div>
      <div class="col-6 col-md-3">
        <input v-model="filter.toDate" type="date" class="form-control" placeholder="Đến ngày" />
      </div>
      <div class="col-6 col-md-2">
        <button class="btn btn-secondary w-100" @click="resetFilter">Làm mới</button>
      </div>
    </div>

    <!-- Orders -->
    <div v-for="order in orders" :key="order.id" class="card mb-3">
      <div class="card-header d-flex justify-content-between">
        <div>
          <strong>Đơn #{{ order.id }}</strong>
        </div>
        <div>
          <span class="badge bg-primary">{{ order.status }}</span>
        </div>
      </div>

      <div class="card-body">
        <p><strong>Ngày tạo:</strong> {{ formatDate(order.createDate) }}</p>
        <p><strong>Họ và tên:</strong> {{ order.fullname }}</p>
        <p><strong>Điện thoại:</strong> {{ order.phone }}</p>
        <p><strong>Địa chỉ:</strong> {{ order.address }}</p>
        <p><strong>Tổng tiền:</strong> {{ formatPrice(order.total) }}</p>

        <!-- Order Details -->
        <table class="table table-bordered mt-3 text-center align-middle">
          <thead class="table-light">
            <tr>
              <th>Tên sản phẩm</th>
              <th>Hình ảnh</th>
              <th>Đơn giá</th>
              <th>Số lượng</th>
              <th>Tổng phụ</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="orderDetail in order.orderDetails" :key="orderDetail.productId" class="">
              <td>{{ orderDetail.productName }}</td>
              <td class=""><img :src="getImageUrl(orderDetail.productImage)" class="img-fluid rounded border" style="width: 70px; height: 100px; object-fit: cover;"></td>
              <td>{{ formatPrice(orderDetail.price) }}</td>
              <td>{{ orderDetail.quantity }}</td>
              <td>{{ formatPrice(orderDetail.subtotal) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Pagination -->
    <nav>
      <ul class="pagination justify-content-center">
        <li class="page-item" :class="{ disabled: filter.page === 1 }">
          <button class="page-link" @click="changePage(1)">&laquo; First</button>
        </li>
        <li
          v-for="page in totalPages <= 7 ? totalPages : Array.from({length: 7}, (_, i) => Math.min(Math.max(filter.page - 3, 1), totalPages - 6) + i).filter(p => p >= 1 && p <= totalPages)"
          :key="page"
          class="page-item"
          :class="{ active: page === filter.page }"
        >
          <button class="page-link" @click="changePage(page)">{{ page }}</button>
        </li>
        <li class="page-item" :class="{ disabled: filter.page === totalPages }">
          <button class="page-link" @click="changePage(totalPages)">Last &raquo;</button>
        </li>
      </ul>
    </nav>
  </div>
</template>

<script setup>
    import { onMounted, ref, watch } from "vue";
    import { useAuthStore } from "../stores/auth";
    import { getAllPaginatedAndFilteredOrders } from "../services/orderService";

    const authStore = useAuthStore();

    const filter = ref({
        username: authStore.user,
        fromDate: null,
        toDate: null,
        page: 1,
        pageSize: 5
    });

    const orders = ref([]);
    const totalPages = ref(1);
    const errorMsg = ref(null);

    const showError = (e, msg) => {
        errorMsg.value = e.response?.data || msg;
        setTimeout(() => errorMsg.value = null, 2000);
    };

    const getAccountOrders = async () => {
        try {
            const response1 = await getAllPaginatedAndFilteredOrders({...filter.value});
            orders.value = response1.data;
            if (response1.data.length < filter.value.pageSize && filter.value.page === 1) {
                totalPages.value = 1;
            } else if (response1.data.length < filter.value.pageSize) {
                totalPages.value = filter.value.page;
            } else {
                totalPages.value = Math.max(totalPages.value, filter.value.page + 1);
            }
        } catch (error) {
            showError(error, 'Lỗi khi tải đơn hàng');
        }
    }

    const resetFilter = () => {
        totalPages.value = 1;
        filter.value = { username: authStore.user, fromDate: null, toDate: null, page: 1, pageSize: 5 };
    };

    const formatPrice = (value) => {
      return new Intl.NumberFormat('vi-VN', {
          style: 'currency',
          currency: 'VND'
      }).format(value)
    }

    const formatDate = (date) => {
        return new Date(date).toLocaleDateString();
    };

    const changePage = (page) => {
        if (page < 1 || page > totalPages.value) return;
            filter.value.page = page;
    };

    const BASE_URL = import.meta.env.VITE_API_URL;
    const getImageUrl = (image) => {
        if (!image) return 'https://placehold.co/300x200';
        return `${BASE_URL}/images/${image}`;
    };

    watch(filter, () => {
        getAccountOrders();
    }, { deep: true });

    onMounted(() => {
        getAccountOrders();
    });
</script>