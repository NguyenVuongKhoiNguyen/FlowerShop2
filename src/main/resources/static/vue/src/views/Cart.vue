<template>
  <div class="container mt-4">
    <div v-if="errorMsg" class="alert alert-danger alert-dismissible fade show" role="alert">
      {{ errorMsg }}
    </div>
    <h2 class="mb-3">Giỏ hàng</h2>

    <!-- Empty cart -->
    <div v-if="cartStore.items.length === 0" class="alert alert-info">
      Không còn gì cả :))))))))))))))))))))
    </div>

    <!-- Cart table -->
    <table v-else class="table table-bordered align-middle">
      <thead class="table-dark">
        <tr>
          <th>#</th>
          <th>Name</th>
          <th>Price</th>
          <th>Quantity</th>
          <th>Subtotal</th>
          <th>Action</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="(item, index) in cartStore.items" :key="item.id">
          <td>{{ index + 1 }}</td>
          <td>{{ item.name }}</td>
          <td>{{ formatPrice(item.price) }}</td>
          <!-- 
            v-model.number="item.quantity"
            if item input change then item.quantity in cart also update
          -->
          <td><input type="number" v-model.number="item.quantity" min="1" :max="item.maxAmount"></td>
          <td>{{ formatPrice(item.subtotal) }}</td>
          <td>
            <button class="btn btn-danger btn-sm"@click="cartStore.removeItem(item.productId)">Xoá</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Footer -->
    <div v-if="cartStore.items.length > 0" class="d-flex justify-content-between align-items-center">
      
      <!-- Total -->
      <h5>
        Total: <strong>{{ formatPrice(cartStore.totalPrice) }}</strong>
      </h5>

      <!-- Actions -->
      <div>
        <button class="btn btn-outline-danger me-2" @click="cartStore.clearCart()">
          Xoá giỏ hàng
        </button>
        <button  class="btn btn-success" @click="saveCart(cartStore.id)"> 
          Lưu giỏ hàng
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
  import { useCartStore } from '../stores/cart';
  import { onMounted, watch, ref } from 'vue';
  import { getProductById } from '../api/productService';
  import { createCart, updateCart } from '../api/cartService';
  import { useAuthStore } from '../stores/auth';
  import { useRouter } from 'vue-router';
  import Cart from '../models/Cart';

  const cartStore = useCartStore();
  const auth = useAuthStore();
  const router = useRouter();
  const errorMsg = ref(null);

  const showError = (e, msg) => {
    errorMsg.value = e.response?.data || msg;
    setTimeout(() => errorMsg.value = null, 2000);
  };

  const getProductAmounts = async () => {
    try {
      for (const item of cartStore.items) {
        const response = await getProductById(item.productId);
        item.maxAmount = response.data.amount;
      }
    } catch (e) {
      showError(e, 'Lỗi khi tải thông tin sản phẩm');
    }
  };
  const formatPrice = (value) => {
      return new Intl.NumberFormat('vi-VN', {
          style: 'currency',
          currency: 'VND'
      }).format(value)
  }

  /**
   * first extract the quantity values into an array
   * via this line "cart.items.map(item => item.quantity)""
   * example: 
   *  if array have  [{id:1, quantity:2}, {id:2, quantity:3}]
   *  then array.map(e => e.quantity) return [2,3]
   * second passing extracted values(mapped array above) into watch via (newQuantities)
   * finally loop through the array and re-caculate the subtotal
   * 
   * Note: 
   * (newValues) (newValues, oldValues) correct
   * (oldValues) : still newValues
   * (oldValues, newValues) wrong
   * 
   * Note: 
   * watch(() => {},() => {}) equals watch(function, function)
   * watch(reactiveValue, () => {}) equals watch(reactive_variable, function)
   * watch(being watched, then do something)
   */
  watch(() => cartStore.items.map(item => item.quantity),
    (newQuantities) => {
      for (let i = 0; i < cartStore.itemCount; i++) {
        cartStore.items[i].subtotal = cartStore.items[i].price * newQuantities[i];        
      }
    }
  );
/*
  watch(cart.items.map(item => item.quantity), (newQuantities) => {
    for (let i = 0; i < cart.itemCount; i++) {
        cart.items[i].subtotal = cart.items[i].price * newQuantities[i];        
      }
  });
*/
  const saveCart = async (id) => {

    if (auth.user === null) {
      router.push("/login");
      return;
    }

    const cartResponse = new Cart(auth.user, []);
    cartStore.items.forEach(item => cartResponse.addItem(item.productId, item.quantity));

    if (id) {
      try {
        const response = await updateCart(cartResponse, id);
        cartStore.clearCart();
        cartStore.successfullyAdded = true;
        setTimeout(() => { cartStore.successfullyAdded = false; }, 2000);
        cartStore.id = null;
        return response;
      } catch (error) {
        showError(error, 'Lỗi khi cập nhật giỏ hàng');
      }
    } else {
      try {
        const response = await createCart(cartResponse);
        cartStore.clearCart();
        cartStore.successfullyAdded = true;
        setTimeout(() => { cartStore.successfullyAdded = false; }, 2000);
        return response;
      } catch (error) {
        showError(error, 'Lỗi khi lưu giỏ hàng');
      }
    }
  }

  onMounted(() => {
    getProductAmounts();
  });
</script>