<template>
    <div class="container py-4">
        <!-- Filter Form -->
        <div class="row g-2 mb-4">
            <div class="col-12 col-md-3">
                <input v-model="filters.productName" type="text" class="form-control" placeholder="Tên sản phẩm" />
            </div>
            <div class="col-6 col-md-2">
                <input v-model.number="filters.minPrice" type="number" class="form-control" placeholder="Giá thấp nhất" />
            </div>
            <div class="col-6 col-md-2">
                <input v-model.number="filters.maxPrice" type="number" class="form-control" placeholder="Giá cao nhất" />
            </div>
            <div class="col-6 col-md-2">
                <select v-model.number="filters.categoryId" class="form-select">
                    <option :value="null">Tất cả danh mục</option>
                    <option v-for="category in categories" :value="category.id">
                        {{ category.name }}
                    </option>
                </select>
            </div>
            <div class="col-6 col-md-2">
                <select v-model="filters.sortOrderByPriceOrSales" class="form-select">
                    <option :value="null">Thứ tự sắp xếp</option>
                    <option value="PRICE-ASC">Giá thấp → cao</option>
                    <option value="PRICE-DESC">Giá cao → thấp</option>
                </select>
            </div>
            <div class="col-12 col-md-1">
                <button class="btn btn-primary w-100" @click="refresh()">Làm mới</button>
            </div>
        </div>

        <!-- Products -->
        <div class="row row-cols-2 row-cols-md-4 g-3">
            <div v-for="product in products" :key="product.id" class="col">
                <div class="card h-100">
                    <img :src="getImageUrl(product.image)" class="card-img-top object-fit-cover" height="400" :alt="product.name" />
                    <div class="card-body">
                        <h6 class="card-title">{{ product.name }}</h6>
                        <p class="card-text text-danger fw-bold">
                            {{ formatPrice(product.costPrice * (1 + product.retailPercentage)) }}
                        </p>
                    </div>
                    <div class="card-footer d-flex gap-2">
                        <button class="btn btn-outline-primary btn-sm flex-fill" @click="addToCart(product.id)">Thêm giỏ</button>
                        <button class="btn btn-primary btn-sm flex-fill" @click="viewDetails(product.id)">Chi tiết</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Loading & Error -->
        <div v-if="loading" class="text-center py-5">
            <div class="spinner-border text-primary" role="status"></div>
        </div>
        <div v-if="error" class="alert alert-danger">{{ error }}</div>

        <!-- Empty state -->
        <div v-if="!loading && products.length === 0" class="text-center text-muted py-5">
            Không tìm thấy sản phẩm nào
        </div>

        <!-- Pagination -->
        <nav v-if="totalPages > 1" class="mt-4">
            <ul class="pagination justify-content-center">
                <li class="page-item" :class="{ disabled: filters.page === 1 }">
                    <button class="page-link" @click="changePage(1)">&laquo; First</button>
                </li>
                <li
                    v-for="p in totalPages <= 7 ? totalPages : Array.from({length: 7}, (_, i) => Math.min(Math.max(filters.page - 3, 1), totalPages - 6) + i).filter(p => p >= 1 && p <= totalPages)"
                    :key="p" class="page-item" :class="{ active: filters.page === p }">
                    <button class="page-link" @click="changePage(p)">{{ p }}</button>
                </li>
                <li class="page-item" :class="{ disabled: filters.page === totalPages }">
                    <button class="page-link" @click="changePage(totalPages)">Last &raquo;</button>
                </li>
            </ul>
        </nav>
    </div>
</template>

<script setup>
    import { ref, onMounted, watch } from "vue";
    import { getAllPaginatedAndFilteredProducts, getTotalProductPages, getProductById } from "../api/productService";
    import { getAllPaginatedAndFilteredCategories } from "../api/categoryService";
    import { useRouter } from 'vue-router';
    import { useCartStore } from "../stores/cart";

    const products = ref([]);
    const categories = ref([]);
    const totalPages = ref(1);
    const loading = ref(false);
    const error = ref(null);

    const showError = (e, msg) => {
        error.value = e.response?.data || msg;
        setTimeout(() => error.value = null, 2000);
    };

    const filters = ref({
        minPrice: null,
        maxPrice: null,
        categoryId: null,
        productName: null,
        sortOrderByPriceOrSales: null,
        available: true,
        page: 1,
        pageSize: 8
    });

    const router = useRouter();
    const cart = useCartStore();

    const fetchProducts = async () => {
        loading.value = true;
        error.value = null;
        try {
            const response1 = await getAllPaginatedAndFilteredProducts({...filters.value});
            const response2 = await getTotalProductPages({...filters.value});
            products.value = response1.data;
            totalPages.value = response2.data;
            return response1
        } catch (e) {
            showError(e, 'Lỗi khi tải sản phẩm');
        } finally {
            loading.value = false;
        }
    }

    const fetchCategories = async () => {
        try {
            const param = {keyword: null, sortOrder: null, page: null, pageSize: null}
            const response = await getAllPaginatedAndFilteredCategories(param);
            categories.value = response.data;
        } catch (e) {
            showError(e, 'Lỗi khi tải danh mục');
        }
    }
    watch(filters, () => { 
        if (filters.value.page > totalPages.value)
            filters.value.page = 1;
        fetchProducts();
    }, { deep: true }); //this means if a single field change, the whole damn form submit

    const refresh = () => {
        filters.value = {
            minPrice: null,
            maxPrice: null,
            categoryId: null,
            productName: null,
            sortOrderByPriceOrSales: null,
            available: true,
            page: 1,
            pageSize: 8
        };
        fetchProducts();
    }

    const changePage = (page) => {
        if (page < 1 || page > totalPages.value) return;
        filters.value.page = page;
        fetchProducts();
    };

    const BASE_URL = import.meta.env.VITE_API_URL;
    const getImageUrl = (image) => {
        if (!image) return 'https://placehold.co/300x200';
        return `${BASE_URL}/images/${image}`;
    };

    const formatPrice = (value) => {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(value)
    }

    const addToCart = async (id) => {
        try {
            const response = await getProductById(id);
            const product = response.data;
            const item = {
                productId: product.id,
                name: product.name,
                price: product.costPrice * (1 + product.retailPercentage),
                quantity: 1,
                subtotal: product.costPrice * (1 + product.retailPercentage)
            }
            cart.addItem(item);
            cart.successfullyAdded = true
            setTimeout(() => {
                cart.successfullyAdded = false;
            }, 2000);
        } catch (e) {
            showError(e, 'Lỗi khi thêm vào giỏ');
        }
    };

    const viewDetails = (id) => {
        router.push(`/product/${id}`);
    };

    onMounted(
        fetchProducts(),
        fetchCategories(),
    );
</script>