<template>
  <div class="container mt-4" style="max-width: 600px;">
    <div v-if="errorMsg" class="alert alert-danger alert-dismissible fade show" role="alert">
      {{ errorMsg }}
    </div>
    <div v-if="successMsg" class="alert alert-success alert-dismissible fade show" role="alert">
      {{ successMsg }}
    </div>

    <div class="card shadow-sm">
      <div class="card-body">

        <!-- Avatar -->
        <div class="d-flex justify-content-center mb-3">
          <img :src="getImageUrl(previewImage || accountInput.photo)" style="width:100px;height:100px;object-fit:cover;border-radius:50%;" />
        </div>

        <div class="mb-3">
          <label class="form-label">Ảnh đại diện</label>
          <input type="file" class="form-control" ref="fileInput" @change="handleFileUpload" />
        </div>

        <div class="mb-3">
          <label class="form-label">Tên đăng nhập</label>
          <input :value="accountInput.username" class="form-control" readonly />
        </div>

        <div class="mb-3">
          <label class="form-label">Mật khẩu mới</label>
          <input v-model="accountInput.password" type="password" class="form-control" placeholder="Để trống nếu không đổi" />
        </div>

        <div class="mb-3">
          <label class="form-label">Họ và tên</label>
          <input v-model="accountInput.fullname" class="form-control" />
        </div>

        <div class="mb-3">
          <label class="form-label">Email</label>
          <input v-model="accountInput.email" class="form-control" />
        </div>

        <div class="mb-3">
          <label class="form-label">Điện thoại</label>
          <input v-model="accountInput.phone" class="form-control" />
        </div>

        <div class="mb-3">
          <label class="form-label">Địa chỉ</label>
          <input v-model="accountInput.address" class="form-control" />
        </div>

        <div class="d-flex justify-content-center">
          <button class="btn btn-primary" @click="save" :disabled="saving">
            <span v-if="saving" class="spinner-border spinner-border-sm me-1" role="status"></span>
            {{ saving ? 'Đang lưu...' : 'Lưu thay đổi' }}
          </button>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue';
import { useAuthStore } from '../stores/auth';
import { getAccountByUsername, updateNonAdminAccount } from '../services/accountService';

const auth = useAuthStore();
const fileInput = ref(null);
const selectedFile = ref(null);
const previewImage = ref(null);
const saving = ref(false);
const errorMsg = ref(null);
const successMsg = ref(null);

const accountInput = reactive({
  username: null,
  password: '',
  fullname: null,
  email: null,
  photo: null,
  phone: null,
  address: null,
  activated: null,
  roles: []
});

const showError = (e, msg) => {
  errorMsg.value = e.response?.data || msg;
  setTimeout(() => errorMsg.value = null, 2000);
};

const BASE_URL = import.meta.env.VITE_API_URL;
const getImageUrl = (img) => {
  if (!img) return 'https://placehold.co/300x200';
  if (img.startsWith('blob:')) return img;
  return `${BASE_URL}/images/${img}`;
};

const handleFileUpload = (event) => {
  const file = event.target.files[0];
  if (!file) return;
  selectedFile.value = file;
  previewImage.value = URL.createObjectURL(file);
};

const fetchAccount = async () => {
  try {
    const response = await getAccountByUsername(auth.user);
    const a = response.data;
    Object.assign(accountInput, {
      username: a.username,
      password: '',
      fullname: a.fullname,
      email: a.email,
      photo: a.photo,
      phone: a.phone,
      address: a.address,
      activated: a.activated,
      roles: a.roleResponses ?? []
    });
  } catch (e) {
    showError(e, 'Lỗi khi tải thông tin tài khoản');
  }
};

const save = async () => {
  saving.value = true;
  try {
    const formData = new FormData();
    if (selectedFile.value) formData.append('file', selectedFile.value);
    formData.append('account', new Blob([JSON.stringify({ ...accountInput })], { type: 'application/json' }));
    await updateNonAdminAccount(formData, accountInput.username);
    successMsg.value = 'Cập nhật thành công';
    selectedFile.value = null;
    previewImage.value = null;
    if (fileInput.value) fileInput.value.value = '';
    await fetchAccount();
    setTimeout(() => successMsg.value = null, 2000);
  } catch (e) {
    showError(e, 'Lỗi khi cập nhật tài khoản');
  } finally {
    saving.value = false;
  }
};

onMounted(() => {
  fetchAccount();
});
</script>
