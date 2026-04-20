<template>
  <div class="container mt-4">
    <div v-if="errorMsg" class="alert alert-danger alert-dismissible fade show" role="alert">
      {{ errorMsg }}
    </div>

    <!-- NAV TABS -->
    <ul class="nav nav-tabs mb-3" id="categoryTab" role="tablist">
      <li class="nav-item">
        <button
          id="formTabBtn"
          class="nav-link active"
          data-bs-toggle="tab"
          data-bs-target="#formTab"
          type="button"
          @click="clear"
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
              <input v-model="categoryInput.name" class="form-control" placeholder="Tên loại sản phẩm" />
            </div>

            <div class="d-flex justify-content-center gap-3">
              <button class="btn btn-primary" @click="save" :disabled="categoryId">Thêm</button>
              <button class="btn btn-info" @click="update" :disabled="!categoryId">Sửa</button>
              <button class="btn btn-danger" @click="remove" :disabled="!categoryId">Xoá</button>
              <button class="btn btn-secondary" @click="clear">Mới</button>
            </div>

          </div>
        </div>
      </div>

      <!-- TABLE TAB -->
      <div class="tab-pane fade" id="listTab">
        <!-- Filter Form -->
        <div class="row g-2 mb-4">
          <div class="col-12 col-md-4">
            <input v-model="filter.keyword" type="text" class="form-control" placeholder="Tên loại sản phẩm" />
          </div>
          <div class="col-6 col-md-3">
            <select v-model="filter.sortOrder" class="form-select">
              <option value="ASC">Cũ → Mới</option>
              <option value="DESC">Mới → Cũ</option>
            </select>
          </div>
          <div class="col-6 col-md-2">
            <button class="btn btn-primary w-100" @click="refresh">Làm mới</button>
          </div>
        </div>

        <div class="card shadow-sm">
          <div class="card-body table-responsive">

            <table class="table table-bordered table-hover text-center align-middle">
              <thead class="table-light">
                <tr>
                  <th>#</th>
                  <th>Tên loại</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(c, index) in categories" :key="c.id">
                  <td>{{ (filter.page - 1) * filter.pageSize + index + 1 }}</td>
                  <td>{{ c.name }}</td>
                  <td>
                    <button class="btn btn-sm btn-warning" @click="edit(c)">Chọn</button>
                  </td>
                </tr>
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
import {
  createCategory,
  updateCategory,
  deleteCategory,
  getAllPaginatedAndFilteredCategories
} from '../api/categoryService';

const errorMsg = ref(null);
const categoryId = ref(null);
const totalPages = ref(1);
const categories = ref([]);

const filter = ref({
  keyword: null,
  sortOrder: "DESC",
  page: 1,
  pageSize: 5
});

const categoryInput = reactive({ name: '' });

const showError = (e, msg) => {
  errorMsg.value = e.response?.data?.message || msg;
  console.log(e);
  setTimeout(() => errorMsg.value = null, 2000);
};

const changeTab = (id) => {
  const tabBtn = document.querySelector('#' + id);
  const tab = new Tab(tabBtn);
  tab.show();
};

const fetchCategories = async () => {
  try {
    const res = await getAllPaginatedAndFilteredCategories({ ...filter.value });
    categories.value = res.data;
    // derive total pages from returned list size vs pageSize
    if (res.data.length < filter.value.pageSize && filter.value.page === 1) {
      totalPages.value = 1;
    } else if (res.data.length < filter.value.pageSize) {
      totalPages.value = filter.value.page;
    } else {
      totalPages.value = Math.max(totalPages.value, filter.value.page + 1);
    }
  } catch (e) {
    showError(e, 'Lỗi khi tải loại sản phẩm');
  }
};

const save = async () => {
  try {
    await createCategory({ ...categoryInput });
    fetchCategories();
    clear();
    changeTab('listTabBtn');
  } catch (e) {
    showError(e, 'Lỗi khi thêm loại sản phẩm');
  }
};

const update = async () => {
  try {
    await updateCategory(categoryId.value, { ...categoryInput });
    fetchCategories();
    clear();
    changeTab('listTabBtn');
  } catch (e) {
    showError(e, 'Lỗi khi sửa loại sản phẩm');
  }
};

const remove = async () => {
  try {
    await deleteCategory(categoryId.value);
    fetchCategories();
    clear();
    changeTab('listTabBtn');
  } catch (e) {
    showError(e, 'Lỗi khi xoá loại sản phẩm');
  }
};

const refresh = () => {
  filter.value = { keyword: null, sortOrder: null, page: 1, pageSize: 5 };
  totalPages.value = 1;
  fetchCategories();
};

const clear = () => {
  categoryInput.name = '';
  categoryId.value = null;
};

const edit = (c) => {
  categoryInput.name = c.name;
  categoryId.value = c.id;
  changeTab('formTabBtn');
};

const changePage = (page) => {
  if (page < 1 || page > totalPages.value) return;
  filter.value.page = page;
  fetchCategories();
};

watch(filter, () => {
  if (filter.value.page > totalPages.value) filter.value.page = 1;
  fetchCategories();
}, { deep: true });

onMounted(() => {
  fetchCategories();
});
</script>
