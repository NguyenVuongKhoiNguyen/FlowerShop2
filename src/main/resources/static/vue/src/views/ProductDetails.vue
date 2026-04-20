<template>
    <div class="container py-4">
        <div class="row g-4">
            <!-- Image column -->
            <div class="col-12 col-md-6">
                <div class="card">
                    <img :src="getImageUrl(product.image)" :alt="product.name" class="card-img-top object-fit-cover" style="height: 450px; width: 100%;"/>
                    <div class="card-body d-flex justify-content-center">
                        <small class="text-muted">Ngày tạo: {{ new Date(product.createDate).toLocaleDateString("vi-VN") }}</small>
                    </div>
                </div>
            </div>

            <!-- Details column -->
            <div class="col-12 col-md-6">
                <div class="d-flex justify-content-between align-items-start mb-2">
                    <h2 class="mb-0">{{ product.name }}</h2>
                    <span
                    class="badge"
                    :class="product.available ? 'bg-success' : 'bg-secondary'"
                    >
                    {{ product.available ? 'Còn hàng' : 'Hết hàng' }}
                    </span>
                </div>

                <p class="text-muted mb-1">Mã sản phẩm: <strong>#{{ product.id }}</strong></p>

                <h3 class="text-danger fw-bold">
                    {{ formatPrice(product.costPrice * (1 + product.retailPercentage)) }}₫
                </h3>

                <p class="mb-3">
                    <strong>Số lượng trong kho: </strong>
                    <span>{{ product.amount }}</span>
                    <span class="ms-3 text-muted">Đã bán: {{ product.sales }}</span>
                </p>

                <div class="mb-3">
                    <label class="form-label"><strong>Số lượng</strong></label>
                    <div class="input-group" style="max-width: 160px;">
                        <button class="btn btn-outline-secondary" @click="decreaseQuantity()" :disabled="quantity <= 1">−</button>
                        <input type="number" class="form-control text-center" v-model.number="quantity" min="1" :max="product.amount"/>
                        <button class="btn btn-outline-secondary" @click="increaseQuantity()" :disabled="quantity >= product.amount">+</button>
                    </div>
                    <div v-if="qty > product.amount" class="form-text text-danger">Số lượng vượt quá tồn kho</div>
                </div>

                <div class="d-flex gap-2 mb-3">
                    <button class="btn btn-primary btn-lg" :disabled="!product.available || product.amount === 0" @click="addToCart(product.id)">Thêm vào giỏ</button>
                    <button class="btn btn-outline-secondary btn-lg" @click="toProductPage()">Quay lại</button>
                </div>

                <hr/>

                <div class="small text-muted">
                    <p class="mb-0"><strong>Danh mục:</strong> {{ category.name }}</p>
                    <br>
                    <p class="mb-0"><strong>Mô tả:</strong> Lorem ipsum dolor sit amet consectetur, adipisicing elit. Hic, dicta optio ad, accusamus aliquam natus corporis asperiores repudiandae quas sed a illum quis. Iusto porro aperiam dicta, debitis error ad. Lorem ipsum dolor sit amet consectetur, adipisicing elit. Laborum, suscipit adipisci? Quae iusto consectetur sequi, accusamus aliquam eaque officiis neque hic perferendis at repudiandae exercitationem? Tenetur deserunt ratione illum saepe?</p>
                </div>
            </div>
        </div>

        <!-- Loading & Error -->
        <div v-if="loading" class="text-center py-5">
            <div class="spinner-border text-primary" role="status"></div>
        </div>
        <div v-if="errorMsg" class="alert alert-danger">{{ errorMsg }}</div>

        <!-- Empty state -->
        <div v-if="!loading && !product" class="text-center text-muted py-5">
            Không tìm thấy sản phẩm nào
        </div>

        <!-- Comments -->
        <div class="mt-5">
            <h5 class="mb-3">Bình luận</h5>

            <!-- new comment input -->
            <div v-if="isLogin" class="mb-4">
                <textarea v-model="newComment" class="form-control mb-2" rows="2" placeholder="Viết bình luận..." />
                <button class="btn btn-primary btn-sm" @click="submitComment">Gửi</button>
            </div>

            <div v-if="commentsLoading" class="text-center py-3">
                <div class="spinner-border spinner-border-sm text-secondary" role="status"></div>
            </div>

            <div v-else-if="comments.length === 0" class="text-muted">
                Chưa có bình luận nào.
            </div>

            <div v-else>
                <div v-for="c in comments" :key="c.id" class="card mb-3 shadow-sm">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-start">
                            <div>
                                <strong>{{ c.username }}</strong>
                                <small class="text-muted ms-2">{{ new Date(c.createDate).toLocaleDateString('vi-VN') }}</small>
                            </div>
                            <button v-if="auth.user === c.username" class="btn btn-sm btn-outline-danger" @click="removeComment(c.id)">Xoá</button>
                        </div>
                        <p class="mb-2 mt-1">{{ c.content }}</p>

                        <div class="d-flex gap-2">
                            <!-- toggle replies -->
                            <button class="btn btn-sm btn-outline-secondary" @click="toggleReplies(c.id)">
                                {{ expandedComment === c.id ? '▲ Ẩn phản hồi' : '▼ Xem phản hồi' }}
                            </button>
                            <!-- reply button -->
                            <button v-if="isLogin" class="btn btn-sm btn-outline-primary" @click="replyingTo = replyingTo === c.id ? null : c.id">
                                Phản hồi
                            </button>
                        </div>

                        <!-- reply input -->
                        <div v-if="isLogin && replyingTo === c.id" class="mt-2">
                            <textarea v-model="newReply[c.id]" class="form-control mb-2" rows="2" placeholder="Viết phản hồi..." />
                            <button class="btn btn-primary btn-sm me-2" @click="submitReply(c.id)">Gửi</button>
                            <button class="btn btn-secondary btn-sm" @click="replyingTo = null">Huỷ</button>
                        </div>

                        <!-- replies -->
                        <div v-if="expandedComment === c.id" class="mt-3 ps-3 border-start">
                            <div v-if="repliesLoading" class="text-muted small">Đang tải...</div>
                            <div v-else-if="replies[c.id]?.length === 0" class="text-muted small">Chưa có phản hồi.</div>
                            <div v-else v-for="r in replies[c.id]" :key="r.id" class="mb-2">
                                <div class="d-flex justify-content-between align-items-start">
                                    <div>
                                        <strong class="small">{{ r.username }}</strong>
                                        <small class="text-muted ms-2">{{ new Date(r.createDate).toLocaleDateString('vi-VN') }}</small>
                                    </div>
                                    <button v-if="auth.user === r.username" class="btn btn-sm btn-outline-danger" @click="removeReply(c.id, r.id)">Xoá</button>
                                </div>
                                <p class="mb-0 small">{{ r.content }}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

</template>

<script setup>
    import { ref, onMounted, computed } from 'vue';
    import { useRoute, useRouter } from 'vue-router';
    import { getProductById } from '../api/productService';
    import { getCategoryById } from '../api/categoryService';
    import { useCartStore } from "../stores/cart";
    import { getProductComments, getCommentReplies, createComment, createReply, deleteComment, deleteReply } from '../api/commentService';
    import { useAuthStore } from '../stores/auth';

    const route = useRoute();
    const router = useRouter();
    const cart = useCartStore();

    const productId = route.params.id;
    const product = ref({});
    const category = ref({});
    const quantity = ref(1);
    const loading = ref(false);
    const error = ref(null);
    const errorMsg = ref(null);

    const showError = (e, msg) => {
        errorMsg.value = e.response?.data || msg;
        setTimeout(() => errorMsg.value = null, 2000);
    };

    const fillProduct = async (id) => {
        try {
            loading.value = true;
            const response1 = await getProductById(id);
            product.value = response1.data;
            const response2 = await getCategoryById(product.value.categoryId);
            category.value = response2.data
        } catch (e) {
            showError(e, 'Lỗi khi tải sản phẩm');
        } finally {
            loading.value = false;
        }
    }
    
    const BASE_URL = import.meta.env.VITE_API_URL;
    const getImageUrl = (image) => {
        if (!image) return 'https://placehold.co/300x200';
        return `${BASE_URL}/images/${image}`;
    };

    const toProductPage = () => {
        router.push("/product");
    }

    const increaseQuantity = () => {
        quantity.value += 1;
    }

    const decreaseQuantity = () => {
        quantity.value -= 1;
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
                quantity: quantity.value,
                subtotal: product.costPrice * (1 + product.retailPercentage)
            }
            cart.addItem(item);
        } catch (e) {
            showError(e, 'Lỗi khi thêm vào giỏ');
        } finally {
            cart.successfullyAdded = true
            setTimeout(() => {
                cart.successfullyAdded = false;
            }, 2000);
        }
    };

    const auth = useAuthStore();
    const isLogin = computed(() => !!auth.token);

    const comments = ref([]);
    const replies = ref({});
    const expandedComment = ref(null);
    const commentsLoading = ref(false);
    const repliesLoading = ref(false);
    const newComment = ref('');
    const newReply = ref({});
    const replyingTo = ref(null);

    const fetchComments = async () => {
        try {
            commentsLoading.value = true;
            const res = await getProductComments(productId);
            comments.value = res.data;
        } catch (e) {
            showError(e, 'Lỗi khi tải bình luận');
        } finally {
            commentsLoading.value = false;
        }
    };

    const toggleReplies = async (commentId) => {
        if (expandedComment.value === commentId) {
            expandedComment.value = null;
            return;
        }
        expandedComment.value = commentId;
        if (replies.value[commentId]) return;
        try {
            repliesLoading.value = true;
            const res = await getCommentReplies(commentId);
            replies.value[commentId] = res.data;
        } catch (e) {
            showError(e, 'Lỗi khi tải phản hồi');
        } finally {
            repliesLoading.value = false;
        }
    };

    const submitComment = async () => {
        if (!newComment.value.trim()) return;
        try {
            await createComment({ productId: Number(productId), username: auth.user, content: newComment.value });
            newComment.value = '';
            await fetchComments();
        } catch (e) {
            showError(e, 'Lỗi khi gửi bình luận');
        }
    };

    const submitReply = async (commentId) => {
        if (!newReply.value[commentId]?.trim()) return;
        try {
            await createReply({ commentId, username: auth.user, content: newReply.value[commentId] });
            newReply.value[commentId] = '';
            delete replies.value[commentId];
            await toggleReplies(commentId);
        } catch (e) {
            showError(e, 'Lỗi khi gửi phản hồi');
        }
    };

    const removeComment = async (commentId) => {
        try {
            await deleteComment(commentId);
            await fetchComments();
        } catch (e) {
            showError(e, 'Lỗi khi xoá bình luận');
        }
    };

    const removeReply = async (commentId, replyId) => {
        try {
            await deleteReply(replyId);
            delete replies.value[commentId];
            await toggleReplies(commentId);
        } catch (e) {
            showError(e, 'Lỗi khi xoá phản hồi');
        }
    };

    onMounted(() => {
        fillProduct(productId);
        fetchComments();
    });
</script>