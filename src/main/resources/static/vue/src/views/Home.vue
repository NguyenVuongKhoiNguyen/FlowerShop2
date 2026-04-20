<template>
    <div class="container py-4">
        <div v-if="!cart" class="alert alert-success">
            Your cart is empty
        </div>

        <div class="py-3">
            <h2>Bán nhiều nhất</h2>
        </div>

        <div class="row row-cols-2 row-cols-md-4 g-3">
            <div v-for="product in top8ProductsWithMostSales" :key="product.id" class="col">
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
        <div v-if="loading1" class="text-center py-5">
            <div class="spinner-border text-primary" role="status"></div>
        </div>
        <div v-if="error1" class="alert alert-danger">{{ error1 }}</div>

        <!-- Empty state -->
        <div v-if="!loading1 && top8ProductsWithMostSales.length === 0" class="text-center text-muted py-5">
            Không tìm thấy sản phẩm nào
        </div>

        <div class="py-3 d-flex justify-content-between">
            <h2>Ngẫu nhiên</h2>
            <button class="btn btn-primary" @click="fetchTop8RandomProducts()">Tạo ngẫu nhiên</button>
        </div>

        <div class="row row-cols-2 row-cols-md-4 g-3">
            <div v-for="product in top8RandomProducts" :key="product.id" class="col">
                <div class="card h-100">
                    <img :src="getImageUrl(product.image)" class="card-img-top object-fit-cover" height="400" :alt="product.name" />
                    <div class="card-body">
                        <h6 class="card-title">{{ product.name }}</h6>
                        <p class="card-text text-danger fw-bold">
                            {{ formatPrice(product.costPrice * (1 + product.retailPercentage)) }}
                        </p>
                    </div>
                    <div class="card-footer d-flex gap-2">
                        <button class="btn btn-outline-primary btn-sm flex-fill" @click="addToCart(product.id)" >Thêm giỏ</button>
                        <button class="btn btn-primary btn-sm flex-fill" @click="viewDetails(product.id)">Chi tiết</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Loading & Error -->
        <div v-if="loading2" class="text-center py-5">
            <div class="spinner-border text-primary" role="status"></div>
        </div>
        <div v-if="error2" class="alert alert-danger">{{ error2 }}</div>

        <!-- Empty state -->
        <div v-if="!loading2 && top8RandomProducts.length === 0" class="text-center text-muted py-5">
            Không tìm thấy sản phẩm nào
        </div>
    </div>
</template>

<script setup>
    import { onMounted, ref } from 'vue';
    import { getRandomProducts, getProductsWithTopSales, getProductById } from '../api/productService';
    import { useCartStore } from '../stores/cart'
    import { useRouter } from 'vue-router';

    const loading1 = ref(false);
    const error1 = ref(null);
    
    const loading2 = ref(false);
    const error2 = ref(null);

    const top8ProductsWithMostSales = ref([]);
    const top8RandomProducts = ref([]);

    const cart = useCartStore();
    const router = useRouter();

    const fetchTop8WithMostSalesProducts = async () => {
        loading1.value = true;
        error1.value = null;
        try {
            const response = await getProductsWithTopSales();
            top8ProductsWithMostSales.value = response.data;
        } catch (error) {
            error1.value = error.response?.data || 'Lỗi khi tải sản phẩm';
            setTimeout(() => error1.value = null, 2000);
        } finally {
            loading1.value = false;
        }
    }

    const fetchTop8RandomProducts = async () => {
        loading2.value = true;
        error2.value = null;
        try {
            const response = await getRandomProducts();
            top8RandomProducts.value = response.data;
        } catch (error) {
            error2.value = error.response?.data || 'Lỗi khi tải sản phẩm';
            setTimeout(() => error2.value = null, 2000);
        } finally {
            loading2.value = false;
        }
    }

    const BASE_URL = import.meta.env.VITE_API_URL;
    const getImageUrl = (image) => {
        if (!image) return 'https://placehold.co/300x200';
        return `${BASE_URL}/images/${image}`;
    }

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
        } catch (error) {
            error1.value = error.response?.data || 'Lỗi khi thêm vào giỏ';
            setTimeout(() => error1.value = null, 2000);
        } finally {
            cart.successfullyAdded = true
            setTimeout(() => {
                cart.successfullyAdded = false;
            }, 2000);
        }
    };

    const viewDetails = (id) => {
        router.push(`/product/${id}`);
    };

    onMounted(() => {
        fetchTop8WithMostSalesProducts();
        fetchTop8RandomProducts();
    });
</script>