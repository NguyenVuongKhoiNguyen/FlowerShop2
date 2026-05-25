<template>
  <div class="container mt-4">
    <div v-if="errorMsg" class="alert alert-danger alert-dismissible fade show" role="alert">
        {{ errorMsg }}
    </div>
    <!-- NAV TABS -->
    <ul class="nav nav-tabs mb-3" id="productTab" role="tablist">
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
              <!-- Preview -->
              <div v-if="previewImage" class="mt-2 d-flex justify-content-center">
                <img :src="previewImage" style="width: 120px; height: 120px; object-fit: cover;" />
              </div>
            </div>
            
            <div class="mb-3">
              <label>Hình ảnh</label>
              <input type="file" class="form-control" ref="fileInput" @change="handleFileUpload" />
            </div>

            <div class="mb-3">
              <input v-model="productInput.name" class="form-control" placeholder="Tên sản phẩm"/>
            </div>

            <div class="mb-3">
              <input type="number" v-model="productInput.costPrice" class="form-control" placeholder="Đơn giá"/>
            </div>

            <div class="mb-3">
              <input type="number" v-model="productInput.retailPercentage" class="form-control" placeholder="Lời(%)"/>
            </div>

            <div class="mb-3">
              <input type="number" v-model="productInput.amount" class="form-control" placeholder="Tồn kho"/>
            </div>

            <div class="mb-3">
              <select v-model="productInput.categoryId" class="form-select">
                <option :value="null">-- Loại --</option>
                <option v-for="c in categories" :key="c.id" :value="c.id">
                  {{ c.name }}
                </option>
              </select>
            </div>

            <div class="form-check mb-3">
              <input id="productStatusInput" type="checkbox" v-model="productInput.available" class="form-check-input" />
              <label for="productStatusInput" class="form-check-label">Còn kinh doanh</label>
            </div>

            <div class="d-flex justify-content-center gap-3">
              <button class="btn btn-primary" @click="save" :disabled="productId">Thêm</button>
              <button class="btn btn-info" @click="update" :disabled="!productId">Sửa</button>
              <button class="btn btn-danger" @click="remove" :disabled="!productId">Xoá</button>
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
                <input v-model="filter.productName" type="text" class="form-control" placeholder="Tên sản phẩm" />
            </div>
            <div class="col-6 col-md-2">
                <input v-model.number="filter.minPrice" type="number" class="form-control" placeholder="Giá thấp nhất" />
            </div>
            <div class="col-6 col-md-2">
                <input v-model.number="filter.maxPrice" type="number" class="form-control" placeholder="Giá cao nhất" />
            </div>
            <div class="col-6 col-md-2">
                <select v-model.number="filter.categoryId" class="form-select">
                    <option :value="null">Tất cả danh mục</option>
                    <option v-for="category in categories" :value="category.id">
                        {{ category.name }}
                    </option>
                </select>
            </div>
            <div class="col-6 col-md-2">
                <select v-model="filter.sortOrderByPriceOrSales" class="form-select">
                    <option :value="null">Thứ tự sắp xếp</option>
                    <option value="PRICE-ASC">Giá thấp → cao</option>
                    <option value="PRICE-DESC">Giá cao → thấp</option>
                    <option value="SALES-ASC">Bán ế → chạy</option>
                    <option value="SALES-DESC">Bán chạy → ế</option>
                </select>
            </div>
            <div class="col-12 col-md-1">
                <button class="btn btn-primary w-100" @click="refresh()">Làm mới</button>
            </div>
        </div>
        <div class="card shadow-sm">
          <div class="card-body table-responsive">

            <table class="table table-bordered table-hover text-center align-middle">
              <thead class="table-light">
                <tr>
                  <th>#</th>
                  <th>Hình</th>
                  <th>Tên Sản phẩm</th>
                  <th>Giá nhập</th>
                  <th>Lời(%)</th>
                  <th>Giá bán lẻ</th>
                  <th>Tồn kho</th>
                  <th>Đã bán</th>
                  <th>Kinh doanh</th>
                  <th></th>
                </tr>
              </thead>

              <tbody>
                <tr v-for="(p, index) in products" :key="p.id">
                  <td>{{ index + 1 }}</td>

                  <td>
                    <img
                      :src="getImageUrl(p.image)"
                      style="width: 60px; height: 60px; object-fit: cover"
                    />
                  </td>

                  <td>{{ p.name }}</td>
                  <td>{{ formatPrice(p.costPrice) }}</td>
                  <td>{{ p.retailPercentage * 100 }}%</td>
                  <td>{{ formatPrice(p.costPrice * (1 + p.retailPercentage)) }}</td>
                  <td>{{ p.amount }}</td>
                  <td>{{ p.sales }}</td>

                  <td>
                    <span
                      class="badge"
                      :class="p.available ? 'bg-success' : 'bg-secondary'"
                    >
                      {{ p.available ? "Còn" : "Khồng" }}
                    </span>
                  </td>

                  <td>
                    <button class="btn btn-sm btn-warning me-2" @click="edit(p)">
                      Chọn
                    </button>
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
                  v-for="page in totalProductPages <= 7 ? totalProductPages : Array.from({length: 7}, (_, i) => Math.min(Math.max(filter.page - 3, 1), totalProductPages - 6) + i).filter(p => p >= 1 && p <= totalProductPages)"
                  :key="page"
                  class="page-item"
                  :class="{ active: page === filter.page }"
                >
                  <button class="page-link" @click="changePage(page)">
                    {{ page }}
                  </button>
                </li>

                <li class="page-item" :class="{ disabled: filter.page === totalProductPages }">
                  <button class="page-link" @click="changePage(totalProductPages)">Last &raquo;</button>
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
  import Product from '../models/Product';
  import { Tab } from 'bootstrap';
  import { onMounted, reactive, ref, watch } from 'vue';
  import { createProduct, deleteProduct, getAllPaginatedAndFilteredProducts, getTotalProductPages, updateProduct } from '../services/productService';
  import { getAllPaginatedAndFilteredCategories } from '../services/categoryService'

  const errorMsg = ref(null);
  const selectedFile = ref(null);
  const previewImage = ref(null);
  const productId = ref(null);
  const fileInput = ref(null);
  const totalProductPages = ref(1);
  const categories = ref([]);
  const products = ref([]);
  const filter = ref({
      minPrice: null,
      maxPrice: null,
      categoryId: null,
      productName: null,
      sortOrderByPriceOrSales: null,
      available: true,
      page: 1,
      pageSize: 5
  });

  const productInput = reactive(new Product());

  const changeTab = (id) => {
    const tabBtn = document.querySelector("#" + id);
      const tab = new Tab(tabBtn);
      tab.show();
  }

  const showError = (e, msg) => {
    errorMsg.value = e.response?.data?.message || msg;
    console.log(e);
    setTimeout(() => errorMsg.value = null, 2000);
  }

  const fetchProducts = async () => {
    try {
        const response1 = await getAllPaginatedAndFilteredProducts({...filter.value});
        const response2 = await getTotalProductPages({...filter.value});
        products.value = response1.data;
        totalProductPages.value = response2.data;
    } catch (e) {
        showError(e, "Lỗi khi tải sản phẩm");
    }
  }

  const fetchCategories = async () => {
    try {
        const params = {keyword: null, sortOrder: null, page: null, pageSize: null}
        const response = await getAllPaginatedAndFilteredCategories(params);
        categories.value = response.data;
    } catch (e) {
        showError(e, "Lỗi khi tải loại sản phẩm");
    }
  }

  const save = async () => {
      try {
          const formData = new FormData()
          //add image into formData
          if (selectedFile.value) {
           formData.append("file", selectedFile.value)
          }
          formData.append(
            "product",
            new Blob(
                [JSON.stringify({ ...productInput })],
                { type: "application/json" }
            )
          );          
          await createProduct(formData);
          changeTab("listTabBtn");
          fetchProducts();
          clear();
      } catch (e) {
          showError(e, "Lỗi khi lưu sản phẩm");
      }
  }

  const update = async () => {
    try {
      const formData = new FormData()
      if (selectedFile.value) {
        formData.append("file", selectedFile.value)
      }
      formData.append(
        "product",
        new Blob(
            [JSON.stringify({ ...productInput })],
            { type: "application/json" }
        )
      );  
      await updateProduct(formData, productId.value);
      changeTab("listTabBtn");
      fetchProducts();
      clear();
    } catch (e) {
      showError(e, "Lỗi khi sửa sản phẩm");
    }
  }

  const remove = async () => {
      try {
          await deleteProduct(productId.value);
          fetchProducts();  
          changeTab("listTabBtn");   
      } catch (e) {
          showError(e, "Lỗi khi xoá sản phẩm");
      }
  }

  const refresh = () => {
      filter.value = {
          minPrice: null,
          maxPrice: null,
          categoryId: null,
          productName: null,
          sortOrderByPriceOrSales: null,
          available: true,
          page: 1,
          pageSize: 5
      };
      fetchProducts();
      fetchCategories();
  }

  const clear = () => {
    Object.assign(productInput, new Product());
    selectedFile.value = null;
    previewImage.value = null;
    productId.value = null;
    if (fileInput.value) fileInput.value.value = "";
  }

  const edit = (p) => {
    Object.assign(productInput,
      new Product(
        p.name, 
        p.image, 
        p.costPrice, 
        p.retailPercentage, 
        p.available, 
        p.amount, 
        p.categoryId
      )
    );
    productId.value = p.id;
    selectedFile.value = null;
    previewImage.value = getImageUrl(p.image);
    if (fileInput.value) fileInput.value.value = "";
    changeTab("formTabBtn");
  }

  const changePage = (page) => {
    if (page < 1 || page > totalProductPages.value) return;
    filter.value.page = page;
    fetchProducts();
    fetchCategories();
  };

  const BASE_URL = import.meta.env.VITE_API_URL;
  const getImageUrl = (image) => {
    if (!image) return 'https://placehold.co/300x200';
    return `${BASE_URL}/images/${image}`;
  };

  watch(filter, () => { 
    if (filter.value.page > totalProductPages.value)
        filter.value.page = 1;
    fetchProducts();
    fetchCategories();
  }, { deep: true });

  const formatPrice = (value) => {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(value)
}

  const handleFileUpload = (event) => {
    const file = event.target.files[0];
    if (!file) return;

    selectedFile.value = file;
    previewImage.value = URL.createObjectURL(file);
  };
  
  onMounted(() => {
    fetchCategories();
    fetchProducts();
  });
</script>