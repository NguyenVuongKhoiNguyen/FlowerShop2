<template>
  <div class="container mt-4">
    <div v-if="errorMsg" class="alert alert-danger alert-dismissible fade show" role="alert">
      {{ errorMsg }}
    </div>

    <!-- NAV TABS -->
    <ul class="nav nav-tabs mb-3" id="orderTab" role="tablist">
      <li class="nav-item">
        <button
          id="formTabBtn"
          class="nav-link active"
          data-bs-toggle="tab"
          data-bs-target="#formTab"
          type="button"
        >
          Biểu mẫu
        </button>
      </li>
      <li class="nav-item">
        <button
          id="listTabBtn"
          class="nav-link"
          data-bs-toggle="tab"
          data-bs-target="#listTab"
          type="button"
        >
          Danh sách
        </button>
      </li>
    </ul>

    <!-- TAB CONTENT -->
    <div class="tab-content">

      <!-- FORM TAB -->
      <div class="tab-pane fade show active" id="formTab">
        <div class="card shadow-sm">
          <div class="card-body">

            <div class="mb-3">
              <label class="form-label">Mã đơn hàng</label>
              <input :value="orderInput.id" class="form-control" readonly />
            </div>

            <div class="mb-3">
              <label class="form-label">Tên đăng nhập</label>
              <input :value="orderInput.username" class="form-control" readonly />
            </div>

            <div class="mb-3">
              <label class="form-label">Họ và tên</label>
              <input v-model="orderInput.fullname" class="form-control"/>
            </div>

            <div class="mb-3">
              <label class="form-label">Điện thoại</label>
              <input v-model="orderInput.phone" class="form-control"/>
            </div>

            <div class="mb-3">
              <label class="form-label">Địa chỉ</label>
              <input v-model="orderInput.address" class="form-control"/>
            </div>

            <div class="mb-3">
              <label class="form-label">Ngày tạo</label>
              <input :value="formatDate(orderInput.createDate)" class="form-control" readonly />
            </div>

            <div class="mb-3">
              <label class="form-label">Tổng tiền</label>
              <input :value="formatPrice(orderInput.total)" class="form-control" readonly />
            </div>

            <div class="mb-3">
              <label class="form-label">Trạng thái</label>
              <select v-model="orderInput.status" class="form-select" :disabled="!orderId">
                <option v-for="s in statuses" :key="s" :value="s">{{ s }}</option>
              </select>
            </div>

            <div class="d-flex justify-content-center gap-3">
              <button class="btn btn-info" @click="update" :disabled="!orderId">Cập nhật</button>
              <button class="btn btn-secondary" @click="clear">Mới</button>
            </div>

          </div>
        </div>
      </div>

      <!-- TABLE TAB -->
      <div class="tab-pane fade" id="listTab">
        <!-- Filter Form -->
        <div class="row g-2 mb-4">
          <div class="col-12 col-md-3">
            <input v-model="filter.username" type="text" class="form-control" placeholder="Tên đăng nhập" />
          </div>
          <div class="col-12 col-md-3">
            <input v-model="filter.fullname" type="text" class="form-control" placeholder="Họ và tên" />
          </div>
          <div class="col-6 col-md-3">
            <input v-model="filter.fromDate" type="date" class="form-control" placeholder="Từ ngày" />
          </div>
          <div class="col-6 col-md-3">
            <input v-model="filter.toDate" type="date" class="form-control" placeholder="Đến ngày" />
          </div>
          <div class="col-12 col-md-2">
            <button class="btn btn-primary w-100" @click="refresh">Làm mới</button>
          </div>
        </div>

        <div class="card shadow-sm">
          <div class="card-body table-responsive">

            <table class="table table-bordered table-hover text-center align-middle">
              <thead class="table-light">
                <tr>
                  <th>#</th>
                  <th>Mã đơn</th>
                  <th>Tên đăng nhập</th>
                  <th>Họ và tên</th>
                  <th>Điện thoại</th>
                  <th>Địa chỉ</th>
                  <th>Ngày tạo</th>
                  <th>Tổng tiền</th>
                  <th>Trạng thái</th>
                  <th>Chi tiết</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                <template v-for="(o, index) in orders" :key="o.id">
                  <!-- main row -->
                  <tr>
                    <td>{{ (filter.page - 1) * filter.pageSize + index + 1 }}</td>
                    <td>{{ o.id }}</td>
                    <td>{{ o.username }}</td>
                    <td>{{ o.fullname }}</td>
                    <td>{{ o.phone }}</td>
                    <td>{{ o.address }}</td>
                    <td>{{ formatDate(o.createDate) }}</td>
                    <td>{{ formatPrice(o.total) }}</td>
                    <td>
                      <span class="badge" :class="statusBadge(o.status)">{{ o.status }}</span>
                    </td>
                    <td>
                      <button
                        class="btn btn-sm btn-outline-secondary"
                        type="button"
                        @click="toggleCollapse(o.id)"
                      >
                        {{ expandedId === o.id ? '▲' : '▼' }}
                      </button>
                    </td>
                    <td>
                      <button class="btn btn-sm btn-warning" @click="edit(o)">Chọn</button>
                    </td>
                  </tr>
                  <!-- collapse detail row -->
                  <tr v-if="expandedId === o.id">
                    <td colspan="8" class="p-0">
                      <div class="p-3 bg-light">
                        <table class="table table-sm table-bordered mb-0 text-center">
                          <thead class="table-secondary">
                            <tr>
                              <th>Sản phẩm</th>
                              <th>Hình</th>
                              <th>Đơn giá</th>
                              <th>Số lượng</th>
                              <th>Thành tiền</th>
                            </tr>
                          </thead>
                          <tbody>
                            <tr v-for="d in o.orderDetails" :key="d.productId">
                              <td>{{ d.productName }}</td>
                              <td>
                                <img :src="getImageUrl(d.productImage)" style="width:50px;height:50px;object-fit:cover;" />
                              </td>
                              <td>{{ formatPrice(d.price) }}</td>
                              <td>{{ d.quantity }}</td>
                              <td>{{ formatPrice(d.price * d.quantity) }}</td>
                            </tr>
                          </tbody>
                        </table>
                      </div>
                    </td>
                  </tr>
                </template>
              </tbody>
            </table>

            <!-- PAGINATION -->
            <nav>
              <ul class="pagination justify-content-center">
                <li class="page-item" :class="{ disabled: filter.page === 1 }">
                  <button class="page-link" @click="changePage(1)">&laquo; First</button>
                </li>
                <li
                  v-for="p in totalPages <= 7 ? totalPages : Array.from({length: 7}, (_, i) => Math.min(Math.max(filter.page - 3, 1), totalPages - 6) + i).filter(p => p >= 1 && p <= totalPages)"
                  :key="p"
                  class="page-item"
                  :class="{ active: p === filter.page }"
                >
                  <button class="page-link" @click="changePage(p)">{{ p }}</button>
                </li>
                <li class="page-item" :class="{ disabled: filter.page === totalPages }">
                  <button class="page-link" @click="changePage(totalPages)">Last &raquo;</button>
                </li>
              </ul>
            </nav>

          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { reactive, ref, watch, onMounted } from 'vue';
import { Tab } from 'bootstrap';
import { getAllPaginatedAndFilteredOrders, updateOrder } from '../api/orderService';

const statuses = ['PENDING', 'PAID', 'CONFIRM', 'PROCESSING', 'SHIPPING', 'DELIVERED', 'CANCELLED', 'REFUND'];

const errorMsg = ref(null);
const orderId = ref(null);
const totalPages = ref(1);
const orders = ref([]);
const expandedId = ref(null);

const filter = ref({
  username: null,
  fullname: null,
  fromDate: null,
  toDate: null,
  page: 1,
  pageSize: 5
});

const orderInput = reactive({
  id: null,
  username: null,
  fullname: null,
  phone: null,
  address: null,
  createDate: null,
  total: null,
  status: null
});

const showError = (e, msg) => {
  errorMsg.value = e.response?.data?.message || msg;
  console.log(e);
  setTimeout(() => errorMsg.value = null, 2000);
};

const changeTab = (id) => {
  const tabBtn = document.querySelector('#' + id);
  new Tab(tabBtn).show();
};

const fetchOrders = async () => {
  try {
    const res = await getAllPaginatedAndFilteredOrders({ ...filter.value });
    orders.value = res.data;
    if (res.data.length < filter.value.pageSize && filter.value.page === 1) {
      totalPages.value = 1;
    } else if (res.data.length < filter.value.pageSize) {
      totalPages.value = filter.value.page;
    } else {
      totalPages.value = Math.max(totalPages.value, filter.value.page + 1);
    }
  } catch (e) {
    showError(e, 'Lỗi khi tải đơn hàng');
  }
};

const update = async () => {
  try {
    await updateOrder({...orderInput}, orderId.value);
    fetchOrders();
    clear();
    changeTab('listTabBtn');
  } catch (e) {
    showError(e, 'Lỗi khi cập nhật đơn hàng');
  }
};

const refresh = () => {
  filter.value = { username: null, fullname: null, fromDate: null, toDate: null, page: 1, pageSize: 5 };
  totalPages.value = 1;
  fetchOrders();
};

const clear = () => {
  Object.assign(orderInput, { id: null, username: null, fullname: null, phone: null, address: null, createDate: null, total: null, status: null });
  orderId.value = null;
};

const edit = (o) => {
  Object.assign(orderInput, {
    id: o.id,
    username: o.username,
    fullname: o.fullname,
    phone: o.phone,
    address: o.address,
    createDate: o.createDate,
    total: o.total,
    status: o.status
  });
  orderId.value = o.id;
  changeTab('formTabBtn');
};

const toggleCollapse = (id) => {
  expandedId.value = expandedId.value === id ? null : id;
};

const changePage = (page) => {
  if (page < 1 || page > totalPages.value) return;
  filter.value.page = page;
  fetchOrders();
};

watch(filter, () => {
  if (filter.value.page > totalPages.value) filter.value.page = 1;
  fetchOrders();
}, { deep: true });

const formatDate = (date) => {
  if (!date) return '';
  return new Date(date).toLocaleDateString('vi-VN');
};

const formatPrice = (value) => {
  if (value == null) return '';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

const BASE_URL = import.meta.env.VITE_API_URL;
const getImageUrl = (image) => {
  if (!image) return 'https://placehold.co/300x200';
  return `${BASE_URL}/images/${image}`;
};

const statusBadge = (status) => {
  const map = {
    PENDING: 'bg-secondary',
    PAID: 'bg-info',
    CONFIRM: 'bg-primary',
    PROCESSING: 'bg-warning text-dark',
    SHIPPING: 'bg-primary',
    DELIVERED: 'bg-success',
    CANCELLED: 'bg-danger',
    REFUND: 'bg-dark'
  };
  return map[status] ?? 'bg-secondary';
};

onMounted(() => {
  fetchOrders();
});
</script>
